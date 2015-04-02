package cn.dhtv.mobile.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.dhtv.mobile.R;
import cn.dhtv.mobile.entity.TvOverview;

/**
 * Created by Jack on 2015/4/1.
 */
public class TvListAdapter extends RecyclerView.Adapter<TvListAdapter.ViewHolder> {
    ArrayList<TvOverview> tvOverviews;
    public TvListAdapter(ArrayList<TvOverview> tvOverviews) {
        this.tvOverviews = tvOverviews;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_tv,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        TvOverview tvOverview = tvOverviews.get(i);
        viewHolder.textView.setText(tvOverview.getTitle());
    }

    @Override
    public int getItemCount() {
        return tvOverviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
