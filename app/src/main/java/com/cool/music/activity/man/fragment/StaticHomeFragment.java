package com.cool.music.activity.man.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.cool.music.dao.MusicDao;
import com.cool.music.dao.SheetDao;
import com.cool.music.dao.UserDao;
import com.cool.music.until.SqliteT;


import java.util.List;

/**
 * 这个类用于操作首页
 */
public class StaticHomeFragment extends Fragment {
    TextView musicCount ;
    TextView userCount ;
    TextView sheetCount ;
    View rootview;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootview = inflater.inflate(R.layout.frame_man_static, container, false);


        musicCount = rootview.findViewById(R.id.man_static_music_count);
        userCount = rootview.findViewById(R.id.man_static_user_count);
        sheetCount = rootview.findViewById(R.id.man_static_sheet_count);
        musicCount.setText(MusicDao.getMusicAll().size() + " ");
        userCount.setText(UserDao.getUserAll().size() + " ");
        sheetCount.setText(SheetDao.getSheet() + " ");


        return rootview;
    }



    @MainThread
    @CallSuper
    public void onResume() {
        super.onResume();
        musicCount.setText(MusicDao.getMusicAll().size() + " ");
        userCount.setText(UserDao.getUserAll().size() + " ");
        sheetCount.setText(SheetDao.getSheet().size() + " ");
    }



}
