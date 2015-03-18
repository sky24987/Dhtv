package cn.dhtv.mobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.toolbox.ImageLoader;

import cn.dhtv.mobile.entity.Category;

/**
 * Created by Jack on 2015/3/17.
 */
public abstract class AbstractListAdapter extends BaseAdapter {

    protected Category category;
    protected ListViewDataList dataList;
    protected ImageLoader mImageLoader;
    protected Context context;

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return dataList.getDataId(position);
    }





    public interface ListViewDataList{
        public Object getItem(int position);
        public ViewType viewType(int position);
        public int size();
        public int getDataId(int position);
    }

    public static class BaseViewHolder {

    }

    public static enum ViewType{

    }


}
