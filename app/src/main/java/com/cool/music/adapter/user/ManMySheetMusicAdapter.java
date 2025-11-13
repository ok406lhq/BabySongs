package com.cool.music.adapter.user;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.cool.music.R;
import com.cool.music.bean.MusicBean;
import com.cool.music.bean.SheetBean;
import com.cool.music.bean.UserBean;
import com.cool.music.dao.PlayMusicDao;
import com.cool.music.dao.SheetDao;
import com.cool.music.dao.UserDao;
import com.cool.music.until.FileUntil;
import com.cool.music.until.Tools;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 这个是音乐的适配器
 */
public class ManMySheetMusicAdapter extends  RecyclerView.Adapter<ManMySheetMusicAdapter.viewHolder>{

    List<SheetBean> list;
    public ManMySheetMusicAdapter(List<SheetBean>  list){
        this.list=list;
    }

    @NonNull
    @Override
    public ManMySheetMusicAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());

        View convertView = inflater.inflate(R.layout.list_user_man_my_sheet_music, parent, false);


        ManMySheetMusicAdapter.viewHolder viewHolder = new ManMySheetMusicAdapter.viewHolder(convertView);


        return  viewHolder;
    }
    viewHolder holder;

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        SheetBean tem = list.get(position);
        this.holder=holder;


        //如果这个没有图片则加载的内容
        if(!(tem.getImg()==null||tem.getImg().equals("")||tem.getImg().equals("0"))){
            Glide.with(holder.view)
                    .load(tem.getImg()) // 图片资源ID
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                    .into(holder.img);
        }else{
            //代表没有图片
            //判断这个里面有没有音乐
            List<MusicBean> ls = PlayMusicDao.getPlayMusicOnSheetCount(tem.getId());//表单的音乐
            if(ls==null||ls.size()==0){
                Glide.with(holder.view)
                        .load(R.drawable.user_tx) // 图片资源ID
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                        .into(holder.img);
            }else{
                //再列表音乐随机选一个进行加载上
                String musicImg= FileUntil.getFileName();
                int randomIndex = ThreadLocalRandom.current().nextInt(ls.size());
                MusicBean music = ls.get(randomIndex);
                Tools.getMusicName( music .getPath(),musicImg);
                Glide.with(holder.view)
                        .load(musicImg) // 图片资源ID
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                        .into(holder.img);
                SheetDao.updateSheet(tem.getId(),musicImg);

            }



        }


        holder.title.setText(tem.getName());
        //歌单:50首·落花惜·未公布
        String title="歌单:";

        String count = PlayMusicDao.getPlayMusicCount(tem.getId());
        title=title+count+"首·";

        UserBean user = UserDao.getUserById(Tools.getOnAccount(holder.view.getContext()));
        title=title+user.getNickname()+"·";
        if(tem.getSta().equals("1")){
            title=title+"已公开";
            Glide.with(holder.view)
                    .load(R.drawable.icon_open) // 图片资源ID
                    // .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                    .into(holder.lock);
        }else{
            title=title+"未公开";
            Glide.with(holder.view)
                    .load(R.drawable.icon_no_open) // 图片资源ID
                    // .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                    .into(holder.lock);
        }

        holder.con.setText(title);


        //删除当前列表
        holder.del.setOnClickListener(v -> {
            int a=SheetDao.deleteSheetById(tem.getId());
            if(a==1){
                int code=0;
                for (SheetBean sheetBean : list) {
                    if(sheetBean.getId().equals(tem.getId())){
                        list.remove(code);
                    }
                    code++;
                }
                notifyDataSetChanged();
            }


        });

        //将其设置成隐私
        holder.lock.setOnClickListener(v -> {
            SheetBean sheet = SheetDao.getSheet(tem.getId());

            String temp="歌单:";


            if(sheet.getSta().equals("0")){
                int a=SheetDao.updateSheetSta(tem.getId(),"1");
                if(a==1){
                    //修改成功
                    Glide.with(holder.view)
                            .load(R.drawable.icon_open) // 图片资源ID
                            // .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                            .into(holder.lock);


                    String count1 = PlayMusicDao.getPlayMusicCount(tem.getId());
                    temp=temp+count1+"首·";
                    temp= temp+user.getNickname()+"·";
                    temp=temp+"已公开";
                    holder.con.setText(temp);


                    Tools.Toast(holder.view.getContext(),"修改成功");
                }else{
                    Tools.Toast(holder.view.getContext(),"修改失败");
                }
            }else{
                int a=SheetDao.updateSheetSta(tem.getId(),"0");
                if(a==1){
                    //修改成功
                    Glide.with(holder.view)
                            .load(R.drawable.icon_no_open) // 图片资源ID
                            // .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                            .into(holder.lock);
                    String count1 = PlayMusicDao.getPlayMusicCount(tem.getId());
                    temp=temp+count1+"首·";
                    temp= temp+user.getNickname()+"·";
                    temp=temp+"未公开";
                    holder.con.setText(temp);

                    Tools.Toast(holder.view.getContext(),"修改成功");
                }else{
                    Tools.Toast(holder.view.getContext(),"修改失败");
                }
            }


        });



    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    static class viewHolder extends RecyclerView.ViewHolder{

        TextView con;
        TextView title;
        LinearLayout father;


        ImageView img;




        View view;

        ImageButton lock;

        ImageButton del;



        public viewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            img=itemView.findViewById(R.id.man_home_my_sheet_list_img);
            title=itemView.findViewById(R.id.man_home_my_sheet_list_title);
            con=itemView.findViewById(R.id.man_home_my_sheet_list_con);
            father=itemView.findViewById(R.id.man_home_sheet_my_list_father);
            lock=itemView.findViewById(R.id.man_home_my_sheet_list_lock);
            del=itemView.findViewById(R.id.man_home_my_sheet_list_del);



        }
    }


}
