package cn.dhtv.mobile.model;

import cn.dhtv.mobile.entity.Category;

/**
 * Created by Jack on 2015/3/20.
 */
public class VideoPageManager extends AbsPageManager<VideoListCollector> {
    @Override
    public void setUp() {
        Category cat1 = new Category();
        cat1.setCatname("热点");
        cat1.setCatid(258);
        Category cat2 = new Category();
        cat2.setCatname("生活");
        cat2.setCatid(262);
        Category cat3 = new Category();
        cat3.setCatname("微电影");
        cat3.setCatid(264);
        Category cat4 = new Category();
        cat4.setCatname("精品");
        cat4.setCatid(265);

        categories.add(cat1);
        categories.add(cat2);
        categories.add(cat3);
        categories.add(cat4);
        for(Category cat:categories){
            mListMap.put(cat,new VideoListCollector(cat, this));
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
        if(mCallBacks != null){
            mCallBacks.onAppend(category, null);
        }
    }

    @Override
    public void onRefreshFails(Category category, AbsListCollector.SyncFlag syncFlag) {
        if(mCallBacks != null){
            mCallBacks.onRefresh(category, null);
        }
    }

    @Override
    public void onAppendFails(Category category, AbsListCollector.SyncFlag syncFlag) {
        if(mCallBacks != null){
            mCallBacks.onAppendFails(category, null);
        }
    }
}
