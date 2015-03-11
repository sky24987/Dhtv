package cn.dhtv.mobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import cn.dhtv.mobile.R;
import cn.dhtv.mobile.activity.WebViewActivity;
import cn.dhtv.mobile.entity.NewsCat;
import cn.dhtv.mobile.entity.NewsOverview;
import cn.dhtv.mobile.model.NewsDataList;

/**
 * Created by Jack on 2015/3/11.
 */
public class NewsListAdapter2 extends BaseAdapter implements View.OnClickListener{

    private NewsCat mNewsCat;
    private NewsDataList mNewsDataList;
    private ImageLoader mImageLoader;
    private Context context;

    public NewsListAdapter2(NewsCat mNewsCat, NewsDataList mNewsDataList, ImageLoader mImageLoader, Context context) {
        this.mNewsCat = mNewsCat;
        this.mNewsDataList = mNewsDataList;
        this.mImageLoader = mImageLoader;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mNewsDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mNewsDataList.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return  mNewsDataList.getItem(position).getAaid();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        NewsOverview item = mNewsDataList.getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.news_list_item, null);
            holder = new ViewHolder();
            holder.imageView = (NetworkImageView) convertView.findViewById(R.id.news_image);
            holder.title = (TextView) convertView.findViewById(R.id.news_title);
            holder.summary = (TextView) convertView.findViewById(R.id.news_summary);
            holder.imageView.setImageUrl(NewsOverview.Pic.PIC_URL_PREFEX+item.getPic()[0].getSrc(),mImageLoader);
            holder.title.setText(item.getTitle());
            holder.summary.setText(item.getSummary());
            holder.url = item.getUrl();
            convertView.setTag(holder);
            convertView.setOnClickListener(this);
        }else {
            holder = (ViewHolder) convertView.getTag();
            holder.title.setText(item.getTitle());
            holder.summary.setText(item.getSummary());
            holder.imageView.setImageUrl(NewsOverview.Pic.PIC_URL_PREFEX+item.getPic()[0].getSrc(),mImageLoader);
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

    static class ViewHolder{
        private String url;
        public NetworkImageView imageView;
        public TextView title;
        public TextView summary;

    }
}
