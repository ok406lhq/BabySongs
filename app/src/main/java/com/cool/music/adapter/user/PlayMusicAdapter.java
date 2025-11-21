package com.cool.music.adapter.user;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.cool.music.R;
import com.cool.music.activity.user.MusicListActivity;
import com.cool.music.activity.user.UserManageActivity;
import com.cool.music.bean.MessageEvent;
import com.cool.music.bean.MusicBean;
import com.cool.music.bean.PlayMusicBean;
import com.cool.music.dao.PlayMusicDao;
import com.cool.music.until.Tools;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;


import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import java.io.File;
import java.util.List;
import java.util.Random;

/**
 * 这个是音乐的适配器
 */
public class PlayMusicAdapter  extends  RecyclerView.Adapter<PlayMusicAdapter.viewHolder>{

    List<PlayMusicBean> list;
    public PlayMusicAdapter(List<PlayMusicBean>  list){
        this.list=list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());

        View convertView = inflater.inflate(R.layout.list_user_play_music, parent, false);


        viewHolder viewHolder = new viewHolder(convertView);




        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        PlayMusicBean tem = list.get(position);

        holder.name.setText(tem .getName());


        //通歌单ID来获取多少首歌曲
        String count=PlayMusicDao.getPlayMusicCount(tem.getId());
        count=count+" 首";
        holder.count.setText(count);

        if(!(tem.getImg()==null||tem.getImg().equals("1")||tem.getImg().equals(""))){
            Glide.with(holder.view)
                    .load(tem.getImg()) // 图片资源ID
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                    .into(holder.img);
        }else{
            Glide.with(holder.view)
                    .load(R.drawable.icon_gd) // 图片资源ID
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                    .into(holder.img);
        }

        //有一个歌单的ID，其次，通过用户ID和歌单ID获取歌曲，然将数据添加的刚刚那个表当中
        String account=Tools.getOnAccount(holder.view.getContext());//得到一个账号ID
        String sheetId=tem.getId();//歌单ID

        List<MusicBean> list2 = PlayMusicDao.getPlayMusicOnSheetCount(sheetId);//是音乐的List
        //将所有音乐的ID和账号ID添加到数据库当中

        //从适配器进行跳转
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (MusicBean musicBean : list2) {
                    String musicId=musicBean.getId();
                    PlayMusicDao.insertPlayMusic(account,musicId);
                }

                //这个界面是重复用，需要根据不同传参来查询不同数据，但是也可以，将list传入过去
                Intent intent=new Intent(holder.view.getContext(), MusicListActivity.class);
                intent.putExtra("sheetID",sheetId);//歌单ID
                intent.putExtra("imgPath",tem.getImg());//歌单ID

                holder.view.getContext().startActivity(intent);
            }
        });


        //点击之后通过，获取当前的播放列表进行随机播放
        holder.run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (MusicBean musicBean : list2) {
                    String musicId=musicBean.getId();
                    PlayMusicDao.insertPlayMusic(account,musicId);
                }


                List<MusicBean> list3 = PlayMusicDao.getCurrentUserPlayMusic(account);//获取所有播放的音乐列表
                if(list3==null||list3.size()==0){
                    Tools.Toast(holder.view.getContext(),"没有音乐播放");
                    return;
                }
                PlayMusicDao.updateCurrentUserPlayMusicSta(account,"0");//将所有状态设置为0
                Random random = new Random();
                int index = random.nextInt(list3.size()); // 生成一个介于 0 和 list.size()-1 之间的随机数
                MusicBean musicBean = list3.get(index);//播放的音乐
                PlayMusicDao.updateCurrentUserPlayMusicSta(account,musicBean.getId(),"1");//将所有状态设置为0
                //从列表当中随机选出一个进行播放
                //将所有音乐状态改为0 让其没有播放
                //他需要时刻获取播放进度，名称，头像，歌曲ID，
                JSONObject jsonObject=new JSONObject();//发送界面，接受界面，数据
                jsonObject.put("send","PlayMusicAdapter");
                jsonObject.put("receive", "UserManageActivity");
                jsonObject.put("data",musicBean);
                jsonObject.put("sta","1");
                EventBus.getDefault().post(new MessageEvent(jsonObject.toJSONString()));











            }
        });





    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView count;
        ImageView run;

        ImageView img;


        View view;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            name=itemView.findViewById(R.id.user_home_play_music_name);
            count=itemView.findViewById(R.id.user_home_play_music_count);
            run=itemView.findViewById(R.id.user_home_play_music_run);
            img=itemView.findViewById(R.id.user_home_play_music_img);

        }
    }

}
