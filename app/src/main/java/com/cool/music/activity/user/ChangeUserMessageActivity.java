package com.cool.music.activity.user;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.cool.music.R;
import com.cool.music.activity.RegisterActivity;
import com.cool.music.bean.UserBean;
import com.cool.music.dao.UserDao;
import com.cool.music.until.FileUntil;
import com.cool.music.until.Tools;
import com.google.android.exoplayer2.C;

public class ChangeUserMessageActivity extends AppCompatActivity {

    Uri result =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_user_message);


        Toolbar bar=findViewById(R.id.user_change_message_bar);
        setSupportActionBar(bar);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String account= Tools.getOnAccount(this);
        UserBean user= UserDao.getUserById(account);
        //向界面加载用户信息

        ImageView tx=findViewById(R.id.user_change_message_tx);
        Glide.with(this)
                .load(user.getImg()) // 图片资源ID
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                .into(tx);

        tx.setOnClickListener(v->{//实现点击功能，打开头像选择器
            openGallery(v);
        });
        getContentLauncher=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                if(uri!=null){
                    tx.setImageURI(uri);
                    result=uri;
                }else{
                    Tools.Toast(ChangeUserMessageActivity.this,"未选择头像");
                }
            }
        });

        EditText nick=findViewById(R.id.user_change_message_nickName);
        nick.setText(user.getNickname());
        EditText address=findViewById(R.id.user_change_message_address);
        address.setText(user.getAddress());
        EditText pwd=findViewById(R.id.user_change_message_pwd);
        pwd.setText(user.getPwd());
        RadioButton man=findViewById(R.id.user_change_message_man);
        RadioButton woman=findViewById(R.id.user_change_message_woman);
        if(user.getSex().equals("男")){
            man.setChecked(true);
        }else{
            woman.setChecked(true);
        }

        Button btn=findViewById(R.id.user_change_message_btn);
        btn.setOnClickListener(v->{//这个也是一个监听

            String sexStr=man.isChecked()?"男":"女";
            if(nick.getText().toString().equals("")){
                Tools.Toast(ChangeUserMessageActivity.this,"请输入昵称");
            }else if(pwd.getText().toString().equals("")){
                Tools.Toast(ChangeUserMessageActivity.this,"请输入密码");
            }else if (address.getText().toString().equals("")){
                Tools.Toast(ChangeUserMessageActivity.this,"请输入地址");
            }else{
                String accountT=account;
                String pwdT=pwd.getText().toString();
                String nickT=nick.getText().toString();
                String addressT=address.getText().toString();

                String path=null;
                //判断一下，头像是否上传一个功
                if(result==null){//代表不需要修改头像
                    path=user.getImg();
                }else{
                    path= FileUntil.saveImageBitmapToFileImg(result,ChangeUserMessageActivity.this);
                }



                int sta=UserDao.updateUserInfoById(accountT,pwdT,nickT,path,addressT,sexStr);
                if(sta==1){
                    Tools.Toast(ChangeUserMessageActivity.this,"更改个人信息成功");
                }else{
                    Tools.Toast(ChangeUserMessageActivity.this,"更改个人信息失败");
                }
            }



        });


    }


    private ActivityResultLauncher<String> getContentLauncher;
    /**打开相册
     *
     * @param v
     */
    private void openGallery(View v){
        getContentLauncher.launch("image/*");
    }
}