package cn.dhtv.mobile.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;

import cn.dhtv.mobile.entity.NewsCat;
import cn.dhtv.mobile.entity.NewsOverview;

/**
 * Created by Jack on 2015/2/2.
 */
public class NewsDataManager {
    private static final int PAGE_SIZE = 10;

    private boolean updated = false;
    private float updateTimestamp = 0;

    private static NewsDataManager instance;
    private ArrayList<NewsCat> newsCats = new ArrayList<>();
    private HashMap<NewsCat,NewsData> newsDataHashMap = new HashMap<>();



    public static String makeRequestURL(NewsCat cat,int page){
        return NewsCat.NEWS_URL+"?"+"catid="+cat.getCatid()+"&page="+page+"&size="+PAGE_SIZE;
    }

    public NewsCat getNewsCat(int catId){
        for(NewsCat cat : newsCats){
            if(cat.getCatid() == catId){
                return cat;
            }
        }
        return null;
    }

    /**
     *
     * @param cat
     * @param newsList
     * @return 是否有新内容添加，如果新旧内容一样，则不处理，返回false
     */
    public boolean refreshData(NewsCat cat,ArrayList<NewsOverview> newsList){
        ArrayList<NewsOverview> oldNewsList = newsDataHashMap.get(cat).getNewsList();
        if(oldNewsList.size() == 0){
            oldNewsList.addAll(newsList);
            updated = true;
            return true;
        }
        NewsOverview oldLatestNews = oldNewsList.get(0);
        int i = newsList.size()-1;
        for(;i>=0;--i){
            if(oldNewsList.get(i).getAaid() > oldLatestNews.getAaid()){
                break;
            }
        }
        if(i >= 0){//至少有一条更新
            oldNewsList.addAll(0,newsList.subList(0,i+1));
            updated = true;
            return true;
        }
        return false;
    }

    public boolean appendNews(NewsCat cat,ArrayList<NewsOverview> newsList){
        return newsDataHashMap.get(cat).appendNews(newsList);
    }

    private NewsDataManager(){
        NewsCat newsCat1 = new NewsCat();
        newsCat1.setCatname("时政");
        newsCat1.setCatid(256);
        NewsCat newsCat2 = new NewsCat();
        newsCat2.setCatname("社会");
        newsCat2.setCatid(260);
        NewsCat newsCat3 = new NewsCat();
        newsCat3.setCatname("文化");
        newsCat3.setCatid(263);
        newsCats.add(newsCat1);
        newsCats.add(newsCat2);
        newsCats.add(newsCat3);
        for(NewsCat cat:newsCats){
            newsDataHashMap.put(cat,new NewsData());
        }
    }

    public static NewsDataManager getInstance(){
        if(instance == null){
            instance = new NewsDataManager();
        }
        return instance;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public ArrayList<NewsCat> getNewsCats() {
        return newsCats;
    }

    public void setNewsCats(ArrayList<NewsCat> newsCats) {
        this.newsCats = newsCats;
    }

    public HashMap<NewsCat, NewsData> getNewsDataHashMap() {
        return newsDataHashMap;
    }

    public void setNewsDataHashMap(HashMap<NewsCat, NewsData> newsDataHashMap) {
        this.newsDataHashMap = newsDataHashMap;
    }

    public class NewsData{
        private boolean hasNew = false;//是否有新内容添加
        private boolean hasMore = true;//是否有更多页
        private int page = 0;//分页

        private ArrayList<NewsOverview> newsList = new ArrayList<NewsOverview>();

        public void resetState(){
            hasNew = false;
            hasMore = true;
            page = 0;
        }

        /**
         *
         * @param list
         * @return 是否有新内容添加
         */
        public boolean appendNews(ArrayList<NewsOverview> list){
            if(list.size() < PAGE_SIZE){
                hasMore = false;
            }
            if(newsList.size() <= 0){
                hasNew = true;
                newsList.addAll(list);
                return true;
            }
            NewsOverview lastNews = newsList.get(newsList.size()-1);
            int i = 0;
            for(;i<list.size();++i){
                if(list.get(i).getAaid() < lastNews.getAaid()){
                    break;
                }
            }
            if(i < list.size()){//至少有一条更新
                newsList.addAll(list.subList(i,list.size()));
                hasNew = true;
                return true;
            }
            return false;
        }



        public ArrayList<NewsOverview> getNewsList() {
            return newsList;
        }

        public void setNewsList(ArrayList<NewsOverview> newsList) {
            this.newsList = newsList;
        }
    }
}
