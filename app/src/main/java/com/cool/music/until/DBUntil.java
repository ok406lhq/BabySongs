package com.cool.music.until;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.cool.music.R;

/**
 * 用于链接sqllite数据库
 */
public class DBUntil extends SQLiteOpenHelper {

    private static final int version = 80;//版本号，每次更改表结构都需要加1，否则不生效

    private static final String databaseName = "db_music.db";//数据库名称必须以db结尾

    private Context context;
    public static SQLiteDatabase con;//链接数据库的链接，通过他可以操作数据库


    public DBUntil(@Nullable Context context) {
        super(context, databaseName, null, version, null);
        this.context = context;
        con = this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL("PRAGMA foreign_keys = false");
        //---------------------------- 所有的数据库操作，在这个位置来写
        db.execSQL("drop table if exists d_user");//创建一个用户表
        //账号，昵称,密码，头像,  权限，地址，性别，听歌时长，曲数 ，注册时间   (可以修改自己账号，与密码）
        db.execSQL("create table d_user(account varchar(20) primary key," + "nickname VARCHAR(50)," + "pwd VARCHAR(50)," + "img VARCHAR(50)," + "pow VARCHAR(50)," +//0 是用户  1是管理员
                "address VARCHAR(50)," + "sex VARCHAR(50)," + "listening_time INTEGER DEFAULT 0," + "song_count INTEGER DEFAULT 0," + "registration_time DATETIME DEFAULT CURRENT_TIMESTAMP" + ")");


        String txPath = FileUntil.saveDrawableToFileSaveAs(context, R.drawable.login);
        String txPathU = FileUntil.saveDrawableToFileSaveAs(context, R.drawable.def_tx);


        String data[] = {"admin", "小莫", "123456", "0", txPath, "深圳", "女"};
        db.execSQL("insert into d_user (account,nickname,pwd,pow,img,address,sex) values(?,?,?,?,?,?,?)", data);
        String data1[] = {"root", "小莫莫", "123456", "1", txPathU, "深圳", "男"};
        db.execSQL("insert into d_user (account,nickname,pwd,pow,img,address,sex) values(?,?,?,?,?,?,?)", data1);
        //--------------------------------
        //创建一个歌单，歌单ID，歌单名字，歌单封面，歌单创建时间，用户ID，是否公开
        //创建一个歌单表  歌单的ID,歌单的名字，歌单的封面，歌单的创建时间，歌单用户ID，      歌单的收藏量
        db.execSQL("drop table if exists d_sheet");
        db.execSQL("create table d_sheet(id varchar(50) primary key," + "name VARCHAR(50)," + "img VARCHAR(50)," + "create_user_id VARCHAR(50)," +//0 是用户  1是管理员
                "sta VARCHAR(50)," +//0未公开 1公开 代表是否公开
                "create_time DATETIME DEFAULT CURRENT_TIMESTAMP" + ")");
        //关系表，这个表代表着，有多个歌单，有同一个用户创建，所以需要一个中间表来关联   id,歌单的ID
        txPathU = FileUntil.saveDrawableToFileSaveAs(context, R.drawable.icon_gd);
        String data2[] = {"1", "民谣歌曲", txPathU, "admin", "1"};//0是只能自己可以见
        db.execSQL("insert into d_sheet (id,name,img,create_user_id,sta) values(?,?,?,?,?)", data2);
        String data3[] = {"2", "流行音乐", txPathU, "admin", "1"};//0是只能自己可以见
        db.execSQL("insert into d_sheet (id,name,img,create_user_id,sta) values(?,?,?,?,?)", data3);


        //创建曲风分类表
        db.execSQL("drop table if exists d_genre");
        db.execSQL("create table d_genre(id varchar(50) primary key," + "name VARCHAR(50)," + "create_time DATETIME DEFAULT CURRENT_TIMESTAMP" + ")");

        //插入默认曲风数据
        db.execSQL("insert into d_genre (id, name) values(?, ?)", new String[]{"1", "流行"});
        db.execSQL("insert into d_genre (id, name) values(?, ?)", new String[]{"2", "摇滚"});
        db.execSQL("insert into d_genre (id, name) values(?, ?)", new String[]{"3", "古典"});
        db.execSQL("insert into d_genre (id, name) values(?, ?)", new String[]{"4", "民谣"});
        db.execSQL("insert into d_genre (id, name) values(?, ?)", new String[]{"5", "电子"});
        db.execSQL("insert into d_genre (id, name) values(?, ?)", new String[]{"6", "爵士"});

        //那个歌单有哪些音乐  id,音乐名字，歌手,音乐路径，音乐图片

        //创建一个歌单表  歌单的ID,歌单的名字，歌单的封面，歌单的创建时间，歌单用户ID，      歌单的收藏量
        db.execSQL("drop table if exists d_music");
        db.execSQL("create table d_music(id varchar(50) primary key," + "name VARCHAR(50)," + "singer VARCHAR(50)," + "img VARCHAR(50)," + "path VARCHAR(50)," + "genre_id VARCHAR(50)," +//曲风ID
                "create_time DATETIME DEFAULT CURRENT_TIMESTAMP" + ")");

        //------------------------上面没问题
        String musicImg = FileUntil.getFileName();
        txPathU = FileUntil.saveMusicThread(context, R.raw.ba_luo_bo, musicImg);
        //通过这个路径需求这个音乐获取这个封面

        String music[] = {"1", "拔萝卜", "momo", musicImg, txPathU, "1"};
        db.execSQL("insert into d_music (id,name,singer,img,path,genre_id) values(?,?,?,?,?,?)", music);

        String musicImg1 = FileUntil.getFileName();
        txPathU = FileUntil.saveMusicThread(context, R.raw.chun_tian_zai_na_li, musicImg1);
        //通过这个路径需求这个音乐获取这个封面

        String music1[] = {"2", "春天在哪里", "momo", musicImg1, txPathU, "4"};
        db.execSQL("insert into d_music (id,name,singer,img,path,genre_id) values(?,?,?,?,?,?)", music1);
        //String musicPath=FileUntil.saveImageBitmapToFileImg(R.drawable.music,context);

        //____________________上面问题
        // ✨ 添加新歌曲 aiwo.mp3
        String musicImg2 = FileUntil.getFileName();
        txPathU = FileUntil.saveMusicThread(context, R.raw.ai_wo_ni_jiu_bao_bao_wo, musicImg2);
        String music2[] = {"3", "爱我你就抱抱我", "momo", musicImg2, txPathU, "3"};  // 根据实际情况修改歌手名字和曲风ID
        db.execSQL("insert into d_music (id,name,singer,img,path,genre_id) values(?,?,?,?,?,?)", music2);
        // ✨ 添加新歌曲
        String musicImg3 = FileUntil.getFileName();
        txPathU = FileUntil.saveMusicThread(context, R.raw.da_gong_ji, musicImg3);
        String music3[] = {"4", "大公鸡", "momo", musicImg3, txPathU, "3"};
        db.execSQL("insert into d_music (id,name,singer,img,path,genre_id) values(?,?,?,?,?,?)", music3);

// ✨ 添加新歌曲
        String musicImg4 = FileUntil.getFileName();
        txPathU = FileUntil.saveMusicThread(context, R.raw.sszymmh, musicImg4);
        String music4[] = {"5", "世上只有momo好", "momo", musicImg4, txPathU, "4"};
        db.execSQL("insert into d_music (id,name,singer,img,path,genre_id) values(?,?,?,?,?,?)", music4);

// ✨✨✨ 添加新歌曲：黑猫警长
        String musicImg5 = FileUntil.getFileName();
        txPathU = FileUntil.saveMusicThread(context, R.raw.hei_mao_jing_zhang, musicImg5);
        String music5[] = {"6", "黑猫警长", "momo", musicImg5, txPathU, "1"};
        db.execSQL("insert into d_music (id,name,singer,img,path,genre_id) values(?,?,?,?,?,?)", music5);

// ✨✨✨ 添加新歌曲：兰花草
        String musicImg6 = FileUntil.getFileName();
        txPathU = FileUntil.saveMusicThread(context, R.raw.lan_hua_cao, musicImg6);
        String music6[] = {"7", "兰花草", "momo", musicImg6, txPathU, "4"};
        db.execSQL("insert into d_music (id,name,singer,img,path,genre_id) values(?,?,?,?,?,?)", music6);

// ✨✨✨ 添加新歌曲：两只老虎
        String musicImg7 = FileUntil.getFileName();
        txPathU = FileUntil.saveMusicThread(context, R.raw.liang_zhi_lao_hu, musicImg7);
        String music7[] = {"8", "两只老虎", "momo", musicImg7, txPathU, "1"};
        db.execSQL("insert into d_music (id,name,singer,img,path,genre_id) values(?,?,?,?,?,?)", music7);

// ✨✨✨ 添加新歌曲：卖报歌
        String musicImg8 = FileUntil.getFileName();
        txPathU = FileUntil.saveMusicThread(context, R.raw.mai_bao_ge, musicImg8);
        String music8[] = {"9", "卖报歌", "momo", musicImg8, txPathU, "1"};
        db.execSQL("insert into d_music (id,name,singer,img,path,genre_id) values(?,?,?,?,?,?)", music8);

// ✨✨✨ 添加新歌曲：卖汤圆
        String musicImg9 = FileUntil.getFileName();
        txPathU = FileUntil.saveMusicThread(context, R.raw.mai_tang_yuan, musicImg9);
        String music9[] = {"10", "卖汤圆", "momo", musicImg9, txPathU, "1"};
        db.execSQL("insert into d_music (id,name,singer,img,path,genre_id) values(?,?,?,?,?,?)", music9);

// ✨✨✨ 添加新歌曲：让我们荡起双桨
        String musicImg10 = FileUntil.getFileName();
        txPathU = FileUntil.saveMusicThread(context, R.raw.rwmdqsj, musicImg10);
        String music10[] = {"11", "让我们荡起双桨", "momo", musicImg10, txPathU, "4"};
        db.execSQL("insert into d_music (id,name,singer,img,path,genre_id) values(?,?,?,?,?,?)", music10);

// ✨✨✨ 添加新歌曲：上学歌
        String musicImg11 = FileUntil.getFileName();
        txPathU = FileUntil.saveMusicThread(context, R.raw.shang_xue_ge, musicImg11);
        String music11[] = {"12", "上学歌", "momo", musicImg11, txPathU, "1"};
        db.execSQL("insert into d_music (id,name,singer,img,path,genre_id) values(?,?,?,?,?,?)", music11);

        //用户删除表单   管理员删除音乐


        //这个歌曲与音乐对应的表单  歌单单id 音乐id
        db.execSQL("drop table if exists d_sheet_music");
        db.execSQL("create table d_sheet_music(id varchar(50) ," + "mid VARCHAR(50)," + "create_time DATETIME DEFAULT CURRENT_TIMESTAMP ," + "PRIMARY KEY (id, mid)" +

                ")");


        db.execSQL("insert into d_sheet_music(id,mid) values(?,?)", new String[]{"1", "1"});
        db.execSQL("insert into d_sheet_music(id,mid) values(?,?)", new String[]{"1", "2"});
        db.execSQL("insert into d_sheet_music(id,mid) values(?,?)", new String[]{"2", "1"});
        // ✨ 添加到歌单1（民谣歌曲）
        db.execSQL("insert into d_sheet_music(id,mid) values(?,?)", new String[]{"1", "3"});
        //他是有一个音乐表的，是当前播放音乐的表，也相当于临时表
        //创建一个歌单表  歌单的ID,歌单的名字，歌单的封面，歌单的创建时间，歌单用户ID，      歌单的收藏量
        db.execSQL("drop table if exists d_music_play");
        db.execSQL("create table d_music_play(id varchar(50)," + "user_id VARCHAR(20)," + "sta VARCHAR(50) DEFAULT '0'," + "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," + "PRIMARY KEY (id, user_id)" + ")");

        db.execSQL("insert into d_music_play(id,user_id) values(?,?)", new String[]{"1", "admin"});
        db.execSQL("insert into d_music_play(id,user_id) values(?,?)", new String[]{"2", "admin"});
        db.execSQL("insert into d_music_play(id,user_id) values(?,?)", new String[]{"3", "admin"});
        db.execSQL("insert into d_music_play(id,user_id) values(?,?)", new String[]{"4", "admin"});
        db.execSQL("insert into d_music_play(id,user_id) values(?,?)", new String[]{"5", "admin"});
// ✨✨✨ 添加新歌曲到播放列表
        db.execSQL("insert into d_music_play(id,user_id) values(?,?)", new String[]{"6", "admin"});
        db.execSQL("insert into d_music_play(id,user_id) values(?,?)", new String[]{"7", "admin"});
        db.execSQL("insert into d_music_play(id,user_id) values(?,?)", new String[]{"8", "admin"});
        db.execSQL("insert into d_music_play(id,user_id) values(?,?)", new String[]{"9", "admin"});
        db.execSQL("insert into d_music_play(id,user_id) values(?,?)", new String[]{"10", "admin"});
        db.execSQL("insert into d_music_play(id,user_id) values(?,?)", new String[]{"11", "admin"});
        db.execSQL("insert into d_music_play(id,user_id) values(?,?)", new String[]{"12", "admin"});


        //创建一个歌单表  歌单的ID,歌单的名字，歌单的封面，歌单的创建时间，歌单用户ID，      歌单的收藏量
        db.execSQL("drop table if exists d_music_like");
        db.execSQL("create table d_music_like(music_id varchar(50)," + "user_id VARCHAR(20)," + "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," + "PRIMARY KEY (music_id, user_id)" + ")");

        db.execSQL("insert into d_music_like(music_id,user_id) values(?,?)", new String[]{"1", "admin"});
        db.execSQL("insert into d_music_like(music_id,user_id) values(?,?)", new String[]{"2", "admin"});
        db.execSQL("insert into d_music_like(music_id,user_id) values(?,?)", new String[]{"3", "admin"});
        db.execSQL("insert into d_music_like(music_id,user_id) values(?,?)", new String[]{"4", "admin"});
        db.execSQL("insert into d_music_like(music_id,user_id) values(?,?)", new String[]{"5", "admin"});
// ✨✨✨ 添加新歌曲到喜欢列表
        db.execSQL("insert into d_music_like(music_id,user_id) values(?,?)", new String[]{"6", "admin"});
        db.execSQL("insert into d_music_like(music_id,user_id) values(?,?)", new String[]{"7", "admin"});
        db.execSQL("insert into d_music_like(music_id,user_id) values(?,?)", new String[]{"8", "admin"});
        db.execSQL("insert into d_music_like(music_id,user_id) values(?,?)", new String[]{"9", "admin"});
        db.execSQL("insert into d_music_like(music_id,user_id) values(?,?)", new String[]{"10", "admin"});
        db.execSQL("insert into d_music_like(music_id,user_id) values(?,?)", new String[]{"11", "admin"});
        db.execSQL("insert into d_music_like(music_id,user_id) values(?,?)", new String[]{"12", "admin"});


        //创建一个歌单表  歌单的ID,歌单的名字，歌单的封面，歌单的创建时间，歌单用户ID，      歌单的收藏量
        db.execSQL("drop table if exists d_music_listen");
        db.execSQL("create table d_music_listen(music_id varchar(50)," + "user_id VARCHAR(20)," + "play_time VARCHAR(50) DEFAULT '0'," + "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," + "PRIMARY KEY (music_id, user_id)" +

                ")");

        db.execSQL("insert into d_music_listen(music_id,user_id) values(?,?)", new String[]{"1", "admin"});
        db.execSQL("insert into d_music_listen(music_id,user_id) values(?,?)", new String[]{"2", "admin"});
        db.execSQL("insert into d_music_listen(music_id,user_id) values(?,?)", new String[]{"3", "admin"});
        db.execSQL("insert into d_music_listen(music_id,user_id) values(?,?)", new String[]{"4", "admin"});
        db.execSQL("insert into d_music_listen(music_id,user_id) values(?,?)", new String[]{"5", "admin"});
// ✨✨✨ 添加新歌曲到听歌记录
        db.execSQL("insert into d_music_listen(music_id,user_id) values(?,?)", new String[]{"6", "admin"});
        db.execSQL("insert into d_music_listen(music_id,user_id) values(?,?)", new String[]{"7", "admin"});
        db.execSQL("insert into d_music_listen(music_id,user_id) values(?,?)", new String[]{"8", "admin"});
        db.execSQL("insert into d_music_listen(music_id,user_id) values(?,?)", new String[]{"9", "admin"});
        db.execSQL("insert into d_music_listen(music_id,user_id) values(?,?)", new String[]{"10", "admin"});
        db.execSQL("insert into d_music_listen(music_id,user_id) values(?,?)", new String[]{"11", "admin"});
        db.execSQL("insert into d_music_listen(music_id,user_id) values(?,?)", new String[]{"12", "admin"});


        //创建帖子表（讨论区）
        db.execSQL("drop table if exists d_post");
        db.execSQL("create table d_post(id varchar(50) primary key," + "user_id VARCHAR(20)," + "music_id VARCHAR(50)," + "content TEXT," + "create_time DATETIME DEFAULT CURRENT_TIMESTAMP" + ")");

        //创建评论表
        db.execSQL("drop table if exists d_comment");
        db.execSQL("create table d_comment(id varchar(50) primary key," + "post_id VARCHAR(50)," + "user_id VARCHAR(20)," + "content TEXT," + "create_time DATETIME DEFAULT CURRENT_TIMESTAMP" + ")");


        db.execSQL("PRAGMA foreign_keys = true");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
