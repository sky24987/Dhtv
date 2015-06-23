package cn.dhtv.mobile.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.squareup.picasso.Picasso;

import cn.dhtv.android.adapter.BaseRecyclerViewAdapter;
import cn.dhtv.mobile.R;
import cn.dhtv.mobile.Singletons;
import cn.dhtv.mobile.entity.VideoOverview;

/**
 * Created by Jack on 2015/4/29.
 */
public class VideoRecyclerViewAdapter extends BaseRecyclerViewAdapter<VideoRecyclerViewAdapter.ViewHolder> {
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = false;

    private ItemViewDataSet mItemViewDataSet;
    private ImageLoader mImageLoader = Singletons.getImageLoader();

    public VideoRecyclerViewAdapter(ItemViewDataSet mItemViewDataSet) {
        this.mItemViewDataSet = mItemViewDataSet;
    }

    @Override
    public int itemCount() {
        return mItemViewDataSet.size();
    }

    @Override
    public ViewHolder onCreateVH(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_video, null);
        ViewHolder viewHolder = new ViewHolder(view,viewType);
        viewHolder.imageView = (ImageView) view.findViewById(R.id.image);
        viewHolder.titleView = (TextView) view.findViewById(R.id.title);
        viewHolder.durition = (TextView) view.findViewById(R.id.duration);
        viewHolder.dataline = (TextView) view.findViewById(R.id.dateline);
        return viewHolder;
    }

    @Override
    public void onBindVH(ViewHolder holder, int position) {
        VideoOverview item = (VideoOverview)mItemViewDataSet.getItem(position);
        holder.titleView.setText(item.getTitle());
        holder.dataline.setText(item.getDateline());
        holder.durition.setText("" + item.getDuration());
        Picasso.with(holder.imageView.getContext()).load(item.getPic()).placeholder(R.drawable.default_image).into(holder.imageView);
        holder.url = item.getUrl();
        holder.videoUrl = item.getVideo();
        holder.pic_url = item.getPic();
        holder.title = item.getTitle();
        holder.summary = item.getSummary();
    }

    @Override
    public int itemViewType(int position) {
        return mItemViewDataSet.viewType(position);
    }

    public static class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder{
        public String url;
        public String videoUrl;
        public String pic_url;
        public String title;
        public String summary;
        public ImageView imageView;
        public TextView titleView;
        public TextView dataline;
        public TextView durition;

        public ViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }
    }
}
