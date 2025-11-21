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
public class SheetMusicDetailAdapter extends  RecyclerView.Adapter<SheetMusicDetailAdapter.viewHolder>{

    List<MusicBean> list;
    String sheetId;
    public SheetMusicDetailAdapter(List<MusicBean>  list,String sheetId){
        this.list=list;
        this.sheetId=sheetId;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());

        View convertView = inflater.inflate(R.layout.list_user_sheet_music_detail, parent, false);


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

        holder.father.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.view.getContext(), RunMusicDetailActivity.class);
                intent.putExtra("musicId",tem.getId());
                holder.view.getContext().startActivity(intent);
            }
        });

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将表格把这个歌曲移除掉
                int a=SheetDao.deleteSheet(sheetId,tem.getId());
                if(a==1){
                    list.remove(position);
                    notifyDataSetChanged();
                    Tools.Toast(holder.view.getContext(),"删除成功");
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

        TextView index;
        TextView title;
        LinearLayout father;


        ImageView img;


        Button btn;


        View view;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            index=itemView.findViewById(R.id.user_my_sheet_list_detail_index);
            img=itemView.findViewById(R.id.user_my_sheet_list_detail_img);
            title=itemView.findViewById(R.id.user_my_sheet_list_detail_title);
            father=itemView.findViewById(R.id.user_my_sheet_list_detail_father);
            btn=itemView.findViewById(R.id.user_my_sheet_list_detail_delete);





        }
    }

}
