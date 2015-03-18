package cn.dhtv.mobile.model;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.dhtv.mobile.adapter.AbstractListAdapter;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.network.NetUtils;

/**
 * Created by Jack on 2015/3/17.
 * 导读分页列表内容采集器,采集并保持列表数据（导读分页列表的背后数据）
 */
public abstract class AbsListCollector implements AbstractListAdapter.ListViewDataList{
    public static final int PAGE_SIZE = 10;

    protected Category category;
    protected Context context;
    protected RequestQueue mRequestQueue = NetUtils.getRequestQueue();
    protected CallBacks mCallBacks;

    protected ObjectMapper mObjectMapper = new ObjectMapper();

    protected boolean newData = false;
    protected boolean hasMore = true;
    protected int currentPage = 0;
    protected long updateTime = 0;
    protected boolean isProcessing = false;


    public abstract void clear();
    public abstract void asyncAppend();
    public abstract void asyncRefresh();




    protected void resetState(){
        newData = false;
        hasMore = true;
        currentPage = 0;
        updateTime = 0;
        isProcessing = false;
    }

    public long getUpdateTime(){
        return updateTime;
    }

    public int getCurrentPage(){
        return currentPage;
    }

    public int nextPage(){
        return currentPage+1;
    }

    public Category getCategory(){
        return category;
    }

    public boolean hasMore(){
        return hasMore;
    }

    public void setCallBacks(CallBacks callBacks){
        this.mCallBacks = callBacks;
    }

    public boolean isProcessing(){
        return isProcessing;
    }




    public interface CallBacks{
        void onRefresh(Category category,SyncFlag syncFlag);
        void onAppend(Category category,SyncFlag syncFlag);
        void onRefreshFails(Category category,SyncFlag syncFlag);
        void onAppendFails(Category category,SyncFlag syncFlag);
    }

    public enum SyncFlag{

    }
}
