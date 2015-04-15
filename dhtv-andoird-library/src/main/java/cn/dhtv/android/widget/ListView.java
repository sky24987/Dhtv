package cn.dhtv.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.TextView;

/**
 * Created by Jack on 2015/2/13.
 * 扩展标准ListView,滚动时通知最后一个item是否显示或隐藏
 */
public class ListView extends android.widget.ListView{
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "dhtvLibrary->ListView";

    private boolean mLastItemVisible = false;
    private OnScrollListener mOnScrollListener;
    private ScrollListenerInterceptor mScrollListenerInterceptor;
    private LastItemObserver mLastItemObserver;

    {
        setScrollListenerInterceptor(new ScrollListenerInterceptor());
    }

    public ListView(Context context) {
        this(context,null);
    }

    public ListView(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    public ListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    public void setOnScrollListener(OnScrollListener l){
        mOnScrollListener = l;
        setScrollListenerInterceptor(mScrollListenerInterceptor);
    }


    public void setLastItemObserver(LastItemObserver mLastItemObserver) {
        this.mLastItemObserver = mLastItemObserver;
    }

    private void setScrollListenerInterceptor(ScrollListenerInterceptor interceptor){
        mScrollListenerInterceptor = interceptor;
        super.setOnScrollListener(mScrollListenerInterceptor);
    }

    public interface LastItemObserver{
        void onLastItemVisibleWhenScrolling();
        void onLastItemVisible();
        void onLastItemInvisibleWhenScrolling();
    }

    /**
     * 需要接收到absListView中OnScrollListener的事件，但是又要继续暴露OnScrollListener接口，
     * 于是编写了这个类，截取OnScroll事件，再将事件原样派发，派发使用新的OnScrollListener实例，表现与absListView一样。
     */
    private class ScrollListenerInterceptor implements OnScrollListener{

        /*
        滚动状态发生改变时被调用
         */
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(DEBUG){
                Log.d("onScrollStateChanged",".."+scrollState);
            }

            //派发
            if(mOnScrollListener != null) {
                mOnScrollListener.onScrollStateChanged(view, scrollState);
            }
        }

        /*
        正在滚动时被调用，API21注释有误
         */
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {


            if((totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount - 1)){
                mLastItemVisible = true;
                if(DEBUG){
                    Log.d(LOG_TAG,"Last Item visible");
                }
            }else{
                mLastItemVisible = false;
                if(DEBUG){
                    Log.d(LOG_TAG,"Last Item invisible");
                }
            }
            if(mLastItemVisible == true && mLastItemObserver != null){
                mLastItemObserver.onLastItemVisibleWhenScrolling();

            }
            if(mLastItemVisible == false && mLastItemObserver != null){
                mLastItemObserver.onLastItemInvisibleWhenScrolling();
            }


            //派发
            if(mOnScrollListener != null) {
                mOnScrollListener.onScroll(view,firstVisibleItem,visibleItemCount,totalItemCount);
            }
        }
    }
}
