package com.intech.yayananies.Models;

import com.google.firebase.firestore.FieldValue;

import java.util.Date;

public class EmployerData {
    private String Name, ID_no, Street_name, City, County, Email, Phone_NO, device_token,UserImage, User_ID;
    private long  SelectionCount,CandidatesCount;
    private Date timestamp;

    public EmployerData() {
        //---empty----
    }


    public EmployerData(String name, String ID_no, String street_name, String city, String county, String email, String phone_NO,
                        String device_token, String userImage, String user_ID, long selectionCount, long candidatesCount, Date timestamp) {
        Name = name;
        this.ID_no = ID_no;
        Street_name = street_name;
        City = city;
        County = county;
        Email = email;
        Phone_NO = phone_NO;
        this.device_token = device_token;
        UserImage = userImage;
        User_ID = user_ID;
        SelectionCount = selectionCount;
        CandidatesCount = candidatesCount;
        this.timestamp = timestamp;
    }


    public String getName() {
        return Name;
    }

    public String getID_no() {
        return ID_no;
    }

    public String getStreet_name() {
        return Street_name;
    }

    public String getCity() {
        return City;
    }

    public String getCounty() {
        return County;
    }

    public String getEmail() {
        return Email;
    }

    public String getPhone_NO() {
        return Phone_NO;
    }

    public String getDevice_token() {
        return device_token;
    }

    public String getUserImage() {
        return UserImage;
    }

    public String getUser_ID() {
        return User_ID;
    }

    public long getSelectionCount() {
        return SelectionCount;
    }

    public long getCandidatesCount() {
        return CandidatesCount;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
