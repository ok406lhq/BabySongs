package com.cool.music.activity.man;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cool.music.R;
import com.cool.music.activity.RegisterActivity;
import com.cool.music.bean.GenreBean;
import com.cool.music.dao.GenreDao;
import com.cool.music.dao.MusicDao;
import com.cool.music.until.FileUntil;
import com.cool.music.until.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddMusicActivity extends AppCompatActivity {
    Uri result =null;
    String nameStr="";
    String singerStr="";
    String selectedGenreId="";
    List<GenreBean> genreList;
    Spinner genreSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_music);

        Toolbar bar=this.findViewById(R.id.man_add_music_bar);
        setSupportActionBar(bar);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 初始化曲风选择器
        genreSpinner = findViewById(R.id.man_add_music_genre);
        loadGenres();


        //这个地方是处理音频的内容
        getContentLauncher=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                if(uri!=null){
                    // tx.setImageURI(uri);
                    result=uri;
                    if(nameStr==null||nameStr.equals("")){
                        Tools.Toast(AddMusicActivity.this,"请输入歌曲名");
                    }else if(singerStr==null||singerStr.equals("")){
                        Tools.Toast(AddMusicActivity.this,"请输入歌手名");
                    }else if(selectedGenreId==null||selectedGenreId.equals("")){
                        Tools.Toast(AddMusicActivity.this,"请选择曲风");
                    }else if(result!=null){

                        //如果上面两个条件都满足了，需要打开文件管理器，选择一音乐
                        //afds11-fdsfds-fdsf
                        String id= UUID.randomUUID().toString().replace("-","");
                        //从url当中提取路径和图片
                        String musicImg1=FileUntil.getFileName();
                        String txPathU = FileUntil.saveMusicThread(AddMusicActivity.this, result, musicImg1);
                        int a=MusicDao.addMusic(id,nameStr,singerStr,musicImg1,txPathU,selectedGenreId);
                        if(a>=1){
                            Tools.Toast(AddMusicActivity.this,"添加成功");
                            finish();
                        }else{
                            Tools.Toast(AddMusicActivity.this,"添加失败");
                        }



                    }


                }else{
                    Tools.Toast(AddMusicActivity.this,"请选择音乐");
                }
            }
        });


        EditText name=this.findViewById(R.id.man_add_music_name);
        EditText singer=this.findViewById(R.id.man_add_music_singer);
        Button add=this.findViewById(R.id.man_add_music_add);
        add.setOnClickListener(v->{
            //点击之后需要判读歌手和名字
            nameStr=name.getText().toString();
            singerStr=singer.getText().toString();

            // 获取选中的曲风
            int selectedPosition = genreSpinner.getSelectedItemPosition();
            if(selectedPosition >= 0 && genreList != null && selectedPosition < genreList.size()){
                selectedGenreId = genreList.get(selectedPosition).getId();
            }

            if(nameStr==null||nameStr.equals("")){
                Tools.Toast(AddMusicActivity.this,"请输入歌曲名");
            }else if(singerStr==null||singerStr.equals("")){
                Tools.Toast(AddMusicActivity.this,"请输入歌手名");
            }else if(selectedGenreId==null||selectedGenreId.equals("")){
                Tools.Toast(AddMusicActivity.this,"请选择曲风");
            }else{

                //如果上面两个条件都满足了，需要打开文件管理器，选择一音乐
                openGallery(v);



            }

        });



        //开始实现添加音乐功能

    }

    private ActivityResultLauncher<String> getContentLauncher;
    /**打开相册
     *
     * @param v
     */
    private void openGallery(View v){
        getContentLauncher.launch("audio/*");
    }

    /**
     * 加载曲风列表
     */
    private void loadGenres() {
        genreList = GenreDao.getAllGenres();
        if (genreList != null && genreList.size() > 0) {
            List<String> genreNames = new ArrayList<>();
            for (GenreBean genre : genreList) {
                genreNames.add(genre.getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    genreNames
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            genreSpinner.setAdapter(adapter);
        }
    }

}