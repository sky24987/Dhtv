package cn.dhtv.mobile.model;

import cn.dhtv.mobile.entity.Category;

/**
 * Created by Jack on 2015/3/18.
 */
public class NewsPageManager extends AbsPageManager<NewsListCollector2> {
    @Override
    public void setUp() {
        Category cat1 = new Category();
        cat1.setCatname("时政");
        cat1.setCatid(256);
        Category cat2 = new Category();
        cat2.setCatname("社会");
        cat2.setCatid(260);
        Category cat3 = new Category();
        cat3.setCatname("文化");
        cat3.setCatid(263);

       /* Category cat4 = new Category();
        cat1.setCatname("时政2");
        cat1.setCatid(256);
        Category cat5 = new Category();
        cat2.setCatname("社会2");
        cat2.setCatid(260);
        Category cat6 = new Category();
        cat3.setCatname("文化2");
        cat3.setCatid(263);*/

        categories.add(cat1);
        categories.add(cat2);
        categories.add(cat3);

       /* categories.add(cat4);
        categories.add(cat5);
        categories.add(cat6);*/

        for(Category cat:categories){
            mListMap.put(cat,new NewsListCollector2(cat, this));
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
