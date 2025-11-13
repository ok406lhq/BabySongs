package com.cool.music.activity.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.cool.music.R;
import com.cool.music.adapter.user.SheetMusicDetailAdapter;
import com.cool.music.bean.MusicBean;
import com.cool.music.bean.SheetBean;
import com.cool.music.dao.PlayMusicDao;
import com.cool.music.dao.SheetDao;
import com.cool.music.until.Tools;

import java.util.List;

public class MySheetDetailActivity extends AppCompatActivity {
    Toolbar toolbar;
    String sheetId=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_sheet_detail);
        toolbar =findViewById(R.id.user_sheet_detail_bar);
        setSupportActionBar(toolbar);
        //实现了返回功能
        toolbar.setNavigationOnClickListener(v->{
            finish();
        });



        Bundle intent = getIntent().getExtras();
        sheetId=intent.getString("sheetId");
        //查询这个歌单，加载图片
        SheetBean sheet = SheetDao.getSheet(sheetId);//这个表单
        toolbar.setTitle(sheet .getName());

        ImageView img=findViewById(R.id.user_sheet_detail_bg);
        if(sheet.getImg()==null||sheet.getImg().equals("")||sheet.getImg().equals("0")){
            Glide.with(this)
                    .load(R.drawable.user_tx) // 图片资源ID
                    //.apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                    .into(img);
        }else{
            Glide.with(this)
                    .load(sheet.getImg()) // 图片资源ID
                    //.apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                    .into(img);
        }

        List<MusicBean> list = PlayMusicDao.getPlayMusicOnSheetCount(sheetId);

        RecyclerView listDe =findViewById(R.id.user_sheet_detail_list);

        SheetMusicDetailAdapter de=new SheetMusicDetailAdapter(list,sheetId);//适配器
        listDe.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        if(list==null||list.size()==0){
            listDe.setAdapter(null);
        }else{
            listDe.setAdapter(de);
            de.notifyDataSetChanged();//通知一下列表改变了
        }

        //实现向歌单里面添加歌曲

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_sheet_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.user_sheet_menu_del) {
            // Handle the settings action
            showDelDialog();
        }else if (id == R.id.user_sheet_menu_add) {
            //使用弹窗显示
            Intent intent=new Intent(MySheetDetailActivity.this, AddSearchMusicActivity.class);
            intent.putExtra("sheetId",sheetId);
            startActivity(intent);
        } else if (id == R.id.user_sheet_menu_update) {
            //名字和是否公开的弹窗，
            showUpdateSheetDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 跟新弹窗
     */
    public void showUpdateSheetDialog(){

        SheetBean sheetA = SheetDao.getSheet(sheetId);
        LayoutInflater inflater=LayoutInflater.from(this);
        View view=inflater.inflate(R.layout.dialog_update_sheet,null);
        EditText title = view.findViewById(R.id.dialog_title);
        title.setText(sheetA.getName());

        RadioButton noP = view.findViewById(R.id.dialog_no_p);

        RadioButton p = view.findViewById(R.id.dialog_p);
        if(sheetA.getSta().equals("1")){
            p.setChecked(true);
        }else{
            noP.setChecked(true);
        }
        title.setText(sheetA.getName());
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String t=title.getText().toString();
                if(t==null||t.equals("")){
                    Tools.Toast(MySheetDetailActivity.this,"请输入歌单名称");
                }else{
                    String sta="0";
                    if(p.isChecked()){
                        sta="1";
                    }
                    int i=SheetDao.updateSheet(sheetId,t,sta);
                    if(i==1){
                        Tools.Toast(MySheetDetailActivity.this,"修改成功");
                        dialog.cancel();
                        toolbar.setTitle(t);
                    }else{
                        Tools.Toast(MySheetDetailActivity.this,"修改失败");
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

    /**
     * 跟新弹窗
     */
    public void showDelDialog(){
        //删除表单
        SheetBean sheetA = SheetDao.getSheet(sheetId);


        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        builder.setMessage("确定删除歌单吗？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int a=SheetDao.deleteSheetById(sheetId);
                if(a==1){
                    Tools.Toast(MySheetDetailActivity.this,"删除成功");
                    finish();
                }else{
                    Tools.Toast(MySheetDetailActivity.this,"删除失败");
                }
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final  AlertDialog dialog=builder.create();
        dialog.show();

    }
    @Override
    public void onResume(){
        super.onResume();
        //刷新列表
        List<MusicBean> list = PlayMusicDao.getPlayMusicOnSheetCount(sheetId);

        RecyclerView listDe =findViewById(R.id.user_sheet_detail_list);

        SheetMusicDetailAdapter de=new SheetMusicDetailAdapter(list,sheetId);//适配器
        listDe.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        if(list==null||list.size()==0){
            listDe.setAdapter(null);
        }else{
            listDe.setAdapter(de);
            de.notifyDataSetChanged();//通知一下列表改变了
        }
    }


}