package com.cool.music.activity.user;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.cool.music.R;
import com.cool.music.adapter.user.ImagePreviewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ImagePreviewActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private ImageView closeButton;
    private ImagePreviewAdapter adapter;
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✨ 设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_image_preview);

        // 隐藏系统UI
        hideSystemUI();

        // 获取传递的数据
        currentPosition = getIntent().getIntExtra("position", 0);
        ArrayList<Integer> photoList = getIntent().getIntegerArrayListExtra("photo_list");

        // 初始化控件
        viewPager = findViewById(R.id.image_preview_viewpager);
        closeButton = findViewById(R.id.image_preview_close);

        // 设置适配器
        adapter = new ImagePreviewAdapter(this, photoList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPosition, false);

        // 关闭按钮
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // ViewPager 点击关闭（可选）
        viewPager.setOnClickListener(v -> finish());
    }

    /**
     * 隐藏系统UI（状态栏和导航栏）
     */
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }
}