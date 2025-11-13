package com.cool.music.activity.user.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.cool.music.MainActivity;
import com.cool.music.R;
import com.cool.music.activity.user.ChangeUserMessageActivity;
import com.cool.music.adapter.user.MySheetMusicAdapter;
import com.cool.music.adapter.user.SheetMusicAdapter;
import com.cool.music.bean.MusicBean;
import com.cool.music.bean.SheetBean;
import com.cool.music.bean.UserBean;
import com.cool.music.dao.MusicDao;
import com.cool.music.dao.SheetDao;
import com.cool.music.dao.UserDao;
import com.cool.music.until.Tools;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 这个类用于操作首页
 */
public class MyFragment extends Fragment {
    View rootview;
    String account=null;
    RecyclerView listDe=null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.frame_user_my, container, false);

        loadMyMessage();

        //打开更改我的个人信息界面
        Button button=rootview.findViewById(R.id.user_my_change_my_message);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开一个个人信息的界面
                Intent intent=new Intent(rootview.getContext(), ChangeUserMessageActivity.class);
                startActivity(intent);
            }
        });


        Button exit=rootview.findViewById(R.id.user_my_exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开一个个人信息的界面
                Intent intent=new Intent(rootview.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        //加载列表





        return rootview;
    }

    private void loadMyMessage() {
        account = Tools.getOnAccount(getContext());
        //默认加载所有音乐
        UserBean user = UserDao.getUserById(account);

        ImageView img = rootview.findViewById(R.id.user_my_tx);
        Glide.with(this)
                .load(user.getImg()) // 图片资源ID
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(1000)))
                .into(img);

        TextView nickName = rootview.findViewById(R.id.user_my_nickName);
        nickName.setText(user.getNickname());

        TextView address = rootview.findViewById(R.id.user_my_address);
        address.setText("居住地：" + user.getAddress());
        ImageView sex = rootview.findViewById(R.id.user_my_sex);
        if (user.getSex().equals("男")) {
            Glide.with(this)
                    .load(R.drawable.sex_man) // 图片资源ID
                    .into(sex);
        } else {
            Glide.with(this)
                    .load(R.drawable.sex_woman) // 图片资源ID
                    .into(sex);
        }

        String regTime = user.getRegistration_time();
        regTime = calculateYearDifference(regTime);
        TextView ling = rootview.findViewById(R.id.user_my_ling);
        ling.setText("村龄：" + regTime + " 年");


        TextView musicCount = rootview.findViewById(R.id.user_my_music_count);
        musicCount.setText(user.getSong_count() + " ");

        TextView time = rootview.findViewById(R.id.user_my_music_time);
        int timeI = Integer.valueOf(user.getListening_time()) / 1000 / 60;
        time.setText(String.valueOf(timeI) + " ");


        //查询列表信息
        List<SheetBean> list = SheetDao.getSheetById(account);
        SheetBean sheetBean=new SheetBean();
        sheetBean.setSta("-1");
        sheetBean.setName("添加歌单");
        list.add(sheetBean);

        listDe = rootview.findViewById(R.id.user_my_recyclerView);
        MySheetMusicAdapter de=new MySheetMusicAdapter(list);//适配器
        listDe.setLayoutManager(new LinearLayoutManager(rootview.getContext(),LinearLayoutManager.VERTICAL, false));
        if(list==null||list.size()==0){
            listDe.setAdapter(null);
        }else{
            listDe.setAdapter(de);
            de.notifyDataSetChanged();//通知一下列表改变了
        }
/*

      */
        /*
        //默认加载所有音乐





  */
    }


    /**
     * 当页面被看见则重新加载
     */
    @Override
    public void onResume() {
        super.onResume();
        //每次的可视化都要重新加载数据
        loadMyMessage();


    }


    /**
     * 计算给定日期字符串与当前日期之间的差值（按年），不满一年计算为0年。
     *
     * @param regDateStr 格式为 "yyyy-MM-dd HH:mm:ss" 的注册日期字符串。
     * @return 相差的年数，不满一年返回0。
     */
    public static String calculateYearDifference(String regDateStr) {
        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 将字符串解析为 LocalDateTime 对象
        LocalDateTime regDateTime = LocalDateTime.parse(regDateStr, formatter);

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 比较两个日期的时间差，只考虑年份部分
        long yearsBetween = ChronoUnit.YEARS.between(regDateTime.toLocalDate(), now.toLocalDate());

        // 如果不满一年，则返回0年
        if (yearsBetween < 1) {
            return "0";
        } else {
            return String.valueOf(yearsBetween);
        }
    }


}
