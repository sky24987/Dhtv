package cn.dhtv.mobile.model;

import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

import cn.dhtv.mobile.adapter.AbstractListAdapter;
import cn.dhtv.mobile.entity.Category;

/**
 * Created by Jack on 2015/3/17.
 */
public abstract class AbsPageManager<L extends AbsListCollector> implements AbsListCollector.CallBacks{
    protected ArrayList<Category> categories = new ArrayList<>();
    protected HashMap<Category,L> mListMap = new HashMap<>();
    protected CallBacks mCallBacks;



    public abstract void setUp();
    public abstract void release();


    public void refresh(Category category){
        mListMap.get(category).asyncRefresh();
    }

    public void append(Category category){
        mListMap.get(category).asyncAppend();
    }

    public CallBacks getCallBacks() {
        return mCallBacks;
    }

    public void setCallBacks(CallBacks mCallBacks) {
        this.mCallBacks = mCallBacks;
    }

    public AbstractListAdapter.ListViewDataList getList(Category category){
        return mListMap.get(category);
    }

    public void clear(){
        for(Category category :categories){
            AbsListCollector absListCollector = mListMap.get(category);
            if(absListCollector == null){
                continue;
            }
            absListCollector.clear();
        }
    }

    public void clear(Category category){
        AbsListCollector absListCollector = mListMap.get(category);
        if(absListCollector != null){
            absListCollector.clear();
        }
    }

    public int indexof(Category category){
        return categories.indexOf(category);
    }

    public boolean isProcessing(Category category){
        if(!categories.contains(category)){
            return false;
        }
        return mListMap.get(category).isProcessing();
    }

    public int getCategoryCount(){
        return categories.size();
    }

    public int getPageCount(){
        return getCategoryCount();
    }

    public Category getCategory(int position){
        return categories.get(position);
    }

    public static interface CallBacks{
        void onRefresh(Category category,CallBackFlag flag);
        void onAppend(Category category,CallBackFlag flag);
        void onRefreshFails(Category category,CallBackFlag flag);
        void onAppendFails(Category category,CallBackFlag flag);
    }

    public static enum CallBackFlag{

    }
}
