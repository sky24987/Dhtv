package cn.dhtv.mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.dhtv.mobile.R;
import cn.dhtv.mobile.entity.NewsCat;
import cn.dhtv.mobile.entity.NewsOverview;
import cn.dhtv.mobile.util.NewsManager;

/**
 * Created by Jack on 2014/12/31.
 */
public class NewsListAdapter extends BaseAdapter {
    private ArrayList<NewsOverview> mNewsList;
    private Context context;
    private NewsCat cat;
    private LayoutInflater inflater;
    private NewsManager newsManager = NewsManager.getInstance();

    public NewsListAdapter(Context context,NewsCat cat){
        this.context = context;
        this.cat = cat;
        mNewsList = newsManager.getNewsList(cat.getCatid());
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mNewsList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return mNewsList.get(position).getAaid();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        NewsOverview item = mNewsList.get(position);
        if(convertView == null){
            convertView = inflater.inflate(R.layout.news_list_item,null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.news_image);
            holder.textView = (TextView) convertView.findViewById(R.id.news_title);
            holder.textView.setText(item.getTitle());
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
            holder.textView.setText(item.getTitle());
        }
        return convertView;
    }

    static class ViewHolder{
        public ImageView imageView;
        public TextView textView;
    }
}
