package com.cool.music.dao;

import com.cool.music.bean.MusicBean;
import com.cool.music.until.SqliteT;

import java.util.List;
import java.util.Map;

public class MusicDao {
    /**
     * 更改音乐
     * @param id
     * @return
     */
    public static int updateMusicImg(String id,String img)
    {
        String sql="update  d_music set img=? where  id=?";
        return SqliteT.update(sql, img, id);
    }

    public static int addMusic(String id,String name,String singer,String img,String path){
        String sql="insert into d_music (id,name,singer,img,path) values(?,?,?,?,?)";
        return SqliteT.update(sql,id,name,singer,img,path);
    }

    public static int addMusic(String id,String name,String singer,String img,String path,String genreId){
        String sql="insert into d_music (id,name,singer,img,path,genre_id) values(?,?,?,?,?,?)";
        return SqliteT.update(sql,id,name,singer,img,path,genreId);
    }


    /**
     * 这个都完全选择的内容
     * @param id
     * @param name
     * @param singer
     * @param img
     * @param path
     * @return
     */
    public static int updateMusic(String id,String name,String singer,String img,String path){
        // 使用 UPDATE 语句来更新指定 ID 的音乐记录
        String sql = "UPDATE d_music SET name = ?, singer = ?, img = ?, path = ? WHERE id = ?";

        // 注意：参数的顺序很重要，它们应该与 SQL 语句中的 '?' 占位符对应
        return SqliteT.update(sql, name, singer, img, path, id);


    }

    /**
     * 选择部分
     * @param id
     * @param name
     * @param singer
     * @return
     */
    public static int updateMusic(String id,String name,String singer){
        // 使用 UPDATE 语句来更新指定 ID 的音乐记录
        String sql = "UPDATE d_music SET name = ?, singer = ?  WHERE id = ?";

        // 注意：参数的顺序很重要，它们应该与 SQL 语句中的 '?' 占位符对应
        return SqliteT.update(sql, name, singer,  id);


    }



    /**
     * 查询音乐
     * @param id
     * @return
     */
    public static MusicBean getMusicById(String id)
    {
        String sql="select * from d_music where id=?";
        return SqliteT.queryOne(sql, MusicBean.class, id);

    }
    /**
     * 查询音乐
     * @param id
     * @return
     */
    public static int deleteMusicById(String id)
    {
        String sql="delete from d_music where id=?";
        int a=SqliteT.update(sql,  id);
        if(a==1){
            DeleteDao.delData();
        }
        return a;

    }

    /**
     * 查询音乐
     * @return
     */
    public static List<MusicBean> getMusicAll()
    {
        String sql="select * from d_music";
        return SqliteT.query(sql, MusicBean.class);

    }








    /**
     * 通过歌名和歌手查询
     * @param name
     * @return
     */
    public static List<MusicBean> getMusicAll(String name)
    {
        if(name.equals("")){
            return getMusicAll();
        }
        name="%"+name+"%";
        String sql="select * from d_music where name like ? or singer like ?";
        return SqliteT.query(sql, MusicBean.class, name,name);

    }

    /**
     * 查询喜欢的音乐
     * @param uid
     * @param musicId
     * @return
     */
    public static int  getLikeMusic(String uid,String musicId)
    {
        String sql="select * from d_music_like where user_id=? and music_id=?";
        List<Map<String, String>> map = SqliteT.queryMapList(sql, uid, musicId);
        if(map!=null&&map.size()>0){
            return 1;
        }
        return 0;

    }


    /**
     * 查询喜欢的音乐
     * @param uid
     * @return
     */
    public static List<MusicBean>  getLikeMusicAll(String uid,String name)
    {
        if(name==null||name.equals("")){//只是查询当前用户喜欢的内容
            String sql="select d_music .* from d_music_like left join d_music on d_music.id=d_music_like.music_id where d_music_like.user_id=?";
            List<MusicBean> list = SqliteT.query(sql, MusicBean.class,uid);
            return list;
        }else{

            name="%"+name+"%";
            String sql="select d_music .* from d_music_like left join d_music on d_music.id=d_music_like.music_id where d_music_like.user_id=?  " +
                    "and (d_music.name like ?  or  d_music.singer like ?)";
            List<MusicBean> list = SqliteT.query(sql, MusicBean.class,uid, name, name);
            return list;


        }




    }


    /**
     * 取消喜欢
     * @param uid
     * @param musicId
     * @return
     */
    public static int  cancelLikeMusic(String uid,String musicId)
    {
        String sql="delete from d_music_like where user_id=? and music_id=?";
        int a=  SqliteT.update(sql, uid, musicId);
        if(a==1){
            DeleteDao.delData();
        }
        return a;
    }

    /**
     * 喜欢
     * @param uid
     * @param musicId
     * @return
     */
    public static int likeMusic(String uid,String musicId)
    {
        String sql="insert into d_music_like(user_id,music_id) values(?,?)";
        return  SqliteT.update(sql, uid, musicId);
    }


    /**
     * 添加这个用户听过多少音乐
     * @param uid
     * @param musicId
     * @return
     */

    public static int addListenMusic(String uid,String musicId,int time)
    {
        String sql="insert into d_music_listen(user_id,music_id,play_time) values(?,?,?)";
        int sta=SqliteT.update(sql, uid, musicId,String.valueOf(time));
        if(sta==0){
            sql="update d_music_listen set play_time=play_time+? where user_id=? and music_id=?";
            sta=SqliteT.update(sql,String.valueOf(time), uid, musicId);
        }
        UserDao.updateUserListenTime(uid);
        //时刻更新用户表听歌时长和数量
        return sta;
    }


    /**
     * 查询音乐榜单（按播放时长）
     * @return
     */
    public static List<MusicBean>  getMusicRanking()
    {
        String sql="select d_music.* from (select music_id , sum(play_time) time from d_music_listen group by music_id  order by time desc) b left join d_music on  d_music.id=b.music_id";
        List<MusicBean> list = SqliteT.query(sql, MusicBean.class);
        return list;
    }

    /**
     * 查询音乐热榜（按收藏量排序，返回前5首）
     * @return
     */
    public static List<MusicBean> getMusicHotRanking()
    {
        String sql="select d_music.* from (select music_id, count(*) as like_count from d_music_like group by music_id order by like_count desc limit 30) b left join d_music on d_music.id=b.music_id";
        List<MusicBean> list = SqliteT.query(sql, MusicBean.class);
        return list;
    }

    /**
     * 根据曲风ID查询音乐
     * @param genreId
     * @return
     */
    public static List<MusicBean> getMusicByGenre(String genreId) {
        String sql = "select * from d_music where genre_id = ?";
        return SqliteT.query(sql, MusicBean.class, genreId);
    }

    /**
     * 根据曲风ID和名称搜索音乐
     * @param genreId
     * @param name
     * @return
     */
    public static List<MusicBean> getMusicByGenre(String genreId, String name) {
        if (name == null || name.equals("")) {
            return getMusicByGenre(genreId);
        }
        name = "%" + name + "%";
        String sql = "select * from d_music where genre_id = ? and (name like ? or singer like ?)";
        return SqliteT.query(sql, MusicBean.class, genreId, name, name);
    }


}
