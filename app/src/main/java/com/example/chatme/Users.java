package com.example.chatme;

public class Users {
    private String id;
    private String name;
    private String email;
    private int imgAvatar;
    private String urlAvatar;

    public Users() {
    }

    public Users(String id, String name, String email, int imgAvatar, String urlAvatar) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.imgAvatar = imgAvatar;
        this.urlAvatar = urlAvatar;
    }

    public String getUrlAvatar() {
        return urlAvatar;
    }

    public void setUrlAvatar(String urlAvatar) {
        this.urlAvatar = urlAvatar;
    }

    public int getImgAvatar() {
        return imgAvatar;
    }

    public void setImgAvatar(int imgAvatar) {
        this.imgAvatar = imgAvatar;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
