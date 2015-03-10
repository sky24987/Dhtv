package cn.dhtv.mobile.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.VideoCat;
import cn.dhtv.mobile.entity.VideoOverview;

/**
 * Created by Jack on 2015/2/11.
 */
public class VideoDataManager extends DataManager{

    private ArrayList<VideoCat> mVideoCatList = new ArrayList<>();
    private HashMap<VideoCat,VideoDataList> mVideoListHashMap = new HashMap<>();

    public VideoDataManager(){
    }

    public void init(){
        VideoCat cat1 = new VideoCat();
        cat1.setCatname("\\u70ed\\u70b9");
        cat1.setCatid(258);
        VideoCat cat2 = new VideoCat();
        cat2.setCatname("\\u751f\\u6d3b");
        cat2.setCatid(262);
        VideoCat cat3 = new VideoCat();
        cat3.setCatname("\\u5fae\\u7535\\u5f71");
        cat3.setCatid(264);
        VideoCat cat4 = new VideoCat();
        cat4.setCatname("\\u7cbe\\u54c1");
        cat4.setCatid(265);
        mVideoCatList.add(cat1);
        mVideoCatList.add(cat2);
        mVideoCatList.add(cat3);
        mVideoCatList.add(cat4);
        for(VideoCat cat:mVideoCatList){
            mVideoListHashMap.put(cat,new VideoDataList());
        }
    }

    public String requestURL(Category cat){
        DataList dataList = getDataList(cat);
        return VideoCat.URL+"?"+"catid="+cat.getCatid()+"&page="+dataList.nextPage()+"&size="+PAGE_SIZE;
    }

    @Override
    public DataList getDataList(String title) {
        Category cat = getCat(title);
        if(cat == null){
            return  null;
        }
        return mVideoListHashMap.get(cat);
    }

    @Override
    public DataList getDataList(Category category) {
        return mVideoListHashMap.get(category);
    }

    @Override
    public DataList getDataList(int catid) {
        Category cat = getCat(catid);
        if(cat == null){
            return  null;
        }
        return mVideoListHashMap.get(catid);
    }

    public Category getCat(int catid){
        for(VideoCat cat : mVideoCatList){
            if(cat.getCatid() == catid){
                return  cat;
            }
        }
        return null;
    }

    public Category getCat(String title){
        for(VideoCat cat : mVideoCatList){
            if(cat.getCatname().equals(title)){
                return  cat;
            }
        }
        return  null;
    }

    @Override
    public void release() {
        mVideoCatList.clear();
        mVideoListHashMap.clear();
    }


    public class VideoDataList extends DataList{

        private ArrayList<VideoOverview> mVideoOverviews = new ArrayList<>();

        @Override
        public int size() {
            return mVideoOverviews.size();
        }

        @Override
        public Object getItem(int position) {
            return mVideoOverviews.get(position);
        }

        @Override
        public void appendList(List list) {
            mVideoOverviews.addAll(list);
        }

        @Override
        public void refresh(List list) {
            super.refresh(list);
            mVideoOverviews.clear();
            mVideoOverviews.addAll(list);
        }
    }
}
