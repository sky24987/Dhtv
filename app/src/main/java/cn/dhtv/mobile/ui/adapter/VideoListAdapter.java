package cn.dhtv.mobile.ui.adapter;

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
import cn.dhtv.mobile.entity.VideoOverview;

/**
 * Created by Jack on 2015/3/20.
 */
public class VideoListAdapter extends AbstractListAdapter implements View.OnClickListener{
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = false;

    public VideoListAdapter(Category category,ItemViewDataSet dataList,ImageLoader imageLoader, Context context) {
        this.category = category;
        this.dataList = dataList;
        this.mImageLoader = imageLoader;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        VideoOverview item = (VideoOverview)dataList.getItem(position);
        if(convertView == null){
            if(DEBUG){
                Log.d(LOG_TAG, "create list view item");
            }
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_video, null);
            holder = new ViewHolder();
            holder.networkImageView = (NetworkImageView) convertView.findViewById(R.id.image);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.durition = (TextView) convertView.findViewById(R.id.duration);
            holder.networkImageView.setImageUrl(item.getPic(),mImageLoader);
            holder.title.setText(item.getTitle());
            holder.dataline = (TextView) convertView.findViewById(R.id.dateline);
            holder.dataline.setText(item.getDateline());
            holder.durition.setText(""+item.getDuration());
            holder.url = item.getUrl();
            convertView.setTag(holder);
            convertView.setOnClickListener(this);
        }else {
            if(DEBUG){
                Log.d(LOG_TAG,"reuse list view item");
            }
            holder = (ViewHolder) convertView.getTag();
            holder.title.setText(item.getTitle());
            holder.dataline.setText(item.getDateline());
            holder.durition.setText(""+item.getDuration());
            holder.networkImageView.setImageUrl(item.getPic(),mImageLoader);
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
        public NetworkImageView networkImageView;
        public TextView title;
        public TextView dataline;
        public TextView durition;
    }
}
