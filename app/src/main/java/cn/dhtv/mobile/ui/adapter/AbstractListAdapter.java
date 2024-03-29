package cn.dhtv.mobile.ui.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import com.android.volley.toolbox.ImageLoader;

import cn.dhtv.mobile.entity.Category;

/**
 * Created by Jack on 2015/3/17.
 */
public abstract class AbstractListAdapter extends BaseAdapter {

    protected Category category;
    protected ItemViewDataSet dataList;
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
        public int viewType(int position);
        public int size();
        public int getDataId(int position);
    }

    public static class BaseViewHolder {

    }

}
