package cn.dhtv.mobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import cn.dhtv.mobile.R;
import cn.dhtv.mobile.activity.WebViewActivity;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.NewsOverview;

/**
 * Created by Jack on 2015/3/17.
 */
public class NewsListAdapter extends AbstractListAdapter implements View.OnClickListener{
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = false;

    public NewsListAdapter(Category category, ListViewDataList dataList, ImageLoader imageLoader, Context context) {
        this.category = category;
        this.dataList = dataList;
        this.mImageLoader = imageLoader;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        NewsOverview item = (NewsOverview)dataList.getItem(position);
        if(convertView == null){
            if(DEBUG){
                Log.d(LOG_TAG, "create list view item");
            }
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_news, null);
            holder = new ViewHolder();
            holder.imageView = (NetworkImageView) convertView.findViewById(R.id.news_image);
            holder.title = (TextView) convertView.findViewById(R.id.news_title);
            holder.summary = (TextView) convertView.findViewById(R.id.news_summary);
            holder.imageView.setDefaultImageResId(R.drawable.default_image);
            holder.imageView.setImageUrl(item.getPic_url(),mImageLoader);
           /* if(item.getPic().length > 0) {
                holder.imageView.setImageUrl(NewsOverview.Pic.PIC_URL_PREFEX + item.getPic()[0].getSrc(), mImageLoader);
            }else {
                holder.imageView.setImageUrl(null, mImageLoader);
            }*/
            holder.title.setText(item.getTitle());
            holder.summary.setText(item.getSummary());
            holder.url = item.getUrl();
            convertView.setTag(holder);
            convertView.setOnClickListener(this);
        }else {
            if(DEBUG){
                Log.d(LOG_TAG,"reuse list view item");
            }
            holder = (ViewHolder) convertView.getTag();
            holder.title.setText(item.getTitle());
            holder.summary.setText(item.getSummary());
            holder.imageView.setImageUrl(item.getPic_url(),mImageLoader);
            /*if(item.getPic().length > 0) {
                holder.imageView.setImageUrl(NewsOverview.Pic.PIC_URL_PREFEX + item.getPic()[0].getSrc(), mImageLoader);
            }else {
                holder.imageView.setImageUrl(null, mImageLoader);
            }*/
            holder.url = item.getUrl();
        }

        return convertView;
    }

    @Override
    public void onClick(View v) {
        String url = ((ViewHolder)v.getTag()).url;
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.setDataAndType(Uri.parse(url),"text/html");
        context.startActivity(intent);
    }

    public static class ViewHolder extends BaseViewHolder{
        private String url;
        public NetworkImageView imageView;
        public TextView title;
        public TextView summary;
    }
}
