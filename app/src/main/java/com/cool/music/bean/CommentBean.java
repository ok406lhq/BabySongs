package com.cool.music.bean;

/**
 * 评论实体类
 */
public class CommentBean {
    private String id;
    private String post_id;
    private String user_id;
    private String content;
    private String create_time;

    public CommentBean() {
    }

    public CommentBean(String id, String post_id, String user_id, String content, String create_time) {
        this.id = id;
        this.post_id = post_id;
        this.user_id = user_id;
        this.content = content;
        this.create_time = create_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
