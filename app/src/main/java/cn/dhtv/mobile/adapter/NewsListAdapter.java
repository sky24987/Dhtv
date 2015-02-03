package cn.dhtv.mobile.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

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
    private static final String LOG_TAG = "NewsListAdapter";

    private ArrayList<NewsOverview> mNewsList;
    private Context context;
    private NewsCat cat;
    private LayoutInflater inflater;
    private NewsManager newsManager = NewsManager.getInstance();
    private ImageLoader mImageLoader;

    public NewsListAdapter(Context context,NewsCat cat,ImageLoader imageLoader){
        this.context = context;
        this.cat = cat;
        mNewsList = newsManager.getNewsList(cat.getCatid());
        this.inflater = LayoutInflater.from(context);
        mImageLoader = imageLoader;
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
            holder.imageView = (NetworkImageView) convertView.findViewById(R.id.news_image);
            holder.title = (TextView) convertView.findViewById(R.id.news_title);
            holder.summary = (TextView) convertView.findViewById(R.id.news_summary);
            holder.imageView.setImageUrl(NewsOverview.Pic.PIC_URL_PREFEX+item.getPic()[0].getSrc(),mImageLoader);
            holder.title.setText(item.getTitle());
            holder.summary.setText(item.getSummary());
            Log.v(LOG_TAG,"summary"+item.getSummary());
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
            holder.title.setText(item.getTitle());
        }
        return convertView;
    }



    static class ViewHolder{
        public NetworkImageView imageView;
        public TextView title;
        public TextView summary;

    }
}
