package com.cool.music.activity.man;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cool.music.R;
import com.cool.music.activity.man.fragment.ManHomeFragment;
import com.cool.music.activity.man.fragment.ManMyFragment;
import com.cool.music.activity.man.fragment.StaticHomeFragment;
import com.cool.music.activity.user.fragment.HomeFragment;
import com.cool.music.activity.user.fragment.LikeFragment;
import com.cool.music.activity.user.fragment.MyFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ManManageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_man_manage);

        //加载初始化界面
        FrameLayout frameLayout=this.findViewById(R.id.man_home_frame);

        FragmentManager fragment_container=getSupportFragmentManager();
        FragmentTransaction transaction= fragment_container.beginTransaction();
        transaction.replace(R.id.man_home_frame,new ManHomeFragment());
        transaction.commit();
        BottomNavigationView bottomNavigationView=findViewById(R.id.man_bottom_menu);//底部菜单
        bottomNavigationView.setOnItemSelectedListener(item -> {

            FragmentManager f=getSupportFragmentManager();
            FragmentTransaction transaction1= f.beginTransaction();
            int id=item.getItemId();//获取点击的item的ID
            if(id==R.id.man_bottom_menu_home){//点击home
                transaction1.replace(R.id.man_home_frame,new ManHomeFragment());
                transaction1.commit();
            }else if(id==R.id.man_bottom_menu_static){//点击home
                transaction1.replace(R.id.man_home_frame,new StaticHomeFragment());
                transaction1.commit();
            }else if(id==R.id.man_bottom_menu_my){//点击home
                transaction1.replace(R.id.man_home_frame,new ManMyFragment());
                transaction1.commit();
            }

            return true;

        });

    }
}