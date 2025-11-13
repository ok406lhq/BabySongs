package com.cool.music.activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cool.music.R;
import com.cool.music.dao.UserDao;
import com.cool.music.until.FileUntil;
import com.cool.music.until.Tools;

public class RegisterActivity extends AppCompatActivity {

    Uri result =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        Toolbar bar=findViewById(R.id.register_bar);
        setSupportActionBar(bar);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });








        //实现注册功能
        ImageView tx=findViewById(R.id.register_tx);

        EditText nick=findViewById(R.id.register_nickName);

        EditText account=findViewById(R.id.register_account);

        EditText address=findViewById(R.id.register_address);

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
                    Tools.Toast(RegisterActivity.this,"未选择头像");
                }
            }
        });






        EditText pwd=findViewById(R.id.register_pwd);
        RadioButton sex=findViewById(R.id.register_man);
        sex.setChecked(true);


        Button reg=findViewById(R.id.register_zhuce_btn);
        reg.setOnClickListener(v->{//这个也是一个监听

            String sexStr=sex.isChecked()?"男":"女";
            if(nick.getText().toString().equals("")){
                Tools.Toast(RegisterActivity.this,"请输入昵称");
            }else if(account.getText().toString().equals("")){
                Tools.Toast(RegisterActivity.this,"请输入账号");
            }else if(pwd.getText().toString().equals("")){
                Tools.Toast(RegisterActivity.this,"请输入密码");
            }else if (address.getText().toString().equals("")){
                Tools.Toast(RegisterActivity.this,"请输入地址");
            }else{
                String accountT=account.getText().toString();
                String pwdT=pwd.getText().toString();
                String nickT=nick.getText().toString();
                String addressT=address.getText().toString();

                //判断一下，头像是否上传一个功
                if(result==null){
                    Tools.Toast(RegisterActivity.this,"请选择头像");
                }else{
                    //将内容存储到本地，返回一个路径进行存储


                    String path=FileUntil.saveImageBitmapToFileImg(result,RegisterActivity.this);

                 //   FileImgUntil.saveImageBitmapToFileImg(uri, RegisterUserActivity.this,path);//保存图片
                    int sta=UserDao.register(accountT,pwdT,nickT,path,addressT,sexStr);
                    if(sta==1){
                        Tools.Toast(RegisterActivity.this,"注册成功");
                    }else{
                        Tools.Toast(RegisterActivity.this,"注册失败");
                    }
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