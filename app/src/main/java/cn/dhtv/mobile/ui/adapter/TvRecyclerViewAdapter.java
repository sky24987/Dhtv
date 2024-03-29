package cn.dhtv.mobile.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cn.dhtv.android.adapter.BaseRecyclerViewAdapter;
import cn.dhtv.mobile.R;
import cn.dhtv.mobile.activity.TVListActivity;
import cn.dhtv.mobile.entity.TvOverview;

/**
 * Created by Jack on 2015/5/11.
 */
public class TvRecyclerViewAdapter extends BaseRecyclerViewAdapter<TvRecyclerViewAdapter.ViewHolder> {
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    ArrayList<TvOverview> tvOverviews;
    TVListActivity.StateDate stateDate;

    public TvRecyclerViewAdapter(ArrayList<TvOverview> tvOverviews, TVListActivity.StateDate stateDate) {
        this.tvOverviews = tvOverviews;
        this.stateDate = stateDate;
    }

    @Override
    public int itemCount() {
        return tvOverviews.size();
    }

    @Override
    public ViewHolder onCreateVH(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_tv,parent,false);
        ViewHolder viewHolder = new ViewHolder(view, viewType);
        viewHolder.textView = (TextView) view.findViewById(R.id.title);
        viewHolder.imageView = (ImageView) view.findViewById(R.id.image);
        return viewHolder;
    }

    @Override
    public void onBindVH(ViewHolder holder, int position) {
        TvOverview tvOverview= tvOverviews.get(position);
        holder.tvOverview = tvOverview;
        holder.textView.setText(tvOverview.getTitle());
        Picasso.with(holder.imageView.getContext()).load(tvOverview.getTv_pic()).placeholder(R.drawable.default_image).into(holder.imageView);
        if(stateDate.selectedTv != null && tvOverview.getTvid() == stateDate.selectedTv.getTvid()){
            holder.textView.setSelected(true);
        }else {
            holder.textView.setSelected(false);
        }
    }

    @Override
    public int itemViewType(int position) {
        return 0;
    }

    public static class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder {
        public TvOverview tvOverview;
        public TextView textView;
        public ImageView imageView;

        public ViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }
    }
}
