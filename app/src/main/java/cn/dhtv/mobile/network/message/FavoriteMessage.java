package cn.dhtv.mobile.network.message;

import java.util.ArrayList;

import cn.dhtv.mobile.entity.Favorite;

/**
 * Created by Jack on 2015/6/17.
 */
public class FavoriteMessage {
    int code;
    String message;
    int totalpage;
    int currentpage;
    int pagesize;
    int size;

    ArrayList<Favorite> favoriteArrayList = new ArrayList<>();

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotalpage() {
        return totalpage;
    }

    public void setTotalpage(int totalpage) {
        this.totalpage = totalpage;
    }

    public int getCurrentpage() {
        return currentpage;
    }

    public void setCurrentpage(int currentpage) {
        this.currentpage = currentpage;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ArrayList<Favorite> getFavoriteArrayList() {
        return favoriteArrayList;
    }

    public void setFavoriteArrayList(ArrayList<Favorite> favoriteArrayList) {
        this.favoriteArrayList = favoriteArrayList;
    }
}
