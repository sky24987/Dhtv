package cn.dhtv.mobile.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.dhtv.mobile.util.DataTest;

/**
 * Created by Jack on 2014/12/30.
 */
public class NewsPagerAdapter extends PagerAdapter{
    private Context context;
    private List<String> newsTypeList;
    //private Map<String,ListView> newsPageMap;

    @Override
    public int getCount() {
        return newsTypeList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
       return view == ((Page)object).getPageView();
    }


    @Override
    public int getItemPosition(Object object) {
        String type = ((Page)object).getType();
        if(newsTypeList.contains(type)){
            return newsTypeList.indexOf(type);
        }else {
            return POSITION_NONE;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Page page = new Page(newsTypeList.get(position));
        container.addView(page.getPageView());
        return page;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(((Page)object).getPageView());
        return;
    }

    public NewsPagerAdapter(Context context,List<String> newsTypeList) {
        this.context = context;
        this.newsTypeList = newsTypeList;
    }

    private class Page{
        private String type;
        private View pageView;

        public String getType() {
            return type;
        }

        public View getPageView() {
            return pageView;
        }

        private Page(String type) {
            this.type = type;
            pageView = new ListView(context);
            ((ListView)pageView).setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, DataTest.newsListMap.get(type)));//TODO:insert adapter
        }
    }
}
