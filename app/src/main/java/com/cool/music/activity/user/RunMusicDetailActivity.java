package com.cool.music.activity.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.cool.music.R;
import com.cool.music.bean.MessageEvent;
import com.cool.music.bean.MusicBean;
import com.cool.music.bean.PostBean;
import com.cool.music.dao.CommentDao;
import com.cool.music.dao.MusicDao;
import com.cool.music.dao.PlayMusicDao;
import com.cool.music.dao.PostDao;
import com.cool.music.dao.UserDao;
import com.cool.music.until.Tools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunMusicDetailActivity extends AppCompatActivity {

    Handler handler;

    TextView ctime;
    ImageView run;
    Toolbar bar;
    TextView textView;
    ImageView like;
    SeekBar seekBar;
    int sta=0;
    String musicId=null;
    Runnable  updateSeekBarRunnable;
    int play=0;
    boolean barFlag=true;

    private RecyclerView commentsRecyclerView;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_run_music_detail);

        // ✨✨✨ 先初始化所有控件（关键！）
        initViews();

        // 注册 EventBus
        EventBus.getDefault().register(this);

        //1.获取歌曲ID，然后查询相关内容
        String musicIda = getIntent().getExtras().getString("musicId");
        handler = new Handler(Looper.getMainLooper());

        // 初始化评论列表
        commentsRecyclerView = this.findViewById(R.id.user_run_comments_list);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 设置监听器
        setupListeners();

        // 最后加载音乐
        loadMusic(musicIda);
    }

    // ✨✨✨ 新增：初始化所有控件的方法
    private void initViews() {
        run = this.findViewById(R.id.user_home_run_play);
        seekBar = findViewById(R.id.user_home_run_seek_bar);
        textView = findViewById(R.id.user_home_run_time_all);
        ctime = findViewById(R.id.user_home_run_time);
        bar = this.findViewById(R.id.user_home_run_title);
        like = this.findViewById(R.id.user_home_run_like);
    }

    // ✨✨✨ 新增：设置所有监听器的方法
    private void setupListeners() {
        // 设置 Toolbar
        setSupportActionBar(bar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                finish();
            }
        });

        // SeekBar 监听
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                UserManageActivity.seekBar.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                UserManageActivity.player.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (UserManageActivity.player != null) {
                    UserManageActivity.player.seekTo(progress);
                    UserManageActivity.player.play();
                    UserManageActivity.seekBar.setProgress(progress);
                }
            }
        });

        // 喜欢按钮监听
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sta==1){
                    int a=MusicDao.cancelLikeMusic(Tools.getOnAccount(RunMusicDetailActivity.this),musicId);
                    if(a==1){
                        Glide.with(RunMusicDetailActivity.this)
                                .load(R.drawable.like)
                                .into(like);
                        Tools.Toast(RunMusicDetailActivity.this,"取消喜欢成功");
                        sta=0;
                    }else{
                        Tools.Toast(RunMusicDetailActivity.this,"取消喜欢失败");
                    }
                }else{
                    int a=MusicDao.likeMusic(Tools.getOnAccount(RunMusicDetailActivity.this),musicId);
                    if(a==1){
                        Glide.with(RunMusicDetailActivity.this)
                                .load(R.drawable.like1)
                                .into(like);
                        Tools.Toast(RunMusicDetailActivity.this,"已喜欢");
                        sta=1;
                    }else{
                        Tools.Toast(RunMusicDetailActivity.this,"失败");
                    }
                }
            }
        });

        // 播放/暂停按钮监听
        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserManageActivity.player.isPlaying()){
                    Glide.with(RunMusicDetailActivity.this)
                            .load(R.drawable.detail_r)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                            .into(run);
                    UserManageActivity.player.pause();
                }else{
                    UserManageActivity.player.play();
                    Glide.with(RunMusicDetailActivity.this)
                            .load(R.drawable.detail_z)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                            .into(run);
                }
            }
        });

        // 上一首/下一首按钮监听
        ImageView pre = this.findViewById(R.id.user_home_run_play_pre);
        pre.setOnClickListener(v -> preMusic());

        ImageView next = this.findViewById(R.id.user_home_run_play_next);
        next.setOnClickListener(v -> nextMusic());

        // 评论功能
        EditText commentInput = this.findViewById(R.id.user_run_comment_input);
        Button commentSend = this.findViewById(R.id.user_run_comment_send);

        commentSend.setOnClickListener(v -> {
            String content = commentInput.getText().toString().trim();
            if (content.isEmpty()) {
                Tools.Toast(this, "请输入评论内容");
                return;
            }

            String postId = java.util.UUID.randomUUID().toString().replace("-", "");
            String userId = Tools.getOnAccount(this);

            int result = PostDao.addPost(postId, userId, musicId, content);
            if (result > 0) {
                Tools.Toast(this, "评论发表成功");
                commentInput.setText("");
                loadPosts();
            } else {
                Tools.Toast(this, "评论发表失败");
            }
        });

        // 进度条更新 Runnable
        // 在 setupListeners() 方法中，修改进度条更新 Runnable
        updateSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                // ✨✨✨ 直接从播放器获取进度，而不是从 UserManageActivity 同步
                if (UserManageActivity.player != null) {
                    int currentPosition = (int) UserManageActivity.player.getCurrentPosition();
                    int totalDuration = (int) UserManageActivity.player.getDuration();

                    // 更新本地进度条
                    seekBar.setProgress(currentPosition);
                    seekBar.setMax(totalDuration);

                    // ✨ 同步到主界面的进度条
                    if (UserManageActivity.seekBar != null) {
                        UserManageActivity.seekBar.setProgress(currentPosition);
                        UserManageActivity.seekBar.setMax(totalDuration);
                    }

                    // ✨✨✨ 更新时间显示（关键！）
                    ctime.setText(formatMilliseconds(currentPosition));
                    textView.setText(formatMilliseconds(totalDuration));

                    // 更新播放/暂停按钮图标
                    if (UserManageActivity.player.isPlaying()) {
                        Glide.with(RunMusicDetailActivity.this)
                                .load(R.drawable.detail_z)
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                                .into(run);
                    } else {
                        Glide.with(RunMusicDetailActivity.this)
                                .load(R.drawable.detail_r)
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                                .into(run);
                    }
                }

                handler.postDelayed(this, 100); // 每100毫秒更新一次
            }
        };
        handler.post(updateSeekBarRunnable);
    }
    // ✨✨✨ 添加 EventBus 订阅方法，监听歌曲切换事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        JSONObject json = JSONObject.parseObject(event.getMessage());
        String send = json.getString("send");

        // 只处理自动播放或手动切歌的消息
        if ("AutoPlay".equals(send) || "RunMusicDetailActivity".equals(send)) {
            MusicBean musicBean = json.getJSONObject("data").toJavaObject(MusicBean.class);

            // 更新详情页显示
            if (musicBean != null && !musicBean.getId().equals(musicId)) {
                // 歌曲已切换，更新UI
                musicId = musicBean.getId();
                updateUIForNewMusic(musicBean);
            }
        }
    }

    // ✨✨✨ 新增：更新UI的方法
// ✨✨✨ 更新：添加空指针检查
    private void updateUIForNewMusic(MusicBean musicBean) {
        if (musicBean == null) {
            return;
        }

        // 更新背景颜色
        String musicPath = musicBean.getImg();
        int[] rgb = getImageMostColor(musicPath);

        LinearLayout line = this.findViewById(R.id.user_home_run_main);
        if (line != null) {
            if (rgb != null) {
                int lightColor = createLighterColor(rgb);
                int darkColor = createDarkerColor(rgb);
                int[] color = new int[]{darkColor, lightColor};
                GradientDrawable gradientDrawable = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM, color
                );
                line.setBackground(gradientDrawable);
            } else {
                int[] defaultColor = new int[]{
                        Color.parseColor("#1a237e"),
                        Color.parseColor("#283593")
                };
                GradientDrawable gradientDrawable = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM, defaultColor
                );
                line.setBackground(gradientDrawable);
            }
        }

        // 更新封面图片
        ImageView img = this.findViewById(R.id.user_home_run_img);
        if (img != null) {
            Glide.with(this)
                    .load(musicBean.getImg())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                    .error(R.drawable.icon_gd)
                    .into(img);
        }

        // 更新歌名和歌手
        TextView name = this.findViewById(R.id.user_home_run_name);
        if (name != null) {
            name.setText(musicBean.getName());
        }

        TextView sing = this.findViewById(R.id.user_home_run_sing);
        if (sing != null) {
            sing.setText(musicBean.getSinger());
        }

        // 更新喜欢状态
        if (like != null) {
            sta = MusicDao.getLikeMusic(Tools.getOnAccount(this), musicBean.getId());
            if (sta == 1) {
                Glide.with(this)
                        .load(R.drawable.like1)
                        .into(like);
            } else {
                Glide.with(this)
                        .load(R.drawable.like)
                        .into(like);
            }
        }

        // 更新标题栏
        if (bar != null) {
            bar.setTitle(musicBean.getName());
        }

        // 重置进度条
        if (seekBar != null) {
            seekBar.setProgress(0);
        }

        // 加载新歌曲的评论列表
        loadPosts();
    }
    /**
     * 获取亮色的rgb值
     * @param color
     * @return
     */
    private int createLighterColor(int[] color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.rgb(color[0], color[1], color[2]), hsv);
        // 降低亮度
        hsv[2] += 5 / 255.0f; // 亮度范围是0到1，所以需要将9转换为0到1之间的值
        // 确保亮度不会低于0
        if (hsv[2] < 0) {
            hsv[2] = 0;
        }
        return Color.HSVToColor(hsv);
    }

    /**
     * 获取暗色
     * @param color
     * @return
     */
    private int createDarkerColor(int[] color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.rgb(color[0], color[1], color[2]), hsv);
        hsv[2] -= 5 / 255.0f; // 亮度范围是0到1，所以需要将9转换为0到1之间的值
        // 确保亮度不会低于0
        if (hsv[2] <= 255) {
            hsv[2] = 255;
        }
        return Color.HSVToColor(hsv);
    }


    /**
     * 获取图片中的主要颜色
     * @param imagePath
     * @return
     */
    private int[] getImageMostColor(String imagePath) {
        // ✨ 添加路径检查
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }

        // ✨ 检查文件是否存在
        File file = new File(imagePath);
        if (!file.exists()) {
            return null;
        }

        Bitmap bitmapA = BitmapFactory.decodeFile(imagePath);

        // ✨ 添加 null 检查（关键！）
        if (bitmapA == null) {
            return null;
        }

        Bitmap bitmap = Bitmap.createScaledBitmap(bitmapA, 100, 100, true);
        if (bitmap == null) {
            return null;
        }

        HashMap<Integer, Integer> colorCount = new HashMap<>();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        for(int x=0; x<width; x++){
            for (int y=0; y<height; y++){
                int pixel = bitmap.getPixel(x, y);
                colorCount.put(pixel, colorCount.getOrDefault(pixel, 0) + 1);
            }
        }

        // 找出最常见的颜色
        Map.Entry<Integer, Integer> colorZ = null;
        for (Map.Entry<Integer, Integer> entry : colorCount.entrySet()) {
            if (colorZ == null || entry.getValue() > colorZ.getValue()) {
                colorZ = entry;
            }
        }

        // ✨ 释放 Bitmap 资源
        if (bitmapA != null && !bitmapA.isRecycled()) {
            bitmapA.recycle();
        }
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }

        if (colorZ != null) {
            int rgbValue = colorZ.getKey();
            int red = (rgbValue >> 16) & 0xFF;
            int green = (rgbValue >> 8) & 0xFF;
            int blue = rgbValue & 0xFF;
            return new int[]{red, green, blue};
        } else {
            return null;
        }
    }

    /**
     * 将毫秒转换分钟
     * @param milliseconds
     * @return
     */
    public static String formatMilliseconds(long milliseconds) {
        // 计算总秒数
        int totalSeconds = (int) (milliseconds / 1000);

        // 计算分钟数
        int minutes = totalSeconds / 60;

        // 计算剩余秒数
        int seconds = totalSeconds % 60;

        // 格式化输出结果
        return String.format("%02d:%02d", minutes, seconds);
    }


    /**
     * 上一首音乐
     */

    private void preMusic(){

        String id;

        //查询当前用户所有的播放列表当中的内容
        List<MusicBean> list = PlayMusicDao.getCurrentUserPlayMusic(Tools.getOnAccount(this));
        if(list!=null||list.size()>0){//如果播放把列表有内容，则找到为
            int index = 0;
            for (MusicBean musicBean : list) {
                if(musicBean.getId().equals(musicId)){
                    break;
                }
                index++;
            }
            //将上一首ID进行返回
            if(index==0){
                id=list.get(list.size()-1).getId();
            }else{
                id=list.get(index-1).getId();
            }
        }else{
            Tools.Toast(this,"音乐列表没有音乐");
            return;
        }

        musicId=id;
        loadMusic(id);
        seekBar.setProgress(0);
        MusicBean music = MusicDao.getMusicById(id);//得到上一首的音乐
        //播放按钮 点进去的那一刻，只是没有播放，需要手动点击播放
        JSONObject jsonObject=new JSONObject();//发送界面，接受界面，数据
        jsonObject.put("send","RunMusicDetailActivity");
        jsonObject.put("receive", "UserManageActivity");
        jsonObject.put("data",music);
        jsonObject.put("sta","1");
        EventBus.getDefault().post(new MessageEvent(jsonObject.toJSONString()));

    }

    /**
     * 下一首夤夜
     */
    private void nextMusic(){
        String id;

        //查询当前用户所有的播放列表当中的内容
        List<MusicBean> list = PlayMusicDao.getCurrentUserPlayMusic(Tools.getOnAccount(this));
        if(list!=null||list.size()>0){//如果播放把列表有内容，则找到为
            int index = 0;
            for (MusicBean musicBean : list) {
                if(musicBean.getId().equals(musicId)){
                    break;
                }
                index++;
            }
            if(index==list.size()-1){
                id=list.get(0).getId();
            }else{
                id=list.get(index+1).getId();
            }

        }else{
            Tools.Toast(this,"音乐列表没有音乐");
            return;
        }
        musicId=id;
        loadMusic(id);
        seekBar.setProgress(0);
        MusicBean music = MusicDao.getMusicById(id);//得到上一首的音乐
        //播放按钮 点进去的那一刻，只是没有播放，需要手动点击播放
        JSONObject jsonObject=new JSONObject();//发送界面，接受界面，数据
        jsonObject.put("send","RunMusicDetailActivity");
        jsonObject.put("receive", "UserManageActivity");
        jsonObject.put("data",music);
        jsonObject.put("sta","1");
        EventBus.getDefault().post(new MessageEvent(jsonObject.toJSONString()));

    }


    @Override
    protected void onResume() {
        super.onResume();
        // 刷新评论列表（从PostDetailActivity返回时）
        if (musicId != null) {
            loadPosts();
        }
    }

    /**
     * 加载帖子列表
     */
    private void loadPosts() {
        if (musicId == null) return;

        List<PostBean> posts = PostDao.getPostsByMusicId(musicId);
        postAdapter = new PostAdapter(posts);
        commentsRecyclerView.setAdapter(postAdapter);
    }

    /**
     * 输入音乐ID，加载音乐
     * @param musicIda
     */
    private void loadMusic(String musicIda){
        // 根据这个ID进行查询这个音乐的信息
        MusicBean musicBean = MusicDao.getMusicById(musicIda);

        // 添加 null 检查
        if (musicBean == null) {
            Tools.Toast(this, "歌曲信息加载失败");
            finish();
            return;
        }

        // 更新 musicId
        musicId = musicBean.getId();

        // 更新 UI
        updateUIForNewMusic(musicBean);

        // 播放按钮设置
        if (run != null) {
            Glide.with(this)
                    .load(R.drawable.detail_z)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                    .into(run);
        }

        // 发送 EventBus 消息
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("send", "RunMusicDetailActivity");
        jsonObject.put("receive", "UserManageActivity");
        jsonObject.put("data", musicBean);
        jsonObject.put("sta", "1");
        EventBus.getDefault().post(new MessageEvent(jsonObject.toJSONString()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);

        // ✨✨✨ 取消注册 EventBus（关键！）
        EventBus.getDefault().unregister(this);

        getDelegate().onDestroy();

    }

    // 帖子适配器
    class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
        private List<PostBean> posts;

        PostAdapter(List<PostBean> posts) {
            this.posts = posts;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            PostBean post = posts.get(position);

            // 获取歌曲信息（但在歌曲详情页中不需要显示，所以隐藏）
            holder.musicTag.setVisibility(View.GONE);

            // 获取用户昵称
            String nickname = UserDao.getUserNickname(post.getUser_id());
            holder.username.setText(nickname != null ? nickname : post.getUser_id());

            holder.time.setText(post.getCreate_time());
            holder.content.setText(post.getContent());

            // 获取评论数量
            int commentCount = CommentDao.getCommentsByPostId(post.getId()).size();
            holder.commentCount.setText(commentCount + "条评论");

            // 点击整个帖子跳转到详情页
            holder.container.setOnClickListener(v -> {
                Intent intent = new Intent(RunMusicDetailActivity.this, PostDetailActivity.class);
                intent.putExtra("postId", post.getId());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return posts != null ? posts.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView musicTag, username, time, content, commentCount;
            LinearLayout container;

            ViewHolder(View view) {
                super(view);
                musicTag = view.findViewById(R.id.post_music_tag);
                username = view.findViewById(R.id.post_username);
                time = view.findViewById(R.id.post_time);
                content = view.findViewById(R.id.post_content);
                commentCount = view.findViewById(R.id.post_comment_count);
                container = view.findViewById(R.id.post_item_container);
            }
        }
    }

}