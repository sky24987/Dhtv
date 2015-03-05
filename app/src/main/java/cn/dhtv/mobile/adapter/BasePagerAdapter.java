package cn.dhtv.mobile.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;

/**
 * Created by Jack on 2015/3/5.
 */
public class BasePagerAdapter extends PagerAdapter {
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }
}
