package com.intech.yayananies.Models;

import java.util.Date;

public class Notification {
    private String title,desc,from,to,type;
    private Date timestamp;

    public Notification() {
    //empty----
    }

    public Notification(String title, String desc, String from, String to, String type, Date timestamp) {
        this.title = title;
        this.desc = desc;
        this.from = from;
        this.to = to;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }
}
