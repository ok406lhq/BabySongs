package com.cool.music.bean;

import java.io.Serializable;

public class MusicBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String singer;
    private String img;
    private String path;
    private String genre_id;
    private String create_time;

    public MusicBean(String id, String name, String singer, String img, String path, String genre_id, String create_time) {
        this.id = id;
        this.name = name;
        this.singer = singer;
        this.img = img;
        this.path = path;
        this.genre_id = genre_id;
        this.create_time = create_time;
    }

    public MusicBean() {
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

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getGenre_id() {
        return genre_id;
    }

    public void setGenre_id(String genre_id) {
        this.genre_id = genre_id;
    }
}
