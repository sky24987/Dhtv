package cn.dhtv.mobile.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import cn.dhtv.mobile.widget.FooterRefreshListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;


import java.util.ArrayList;

import cn.dhtv.mobile.R;
import cn.dhtv.mobile.entity.NewsCat;
import cn.dhtv.mobile.util.NewsDataManager;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by Jack on 2014/12/30.
 */
public class NewsPagerAdapter extends PagerAdapter{
    public static final String LOG_TAG = "NewsPagerAdapter";

    private Context context;
    private EventsListener listener;
    private ArrayList<Page> pages = new ArrayList<>();
    private LayoutInflater inflater;
    private NewsDataManager mNewsDataManager = NewsDataManager.getInstance();
    private ImageLoader mImageLoader;


    @Override
    public int getCount() {
        return mNewsDataManager.getNewsCats().size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
       return view == ((Page)object).getPageView();
    }


    @Override
    public int getItemPosition(Object object) {
        NewsCat cat = ((Page)object).getCat();
        int postion = mNewsDataManager.getNewsCats().indexOf(cat);
        if(postion >= 0){
            return postion;
        }else {
            return POSITION_NONE;
        }

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        NewsCat newsCat = mNewsDataManager.getNewsCats().get(position);
        Log.v(LOG_TAG,"init page view "+position+"："+newsCat.getCatname());
        Page page = new Page(newsCat);
        pages.add(page);
        container.addView(page.getPageView());
        return page;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        NewsCat newsCat = mNewsDataManager.getNewsCats().get(position);
        Log.v(LOG_TAG,"destroy page view "+position+"："+newsCat.getCatname());

        ((Page) object).setRefreshing(false);
        pages.remove(object);

        container.removeView(((Page)object).getPageView());
        return;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mNewsDataManager.getNewsCats().get(position).getCatname();
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

    /**
     * 通知更新所有页面
     */
    public void notifyRefreshNews(){
        for(Page page:pages){
            page.notifyDataSetChange();
            page.setRefreshing(false);
        }
    }

    public void  setRefreshState(boolean b){
        for(Page page:pages){
            page.setRefreshing(false);
        }
    }

    //刷新新闻方法，当Page需要刷新时由Page内部调用
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
    public class Page implements OnRefreshListener{
        //private String type;
        private NewsCat cat;
        private ViewGroup pageView;//页面视图
        private PullToRefreshLayout refreshLayout;//页面视图
        private FooterRefreshListView newsList;
        private View emptyView;
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

        private Page(final NewsCat cat) {
            this.cat = cat;
            this.listAdapter = new NewsListAdapter(context,cat,mImageLoader);
            pageView =  (ViewGroup)inflater.inflate(R.layout.news_page,null);
            refreshLayout = (PullToRefreshLayout)pageView.findViewById(R.id.refresh_view);
            newsList = (FooterRefreshListView) pageView.findViewById(R.id.news_list);
            ActionBarPullToRefresh.from((Activity) context).theseChildrenArePullable(newsList).listener(this).setup(refreshLayout);


//            TODO:test
            emptyView = new TextView(context);
            ((TextView)emptyView).setText("empty");
            emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshPage(cat);
                }
            });
            ((TextView)emptyView).setVisibility(View.GONE);
            pageView.addView(emptyView);
            newsList.setEmptyView(emptyView);

            newsList.setAdapter(listAdapter);

        }

        public void notifyDataSetChange(){
            listAdapter.notifyDataSetChanged();
        }

        public void setRefreshing(Boolean b){
            if(b == false) {
                refreshLayout.setRefreshing(false);
            }
        }

        @Override
        public void onRefreshStarted(View view) {
            refreshPage(cat);
        }
    }
}
