package com.intech.yayananies.Models;

import java.util.Date;

public class Bureau {


        private String Name, Bureau_Name, ID_no, Building, Street_name, City,User_ID,
                County, Email, Box_No, Postal_code,Phone_NO, device_token, RegistrationFee,Bureau_Image,mpesa_receipt;
        private long No_of_candidates;
        private Date timestamp;

    public Bureau() {
        //empty
    }


    public Bureau(String name, String bureau_Name, String ID_no, String building, String street_name, String city, String user_ID, String county, String email, String box_No, String postal_code, String phone_NO,
                  String device_token, String registrationFee, String bureau_Image, String mpesa_receipt, long no_of_candidates, Date timestamp) {
        Name = name;
        Bureau_Name = bureau_Name;
        this.ID_no = ID_no;
        Building = building;
        Street_name = street_name;
        City = city;
        User_ID = user_ID;
        County = county;
        Email = email;
        Box_No = box_No;
        Postal_code = postal_code;
        Phone_NO = phone_NO;
        this.device_token = device_token;
        RegistrationFee = registrationFee;
        Bureau_Image = bureau_Image;
        this.mpesa_receipt = mpesa_receipt;
        No_of_candidates = no_of_candidates;
        this.timestamp = timestamp;
    }

    public String getUser_ID() {
        return User_ID;
    }

    public String getMpesa_receipt() {
        return mpesa_receipt;
    }

    public String getBureau_Image() {
        return Bureau_Image;
    }

    public void setBureau_Image(String bureau_Image) {
        Bureau_Image = bureau_Image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getBureau_Name() {
        return Bureau_Name;
    }

    public void setBureau_Name(String bureau_Name) {
        Bureau_Name = bureau_Name;
    }

    public String getID_no() {
        return ID_no;
    }

    public void setID_no(String ID_no) {
        this.ID_no = ID_no;
    }

    public String getBuilding() {
        return Building;
    }

    public void setBuilding(String building) {
        Building = building;
    }

    public String getStreet_name() {
        return Street_name;
    }

    public void setStreet_name(String street_name) {
        Street_name = street_name;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getCounty() {
        return County;
    }

    public void setCounty(String county) {
        County = county;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getBox_No() {
        return Box_No;
    }

    public void setBox_No(String box_No) {
        Box_No = box_No;
    }

    public String getPostal_code() {
        return Postal_code;
    }

    public void setPostal_code(String postal_code) {
        Postal_code = postal_code;
    }

    public String getPhone_NO() {
        return Phone_NO;
    }

    public void setPhone_NO(String phone_NO) {
        Phone_NO = phone_NO;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getRegistrationFee() {
        return RegistrationFee;
    }

    public void setRegistrationFee(String registrationFee) {
        RegistrationFee = registrationFee;
    }

    public long getNo_of_candidates() {
        return No_of_candidates;
    }

    public void setNo_of_candidates(long no_of_candidates) {
        No_of_candidates = no_of_candidates;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
