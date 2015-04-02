package cn.dhtv.mobile.entity;

import java.io.Serializable;

/**
 * Created by Jack on 2015/1/21.
 */
public class Category implements Serializable {
    public static final String URL = "http://api.dhtv.cn/category/";

    private int upid;
    private String catname;
    private String description;
    private String name;
    private int level;
    private int topid;
    private int catid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCatid() {
        return catid;
    }

    public void setCatid(int catid) {
        this.catid = catid;
    }

    public int getTopid() {
        return topid;
    }

    public void setTopid(int topid) {
        this.topid = topid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCatname() {
        return catname;
    }

    public void setCatname(String catname) {
        this.catname = catname;
    }

    public int getUpid() {
        return upid;
    }

    public void setUpid(int upid) {
        this.upid = upid;
    }



}
