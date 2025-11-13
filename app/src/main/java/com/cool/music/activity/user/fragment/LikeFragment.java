package com.cool.music.activity.user.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cool.music.R;
import com.cool.music.adapter.user.SheetMusicAdapter;
import com.cool.music.bean.MusicBean;
import com.cool.music.dao.MusicDao;
import com.cool.music.until.Tools;

import java.util.List;

/**
 * 这个类用于操作首页
 */
public class LikeFragment extends Fragment {
    View rootview;
    RecyclerView listDe=null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.frame_user_like, container, false);

        String account= Tools.getOnAccount(getContext());
        //默认加载所有音乐

        List<MusicBean> list = MusicDao.getLikeMusicAll(account,null);
        listDe =rootview.findViewById(R.id.user_home_music_list);

        SheetMusicAdapter de=new SheetMusicAdapter(list);//适配器
        listDe.setLayoutManager(new LinearLayoutManager(rootview.getContext(),LinearLayoutManager.VERTICAL, false));
        if(list==null||list.size()==0){
            listDe.setAdapter(null);
        }else{
            listDe.setAdapter(de);
            de.notifyDataSetChanged();//通知一下列表改变了
        }

        SearchView searchView=rootview.findViewById(R.id.user_home_music_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<MusicBean> list = MusicDao.getLikeMusicAll(account,query);
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
                List<MusicBean> list = MusicDao.getLikeMusicAll(account,newText);
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

        return rootview;
    }


    /**
     * 当页面被看见则重新加载
     */
    @Override
    public void onResume() {
        super.onResume();
        String account= Tools.getOnAccount(getContext());
        //默认加载所有音乐

        List<MusicBean> list = MusicDao.getLikeMusicAll(account,null);
        listDe =rootview.findViewById(R.id.user_home_music_list);

        SheetMusicAdapter de=new SheetMusicAdapter(list);//适配器
        listDe.setLayoutManager(new LinearLayoutManager(rootview.getContext(),LinearLayoutManager.VERTICAL, false));
        if(list==null||list.size()==0){
            listDe.setAdapter(null);
        }else{
            listDe.setAdapter(de);
            de.notifyDataSetChanged();//通知一下列表改变了
        }


    }
}
