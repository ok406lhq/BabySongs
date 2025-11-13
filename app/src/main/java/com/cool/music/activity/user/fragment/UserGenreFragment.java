package com.cool.music.activity.user.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cool.music.R;
import com.cool.music.adapter.man.GenreAdapter;
import com.cool.music.adapter.user.UserGenreMusicAdapter;
import com.cool.music.bean.GenreBean;
import com.cool.music.bean.MusicBean;
import com.cool.music.dao.GenreDao;
import com.cool.music.dao.MusicDao;

import java.util.List;

/**
 * 用户曲库Fragment
 */
public class UserGenreFragment extends Fragment {

    private RecyclerView genreList;
    private RecyclerView musicList;
    private TextView musicTitle;
    private GenreAdapter genreAdapter;
    private UserGenreMusicAdapter musicAdapter;
    private List<GenreBean> genres;
    private String currentGenreId = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_user_genre, container, false);

        // 初始化视图
        genreList = rootview.findViewById(R.id.user_genre_list);
        musicList = rootview.findViewById(R.id.user_genre_music_list);
        musicTitle = rootview.findViewById(R.id.user_genre_music_title);

        // 加载曲风列表
        loadGenres();

        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 刷新当前选中的曲风的音乐列表
        if (!currentGenreId.isEmpty()) {
            loadMusicByGenre(currentGenreId);
        }
    }

    private void loadGenres() {
        genres = GenreDao.getAllGenres();
        genreList.setLayoutManager(new LinearLayoutManager(getContext()));

        genreAdapter = new GenreAdapter(genres, (genre, position) -> {
            // 点击曲风时，加载对应的音乐列表
            currentGenreId = genre.getId();
            musicTitle.setText(genre.getName() + " - 歌曲列表");
            loadMusicByGenre(genre.getId());
        });

        genreList.setAdapter(genreAdapter);

        // 默认选中第一个曲风
        if (genres != null && genres.size() > 0) {
            currentGenreId = genres.get(0).getId();
            musicTitle.setText(genres.get(0).getName() + " - 歌曲列表");
            loadMusicByGenre(currentGenreId);
        }
    }

    private void loadMusicByGenre(String genreId) {
        List<MusicBean> musicBeans = MusicDao.getMusicByGenre(genreId);
        musicList.setLayoutManager(new LinearLayoutManager(getContext()));

        if (musicBeans == null || musicBeans.size() == 0) {
            musicList.setAdapter(null);
        } else {
            musicAdapter = new UserGenreMusicAdapter(musicBeans);
            musicList.setAdapter(musicAdapter);
            musicAdapter.notifyDataSetChanged();
        }
    }
}
