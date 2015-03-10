package cn.dhtv.mobile.model.base;

import android.content.Context;

import com.android.volley.RequestQueue;

import java.util.ArrayList;
import java.util.HashMap;

import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.network.NetUtils;

/**
 * Created by Jack on 2015/3/10.
 */
public abstract class ListManager<C extends Category,L extends DataList> {
    public static final int PAGE_SIZE = 10;

    protected RequestQueue mRequestQueue;

    protected ArrayList<C> mCategories = new ArrayList<>();
    protected HashMap<C,L> mDataListMap = new HashMap<>();
    protected ArrayList<C> mProcessingList = new ArrayList<>();
    protected CallBacks<C> mCallBacks;


    public ListManager(){
        mRequestQueue = NetUtils.getRequestQueue();
    }

    public ListManager(Context context){
        mRequestQueue = NetUtils.getRequestQueue(context);
    }

    public void clear(){
        mDataListMap.clear();
    }

    public L getDataList(C category){
        return mDataListMap.get(category);
    }

    public ArrayList<C> getCategories(){
        return mCategories;
    }

    public int getCategoryCount(){
        return mCategories.size();
    }

    public CallBacks<C> getCallBacks() {
        return mCallBacks;
    }

    public void setCallBacks(CallBacks<C> mCallBacks) {
        this.mCallBacks = mCallBacks;
    }

    public abstract void refresh(C category,int flag);

    public abstract void append(C category,int flag);

    public abstract void release();


    public static interface CallBacks<C2 extends Category>{
        void onRefresh(C2 category,int flag);
        void onAppend(C2 category,int flag);
        void onRefreshFails(C2 category,int flag);
        void onAppendFails(C2 category,int flag);
    }
}
