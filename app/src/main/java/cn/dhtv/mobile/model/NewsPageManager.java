package cn.dhtv.mobile.model;

import java.util.ArrayList;

import cn.dhtv.mobile.entity.Category;

/**
 * Created by Jack on 2015/3/18.
 */
public class NewsPageManager extends AbsPageManager<NewsListCollector> {
    @Override
    public void setUp() {
        Category cat1 = new Category();
        cat1.setCatname("时政");
        cat1.setCatid(256);
        cat1.setBid(34);
        Category cat2 = new Category();
        cat2.setCatname("社会");
        cat2.setCatid(260);
        cat2.setBid(35);
        Category cat3 = new Category();
        cat3.setCatname("文化");
        cat3.setCatid(263);
        cat3.setBid(38);

        categories.add(cat1);
        categories.add(cat2);
        categories.add(cat3);

        for(Category cat:categories){
            mListMap.put(cat,new NewsListCollector(cat, this));
        }
    }

    public void setUp(ArrayList<Category> list){
        for (Category category : list){
            categories.add(category);
        }
        for (Category category:categories){
            mListMap.put(category,new NewsListCollector(category,this));
        }
    }

    public void change(ArrayList<Category> list){
        clear();
        categories.addAll(list);
        mListMap.clear();
        for(Category cat:categories){
            mListMap.put(cat,new NewsListCollector(cat, this));
        }
    }

    @Override
    public void release() {

    }


    @Override
    public void onRefresh(Category category, AbsListCollector.SyncFlag syncFlag) {
        if(mCallBacks != null){
            mCallBacks.onRefresh(category, null);
        }
    }

    @Override
    public void onAppend(Category category, AbsListCollector.SyncFlag syncFlag) {
        CallBackFlag callBackFlag = null;
        if(syncFlag == AbsListCollector.SyncFlag.DB_NULL){
            callBackFlag = CallBackFlag.DB_NULL;
        }
        if(mCallBacks != null){
            mCallBacks.onAppend(category, callBackFlag);
        }
    }

    @Override
    public void onRefreshFails(Category category, AbsListCollector.SyncFlag syncFlag) {
        if(mCallBacks != null){
            mCallBacks.onRefreshFails(category, null);
        }
    }

    @Override
    public void onAppendFails(Category category, AbsListCollector.SyncFlag syncFlag) {
        CallBackFlag callBackFlag = null;
        if(syncFlag == AbsListCollector.SyncFlag.DB_NULL){
            callBackFlag = CallBackFlag.DB_NULL;
        }
        if(mCallBacks != null){
            mCallBacks.onAppendFails(category, callBackFlag);
        }
    }

    @Override
    public void onFirstFetch(Category category, AbsListCollector.SyncFlag syncFlag) {
        if(mCallBacks != null){
            mCallBacks.onFirstFetch(category, null);
        }
    }

    @Override
    public void onFirstFetchFails(Category category, AbsListCollector.SyncFlag syncFlag) {
        if(mCallBacks != null){
            mCallBacks.onFirstFetchFails(category, null);
        }
    }
}
