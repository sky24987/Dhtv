package cn.dhtv.mobile.adapter;

/**
 * Created by Jack on 2015/4/15.
 */
public interface ItemViewDataSet {
    public Object getItem(int position);
    public int viewType(int position);
    public int size();
    public int getDataId(int position);
}
