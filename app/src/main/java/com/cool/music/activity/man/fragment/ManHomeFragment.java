package com.cool.music.activity.man.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cool.music.R;
import com.cool.music.activity.man.AddMusicActivity;
import com.cool.music.activity.man.GenreManageActivity;
import com.cool.music.activity.user.AddSearchMusicActivity;
import com.cool.music.activity.user.MySheetDetailActivity;
import com.cool.music.activity.user.SearchMusicActivity;
import com.cool.music.adapter.user.ManMusicAdapter;
import com.cool.music.adapter.user.PlayMusicAdapter;
import com.cool.music.adapter.user.SheetMusicAdapter;
import com.cool.music.bean.MusicBean;
import com.cool.music.bean.PlayMusicBean;
import com.cool.music.dao.MusicDao;
import com.cool.music.dao.PlayMusicDao;

import java.util.List;

/**
 * 这个类用于操作首页
 */
public class ManHomeFragment extends Fragment {

    View rootview;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootview = inflater.inflate(R.layout.frame_man_home, container, false);


        Toolbar toolbar=rootview.findViewById(R.id.man_home_bar);
        toolbar.setTitle("音乐列表");
        //toolbar.inflateMenu(R.menu.menu_man_bar);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        //加载列表是加载所有音乐的列表
        List<MusicBean> list = MusicDao.getMusicAll();
        RecyclerView listDe=rootview.findViewById(R.id.man_home_list);
        ManMusicAdapter de=new ManMusicAdapter(list);

        listDe.setLayoutManager(new LinearLayoutManager(rootview.getContext(),LinearLayoutManager.VERTICAL, false));
        if(list==null||list.size()==0){
            listDe.setAdapter(null);
        }else{
            listDe.setAdapter(de);
            de.notifyDataSetChanged();//通知一下列表改变了
        }
        //接下来实现查询音乐
        SearchView search = rootview.findViewById(R.id.man_home_search);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<MusicBean> list = MusicDao.getMusicAll(query);
                RecyclerView listDe=rootview.findViewById(R.id.man_home_list);
                ManMusicAdapter de=new ManMusicAdapter(list);

                listDe.setLayoutManager(new LinearLayoutManager(rootview.getContext(),LinearLayoutManager.VERTICAL, false));
                if(list==null||list.size()==0){
                    listDe.setAdapter(null);
                }else{
                    listDe.setAdapter(de);
                    de.notifyDataSetChanged();//通知一下列表改变了
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<MusicBean> list = MusicDao.getMusicAll(newText);
                RecyclerView listDe=rootview.findViewById(R.id.man_home_list);
                ManMusicAdapter de=new ManMusicAdapter(list);

                listDe.setLayoutManager(new LinearLayoutManager(rootview.getContext(),LinearLayoutManager.VERTICAL, false));
                if(list==null||list.size()==0){
                    listDe.setAdapter(null);
                }else{
                    listDe.setAdapter(de);
                    de.notifyDataSetChanged();//通知一下列表改变了
                }

                return false;
            }
        });

        return rootview;
    }






    @MainThread
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id==R.id.man_bar_menu_add){
            //跳转到曲风管理界面
            Intent intent=new Intent(rootview.getContext(), AddMusicActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @MainThread
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_man_bar, menu);
        //MenuItem searchItem = menu.findItem(R.id.man_bar_menu_search);
        super.onCreateOptionsMenu(menu, inflater);

    }
    @MainThread
    @CallSuper
    public void onResume() {
        super.onResume();
        Toolbar toolbar=rootview.findViewById(R.id.man_home_bar);
        toolbar.setTitle("音乐列表");
        //toolbar.inflateMenu(R.menu.menu_man_bar);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        //加载列表是加载所有音乐的列表
        List<MusicBean> list = MusicDao.getMusicAll();
        RecyclerView listDe=rootview.findViewById(R.id.man_home_list);
        ManMusicAdapter de=new ManMusicAdapter(list);

        listDe.setLayoutManager(new LinearLayoutManager(rootview.getContext(),LinearLayoutManager.VERTICAL, false));
        if(list==null||list.size()==0){
            listDe.setAdapter(null);
        }else{
            listDe.setAdapter(de);
            de.notifyDataSetChanged();//通知一下列表改变了
        }


    }



}
