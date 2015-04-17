package cn.dhtv.android.widget;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import cn.dhtv.android.adapter.BaseRecyclerViewAdapter;

/**
 * Created by Jack on 2015/4/8.
 */
public class BaseRecyclerView extends RecyclerView {
    private OnItemAttachDetachListener mOnItemAttachDetachListener;




    public BaseRecyclerView(Context context) {
        this(context, null);
    }

    public BaseRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }


    @Override
    public void onChildAttachedToWindow(View child) {
        if(mOnItemAttachDetachListener!= null){
            mOnItemAttachDetachListener.onItemAttachListener(child);
        }
    }

    @Override
    public void onChildDetachedFromWindow(View child) {
        if(mOnItemAttachDetachListener != null){
            mOnItemAttachDetachListener.onItemDetachListener(child);
        }
    }

    public void setOnItemAttachDetachListener(OnItemAttachDetachListener onItemAttachDetachListener) {
        this.mOnItemAttachDetachListener = onItemAttachDetachListener;
    }

    public interface OnItemAttachDetachListener{
        void onItemAttachListener(View view);
        void onItemDetachListener(View view);
    }
}
