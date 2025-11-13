package com.cool.music.adapter.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.cool.music.MainActivity;
import com.cool.music.R;
import com.cool.music.activity.user.MySheetDetailActivity;
import com.cool.music.activity.user.RunMusicDetailActivity;
import com.cool.music.activity.user.UserManageActivity;
import com.cool.music.bean.MusicBean;
import com.cool.music.bean.SheetBean;
import com.cool.music.bean.UserBean;
import com.cool.music.dao.MusicDao;
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
public class MySheetMusicAdapter extends  RecyclerView.Adapter<MySheetMusicAdapter.viewHolder>{

    List<SheetBean> list;
    public MySheetMusicAdapter(List<SheetBean>  list){
        this.list=list;
    }

    @NonNull
    @Override
    public MySheetMusicAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());

        View convertView = inflater.inflate(R.layout.list_user_my_sheet_music, parent, false);


        MySheetMusicAdapter.viewHolder viewHolder = new MySheetMusicAdapter.viewHolder(convertView);




        return  viewHolder;
    }
    viewHolder holder;

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        SheetBean tem = list.get(position);
        this.holder=holder;

        if(tem.getSta().equals("-1")){
            Glide.with(holder.view)
                    .load(R.drawable.sheet_add) // 图片资源ID
                   // .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                    .into(holder.img);
            holder.con.setText("添加属于你自己的歌单");
            holder.title.setText(tem.getName());
            holder.father.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //我希望打开弹窗
                    showUpdateSheetDialog();
                }
            });
        }else{
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
            }else{
                title=title+"未公开";
            }

            holder.con.setText(title);

            holder.father.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(holder.view.getContext(),  MySheetDetailActivity.class);
                    intent.putExtra("sheetId",tem.getId());
                    holder.view.getContext().startActivity(intent);



                }
            });

        }









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


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            img=itemView.findViewById(R.id.user_home_my_sheet_list_img);
            title=itemView.findViewById(R.id.user_home_my_sheet_list_title);
            con=itemView.findViewById(R.id.user_home_my_sheet_list_con);
            father=itemView.findViewById(R.id.user_home_sheet_my_list_father);





        }
    }


    /**
     * 跟新弹窗
     */
    public void showUpdateSheetDialog(){


        LayoutInflater inflater=LayoutInflater.from(holder.view.getContext());
        View view=inflater.inflate(R.layout.dialog_update_sheet,null);
        EditText title = view.findViewById(R.id.dialog_title);


        RadioButton noP = view.findViewById(R.id.dialog_no_p);

        RadioButton p = view.findViewById(R.id.dialog_p);
        noP.setChecked(true);
        AlertDialog.Builder builder=new AlertDialog.Builder(holder.view.getContext());
        builder.setView(view);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String t=title.getText().toString();
                if(t==null||t.equals("")){
                    Tools.Toast(holder.view.getContext(),"请输入歌单名称");
                }else{
                    String sta="0";
                    if(p.isChecked()){
                        sta="1";
                    }
                    String uuid= UUID.randomUUID().toString().replace("-","");
                    String account=Tools.getOnAccount(holder.view.getContext());
                    int i=SheetDao.addSheet(uuid,t,sta,account,sta);
                    if(i==1){
                        Tools.Toast(holder.view.getContext(),"添加成功");
                        dialog.cancel();



                        SheetBean newSheet = new SheetBean();
                        newSheet.setId(uuid);
                        newSheet.setName(t);
                        newSheet.setSta(String.valueOf(sta));
                        newSheet.setCreate_user_id(account);
                        // 设置其他必要的属性...

                        // 将新的 SheetBean 添加到适配器的数据源中。
                        list.add(list.size()-1, newSheet); // 将新项插入到列表开头

                        // 通知适配器数据集发生变化。
                        notifyDataSetChanged();
                       // toolbar.setTitle(t);
                    }else{
                        Tools.Toast(holder.view.getContext(),"添加失败");
                    }
                }

            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        builder.setTitle("歌单管理");
        final  AlertDialog dialog=builder.create();
        dialog.show();

    }

}
