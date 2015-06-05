package cn.dhtv.mobile.ui.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;

import cn.dhtv.android.adapter.BaseRecyclerViewAdapter;
import cn.dhtv.mobile.Data;
import cn.dhtv.mobile.R;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.network.NetUtils;
import cn.dhtv.mobile.util.TextUtils;

/**
 * Created by Jack on 2015/5/4.
 */
public class ProgramRecyclerViewAdapter extends BaseRecyclerViewAdapter<ProgramRecyclerViewAdapter.ViewHolder> {
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
    public ProgramRecyclerViewAdapter.ViewHolder onCreateVH(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_program,parent,false);
        ViewHolder viewHolder = new ViewHolder(v,viewType);
//        viewHolder.mNetworkImageView = (NetworkImageView) v.findViewById(R.id.image);
//        viewHolder.mSimpleDraweeView = (SimpleDraweeView) v.findViewById(R.id.image);
        viewHolder.imageView = (ImageView) v.findViewById(R.id.image);
        viewHolder.textViewTitle = (TextView) v.findViewById(R.id.title);
        return viewHolder;
    }

    @Override
    public void onBindVH(ProgramRecyclerViewAdapter.ViewHolder holder, int position) {
        Category category = (Category) mItemViewDataSet.getItem(position);
        holder.textViewTitle.setText(category.getCatname());
//        myAppInfo.mNetworkImageView.setDefaultImageResId(R.drawable.default_image);
//        myAppInfo.mNetworkImageView.setImageUrl(TextUtils.URL_RES_IMG+category.getName()+".jpg", NetUtils.getImageLoader());
//        myAppInfo.mSimpleDraweeView.setImageURI(Uri.parse(TextUtils.URL_RES_IMG+category.getName()+".jpg"));
        Glide.with(holder.imageView.getContext()).load(TextUtils.URL_RES_IMG+category.getName()+".jpg").placeholder(R.drawable.default_image).into(holder.imageView);
        holder.category = category;
    }

    @Override
    public int itemViewType(int position) {
        return mItemViewDataSet.viewType(position);
    }




    public static class ViewHolder extends BaseRecyclerViewAdapter.ViewHolder{
//        public Category program;

        public Category category;
        public ImageView imageView;
//        public NetworkImageView mNetworkImageView;
//        public SimpleDraweeView mSimpleDraweeView;
        public TextView textViewTitle;

        public ViewHolder(View itemView, int viewType) {
            super(itemView, viewType);
        }
    }
}
