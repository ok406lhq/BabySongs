package com.cool.music.activity.man;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

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
import com.cool.music.activity.user.UserManageActivity;
import com.cool.music.bean.MusicBean;
import com.cool.music.dao.MusicDao;
import com.cool.music.dao.PlayMusicDao;
import com.cool.music.until.FileUntil;
import com.cool.music.until.Tools;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;

import java.io.File;
import java.util.UUID;

public class UpdateMusicActivity extends AppCompatActivity {
    Handler handler;
    public static ExoPlayer player;
    Runnable  updateSeekBarRunnable;
    public static SeekBar seekBar;
    int duration=-1;

    int oldTime=0;//统计音乐上一次的值

    ImageButton userHomeRun;
    Uri result =null;
    String nameStr="";
    String singerStr="";
    String musicId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_music);

        Toolbar bar=this.findViewById(R.id.man_update_music_bar);
        setSupportActionBar(bar);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(handler!=null){
                    handler.removeCallbacksAndMessages(null);
                }
                if(player.isPlaying()){
                    player.pause();
                }
                finish();
            }
        });







        //这个地方是处理音频的内容
        getContentLauncher=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                if(uri!=null){
                    // tx.setImageURI(uri); 
                    
                    result=uri;
                    if(nameStr==null||nameStr.equals("")){
                        Tools.Toast(UpdateMusicActivity.this,"请输入歌曲名");
                    }else if(singerStr==null||singerStr.equals("")){
                        Tools.Toast(UpdateMusicActivity.this,"请输入歌手名");
                    }else if(result!=null){

                        String musicImg1= FileUntil.getFileName();
                        String txPathU = FileUntil.saveMusicThread(UpdateMusicActivity.this, result, musicImg1);
                        int a= MusicDao.updateMusic(musicId,nameStr,singerStr,musicImg1,txPathU);
                        if(a>=1){
                            Tools.Toast(UpdateMusicActivity.this,"更改成功");
                            MusicBean mb = MusicDao.getMusicById(musicId);
                            load(mb);
                        }else{
                            Tools.Toast(UpdateMusicActivity.this,"更改失败");
                        }



                    }


                }else{
                    Tools.Toast(UpdateMusicActivity.this,"请选择音乐");
                }
            }
        });

        //获取上一个界面传过来的musicId
        Bundle extras = getIntent().getExtras();
        musicId=extras.getString("musicId","");

        MusicBean music = MusicDao.getMusicById(musicId);


        EditText name=this.findViewById(R.id.man_update_music_name);
        EditText singer=this.findViewById(R.id.man_update_music_singer);
        Button add=this.findViewById(R.id.man_update_music_add);

        Switch aSwitch=this.findViewById(R.id.man_update_music_switch);


        name.setText(music.getName());
        singer.setText(music.getSinger());


        add.setOnClickListener(v->{
            //点击之后需要判读歌手和名字
            nameStr=name.getText().toString();
            singerStr=singer.getText().toString();
            if(nameStr==null||nameStr.equals("")){
                Tools.Toast(UpdateMusicActivity.this,"请输入歌曲名");
            }else if(singerStr==null||singerStr.equals("")){
                Tools.Toast(UpdateMusicActivity.this,"请输入歌手名");
            }else{

                if(aSwitch.isChecked()){//是选择的，则让他更改
                    openGallery(v);
                }else{
                   int a= MusicDao.updateMusic(musicId,nameStr,singerStr);
                   if(a>=1){
                       Tools.Toast(UpdateMusicActivity.this,"修改成功");
                   }else{
                       Tools.Toast(UpdateMusicActivity.this,"修改失败");
                   }
                }

                //如果上面两个条件都满足了，需要打开文件管理器，选择一音乐




            }

        });

        load(music);//初始化


    }


    private void load(MusicBean musicBean){
        //开始实现加载播放音乐功能
        // 获取 ImageButton 并设置图片
        ImageButton userHomeTx = findViewById(R.id.man_home_tx);//唱片图片
        // 获取 TextView 并设置文本
        TextView userHomeName = findViewById(R.id.man_home_name);//名字
        // 获取 ImageView 并设置图片
        userHomeRun = findViewById(R.id.man_home_run);//运行按钮
        seekBar = findViewById(R.id.man_music_seekbar);//运行按钮


        //播放音乐变成
        Glide.with(this)
                .load(musicBean.getImg()) // 图片资源ID
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                .into(userHomeTx);


        //默认是不播放内容的
        Glide.with(this)
                .load(R.drawable.music_run) // 图片资源ID
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                .into(userHomeRun);

        userHomeName.setText(musicBean.getName()+"-"+musicBean.getSinger());



        player = new ExoPlayer.Builder(this).build();//定义了一个播放器
        handler= new Handler(Looper.getMainLooper());//初始化




        //设置进度条监听
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 当 SeekBar 进度改变时触发
                if (progress == duration&&duration!=-1) {
                    // SeekBar 满了
                    Glide.with(UpdateMusicActivity.this)
                            .load(R.drawable.music_run) // 图片资源ID
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                            .into(userHomeRun);
                    seekBar.setProgress(duration);
                }
                if(oldTime!=-1){
                    String str=String.valueOf(oldTime)+"-"+String.valueOf(progress);
                    int time=progress-oldTime;//每次的间隔
                    oldTime=progress;
                    //每次进度条改变的时候
                    String account=Tools.getOnAccount(UpdateMusicActivity.this);
                    MusicDao.addListenMusic(account,musicId,time);
                    Log.d("进度条改变内容",str);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                player.pause();
                oldTime=-1;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (player != null) {
                    player.seekTo(progress); // 将播放器定位到指定位置
                    player.play();
                    oldTime=progress;
                }
            }
        });


        //初始化人
        updateSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                if (player != null && player.isPlaying()) {
                    int currentPosition = (int) player.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    duration = (int) player.getDuration();
                    if (duration > 0) {
                        seekBar.setMax(duration); // 设置 SeekBar 的最大值
                    }
                }
                //代表当前进度 跟新一下图片表
                if(player.isPlaying()){
                    Glide.with(UpdateMusicActivity.this)
                            .load(R.drawable.music_z) // 图片资源ID
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                            .into(userHomeRun);
                }else{
                    Glide.with(UpdateMusicActivity.this)
                            .load(R.drawable.music_run) // 图片资源ID
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                            .into(userHomeRun);
                }
                handler.postDelayed(this, 100); // 每秒更新一次
            }
        };
        handler.post(updateSeekBarRunnable);


        File file = new File(musicBean.getPath());//加载文件
        if(file.exists()){//则加载文件
            String filePath = file.getAbsolutePath();
            MediaItem mediaItem = MediaItem.fromUri(filePath);
            DefaultDataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(this);
            ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem);
            player.setMediaSource(mediaSource);

            player.prepare();
            musicId=musicBean.getId();
            seekBar.setMax(duration);
            seekBar.setProgress(0);
            //每秒更新一次进度


            //player.play();


            //当进度条监听



        }

        userHomeRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player.isPlaying()){
                    Glide.with(UpdateMusicActivity.this)
                            .load(R.drawable.music_run) // 图片资源ID
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                            .into(userHomeRun);
                    player.pause();

                }else{
                    Glide.with(UpdateMusicActivity.this)
                            .load(R.drawable.music_z) // 图片资源ID
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                            .into(userHomeRun);
                    player.play();
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
        getContentLauncher.launch("audio/*");
    }
}