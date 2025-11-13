package com.cool.music.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cool.music.bean.UserBean;
import com.cool.music.until.DBUntil;
import com.cool.music.until.SqliteT;

import java.util.List;
import java.util.Map;

public class UserDao {
    public static SQLiteDatabase db=DBUntil.con;
    /**
     * 输入账号和密码来判断是否可以登录，区分管理员和用户
     * @param account
     * @param pwd
     * @return
     */
    public static int isLogin(String account,String pwd) {

        String data[] = {account, pwd};
        String sql = "select * from d_user where account=? and pwd=?";
        Cursor result = db.rawQuery(sql, data);
        if(result.moveToNext()){
            int powIndex = result.getColumnIndex("pow");
            // 打印查询到的内容
            // 获取列值
            int pow = result.getInt(powIndex);
            Log.d("登录权限:", "pow: " + pow);
            return  pow;
        }

        return -1;//代表账号密码错误
    }


    /**
     * 查询用户信息
     * @param account
     * @return
     */
    public static UserBean getUserById(String account) {
        String sql = "select * from d_user where account=?";
        UserBean user = SqliteT.queryOne(sql, UserBean.class, account);

        return user;//代表账号密码错误
    }


    /**
     * 查询用户信息
     * @return
     */
    public static List<UserBean> getUserAll() {
        String sql = "select * from d_user";
        List<UserBean> list = SqliteT.query(sql, UserBean.class);
        return list;//代表账号密码错误
    }



    /**
     * 时刻更新用户歌曲数量和市场
     * @param account
     */
    public static void updateUserListenTime(String account){
        String sql="select user_id,music_id,sum(play_time)  time  from d_music_listen where user_id=? group  by user_id , music_id ";
        List<Map<String,String>> list=SqliteT.queryMapList(sql,account);
        int songCount=0;//歌曲数量
        int songTime=0;//听歌的总时间
        if(list!=null&&list.size()>0){
            songCount=list.size();
            for (Map<String, String> stringStringMap : list) {
                int time=Integer.valueOf(stringStringMap.get("time"));
                songTime+=time;
            }
        }


        sql="update d_user set listening_time=?,song_count=? where account=?";
        SqliteT.update(sql, String.valueOf(songTime), String.valueOf(songCount), account);
    }
    /**
     * 这个是注册账号
     * @param account
     * @param pwd
     * @param nickName
     * @param img
     * @param address
     * @param sex
     * @return
     */
    public static int register(String account,String pwd,String nickName,String img,String address,String sex){
        String data[]={account,pwd,nickName,img,address,sex,"0"};
        String sql="insert into d_user(account,pwd,nickName,img,address,sex,pow) values(?,?,?,?,?,?,?)";
        try {
            db.execSQL(sql,data);
            return 1;
        }catch (Exception e){
            return 0;
        }

    }

    /**
     * 更改用户信息
     * @param userId
     * @param pwd
     * @param nickName
     * @param img
     * @param address
     * @param sex
     * @return
     */
    public static int updateUserInfoById(String userId, String pwd, String nickName, String img, String address, String sex) {
        String[] data = {pwd, nickName, img, address, sex, userId};
        String sql = "UPDATE d_user SET pwd=?, nickName=?, img=?, address=?, sex=? WHERE account=?";
        return  SqliteT.update(sql, data);

    }

    /**
     * 获取用户昵称
     * @param account
     * @return
     */
    public static String getUserNickname(String account) {
        UserBean user = getUserById(account);
        return user != null ? user.getNickname() : null;
    }


}
