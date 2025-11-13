package com.cool.music.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cool.music.bean.MusicBean;
import com.cool.music.bean.PlayMusicBean;
import com.cool.music.until.DBUntil;
import com.cool.music.until.SqliteT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PlayMusicDao {

    public static SQLiteDatabase db= DBUntil.con;


    /**
     * 每次获取5个随机的音乐列表，然后将其打乱进行返回
     * @return
     */
    public static List<PlayMusicBean> getPlayMusic() {//一次只是获取5个随机的音乐
        String sql = "select * from d_sheet where sta='1'";
        List<PlayMusicBean> list = SqliteT.query(sql, PlayMusicBean.class);

        List<PlayMusicBean> randomMusicList =new ArrayList<>(5);
        // 打乱列表
        Collections.shuffle(list);
        // 添加前5个音乐到新的列表中
        int len=list.size();
        if(len<5){
            len=list.size();
        }else{
            len=5;
        }
        for (int i = 0; i < len; i++) {
            randomMusicList.add(list.get(i));
        }
        return randomMusicList;//代表账号密码错误
    }

    /**
     * 通过歌单ID，来获取这个表单里面有多少数量的夤夜
     * @param id
     * @return
     */

    public static String getPlayMusicCount(String id){
        String sql="select * from d_sheet_music where id=?";
        List<Map<String, String>> list = SqliteT.queryMapList(sql, id);
        return String.valueOf(list.size());
    }


    /**
     * 获取歌单里面的音乐
     * @param id
     * @return
     */

    public static List<MusicBean> getPlayMusicOnSheetCount(String id){
        String sql="select * from d_sheet_music where id=?";
        List<Map<String, String>> list = SqliteT.queryMapList(sql, id);

        List<MusicBean> list2=new ArrayList<>();
        for (Map<String, String> map : list) {
            String mid = map.get("mid");
            String sql1="select * from d_music where id=?";
            List<MusicBean> list1 = SqliteT.query(sql1, MusicBean.class, mid);
            if(list1!=null&&list1.size()>0){
                list2.add(list1.get(0));
            }

        }
        return list2;
    }

    /**
     * 账号和音乐ID,添加用户的临时播放表
     * @param uid
     * @param mid
     */

    public static void insertPlayMusic(String uid,String mid){
        String sql="insert into d_music_play(id,user_id) values(?,?)";
        SqliteT.update(sql,mid,uid);
    }


    /**
     * 获取当前用户的播放列表
     * @param uid
     * @return
     */
    public static List<MusicBean>  getCurrentUserPlayMusic(String uid){
        String sql="SELECT * FROM d_music_play where user_id=?";
        List<Map<String, String>> list = SqliteT.queryMapList(sql, uid);

        List<MusicBean> list2=new ArrayList<>();
        for (Map<String, String> stringStringMap : list) {
            String mid=stringStringMap.get("id");//获取音乐ID
            String sql1="select * from d_music where id=?";
            List<MusicBean> list1 = SqliteT.query(sql1, MusicBean.class, mid);
            if(list1!=null&&list1.size()>0){
                list2.add(list1.get(0));
            }
        }
        return list2;
    }


    /**
     * 获取当前用户的播放列表
     * @param uid
     * @return
     */
    public static void  updateCurrentUserPlayMusicSta(String uid,String sta){
        String sql="update d_music_play set sta=? where user_id=?";
        SqliteT.update(sql, sta,uid);
    }

    /**
     * 获取当前用户的播放列表
     * @param uid
     * @return
     */
    public static void  updateCurrentUserPlayMusicSta(String uid,String mid,String sta){
        String sql="update d_music_play set sta=? where user_id=? and id=?";
        SqliteT.update(sql, sta,uid,mid);
    }


    /**
     * 获取状态值，当前用户播放的夤夜
     * @param uid
     * @param sta
     */
    public static MusicBean  getCurrentUserPlayMusic(String uid,String sta){
        String sql="select * from d_music_play where user_id=? and sta=?";
        List<Map<String, String>> list = SqliteT.queryMapList(sql, uid, sta);
        if( list!=null&& list.size()>0){
            Map<String, String> map = list.get(0);
            String id=map.get("id");
            String sql1="select * from d_music where id=?";
            List<MusicBean> list1 = SqliteT.query(sql1, MusicBean.class, id);
            if(list1!=null&&list1.size()>0){
                return list1.get(0);
            }

        }
        return null;
    }

    /**
     * 清空用户播放列表
     * @param account
     */
    public static void clearUserPlaylist(String account) {
        String sql = "DELETE FROM d_music_play WHERE user_id = ?";
        SqliteT.update(sql, account);
    }

    /**
     * 添加歌曲到播放列表
     * @param account
     * @param musicId
     */
    public static void addToPlaylist(String account, String musicId) {
        try {
            String sql = "INSERT OR REPLACE INTO d_music_play(id, user_id, sta) VALUES(?, ?, '0')";
            db.execSQL(sql, new String[]{musicId, account});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
