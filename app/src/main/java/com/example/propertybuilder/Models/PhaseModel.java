package com.example.propertybuilder.Models;

public class PhaseModel {
    String phaseId;
    String postId;
    String phaseImages;
    String phaseDec;
    String phaseName;
    String phaseVideo;

//    public PhaseModel(String phaseId, String phaseImages, String phaseDec) {
//        this.phaseId = phaseId;
//        this.phaseImages = phaseImages;
//        this.phaseDec = phaseDec;
//    }


    public String getPhaseName() {
        return phaseName;
    }

    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }

    public String getPhaseVideo() {
        return phaseVideo;
    }

    public void setPhaseVideo(String phaseVideo) {
        this.phaseVideo = phaseVideo;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(String phaseId) {
        this.phaseId = phaseId;
    }

    public String getPhaseImages() {
        return phaseImages;
    }

    public void setPhaseImages(String phaseImages) {
        this.phaseImages = phaseImages;
    }

    public String getPhaseDec() {
        return phaseDec;
    }

    public void setPhaseDec(String phaseDec) {
        this.phaseDec = phaseDec;
    }
}
