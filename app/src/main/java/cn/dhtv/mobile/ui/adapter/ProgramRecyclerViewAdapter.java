package cn.dhtv.mobile.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import cn.dhtv.android.adapter.BaseRecyclerViewAdapter;
import cn.dhtv.mobile.R;
import cn.dhtv.mobile.entity.Category;

/**
 * Created by Jack on 2015/5/4.
 */
public class ProgramRecyclerViewAdapter extends BaseRecyclerViewAdapter<VideoRecyclerViewAdapter.ViewHolder> {
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = false;

    private ItemViewDataSet mItemViewDataSet;

    public ProgramRecyclerViewAdapter(ItemViewDataSet mItemViewDataSet) {
        this.mItemViewDataSet = mItemViewDataSet;
    }

    @Override
    public int itemCount() {
        return mItemViewDataSet.size();
    }

    @Override
    public VideoRecyclerViewAdapter.ViewHolder onCreateVH(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_program,parent,false);
        ViewHolder viewHolder = new ViewHolder(v,viewType);

        return null;
    }

    @Override
    public void onBindVH(VideoRecyclerViewAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int itemViewType(int position) {
        return mItemViewDataSet.viewType(position);
    }




    public static class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder{
        public Category program;
        public NetworkImageView mNetworkImageView;
        public TextView textViewTitle;

        public ViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }
    }
}
