package com.cool.music.bean;

public class UserBean {





    private String account;

    private String nickname;


    private String pwd;

    private String img;

    private String pow;

    private String address;

    private String sex;

    private String listening_time;

    private String song_count;

    private String registration_time;


    public UserBean(String account, String nickname, String pwd, String img, String pow, String address, String sex, String listening_time, String song_count, String registration_time) {
        this.account = account;
        this.nickname = nickname;
        this.pwd = pwd;
        this.img = img;
        this.pow = pow;
        this.address = address;
        this.sex = sex;
        this.listening_time = listening_time;
        this.song_count = song_count;
        this.registration_time = registration_time;
    }

    public UserBean() {
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPow() {
        return pow;
    }

    public void setPow(String pow) {
        this.pow = pow;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getListening_time() {
        return listening_time;
    }

    public void setListening_time(String listening_time) {
        this.listening_time = listening_time;
    }

    public String getSong_count() {
        return song_count;
    }

    public void setSong_count(String song_count) {
        this.song_count = song_count;
    }

    public String getRegistration_time() {
        return registration_time;
    }

    public void setRegistration_time(String registration_time) {
        this.registration_time = registration_time;
    }
}
