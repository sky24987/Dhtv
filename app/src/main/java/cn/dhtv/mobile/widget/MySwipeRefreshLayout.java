package cn.dhtv.mobile.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Jack on 2015/4/17.
 */
public class MySwipeRefreshLayout extends SwipeRefreshLayout {
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private float mInitialDownY;
    private float mInitialDownX;
    private float mYXRatio = 1;
    private boolean mMatchRatio = true;

    public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mMatchRatio = true;
                mInitialDownX = ev.getX();
                mInitialDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                    float totalDX = ev.getX() - mInitialDownX;
                    float totalDY = ev.getY() - mInitialDownY;
                    if(mMatchRatio) {
                        if (Math.abs(totalDY) / Math.abs(totalDX) < mYXRatio) {
                            //如果水平拖拽距离过大，则不处理
                            mMatchRatio = false;
                        }
                    }

        }

        if(mMatchRatio == false){
            return  false;
        }

        return super.onInterceptTouchEvent(ev);


    }

    /*@Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(LOG_TAG,"ondetachedfrom window");
        setEnabled(false);
    }*/
}
