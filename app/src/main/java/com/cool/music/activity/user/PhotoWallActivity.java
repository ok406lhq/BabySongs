package com.cool.music.activity.user;

/**
 * 包名：com.cool.music.activity.user
 * 创建人: Latte
 * 创建时间： 2025/11/14 16:51
 * 描述：
 */

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.cool.music.R;
import com.cool.music.adapter.user.PhotoWallAdapter;

import java.util.ArrayList;
import java.util.List;

public class PhotoWallActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PhotoWallAdapter adapter;
    private List<Integer> photoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_wall);

        // 设置 Toolbar
        Toolbar toolbar = findViewById(R.id.photo_wall_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("照片墙 📸");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 初始化照片列表
        initPhotoList();

        // 设置 RecyclerView
        recyclerView = findViewById(R.id.photo_wall_recycler_view);

        // ✨ 使用瀑布流布局（2列）
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        // 设置适配器
        adapter = new PhotoWallAdapter(this, photoList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 初始化照片列表
     * ✨ 在这里添加你的图片资源
     */
    private void initPhotoList() {
        photoList = new ArrayList<>();

        // ✨✨✨ 添加你的照片资源 ID
        // 替换为你实际的图片资源
        photoList.add(R.drawable.photo_1);
        photoList.add(R.drawable.photo_2);
        photoList.add(R.drawable.photo_3);
        photoList.add(R.drawable.photo_4);
        photoList.add(R.drawable.photo_5);
        photoList.add(R.drawable.photo_6);
        photoList.add(R.drawable.photo_7);
        photoList.add(R.drawable.photo_8);
        photoList.add(R.drawable.photo_9);
        photoList.add(R.drawable.photo_10);
        photoList.add(R.drawable.photo_11);
        photoList.add(R.drawable.photo_12);
        photoList.add(R.drawable.photo_13);
        photoList.add(R.drawable.photo_14);
        photoList.add(R.drawable.photo_15);
        photoList.add(R.drawable.photo_16);
        photoList.add(R.drawable.photo_17);
        photoList.add(R.drawable.photo_18);
        photoList.add(R.drawable.photo_19);
        photoList.add(R.drawable.photo_20);

        // 可以添加更多照片...
    }
}