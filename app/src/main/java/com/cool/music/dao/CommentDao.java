package com.cool.music.dao;

import com.cool.music.bean.CommentBean;
import com.cool.music.until.SqliteT;

import java.util.List;

/**
 * 评论数据访问类
 */
public class CommentDao {

    /**
     * 添加评论
     * @param id
     * @param postId
     * @param userId
     * @param content
     * @return
     */
    public static int addComment(String id, String postId, String userId, String content) {
        String sql = "insert into d_comment (id, post_id, user_id, content) values(?, ?, ?, ?)";
        return SqliteT.update(sql, id, postId, userId, content);
    }

    /**
     * 删除评论
     * @param id
     * @return
     */
    public static int deleteComment(String id) {
        String sql = "delete from d_comment where id = ?";
        return SqliteT.update(sql, id);
    }

    /**
     * 根据帖子ID查询所有评论
     * @param postId
     * @return
     */
    public static List<CommentBean> getCommentsByPostId(String postId) {
        String sql = "select * from d_comment where post_id = ? order by create_time asc";
        return SqliteT.query(sql, CommentBean.class, postId);
    }

    /**
     * 根据用户ID查询评论
     * @param userId
     * @return
     */
    public static List<CommentBean> getCommentsByUserId(String userId) {
        String sql = "select * from d_comment where user_id = ? order by create_time desc";
        return SqliteT.query(sql, CommentBean.class, userId);
    }

    /**
     * 获取帖子的评论数量
     * @param postId
     * @return
     */
    public static int getCommentCount(String postId) {
        String sql = "select count(*) as count from d_comment where post_id = ?";
        List<CommentBean> list = SqliteT.query(sql, CommentBean.class, postId);
        return list != null ? list.size() : 0;
    }
}
