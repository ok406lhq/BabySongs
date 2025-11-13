package com.cool.music.adapter.user;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.cool.music.R;
import com.cool.music.activity.user.RunMusicDetailActivity;
import com.cool.music.bean.MusicBean;
import com.cool.music.dao.SheetDao;
import com.cool.music.until.Tools;

import java.util.List;

/**
 * 这个是音乐的适配器
 */
public class AddSheetMusicAdapter extends  RecyclerView.Adapter<AddSheetMusicAdapter.viewHolder>{

    String sheetId;
    List<MusicBean> list;
    public AddSheetMusicAdapter(List<MusicBean>  list,String sheetId){
        this.list=list;
        this.sheetId=sheetId;
    }

    @NonNull
    @Override
    public AddSheetMusicAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());

        View convertView = inflater.inflate(R.layout.list_user_add_sheet_music, parent, false);


        AddSheetMusicAdapter.viewHolder viewHolder = new AddSheetMusicAdapter.viewHolder(convertView);




        return  viewHolder;
    }
    viewHolder holder;
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        MusicBean tem = list.get(position);
        this.holder=holder;
        holder.index.setText(String.valueOf(position+1));

        Glide.with(holder.view)
                .load(tem.getImg()) // 图片资源ID
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(25)))
                .into(holder.img);


        String title=tem.getName()+"-"+tem.getSinger();
        holder.title.setText(title);

        holder.father.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.view.getContext(), RunMusicDetailActivity.class);
                intent.putExtra("musicId",tem.getId());
                holder.view.getContext().startActivity(intent);
            }
        });
        int sta=SheetDao.isSheetHaveMusic(sheetId,tem.getId());
        holder.btn.setOnClickListener(v->{//添加监听
           int a=add(sheetId,tem.getId());
           if(a==1){
               holder.btn.setVisibility(View.GONE);//显示添加按钮
               holder.del.setVisibility(View.VISIBLE);//显示添加按钮
           }

        });
        holder.del.setOnClickListener(v->{//移除
            int a=remove(sheetId,tem.getId());
            if(a==1){
                holder.btn.setVisibility(View.VISIBLE);//显示添加按钮
                holder.del.setVisibility(View.GONE);//显示添加按钮
            }
        });
        if(sta==0){
            holder.btn.setVisibility(View.VISIBLE);//显示添加按钮
            holder.del.setVisibility(View.GONE);//显示添加按钮
        }else{
            holder.btn.setVisibility(View.GONE);//显示添加按钮
            holder.del.setVisibility(View.VISIBLE);//显示添加按钮
        }

    }

    /**
     * 移除歌曲
     * @param id
     * @param mid
     */
    private  int remove(String id,String mid){
        int a=SheetDao.deleteSheet(id,mid);
        if(a==1){
            Tools.Toast(holder.view.getContext(),"移除成功");
        }else{
            Tools.Toast(holder.view.getContext(),"移除失败");
        }

        return a;

    }

    /**
     * 添加
     * @param id
     * @param mid
     */
    private  int add(String id,String mid){
        int a=SheetDao.addSheet(id,mid);
        if(a==1){
            Tools.Toast(holder.view.getContext(),"添加成功");
        }else{
            Tools.Toast(holder.view.getContext(),"添加失败");
        }
        return a;

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


        Button btn;

        Button del;

        View view;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            index=itemView.findViewById(R.id.user_my_sheet_list_add_detail_index);
            img=itemView.findViewById(R.id.user_my_sheet_list_add_detail_img);
            title=itemView.findViewById(R.id.user_my_sheet_list_add_detail_title);
            father=itemView.findViewById(R.id.user_my_sheet_list_add_detail_father);

            btn=itemView.findViewById(R.id.user_my_sheet_list_add_detail_add);
            del=itemView.findViewById(R.id.user_my_sheet_list_add_detail_del);



        }
    }

}
