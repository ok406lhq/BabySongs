package com.cool.music.activity.user;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cool.music.R;
import com.cool.music.adapter.user.LocalMusicAdapter;
import com.cool.music.bean.MusicBean;
import com.cool.music.dao.MusicDao;
import com.cool.music.dao.PlayMusicDao;
import com.cool.music.until.LocalMusicScanner;
import com.cool.music.until.Tools;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 本地音乐扫描Activity
 * 用于扫描设备中的MP3文件并添加到播放列表
 */
public class LocalScanActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_CODE = 1001;

    private RecyclerView recyclerView;
    private LocalMusicAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvStatus;
    private Button btnScan;
    private Button btnSelectAll;
    private Button btnDeselectAll;
    private Button btnAddToPlaylist;
    private TextView tvSelectedCount;

    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_local_scan);

        initViews();
        setupListeners();

        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        // 检查权限
        if (checkPermissions()) {
            // 自动开始扫描
            startScanning();
        } else {
            requestPermissions();
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rv_local_music);
        progressBar = findViewById(R.id.progress_bar);
        tvStatus = findViewById(R.id.tv_status);
        btnScan = findViewById(R.id.btn_scan);
        btnSelectAll = findViewById(R.id.btn_select_all);
        btnDeselectAll = findViewById(R.id.btn_deselect_all);
        btnAddToPlaylist = findViewById(R.id.btn_add_to_playlist);
        tvSelectedCount = findViewById(R.id.tv_selected_count);

        // 设置RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LocalMusicAdapter(null);
        recyclerView.setAdapter(adapter);

        // 返回按钮
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void setupListeners() {
        btnScan.setOnClickListener(v -> {
            if (checkPermissions()) {
                startScanning();
            } else {
                requestPermissions();
            }
        });

        btnSelectAll.setOnClickListener(v -> {
            adapter.selectAll();
            updateSelectedCount();
        });

        btnDeselectAll.setOnClickListener(v -> {
            adapter.deselectAll();
            updateSelectedCount();
        });

        btnAddToPlaylist.setOnClickListener(v -> addSelectedToPlaylist());
    }

    /**
     * 检查权限
     */
    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        } else {
            // Android 12 及以下
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * 请求权限
     */
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_AUDIO},
                    REQUEST_PERMISSION_CODE);
        } else {
            // Android 12 及以下
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning();
            } else {
                Tools.Toast(this, "需要存储权限才能扫描本地音乐");
                tvStatus.setText("权限被拒绝，无法扫描本地音乐");
            }
        }
    }

    /**
     * 开始扫描
     */
    private void startScanning() {
        progressBar.setVisibility(View.VISIBLE);
        tvStatus.setText("正在扫描本地音乐...");
        btnScan.setEnabled(false);
        btnAddToPlaylist.setEnabled(false);

        executorService.execute(() -> {
            try {
                List<MusicBean> musicList = LocalMusicScanner.scanLocalMusic(this);

                mainHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnScan.setEnabled(true);

                    if (musicList != null && !musicList.isEmpty()) {
                        adapter.updateMusicList(musicList);
                        tvStatus.setText("找到 " + musicList.size() + " 首本地音乐");
                        btnAddToPlaylist.setEnabled(true);
                        updateSelectedCount();
                    } else {
                        tvStatus.setText("未找到本地音乐文件");
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnScan.setEnabled(true);
                    tvStatus.setText("扫描失败: " + e.getMessage());
                    Tools.Toast(this, "扫描失败，请重试");
                });
            }
        });
    }

    /**
     * 添加选中的音乐到播放列表
     */
    private void addSelectedToPlaylist() {
        List<MusicBean> selectedMusic = adapter.getSelectedMusic();

        if (selectedMusic.isEmpty()) {
            Tools.Toast(this, "请先选择要添加的音乐");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        tvStatus.setText("正在添加到播放列表...");
        btnAddToPlaylist.setEnabled(false);

        executorService.execute(() -> {
            int addedCount = 0;
            int duplicateCount = 0;
            String currentAccount = Tools.getOnAccount(this);

            for (MusicBean music : selectedMusic) {
                try {
                    // 检查是否已存在（通过ID判断）
                    MusicBean existing = MusicDao.getMusicById(music.getId());

                    if (existing == null) {
                        // 添加到音乐库
                        int result = MusicDao.addMusic(
                                music.getId(),
                                music.getName(),
                                music.getSinger(),
                                music.getImg(),
                                music.getPath()
                        );

                        if (result > 0) {
                            // 同时添加到当前用户的播放列表
                            PlayMusicDao.addToPlaylist(currentAccount, music.getId());
                            addedCount++;
                        }
                    } else {
                        // 即使歌曲已存在，也添加到播放列表（如果还没有的话）
                        PlayMusicDao.addToPlaylist(currentAccount, music.getId());
                        duplicateCount++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            final int finalAddedCount = addedCount;
            final int finalDuplicateCount = duplicateCount;

            mainHandler.post(() -> {
                progressBar.setVisibility(View.GONE);
                btnAddToPlaylist.setEnabled(true);

                String message = "成功添加 " + finalAddedCount + " 首新音乐";
                if (finalDuplicateCount > 0) {
                    message += "，" + finalDuplicateCount + " 首已存在";
                }
                message += "到播放列表";

                tvStatus.setText(message);
                Tools.Toast(this, message);

                if (finalAddedCount > 0 || finalDuplicateCount > 0) {
                    // 清空选择
                    adapter.deselectAll();
                    updateSelectedCount();
                }
            });
        });
    }

    /**
     * 更新选中数量显示
     */
    private void updateSelectedCount() {
        int count = adapter.getSelectedCount();
        tvSelectedCount.setText("已选择: " + count + " 首");
        btnAddToPlaylist.setEnabled(count > 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
