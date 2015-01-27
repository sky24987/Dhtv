package cn.dhtv.mobile.util;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import cn.dhtv.mobile.entity.NewsCat;
import cn.dhtv.mobile.entity.NewsOverview;

/**
 * Created by Jack on 2015/1/21.
 */
public class NewsManager {
    private static final int PAGE_SIZE = 2;
    private static NewsManager instance;

    private ArrayList<NewsCat> newsCats = new ArrayList<>();
    private HashMap<Integer,ArrayList<NewsOverview>> newsOverviewHashMap = new HashMap<>();
    private boolean fetching = false;//是否正在获取数据
    private Context context;
    //private boolean dataLocking = false;


    private NewsManager(){
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
            newsOverviewHashMap.put(cat.getCatid(),new ArrayList<NewsOverview>());
        }
    }

    public int getCatCount(){
        return newsCats.size();
    }

    public NewsCat getCat(int position){
        return newsCats.get(position);
    }

    public int getCatPostion(NewsCat cat){
        return newsCats.indexOf(cat);
    }

    public boolean isFetching() {
        return fetching;
    }

    public void setFetching(boolean fetching) {
        this.fetching = fetching;
    }

    public static NewsManager getInstance(){
        //NewsManager instance = NewsManager.instance;
        if(instance == null){
            instance = new NewsManager();
        }
        return instance;
    }

    public static String makeRequestURL(NewsCat cat,int page){
        return NewsCat.NEWS_URL+"?"+"catid="+cat.getCatid()+"&page="+page+"&size="+PAGE_SIZE;
    }

    public void refreshNews(Handler handler){
        //TODO:complete
    }

    public void appendEnd(int cat){
        //TODO:complete
    }

    public void appendFront(int cat,ArrayList<NewsOverview> newsList){
        //TODO:complete
        for(NewsCat newsCat :newsCats){
            if(newsCat.getCatid() == cat){
                ArrayList<NewsOverview> list = newsOverviewHashMap.get(newsCat.getCatid());
                for(NewsOverview news:newsList){
                    list.add(0,news);
                }
            }
        }
    }





    public ArrayList<NewsOverview> getNewsList(int cat){
        return newsOverviewHashMap.get(cat);
    }




    private void init(){

    }
}
