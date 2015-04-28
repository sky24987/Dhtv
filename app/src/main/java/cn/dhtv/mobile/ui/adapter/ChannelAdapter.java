package cn.dhtv.mobile.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.dhtv.android.adapter.BaseRecyclerViewAdapter;
import cn.dhtv.mobile.R;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.fragment.LiveTvFragment;

/**
 * Created by Jack on 2015/4/9.
 */
public class ChannelAdapter extends BaseRecyclerViewAdapter<ChannelAdapter.ViewHolder>{
    private ArrayList<Category> channels;
    private LiveTvFragment.DataState channelDataState;

    public ChannelAdapter(ArrayList<Category> channels, LiveTvFragment.DataState channelDataState) {
        this.channels = channels;
        this.channelDataState = channelDataState;
    }

    @Override
    public int itemCount() {
        return channels.size();
    }

    @Override
    public ViewHolder onCreateVH(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_channel,parent,false);
        ChannelAdapter.ViewHolder viewHolder = new ViewHolder(view,viewType);
        viewHolder.networkImageView = (NetworkImageView) view.findViewById(R.id.network_image);
        viewHolder.title = (TextView) view.findViewById(R.id.title);
        return  viewHolder;
    }

    @Override
    public void onBindVH(ViewHolder holder, int position) {
        Category channel = channels.get(position);
        holder.item = channel;
        holder.networkImageView.setDefaultImageResId(R.drawable.default_image);
        holder.title.setText(channel.getCatname());

        if(channelDataState.selected == null || channel.getCatid() != channelDataState.selected.getCatid()){
            holder.title.setSelected(false);
        }else {
            holder.title.setSelected(true);
        }
    }

    @Override
    public int itemViewType(int position) {
        return 0;
    }

    public static class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder{
        public NetworkImageView networkImageView;
        public TextView title;

        public ViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }
    }
}
