package com.example.propertybuilder.Models;

public class UserDataModel {
    String id;
    String developerId;
    String postName;
    String developerPostName;
    String developerPostDetail;
    String lat;
    String lng;

    int developerProfileImage;

//    public UserDataModel(String id, String developerName, String developerPostName, String developerPostDetail, int developerProfileImage) {
//        this.id = id;
//        this.developerName = developerName;
//        this.developerPostName = developerPostName;
//        this.developerPostDetail = developerPostDetail;
//        this.developerProfileImage = developerProfileImage;
//    }


    public String getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(String developerId) {
        this.developerId = developerId;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getDeveloperPostName() {
        return developerPostName;
    }

    public void setDeveloperPostName(String developerPostName) {
        this.developerPostName = developerPostName;
    }

    public String getPostId() {
        return id;
    }

    public void setPostId(String id) {
        this.id = id;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getDeveloperPostDetail() {
        return developerPostDetail;
    }

    public void setDeveloperPostDetail(String developerPostDetail) {
        this.developerPostDetail = developerPostDetail;
    }

    public int getDeveloperProfileImage() {
        return developerProfileImage;
    }

    public void setDeveloperProfileImage(int developerProfileImage) {
        this.developerProfileImage = developerProfileImage;
    }

}
