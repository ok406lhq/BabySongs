package com.cool.music.dao;

import com.cool.music.bean.PostBean;
import com.cool.music.until.SqliteT;

import java.util.List;

/**
 * 帖子数据访问类
 */
public class PostDao {

    /**
     * 添加帖子
     * @param id
     * @param userId
     * @param musicId
     * @param content
     * @return
     */
    public static int addPost(String id, String userId, String musicId, String content) {
        String sql = "insert into d_post (id, user_id, music_id, content) values(?, ?, ?, ?)";
        return SqliteT.update(sql, id, userId, musicId, content);
    }

    /**
     * 删除帖子
     * @param id
     * @return
     */
    public static int deletePost(String id) {
        String sql = "delete from d_post where id = ?";
        return SqliteT.update(sql, id);
    }

    /**
     * 根据ID查询帖子
     * @param id
     * @return
     */
    public static PostBean getPostById(String id) {
        String sql = "select * from d_post where id = ?";
        return SqliteT.queryOne(sql, PostBean.class, id);
    }

    /**
     * 查询所有帖子（按时间倒序）
     * @return
     */
    public static List<PostBean> getAllPosts() {
        String sql = "select * from d_post order by create_time desc";
        return SqliteT.query(sql, PostBean.class);
    }

    /**
     * 根据用户ID查询帖子
     * @param userId
     * @return
     */
    public static List<PostBean> getPostsByUserId(String userId) {
        String sql = "select * from d_post where user_id = ? order by create_time desc";
        return SqliteT.query(sql, PostBean.class, userId);
    }

    /**
     * 根据音乐ID查询帖子
     * @param musicId
     * @return
     */
    public static List<PostBean> getPostsByMusicId(String musicId) {
        String sql = "select * from d_post where music_id = ? order by create_time desc";
        return SqliteT.query(sql, PostBean.class, musicId);
    }
}
