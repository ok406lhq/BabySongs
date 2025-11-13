package com.cool.music.activity.user;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cool.music.R;
import com.cool.music.adapter.user.SheetMusicAdapter;
import com.cool.music.bean.MusicBean;
import com.cool.music.dao.MusicDao;

import java.util.List;

public class SearchMusicActivity extends AppCompatActivity {
    RecyclerView listDe=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_music);







        //默认加载所有音乐
        List<MusicBean> list = MusicDao.getMusicAll();
        listDe =this.findViewById(R.id.user_home_music_list);

        SheetMusicAdapter de=new SheetMusicAdapter(list);//适配器
        listDe.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        if(list==null||list.size()==0){
            listDe.setAdapter(null);
        }else{
            listDe.setAdapter(de);
            de.notifyDataSetChanged();//通知一下列表改变了
        }
        Toolbar bar=this.findViewById(R.id.user_home_music_bar);
        setSupportActionBar(bar);//实现了返回功能
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        bar.setNavigationOnClickListener(v->{
            finish();
        });



        SearchView searchView=findViewById(R.id.user_home_music_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<MusicBean> list = MusicDao.getMusicAll(query);
                SheetMusicAdapter de=new SheetMusicAdapter(list);//适配器
                if(list==null||list.size()==0){
                    listDe.setAdapter(null);
                }else{
                    listDe.setAdapter(de);
                    de.notifyDataSetChanged();//通知一下列表改变了
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<MusicBean> list = MusicDao.getMusicAll(newText);
                SheetMusicAdapter de=new SheetMusicAdapter(list);//适配器
                if(list==null||list.size()==0){
                    listDe.setAdapter(null);
                }else{
                    listDe.setAdapter(de);
                    de.notifyDataSetChanged();//通知一下列表改变了
                }
                return true;
            }
        });
    }
}