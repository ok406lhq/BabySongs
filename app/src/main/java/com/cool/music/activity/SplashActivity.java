package com.cool.music.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.cool.music.MainActivity;
import com.cool.music.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 1500; // 1.5秒
    private ImageView splashImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✨✨✨ 在 setContentView 之前设置全屏（使用传统方法）
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_splash);

        // ✨✨✨ 在 setContentView 之后隐藏导航栏和状态栏
        hideSystemUI();

        // 获取闪屏图片
        splashImage = findViewById(R.id.splash_image);

        // 启动淡入动画
        startFadeInAnimation();

        // 延迟2.5秒后跳转到主界面
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // 淡出动画并跳转
                fadeOutAndFinish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    /**
     * ✨✨✨ 隐藏系统UI（状态栏和导航栏）
     */
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 (API 30) 及以上
            getWindow().setDecorFitsSystemWindows(false);
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        } else {
            // Android 11 以下
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
    }

    /**
     * 当窗口焦点改变时，重新隐藏系统栏
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    /**
     * 淡入动画
     */
    private void startFadeInAnimation() {
        // 初始透明度为0
        splashImage.setAlpha(0f);

        // 创建淡入动画
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(splashImage, "alpha", 0f, 1f);
        fadeIn.setDuration(500); // 500毫秒淡入
        fadeIn.start();
    }

    /**
     * 淡出并跳转
     */
    private void fadeOutAndFinish() {
        // 创建淡出动画
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(splashImage, "alpha", 1f, 0f);
        fadeOut.setDuration(500); // 500毫秒淡出
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 跳转到主界面
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
                // 无过渡动画
                overridePendingTransition(0, 0);
            }
        });
        fadeOut.start();
    }
}