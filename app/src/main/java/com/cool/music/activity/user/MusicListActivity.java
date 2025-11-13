package com.cool.music.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.cool.music.R;
import com.cool.music.adapter.user.PlayMusicAdapter;
import com.cool.music.adapter.user.SheetMusicAdapter;
import com.cool.music.bean.MusicBean;
import com.cool.music.bean.PlayMusicBean;
import com.cool.music.dao.PlayMusicDao;

import java.util.List;

public class MusicListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_music_list);


        Toolbar toolbar=findViewById(R.id.man_home_music_list_bar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v->{
            finish();
        });

        // 启用返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //
        //Intent intent=new Intent(holder.view.getContext(), MusicListActivity.class);
        Intent intent=getIntent();
        Bundle extras = intent.getExtras();
        String sheetID = extras.getString("sheetID"); // 获取字符串类型的值
        String imgPath = extras.getString("imgPath"); // 图片路劲
        //根据这个sheetID查询歌单内容


        ImageView img =findViewById(R.id.man_home_music_list_bg);//这个是加载歌单的背景图片
        Glide.with(this)
                .load(imgPath) // 图片资源ID
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                .into(img);

        List<MusicBean>list = PlayMusicDao.getPlayMusicOnSheetCount(sheetID);//这个是表单的所有的音乐



        RecyclerView listDe =findViewById(R.id.man_home_music_list_srecycler);

        SheetMusicAdapter de=new SheetMusicAdapter(list);//适配器
        listDe.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        if(list==null||list.size()==0){
            listDe.setAdapter(null);
        }else{
            listDe.setAdapter(de);
            de.notifyDataSetChanged();//通知一下列表改变了
        }



    }
}