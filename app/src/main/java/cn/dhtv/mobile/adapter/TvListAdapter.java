package cn.dhtv.mobile.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.dhtv.mobile.R;
import cn.dhtv.mobile.entity.TvOverview;
import cn.dhtv.mobile.fragment.TvFragment;

/**
 * Created by Jack on 2015/4/1.
 */
public class TvListAdapter extends RecyclerView.Adapter<TvListAdapter.ViewHolder> implements  View.OnClickListener {
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    ArrayList<TvOverview> tvOverviews;
    TvFragment.StateDate stateDate;
    OnItemClickListener mOnItemClickListener;

    public TvListAdapter(ArrayList<TvOverview> tvOverviews,TvFragment.StateDate stateDate) {
        this.tvOverviews = tvOverviews;
        this.stateDate = stateDate;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_tv,viewGroup,false);
        ViewHolder vh = new ViewHolder(v);
        v.setTag(vh);
        v.setOnClickListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if(DEBUG){
            Log.d(LOG_TAG,"onBindViewHolder at:"+i);
        }
        TvOverview tvOverview = tvOverviews.get(i);
        viewHolder.tvOverview = tvOverview;
        viewHolder.textView.setText(tvOverview.getTitle());
        if(stateDate.selectedTv != null && tvOverview.getTvid() == stateDate.selectedTv.getTvid()){
            if(DEBUG){
                Log.d(LOG_TAG,"select textView at:"+i);
            }
            viewHolder.textView.setSelected(true);
        }else {
            viewHolder.textView.setSelected(false);
        }
    }



    @Override
    public int getItemCount() {
        return tvOverviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TvOverview tvOverview;
        public TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.title);
        }
    }

    public void setmOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if(mOnItemClickListener != null){
            mOnItemClickListener.OnItemClicked((ViewHolder) v.getTag());
        }
    }

    public interface OnItemClickListener{
        void OnItemClicked(ViewHolder viewHolder);
    }
}
