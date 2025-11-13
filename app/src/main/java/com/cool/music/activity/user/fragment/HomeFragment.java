package com.cool.music.activity.user.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.cool.music.R;
import com.cool.music.activity.user.SearchMusicActivity;
import com.cool.music.adapter.user.PlayMusicAdapter;
import com.cool.music.adapter.user.SheetMusicAdapter;
import com.cool.music.bean.MessageEvent;
import com.cool.music.bean.MusicBean;
import com.cool.music.bean.PlayMusicBean;
import com.cool.music.dao.MusicDao;
import com.cool.music.dao.PlayMusicDao;
import com.cool.music.until.Tools;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 这个类用于操作首页
 */
public class HomeFragment extends Fragment {

    private View rootview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.frame_user_home, container, false);


        // 推荐歌单（目前隐藏）
        List<PlayMusicBean> list = PlayMusicDao.getPlayMusic();
        PlayMusicAdapter de = new PlayMusicAdapter(list);

        RecyclerView listDe = rootview.findViewById(R.id.man_home_playlist_srecycler);
        listDe.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        if (list == null || list.size() == 0) {
            listDe.setAdapter(null);
        } else {
            listDe.setAdapter(de);
            de.notifyDataSetChanged();
        }

        // 开始加载音乐热榜（按收藏量排序，前5首）
        List<MusicBean> list2 = MusicDao.getMusicHotRanking();

        SheetMusicAdapter dee = new SheetMusicAdapter(list2);

        RecyclerView listDee = rootview.findViewById(R.id.man_home_rank_srecycler);
        listDee.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        if (list2 == null || list2.size() == 0) {
            listDee.setAdapter(null);
        } else {
            listDee.setAdapter(dee);
            dee.notifyDataSetChanged();
        }

        // 搜索按钮
        Button searchView = rootview.findViewById(R.id.man_home_music_search);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchMusicActivity.class);
                rootview.getContext().startActivity(intent);
            }
        });

        // ✨✨✨ 播放全部按钮点击事件
        TextView playAllButton = rootview.findViewById(R.id.play_all_button);
        if (playAllButton != null) {
            playAllButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playAllMusic();
                }
            });
        }

        return rootview;
    }

    /**
     * ✨✨✨ 播放全部音乐（从第一首开始）
     */
    private void playAllMusic() {
        // 获取所有音乐列表
        List<MusicBean> musicList = MusicDao.getMusicHotRanking();

        if (musicList == null || musicList.size() == 0) {
            Tools.Toast(getContext(), "暂无可播放的歌曲");
            return;
        }

        // 获取第一首歌曲
        MusicBean firstMusic = musicList.get(0);

        if (firstMusic == null) {
            Tools.Toast(getContext(), "歌曲加载失败");
            return;
        }

        // 清空并重新添加播放列表
        String account = Tools.getOnAccount(getContext());
        PlayMusicDao.clearUserPlaylist(account); // 需要添加这个方法

        // 将所有歌曲添加到播放列表
        for (MusicBean music : musicList) {
            PlayMusicDao.addToPlaylist(account, music.getId());
        }

        // 通过 EventBus 发送播放消息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("send", "HomeFragment");
        jsonObject.put("receive", "UserManageActivity");
        jsonObject.put("data", firstMusic);
        jsonObject.put("sta", "1");
        EventBus.getDefault().post(new MessageEvent(jsonObject.toJSONString()));

        Tools.Toast(getContext(), "开始播放：" + firstMusic.getName());
    }


}