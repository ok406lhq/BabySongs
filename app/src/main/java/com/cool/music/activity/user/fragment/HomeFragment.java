package com.cool.music.activity.user.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
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
    private NestedScrollView nestedScrollView;
    private LinearLayout bottomHintLayout;
    private boolean hasShownBottomHint = false;
    private View rootview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.frame_user_home, container, false);

        // 初始化控件
        nestedScrollView = rootview.findViewById(R.id.nested_scroll_view);
        bottomHintLayout = rootview.findViewById(R.id.bottom_hint_layout);

        // ✨✨✨ 设置滚动监听
        setupScrollListener();

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
        List<MusicBean> list2 = MusicDao.getMusicAll();

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
     * ✨✨✨ 设置滚动监听，实现底部提示
     */
    private void setupScrollListener() {
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // 检查是否滚动到底部
                if (!v.canScrollVertically(1)) {
                    // 已经到底部
                    if (!hasShownBottomHint) {
                        showBottomHint();
                        hasShownBottomHint = true;
                    }
                } else {
                    // 还没到底部，重置标志
                    if (hasShownBottomHint && scrollY < oldScrollY) {
                        // 向上滚动时隐藏提示
                        hideBottomHint();
                        hasShownBottomHint = false;
                    }
                }
            }
        });
    }

    /**
     * ✨✨✨ 显示底部提示（带动画）
     */
    private void showBottomHint() {
        if (bottomHintLayout != null) {
            bottomHintLayout.setVisibility(View.VISIBLE);
            bottomHintLayout.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start();
        }
    }

    /**
     * ✨✨✨ 隐藏底部提示（带动画）
     */
    private void hideBottomHint() {
        if (bottomHintLayout != null) {
            bottomHintLayout.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            bottomHintLayout.setVisibility(View.GONE);
                        }
                    })
                    .start();
        }
    }

    /**
     * ✨✨✨ 播放全部音乐（从第一首开始）
     */
    private void playAllMusic() {
        // 获取所有音乐列表
        List<MusicBean> musicList = MusicDao.getMusicAll();

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