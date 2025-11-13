package com.cool.music.bean;

/**
 * 帖子实体类
 */
public class PostBean {
    private String id;
    private String user_id;
    private String music_id;
    private String content;
    private String create_time;

    public PostBean() {
    }

    public PostBean(String id, String user_id, String music_id, String content, String create_time) {
        this.id = id;
        this.user_id = user_id;
        this.music_id = music_id;
        this.content = content;
        this.create_time = create_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMusic_id() {
        return music_id;
    }

    public void setMusic_id(String music_id) {
        this.music_id = music_id;
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
