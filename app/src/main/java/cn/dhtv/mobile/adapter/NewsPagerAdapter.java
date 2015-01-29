package cn.dhtv.mobile.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.Inflater;

import cn.dhtv.mobile.R;
import cn.dhtv.mobile.entity.NewsCat;
import cn.dhtv.mobile.entity.NewsOverview;
import cn.dhtv.mobile.util.DataTest;
import cn.dhtv.mobile.util.NewsManager;

/**
 * Created by Jack on 2014/12/30.
 */
public class NewsPagerAdapter extends PagerAdapter{
    public static final String LOG_TAG = "NewsPagerAdapter";

    private Context context;
    private EventsListener listener;
    private ArrayList<Page> pages = new ArrayList<>();
    private LayoutInflater inflater;
    private NewsManager newsManager = NewsManager.getInstance();
    private ImageLoader mImageLoader;


    @Override
    public int getCount() {
        return newsManager.getCatCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
       return view == ((Page)object).getPageView();
    }


    @Override
    public int getItemPosition(Object object) {
        NewsCat cat = ((Page)object).getCat();
        int postion = newsManager.getCatPostion(cat);
        if(postion >= 0){
            return postion;
        }else {
            return POSITION_NONE;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        NewsCat newsCat = newsManager.getCat(position);
        Log.v(LOG_TAG,"init page view "+position+"："+newsCat.getCatname());
        Page page = new Page(newsCat);
        pages.add(page);
        container.addView(page.getPageView());
        return page;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        NewsCat newsCat = newsManager.getCat(position);
        Log.v(LOG_TAG,"destroy page view "+position+"："+newsCat.getCatname());

        ((Page) object).setRefreshing(false);
        pages.remove(object);

        container.removeView(((Page)object).getPageView());
        return;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return newsManager.getCat(position).getCatname();
    }

    public NewsPagerAdapter(Context context,EventsListener listener,ImageLoader imageLoader) {
        this.context = context;
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
        mImageLoader = imageLoader;
        //this.newsTypeList = newsTypeList;
    }

    /*public void refreshData(){
        for(Page page:pages){
            page.notifyDataSetChange();
        }
    }

    public  void appendNewsToEnd(NewsCat cat){
        for(Page page:pages){
            if(page.getCat() == cat){
                page.notifyDataSetChange();
                break;
            }
        }
    }*/

    /**
     * 通知分页更新新闻列表
     * @param cat
     */
    public void notifyNewsListChange(NewsCat cat){
        for(Page page:pages){
            if(page.getCat() == cat){
                page.notifyDataSetChange();
                page.setRefreshing(false);
                break;
            }
        }
    }

    //刷新新闻方法，当Page需要刷新时由Page调用
    public void refreshPage(NewsCat cat){
        Log.v(LOG_TAG,"refreshPage:"+cat.getCatname());
        listener.onRefresh(cat);
    }

    public  interface EventsListener{
        void onPullDown(NewsCat cat);
        void onPullUp(NewsCat cat);
        void onRefresh(NewsCat cat);
    }



    /**
     * 新闻列表页类
     */
    public class Page implements SwipeRefreshLayout.OnRefreshListener{
        //private String type;
        private NewsCat cat;
        private SwipeRefreshLayout pageView;//页面视图
        private ListView newsList;
        private BaseAdapter listAdapter;


        /*public String getType() {
            return type;
        }*/

        public View getPageView() {
            return pageView;
        }

        public NewsCat getCat() {
            return cat;
        }

        private Page(NewsCat cat) {
            this.cat = cat;
            this.listAdapter = new NewsListAdapter(context,cat,mImageLoader);
            pageView = (SwipeRefreshLayout) inflater.inflate(R.layout.news_page,null);
            newsList = (ListView) pageView.findViewById(R.id.news_list);
            pageView.setOnRefreshListener(this);
            newsList.setAdapter(listAdapter);
        }

        public void notifyDataSetChange(){
            listAdapter.notifyDataSetChanged();
        }

        public void setRefreshing(Boolean b){
            pageView.setRefreshing(b);
        }

        @Override
        public void onRefresh() {
            pageView.setRefreshing(false);//TODO:测试用，记得删除
            refreshPage(cat);
        }
    }
}
