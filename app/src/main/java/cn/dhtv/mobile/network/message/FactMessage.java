package cn.dhtv.mobile.network.message;

import java.util.ArrayList;

import cn.dhtv.mobile.entity.Fact;

/**
 * Created by Jack on 2015/6/16.
 */
public class FactMessage {
    int code;
    String message;
    int totalpage;
    int currentpage;
    int pagesize;
    int size;

    ArrayList<Fact> factArrayList = new ArrayList<>();

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

    public ArrayList<Fact> getFactArrayList() {
        return factArrayList;
    }

    public void setFactArrayList(ArrayList<Fact> factArrayList) {
        this.factArrayList = factArrayList;
    }
}
