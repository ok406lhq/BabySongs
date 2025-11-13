package com.cool.music.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.cool.music.R;
import com.cool.music.activity.user.fragment.DiscussFragment;
import com.cool.music.activity.user.fragment.HomeFragment;
import com.cool.music.activity.user.fragment.LikeFragment;
import com.cool.music.activity.user.fragment.MyFragment;
import com.cool.music.activity.user.fragment.UserGenreFragment;
import com.cool.music.bean.MessageEvent;
import com.cool.music.bean.MusicBean;
import com.cool.music.dao.MusicDao;
import com.cool.music.dao.PlayMusicDao;
import com.cool.music.until.MusicPlayerManager;
import com.cool.music.until.Tools;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

public class UserManageActivity extends AppCompatActivity {

    private String musicId;
    private Handler handler;
    public static ExoPlayer player;  // 保持静态引用，但通过单例管理

    public static SeekBar seekBar;
    private Runnable updateSeekBarRunnable;
    private int oldTime = 0;
    private int duration = -1;

    // ✨ 添加标志，防止重复初始化
    private static boolean isPlayerInitialized = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        // ✨ 注意：不要在这里 release player，因为后台播放需要保持
        // 只在应用真正退出时才释放

        // 取消注册 EventBus
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        getDelegate().onDestroy();
    }

    // ✨✨✨ 添加 onStop，停止UI更新
    @Override
    protected void onStop() {
        super.onStop();
        if (handler != null && updateSeekBarRunnable != null) {
            handler.removeCallbacks(updateSeekBarRunnable);
        }
    }

    // ✨✨✨ 添加 onResume，恢复UI更新
    @Override
    protected void onResume() {
        super.onResume();

        // 恢复进度条更新
        if (handler != null && updateSeekBarRunnable != null) {
            handler.post(updateSeekBarRunnable);
        }

        // ✨ 同步当前播放状态
        syncPlayerState();
    }

    private Player.Listener playerListener = new Player.Listener() {
        @Override
        public void onPlaybackStateChanged(int playbackState) {
            if (playbackState == Player.STATE_ENDED) {
                Log.d("UserManageActivity", "歌曲播放完成，自动播放下一首");
                playNextMusic();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_manage);

        // 注册 EventBus
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        // ✨✨✨ 使用单例获取播放器
        player = MusicPlayerManager.getInstance().getPlayer(this);

        // ✨ 只在第一次初始化时添加监听器
        if (!isPlayerInitialized) {
            player.addListener(playerListener);
            isPlayerInitialized = true;
        }

        // 实现点击可以进行跳转和页面的的切换功能
        FrameLayout frameLayout = this.findViewById(R.id.user_home_frame);

        FragmentManager fragment_container = getSupportFragmentManager();
        FragmentTransaction transaction = fragment_container.beginTransaction();
        transaction.replace(R.id.user_home_frame, new HomeFragment());
        transaction.commit();
        BottomNavigationView bottomNavigationView = findViewById(R.id.user_bottom_menu);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            FragmentManager f = getSupportFragmentManager();
            FragmentTransaction transaction1 = f.beginTransaction();
            int id = item.getItemId();
            if (id == R.id.user_bottom_menu_home) {
                transaction1.replace(R.id.user_home_frame, new HomeFragment());
                transaction1.commit();
            } else if (id == R.id.user_bottom_menu_genre) {
                transaction1.replace(R.id.user_home_frame, new UserGenreFragment());
                transaction1.commit();
            } else if (id == R.id.user_bottom_menu_list) {
                transaction1.replace(R.id.user_home_frame, new LikeFragment());
                transaction1.commit();
            } else if (id == R.id.user_bottom_menu_discuss) {
                transaction1.replace(R.id.user_home_frame, new DiscussFragment());
                transaction1.commit();
            } else if (id == R.id.user_bottom_menu_my) {
                transaction1.replace(R.id.user_home_frame, new MyFragment());
                transaction1.commit();
            }
            return true;
        });

        //--------------------------------让界面显示
        LinearLayout linearLayout = findViewById(R.id.user_music_bar);
        ImageButton userHomeTx = findViewById(R.id.user_home_tx);
        TextView userHomeName = findViewById(R.id.user_home_name);
        ImageView userHomeRun = findViewById(R.id.user_home_run);
        seekBar = findViewById(R.id.user_music_seekbar);

        handler = new Handler(Looper.getMainLooper());

        // ✨ 获取当前正在播放的音乐
        musicId = MusicPlayerManager.getInstance().getCurrentMusicId();

        if (musicId == null) {
            // 如果没有正在播放的音乐，从数据库加载
            MusicBean music = PlayMusicDao.getCurrentUserPlayMusic(Tools.getOnAccount(this), "1");
            if (music == null) {
                linearLayout.setVisibility(View.GONE);
            } else {
                musicId = music.getId();
                updateMusicUI(music, linearLayout, userHomeTx, userHomeName, userHomeRun);
            }
        } else {
            // 如果有正在播放的音乐，显示它
            MusicBean music = MusicDao.getMusicById(musicId);
            if (music != null) {
                updateMusicUI(music, linearLayout, userHomeTx, userHomeName, userHomeRun);
            }
        }

        // 设置播放栏点击事件
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicId != null) {
                    Intent intent = new Intent(UserManageActivity.this, RunMusicDetailActivity.class);
                    intent.putExtra("musicId", musicId);
                    startActivity(intent);
                }
            }
        });

        // 播放/暂停按钮
        userHomeRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    Glide.with(UserManageActivity.this)
                            .load(R.drawable.music_run)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                            .into(userHomeRun);
                    player.pause();
                } else {
                    Glide.with(UserManageActivity.this)
                            .load(R.drawable.music_z)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                            .into(userHomeRun);
                    player.play();
                }
            }
        });

        // 设置进度条监听
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // SeekBar 进度改变时
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                player.pause();
                oldTime = -1;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (player != null) {
                    player.seekTo(progress);
                    player.play();
                    oldTime = progress;
                }
            }
        });

        // 初始化进度条更新
        updateSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                if (player != null && player.isPlaying()) {
                    int currentPosition = (int) player.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    duration = (int) player.getDuration();
                    if (duration > 0) {
                        seekBar.setMax(duration);
                    }
                }

                // 更新播放按钮图标
                if (player.isPlaying()) {
                    Glide.with(UserManageActivity.this)
                            .load(R.drawable.music_z)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                            .into(userHomeRun);
                } else {
                    Glide.with(UserManageActivity.this)
                            .load(R.drawable.music_run)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                            .into(userHomeRun);
                }
                handler.postDelayed(this, 100);
            }
        };
        handler.post(updateSeekBarRunnable);
    }

    // ✨✨✨ 新增：更新音乐UI的辅助方法
    private void updateMusicUI(MusicBean music, LinearLayout linearLayout,
                               ImageButton userHomeTx, TextView userHomeName, ImageView userHomeRun) {
        linearLayout.setVisibility(View.VISIBLE);

        Glide.with(this)
                .load(music.getImg())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                .into(userHomeTx);

        String name = music.getName() + "-" + music.getSinger();
        userHomeName.setText(name);

        MusicPlayerManager.getInstance().setCurrentMusicId(music.getId());
    }

    // ✨✨✨ 新增：同步播放器状态
    private void syncPlayerState() {
        // 更新UI显示当前播放的歌曲
        String currentId = MusicPlayerManager.getInstance().getCurrentMusicId();
        if (currentId != null && !currentId.equals(musicId)) {
            musicId = currentId;
            MusicBean music = MusicDao.getMusicById(musicId);
            if (music != null) {
                LinearLayout linearLayout = findViewById(R.id.user_music_bar);
                ImageButton userHomeTx = findViewById(R.id.user_home_tx);
                TextView userHomeName = findViewById(R.id.user_home_name);
                ImageView userHomeRun = findViewById(R.id.user_home_run);
                updateMusicUI(music, linearLayout, userHomeTx, userHomeName, userHomeRun);
            }
        }
    }

    // 播放下一首歌曲的方法
    private void playNextMusic() {
        String nextMusicId = getNextMusicId();
        if (nextMusicId != null) {
            musicId = nextMusicId;

            PlayMusicDao.updateCurrentUserPlayMusicSta(Tools.getOnAccount(this), "0");
            PlayMusicDao.updateCurrentUserPlayMusicSta(Tools.getOnAccount(this), nextMusicId, "1");

            MusicBean nextMusic = MusicDao.getMusicById(nextMusicId);
            if (nextMusic != null) {
                MusicPlayerManager.getInstance().setCurrentMusicId(nextMusicId);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("send", "AutoPlay");
                jsonObject.put("receive", "UserManageActivity");
                jsonObject.put("data", nextMusic);
                jsonObject.put("sta", "1");
                EventBus.getDefault().post(new MessageEvent(jsonObject.toJSONString()));
            }
        } else {
            Log.d("UserManageActivity", "没有下一首歌曲了");
            Tools.Toast(this, "播放列表已结束");
        }
    }

    // 获取下一首歌曲ID
    private String getNextMusicId() {
        List<MusicBean> list = PlayMusicDao.getCurrentUserPlayMusic(Tools.getOnAccount(this));
        if (list != null && list.size() > 0) {
            int currentIndex = -1;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId().equals(musicId)) {
                    currentIndex = i;
                    break;
                }
            }

            if (currentIndex != -1) {
                if (currentIndex == list.size() - 1) {
                    return list.get(0).getId();
                } else {
                    return list.get(currentIndex + 1).getId();
                }
            }
        }
        return null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        LinearLayout linearLayout = findViewById(R.id.user_music_bar);
        ImageButton userHomeTx = findViewById(R.id.user_home_tx);
        TextView userHomeName = findViewById(R.id.user_home_name);
        ImageButton userHomeRun = findViewById(R.id.user_home_run);
        SeekBar seekBar = findViewById(R.id.user_music_seekbar);

        JSONObject json = JSONObject.parseObject(event.getMessage());
        MusicBean musicBean = json.getJSONObject("data").toJavaObject(MusicBean.class);

        // ✨ 检查是否是同一首歌，避免重复播放
        String currentPlayingId = MusicPlayerManager.getInstance().getCurrentMusicId();
        if (musicBean.getId().equals(currentPlayingId) && player.isPlaying()) {
            // 如果是同一首歌且正在播放，不做处理
            return;
        }

        File file = new File(musicBean.getPath());
        if (file.exists()) {
            String filePath = file.getAbsolutePath();
            MediaItem mediaItem = MediaItem.fromUri(filePath);
            DefaultDataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(this);
            ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem);
            player.setMediaSource(mediaSource);
            player.prepare();

            PlayMusicDao.updateCurrentUserPlayMusicSta(Tools.getOnAccount(this), "0");
            PlayMusicDao.updateCurrentUserPlayMusicSta(Tools.getOnAccount(this), musicBean.getId(), "1");

            musicId = musicBean.getId();
            MusicPlayerManager.getInstance().setCurrentMusicId(musicId);

            linearLayout.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .load(musicBean.getImg())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                    .into(userHomeTx);

            Glide.with(this)
                    .load(R.drawable.music_z)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                    .into(userHomeRun);

            userHomeName.setText(musicBean.getName());
            seekBar.setMax(duration);
            seekBar.setProgress(0);

            handler.post(updateSeekBarRunnable);
            player.play();
        } else {
            Tools.Toast(this, "音乐已经被删除，不存在");
        }
    }
}