package com.example.chatme;

public class ChatMe {

    private String text;
    private String name;
    private String imageUrl;
    private String recipient;
    private String sender;
    private boolean isMine;

    public ChatMe() {
    }

    public ChatMe(String text, String name, String imageUrl, String recipient, String sender, boolean isMine) {
        this.text = text;
        this.name = name;
        this.imageUrl = imageUrl;
        this.recipient = recipient;
        this.sender = sender;
        this.isMine = isMine;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
