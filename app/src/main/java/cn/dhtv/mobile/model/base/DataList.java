package cn.dhtv.mobile.model.base;

import java.util.ArrayList;

import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.util.DataManager;

/**
 * Created by Jack on 2015/3/10.
 */
public class DataList<C extends Category,I> {
    public static final int PAGE_SIZE = 10;


    private C mCategory;
    private ArrayList<I> mDataList = new ArrayList<>();


    private boolean newData = false;
    private boolean hasMore = true;
    private int currentPage = 0;
    private int updateTime = 0;

    public DataList(C catagory){
        mCategory = catagory;
    }

    public void reserState(){
        newData = false;
        hasMore = true;
        currentPage = 0;
    }

    public int nextPage(){
        return currentPage + 1;
    }

    public I getItem(int position){
        return mDataList.get(position);
    }

    public int size(){
        return mDataList.size();
    }

    public void append(ArrayList<I> list){
        mDataList.addAll(list);
    }

    public void replace(ArrayList<I> list){
        clear();
        append(list);
    }

    public void clear(){
        mDataList.clear();
    }

    public C getCategory() {
        return mCategory;
    }

    public ArrayList<I> getDataList() {
        return mDataList;
    }

    public boolean isNewData() {
        return newData;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setNewData(boolean newData) {
        this.newData = newData;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }
}
