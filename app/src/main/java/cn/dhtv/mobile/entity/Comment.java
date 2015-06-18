package cn.dhtv.mobile.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Jack on 2015/6/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {
    int cid;
    String message;
    String dateline;
    int status;
    String title;
    String user;

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
