package com.cool.music;

import android.app.Application;
import com.cool.music.until.FileUntil;
import com.cool.music.until.MusicPlayerManager;

public class MusicApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // ✨✨✨ 初始化 FileUntil（关键！）
        FileUntil.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // 应用退出时释放播放器
        MusicPlayerManager.getInstance().release();
    }
}