package com.cool.music.activity.user;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cool.music.R;
import com.cool.music.adapter.user.AddSheetMusicAdapter;
import com.cool.music.adapter.user.SheetMusicAdapter;
import com.cool.music.bean.MusicBean;
import com.cool.music.dao.MusicDao;

import java.util.List;

public class AddSearchMusicActivity extends AppCompatActivity {
    RecyclerView listDe=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 启用全屏显示，支持刘海屏
        enableNotchSupport();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_music);

        // 适配刘海屏和水滴屏
        applyNotchAdaptation();

        //默认加载所有音乐
        List<MusicBean> list = MusicDao.getMusicAll();
        listDe =this.findViewById(R.id.user_home_music_list);

        SheetMusicAdapter de=new SheetMusicAdapter(list);//适配器
        listDe.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        if(list==null||list.size()==0){
            listDe.setAdapter(null);
        }else{
            listDe.setAdapter(de);
            de.notifyDataSetChanged();//通知一下列表改变了
        }
        Toolbar bar=this.findViewById(R.id.user_home_music_bar);
        setSupportActionBar(bar);//实现了返回功能
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        bar.setNavigationOnClickListener(v->{
            finish();
        });



        SearchView searchView=findViewById(R.id.user_home_music_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<MusicBean> list = MusicDao.getMusicAll(query);
                SheetMusicAdapter de=new SheetMusicAdapter(list);//适配器
                if(list==null||list.size()==0){
                    listDe.setAdapter(null);
                }else{
                    listDe.setAdapter(de);
                    de.notifyDataSetChanged();//通知一下列表改变了
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<MusicBean> list = MusicDao.getMusicAll(newText);
                SheetMusicAdapter de=new SheetMusicAdapter(list);//适配器
                if(list==null||list.size()==0){
                    listDe.setAdapter(null);
                }else{
                    listDe.setAdapter(de);
                    de.notifyDataSetChanged();//通知一下列表改变了
                }
                return true;
            }
        });
    }

    /**
     * 启用刘海屏支持
     */
    private void enableNotchSupport() {
        // Android P (API 28) 及以上支持刘海屏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }

        // 设置状态栏透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        // 设置状态栏图标颜色为深色（适配浅色背景）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            );
        }
    }

    /**
     * 适配刘海屏和水滴屏
     */
    private void applyNotchAdaptation() {
        View rootView = findViewById(android.R.id.content);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            // 获取系统栏和刘海区域的 insets
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets displayCutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());

            // 获取状态栏高度（包括刘海区域）
            int topInset = Math.max(systemBars.top, displayCutout.top);
            int leftInset = Math.max(systemBars.left, displayCutout.left);
            int rightInset = Math.max(systemBars.right, displayCutout.right);
            int bottomInset = systemBars.bottom;

            // 给 Toolbar 设置顶部 padding
            Toolbar toolbar = findViewById(R.id.user_home_music_bar);
            if (toolbar != null) {
                toolbar.setPadding(
                        toolbar.getPaddingLeft() + leftInset,
                        toolbar.getPaddingTop() + topInset,
                        toolbar.getPaddingRight() + rightInset,
                        toolbar.getPaddingBottom()
                );
            }

            // 给列表设置左右 padding（避免刘海遮挡）
            RecyclerView recyclerView = findViewById(R.id.user_home_music_list);
            if (recyclerView != null) {
                recyclerView.setPadding(
                        recyclerView.getPaddingLeft() + leftInset,
                        recyclerView.getPaddingTop(),
                        recyclerView.getPaddingRight() + rightInset,
                        recyclerView.getPaddingBottom() + bottomInset
                );
                recyclerView.setClipToPadding(false);
            }

            return insets;
        });
    }
}