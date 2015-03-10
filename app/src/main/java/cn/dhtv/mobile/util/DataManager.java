package cn.dhtv.mobile.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.VideoCat;

/**
 * Created by Jack on 2015/3/6.
 */
public abstract class DataManager {
    public static final int PAGE_SIZE = 10;

    public abstract void release();

    public abstract DataList getDataList(String title);

    public abstract DataList getDataList(int catid);

    public abstract DataList getDataList(Category category);

    public abstract class DataList {
        private Category mCategory;
        private boolean newData = false;
        private boolean hasMore = true;
        private int page = 0;

        public void resetState(){
            newData = false;
            hasMore = true;
            page = 0;
        }

        public int nextPage(){
            return page + 1;
        }

        public abstract int size();

        public abstract Object getItem(int position);

        public abstract void appendList(List list);

        public void refresh(List list){
            resetState();
        }

        public boolean isNewData() {
            return newData;
        }

        public void setNewData(boolean newData) {
            this.newData = newData;
        }

        public boolean isHasMore() {
            return hasMore;
        }

        public void setHasMore(boolean hasMore) {
            this.hasMore = hasMore;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }
    }
}
