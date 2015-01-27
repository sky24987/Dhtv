package cn.dhtv.mobile.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.dhtv.mobile.entity.NewsCat;
import cn.dhtv.mobile.entity.NewsOverview;
import cn.dhtv.mobile.util.DataTest;
import cn.dhtv.mobile.util.NewsManager;

/**
 * Created by Jack on 2014/12/30.
 */
public class NewsPagerAdapter extends PagerAdapter{
    private Context context;
    private EventsListener listener;
    private ArrayList<Page> pages = new ArrayList<>();
    private NewsManager newsManager = NewsManager.getInstance();


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
        Page page = new Page(newsCat);
        pages.add(page);
        container.addView(page.getPageView());
        return page;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(((Page)object).getPageView());
        pages.remove(object);
        return;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return newsManager.getCat(position).getCatname();
    }

    public NewsPagerAdapter(Context context,EventsListener listener) {
        this.context = context;
        this.listener = listener;
        //this.newsTypeList = newsTypeList;
    }

    public void refreshData(){
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
    }

    public void notifyNewsListChange(NewsCat cat){
        for(Page page:pages){
            if(page.getCat() == cat){
                page.notifyDataSetChange();
                break;
            }
        }
    }

    public  interface EventsListener{
        void onPullDown(NewsCat cat);
        void onPullUp(NewsCat cat);
    }



    /**
     * 新闻列表页类
     */
    public class Page{
        //private String type;
        private NewsCat cat;
        private View pageView;//页面视图
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
            this.listAdapter = new NewsListAdapter(context,cat);
            pageView = new ListView(context);
            ((ListView)pageView).setAdapter(listAdapter);
        }

        public void notifyDataSetChange(){
            listAdapter.notifyDataSetChanged();
        }
    }
}
