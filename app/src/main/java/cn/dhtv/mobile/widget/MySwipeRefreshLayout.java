package cn.dhtv.mobile.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by Jack on 2015/4/17.
 */
public class MySwipeRefreshLayout extends SwipeRefreshLayout {
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    /*@Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(LOG_TAG,"ondetachedfrom window");
        setEnabled(false);
    }*/
}
