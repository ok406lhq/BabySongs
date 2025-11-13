package com.cool.music.until;

/**
 * 包名：com.cool.music.until
 * 创建人: Latte
 * 创建时间： 2025/11/13 10:28
 * 描述：
 */

import android.content.Context;
import com.google.android.exoplayer2.ExoPlayer;

public class MusicPlayerManager {
    private static MusicPlayerManager instance;
    private ExoPlayer player;
    private String currentMusicId;

    private MusicPlayerManager() {}

    public static synchronized MusicPlayerManager getInstance() {
        if (instance == null) {
            instance = new MusicPlayerManager();
        }
        return instance;
    }

    public ExoPlayer getPlayer(Context context) {
        if (player == null) {
            player = new ExoPlayer.Builder(context.getApplicationContext()).build();
        }
        return player;
    }

    public void setCurrentMusicId(String musicId) {
        this.currentMusicId = musicId;
    }

    public String getCurrentMusicId() {
        return currentMusicId;
    }

    public void release() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
