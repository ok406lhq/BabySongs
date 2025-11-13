package com.cool.music.activity.man;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cool.music.R;
import com.cool.music.adapter.man.GenreAdapter;
import com.cool.music.adapter.user.ManMusicAdapter;
import com.cool.music.bean.GenreBean;
import com.cool.music.bean.MusicBean;
import com.cool.music.dao.GenreDao;
import com.cool.music.dao.MusicDao;

import java.util.List;

/**
 * 曲风分类管理界面
 */
public class GenreManageActivity extends AppCompatActivity {

    private RecyclerView genreList;
    private RecyclerView musicList;
    private TextView musicTitle;
    private Button btnAddMusic;
    private GenreAdapter genreAdapter;
    private ManMusicAdapter musicAdapter;
    private List<GenreBean> genres;
    private String currentGenreId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_manage);

        // 初始化 Toolbar
        Toolbar toolbar = findViewById(R.id.genre_manage_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // 初始化视图
        genreList = findViewById(R.id.genre_list);
        musicList = findViewById(R.id.genre_music_list);
        musicTitle = findViewById(R.id.genre_music_title);
        btnAddMusic = findViewById(R.id.btn_add_music);

        // 加载曲风列表
        loadGenres();

        // 设置新增音乐按钮点击事件
        btnAddMusic.setOnClickListener(v -> {
            Intent intent = new Intent(GenreManageActivity.this, AddMusicActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 刷新当前选中的曲风的音乐列表
        if (!currentGenreId.isEmpty()) {
            loadMusicByGenre(currentGenreId);
        }
    }

    private void loadGenres() {
        genres = GenreDao.getAllGenres();
        genreList.setLayoutManager(new LinearLayoutManager(this));

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
        musicList.setLayoutManager(new LinearLayoutManager(this));

        if (musicBeans == null || musicBeans.size() == 0) {
            musicList.setAdapter(null);
        } else {
            musicAdapter = new ManMusicAdapter(musicBeans);
            musicList.setAdapter(musicAdapter);
            musicAdapter.notifyDataSetChanged();
        }
    }
}
