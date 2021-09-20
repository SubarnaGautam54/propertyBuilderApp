package com.example.propertybuilder.Models;

public class BookingDateModel {
    String bookingId;
    String appointmentId;
    int customerImage;
    String customerName;
    String customerID;
    String postID;
    String propertyName;
    String propertyDescription;
    String bookingDate;
    String bookingTime;
    String bookingType;
    String appointmentStatus;

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(String appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    //    public BookingDateModel(String bookingId, int customerImage, String customerName, String propertyName, String propertyDescription, String bookingDate, String bookingTime, String bookingType) {
//        this.bookingId = bookingId;
//        this.customerImage = customerImage;
//        this.customerName = customerName;
//        this.propertyName = propertyName;
//        this.propertyDescription = propertyDescription;
//        this.bookingDate = bookingDate;
//        this.bookingTime = bookingTime;
//        this.bookingType = bookingType;
//    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public int getCustomerImage() {
        return customerImage;
    }

    public void setCustomerImage(int customerImage) {
        this.customerImage = customerImage;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyDescription() {
        return propertyDescription;
    }

    public void setPropertyDescription(String propertyDescription) {
        this.propertyDescription = propertyDescription;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }
}
