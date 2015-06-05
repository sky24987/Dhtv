package cn.dhtv.mobile.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import cn.dhtv.android.adapter.BaseRecyclerViewAdapter;
import cn.dhtv.mobile.R;
import cn.dhtv.mobile.Singletons;
import cn.dhtv.mobile.entity.NewsOverview;
import cn.dhtv.mobile.network.NetUtils;

/**
 * Created by Jack on 2015/4/15.
 */
public class NewsRecyclerViewAdapter extends BaseRecyclerViewAdapter<NewsRecyclerViewAdapter.ViewHolder> {
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = false;

    private ItemViewDataSet mItemViewDataSet;
    private ImageLoader mImageLoader = Singletons.getImageLoader();

    public NewsRecyclerViewAdapter(ItemViewDataSet mItemViewDataSet) {
        this.mItemViewDataSet = mItemViewDataSet;
    }

    @Override
    public int itemCount() {
        return mItemViewDataSet.size();
    }

    @Override
    public ViewHolder onCreateVH(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_news,parent,false);
        ViewHolder viewHolder = new ViewHolder(view,viewType);
        viewHolder.imageView = (NetworkImageView) view.findViewById(R.id.news_image);
        viewHolder.title = (TextView) view.findViewById(R.id.news_title);
        viewHolder.summary = (TextView) view.findViewById(R.id.news_summary);
        return viewHolder;
    }

    @Override
    public void onBindVH(ViewHolder holder, int position) {
        NewsOverview item = (NewsOverview) mItemViewDataSet.getItem(position);
        holder.imageView.setDefaultImageResId(R.drawable.default_image);
        holder.imageView.setImageUrl(item.getPic_url(),mImageLoader);
        holder.title.setText(item.getTitle());
        holder.summary.setText(item.getSummary());
        holder.url = item.getUrl();
    }

    @Override
    public int itemViewType(int position) {
        return mItemViewDataSet.viewType(position);
    }



    public static class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder{
        public String url;
        public NetworkImageView imageView;
        public TextView title;
        public TextView summary;

        public ViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }
    }
}
