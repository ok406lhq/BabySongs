package com.cool.music.bean;

/**
 * 曲风分类实体类
 */
public class GenreBean {

    private String id;
    private String name;  // 曲风名称（如：摇滚、流行、古典等）
    private String create_time;

    public GenreBean() {
    }

    public GenreBean(String id, String name, String create_time) {
        this.id = id;
        this.name = name;
        this.create_time = create_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
