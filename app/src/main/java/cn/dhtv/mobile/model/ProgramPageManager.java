package cn.dhtv.mobile.model;

import cn.dhtv.mobile.entity.Category;

/**
 * Created by Jack on 2015/3/26.
 */
public class ProgramPageManager extends AbsPageManager<ProgramCollector> {

    @Override
    public void setUp() {
        Category cat1 = new Category();
        cat1.setCatname("新闻综合");
        cat1.setCatid(17);
        Category cat2 = new Category();
        cat2.setCatname("经济科教");
        cat2.setCatid(20);
        Category cat3 = new Category();
        cat3.setCatname("都市生活");
        cat3.setCatid(21);
        Category cat4 = new Category();
        cat4.setCatname("公共频道");
        cat4.setCatid(22);
        Category cat5 = new Category();
        cat5.setCatname("瓯江先锋");
        cat5.setCatid(23);

        categories.add(cat1);
        categories.add(cat2);
        categories.add(cat3);
        categories.add(cat4);
        categories.add(cat5);
        for(Category cat:categories){
            mListMap.put(cat,new ProgramCollector(cat, this));
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
