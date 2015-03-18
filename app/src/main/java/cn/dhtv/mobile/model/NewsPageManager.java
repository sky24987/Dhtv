package cn.dhtv.mobile.model;

import cn.dhtv.android.adapter.BasePagerAdapter;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.NewsCat;

/**
 * Created by Jack on 2015/3/18.
 */
public class NewsPageManager extends AbsPageManager<NewsListCollector> {
    @Override
    public void setUp() {
        Category cat1 = new NewsCat();
        cat1.setCatname("时政");
        cat1.setCatid(256);
        Category cat2 = new NewsCat();
        cat2.setCatname("社会");
        cat2.setCatid(260);
        Category cat3 = new NewsCat();
        cat3.setCatname("文化");
        cat3.setCatid(263);

        categories.add(cat1);
        categories.add(cat2);
        categories.add(cat3);
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
