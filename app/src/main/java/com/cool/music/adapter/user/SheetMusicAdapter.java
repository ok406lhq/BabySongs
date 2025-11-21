package com.cool.music.adapter.user;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.cool.music.MainActivity;
import com.cool.music.R;
import com.cool.music.activity.user.MusicListActivity;
import com.cool.music.activity.user.RunMusicDetailActivity;
import com.cool.music.bean.MessageEvent;
import com.cool.music.bean.MusicBean;
import com.cool.music.bean.PlayMusicBean;
import com.cool.music.dao.PlayMusicDao;
import com.cool.music.until.Tools;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Random;

/**
 * 这个是音乐的适配器
 */
public class SheetMusicAdapter extends  RecyclerView.Adapter<SheetMusicAdapter.viewHolder>{

    List<MusicBean> list;
    public SheetMusicAdapter(List<MusicBean>  list){
        this.list=list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());

        View convertView = inflater.inflate(R.layout.list_user_sheet_music, parent, false);


        viewHolder viewHolder = new viewHolder(convertView);




        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        MusicBean tem = list.get(position);

        holder.index.setText(String.valueOf(position+1));

        Glide.with(holder.view)
                .load(tem.getImg()) // 图片资源ID
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(25)))
                .into(holder.img);


        String title=tem.getName()+"-"+tem.getSinger();
        holder.title.setText(title);

        // 点击整个项时跳转到播放详情页
        holder.father.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 将歌曲添加到播放列表
                String account = Tools.getOnAccount(holder.view.getContext());
                PlayMusicDao.insertPlayMusic(account, tem.getId());

                // 跳转到播放详情页
                Intent intent=new Intent(holder.view.getContext(), RunMusicDetailActivity.class);
                intent.putExtra("musicId",tem.getId());
                holder.view.getContext().startActivity(intent);
            }
        });

        // 播放按钮点击事件
        holder.playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 将歌曲添加到播放列表
                String account = Tools.getOnAccount(holder.view.getContext());
                PlayMusicDao.insertPlayMusic(account, tem.getId());

                // 发送播放消息
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("send", "SheetMusicAdapter");
                jsonObject.put("receive", "UserManageActivity");
                jsonObject.put("data", tem);
                jsonObject.put("sta", "1");
                EventBus.getDefault().post(new MessageEvent(jsonObject.toJSONString()));

                // 跳转到播放详情页
                Intent intent = new Intent(holder.view.getContext(), RunMusicDetailActivity.class);
                intent.putExtra("musicId", tem.getId());
                holder.view.getContext().startActivity(intent);
            }
        });






    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder{

        TextView index;
        TextView title;
        LinearLayout father;
        ImageView img;
        ImageView playBtn;
        View view;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            index=itemView.findViewById(R.id.user_home_sheet_list_index);
            img=itemView.findViewById(R.id.user_home_sheet_list_img);
            title=itemView.findViewById(R.id.user_home_sheet_list_title);
            father=itemView.findViewById(R.id.user_home_sheet_list_father);
            playBtn=itemView.findViewById(R.id.user_home_sheet_list_play);
        }
    }

}
