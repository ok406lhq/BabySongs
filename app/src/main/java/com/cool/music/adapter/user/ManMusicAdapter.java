package com.cool.music.adapter.user;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.cool.music.R;
import com.cool.music.activity.man.UpdateMusicActivity;
import com.cool.music.activity.user.RunMusicDetailActivity;
import com.cool.music.bean.MusicBean;
import com.cool.music.dao.MusicDao;
import com.cool.music.until.Tools;

import java.util.List;

/**
 * 这个是音乐的适配器
 */
public class ManMusicAdapter extends  RecyclerView.Adapter<ManMusicAdapter.viewHolder>{

    List<MusicBean> list;
    public ManMusicAdapter(List<MusicBean>  list){
        this.list=list;
    }

    @NonNull
    @Override
    public ManMusicAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());

        View convertView = inflater.inflate(R.layout.list_man_music, parent, false);


        ManMusicAdapter.viewHolder viewHolder = new ManMusicAdapter.viewHolder(convertView);




        return  viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        MusicBean tem = list.get(position);


        Glide.with(holder.view)
                .load(tem.getImg()) // 图片资源ID
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(25)))
                .into(holder.img);





        String title=tem.getName()+"-"+tem.getSinger();
        holder.title.setText(title);


        holder.father.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转更改界面
                Intent intent=new Intent(holder.view.getContext(),UpdateMusicActivity.class);
                intent.putExtra("musicId",tem.getId());
                holder.view.getContext().startActivity(intent);
            }
        });


        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a=MusicDao.deleteMusicById(tem.getId());
                if(a==1){
                    Tools.Toast(holder.view.getContext(),"删除成功");
                    // 移除项目并通知适配器
                    // 使用 removeIf 移除符合条件的元素
                    //int initialSize = list.size();
                    list.removeIf(music -> music.getId().equals(tem.getId()));//条件移除
                    notifyDataSetChanged();
                }else{
                    Tools.Toast(holder.view.getContext(),"删除失败");
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder{


        TextView title;
        RelativeLayout father;


        ImageView img;

        ImageView del;



        View view;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;

            img=itemView.findViewById(R.id.man_home_list_img);
            title=itemView.findViewById(R.id.man_home_list_title);
            father=itemView.findViewById(R.id.man_home_list_father);
            del=itemView.findViewById(R.id.man_home_del);





        }
    }

}
