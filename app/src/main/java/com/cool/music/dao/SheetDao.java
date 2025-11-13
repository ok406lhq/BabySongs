package com.cool.music.dao;

import com.cool.music.bean.SheetBean;
import com.cool.music.until.SqliteT;

import java.util.List;
import java.util.Map;

public class SheetDao {


    /**
     * 查询当前用户的所有的歌单
     * @param id
     * @return
     */
    public static List<SheetBean> getSheetById(String id){
        String sql="select * from d_sheet where create_user_id=?";
        return SqliteT.query(sql,SheetBean.class,id);
    }

    /**
     * 查询表单
     * @param id
     * @return
     */
    public static SheetBean getSheet(String id){
        String sql="select * from d_sheet where id=?";
        return SqliteT.queryOne(sql,SheetBean.class,id);
    }

    /**
     * 查询苏欧阳
     * @return
     */

    public static List<SheetBean> getSheet(){
        String sql="select * from d_sheet";
        return SqliteT.query(sql,SheetBean.class);
    }



    /**
     * 更改歌单
     * @param id
     * @param name
     * @param sta
     * @return
     */
    public static int updateSheet(String id,String name,String sta){
        String sql="update d_sheet set name=?,sta=? where id=?";
        return SqliteT.update(sql,name,sta,id);
    }

    /**
     * 更改状态歌单
     * @param id
     * @param sta
     * @return
     */
    public static int updateSheetSta(String id,String sta){
        String sql="update d_sheet set sta=? where id=?";
        return SqliteT.update(sql,sta,id);
    }


    /**
     *
     * @param id
     * @param name
     * @param img
     * @param create_user_id
     * @param sta
     * @return
     */
    public static int addSheet(String id,String name,String img,String create_user_id,String sta){
 /*       db.execSQL("drop table if exists d_sheet");
        db.execSQL("create table d_sheet(id varchar(50) primary key," +
                "name VARCHAR(50)," +
                "img VARCHAR(50)," +
                "create_user_id VARCHAR(50)," +//0 是用户  1是管理员
                "sta VARCHAR(50)," +//0未公开 1公开 代表是否公开
                "create_time DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")");*/
        String sql="insert into d_sheet(id,name,img,create_user_id,sta) values(?,?,?,?,?)";
        return SqliteT.update(sql,id,name,img,create_user_id,sta);
    }

    public static int updateSheet(String id,String path){
 /*       db.execSQL("drop table if exists d_sheet");
        db.execSQL("create table d_sheet(id varchar(50) primary key," +
                "name VARCHAR(50)," +
                "img VARCHAR(50)," +
                "create_user_id VARCHAR(50)," +//0 是用户  1是管理员
                "sta VARCHAR(50)," +//0未公开 1公开 代表是否公开
                "create_time DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")");*/
        String sql="update d_sheet set img=? where id=?";
        return SqliteT.update(sql,path,id);
    }
    /**
     * 删除表单里面歌曲
     * @param id
     * @param mid
     * @return
     */

    public static int deleteSheet(String id,String mid){

        String sql="delete from d_sheet_music where id=? and mid=?";
        int a= SqliteT.update(sql,id,mid);
        if(a==1){
            DeleteDao.delData();
        }
        return a;
    }

    public static int deleteSheetById(String id){

        String sql="delete from d_sheet where id=?";
        int a=SqliteT.update(sql,id);
        if(a==1){
            DeleteDao.delData();
        }
        return a;
    }

    /**
     * 向表单里面添加夤夜
     * @param id
     * @param mid
     * @return
     */
    public static int addSheet(String id,String mid){

        String sql="insert into d_sheet_music(id,mid) values(?,?)";
        return SqliteT.update(sql,id,mid);
    }

    /**
     * 查询
     * @param id
     * @param mid
     * @return
     */
    public static int isSheetHaveMusic(String id,String mid){

        String sql="select * from d_sheet_music where id=? and mid=?";
        List<Map<String, String>> list = SqliteT.queryMapList(sql, id, mid);
        if(list==null||list.size()==0){
            return 0;
        }else{
            return 1;
        }

    }
}
