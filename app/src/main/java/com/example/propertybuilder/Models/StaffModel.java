package com.example.propertybuilder.Models;

public class StaffModel {
    String staffId;
    String staffImage;
    String staffName;
    String staffAddress;
    String staffPhone;
    String staffEmail;
    String staffPassword;
    String staffType;
    String staffTypeStatus;

    public String getStaffTypeStatus() {
        return staffTypeStatus;
    }

    public void setStaffTypeStatus(String staffTypeStatus) {
        this.staffTypeStatus = staffTypeStatus;
    }

    //    public StaffModel(String staffId, int staffImage, String staffName, String staffAddress, String staffEmail, String staffPassword) {
//        this.staffId = staffId;
//        this.staffImage = staffImage;
//        this.staffName = staffName;
//        this.staffAddress = staffAddress;
//        this.staffEmail = staffEmail;
//        this.staffPassword = staffPassword;
//    }


    public String getStaffPhone() {
        return staffPhone;
    }

    public void setStaffPhone(String staffPhone) {
        this.staffPhone = staffPhone;
    }

    public String getStaffType() {
        return staffType;
    }

    public void setStaffType(String staffType) {
        this.staffType = staffType;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getStaffImage() {
        return staffImage;
    }

    public void setStaffImage(String staffImage) {
        this.staffImage = staffImage;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getStaffAddress() {
        return staffAddress;
    }

    public void setStaffAddress(String staffAddress) {
        this.staffAddress = staffAddress;
    }

    public String getStaffEmail() {
        return staffEmail;
    }

    public void setStaffEmail(String staffEmail) {
        this.staffEmail = staffEmail;
    }

    public String getStaffPassword() {
        return staffPassword;
    }

    public void setStaffPassword(String staffPassword) {
        this.staffPassword = staffPassword;
    }
}
