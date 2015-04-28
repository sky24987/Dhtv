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
import cn.dhtv.mobile.fragment.AudioFragment;

/**
 * Created by Jack on 2015/4/7.
 */
public class FMAdapter extends BaseRecyclerViewAdapter<FMAdapter.ViewHolder> {
    ArrayList<Category> fmList;
    AudioFragment.StateData stateData;

    public FMAdapter(ArrayList<Category> fmList,AudioFragment.StateData stateData) {
        this.fmList = fmList;
        this.stateData = stateData;
    }

    @Override
    public int itemCount() {
        return fmList.size();
    }

    @Override
    public FMAdapter.ViewHolder onCreateVH(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_fm, parent, false);
        ViewHolder holder = new ViewHolder(v,0);
        holder.networkImageView = (NetworkImageView) v.findViewById(R.id.network_image);
        holder.title = (TextView) v.findViewById(R.id.title);
        return holder;
    }

    @Override
    public void onBindVH(FMAdapter.ViewHolder holder, int position) {
        Category fm = fmList.get(position);
        holder.item = fm;
        holder.networkImageView.setDefaultImageResId(R.drawable.default_image);
        holder.title.setText(fm.getCatname());
        if(stateData.selectedFm != null && stateData.selectedFm.getCatid() == fm.getCatid()){
            holder.title.setSelected(true);
        }else {
            holder.title.setSelected(false);
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
