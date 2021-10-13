package com.trignsoft.propertybuilder.Models;

public class PostCommentModel {
    private String id;
    private String userId;
    private String userName;
    private String userComment;
    private String userVideo;

    public String getUserVideo() {
        return userVideo;
    }

    public void setUserVideo(String userVideo) {
        this.userVideo = userVideo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }
}
