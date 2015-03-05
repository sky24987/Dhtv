package cn.dhtv.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.dhtv.android.R;

/**
 * Created by Jack on 2015/3/4.
 * 可设置底部刷新视图的ListView
 */
public class FooterRefreshListView extends ListView {
    private static final String LOG_TAG = "'dhtvlib'.FooterRefreshListView";
    private static final boolean DEBUG = true;

    private View mRefreshFooter;
    private OnRefreshFooterListener mOnRefreshFooterListener;
    private RefreshFooterStatus mRefreshFooterStatus = RefreshFooterStatus.CLICKABLE;

    public FooterRefreshListView(Context context) {
        this(context,null);
    }

    public FooterRefreshListView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public FooterRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public void setOnRefreshFooterListener(OnRefreshFooterListener mOnRefreshFooterListener) {
        this.mOnRefreshFooterListener = mOnRefreshFooterListener;
        setRefreshFooterStatus(mRefreshFooterStatus);
    }

    public View getRefreshFooter() {
        return mRefreshFooter;
    }

    public void setRefreshFooter(View mRefreshFooter) {
        removeFooterView(this.mRefreshFooter);
        this.mRefreshFooter = mRefreshFooter;
        addFooterView(this.mRefreshFooter);
        setRefreshFooterStatus(mRefreshFooterStatus);
    }

    public void setRefreshFooterStatus(RefreshFooterStatus status){
        mRefreshFooterStatus = status;
        if(mOnRefreshFooterListener != null){
            switch (mRefreshFooterStatus){
                case CLICKABLE:
                    mOnRefreshFooterListener.onClickable();
                    break;
                case REFRESHING:
                    mOnRefreshFooterListener.onRefreshing();
                    break;
                case NO_MORE:
                    mOnRefreshFooterListener.onNoMore();
                    break;
                case FORCE_CLICK_STATE:
                    mOnRefreshFooterListener.onForceClickState();
                    break;
            }
        }

        if(DEBUG){
            Log.d(LOG_TAG,"setRefreshFooterStatus:"+status);
        }
    }

    public RefreshFooterStatus getRefreshFooterStatus() {
        return mRefreshFooterStatus;
    }

    public enum RefreshFooterStatus{
        CLICKABLE,REFRESHING,NO_MORE,FORCE_CLICK_STATE
    }

    public interface OnRefreshFooterListener{
        void onClickable();
        void onRefreshing();
        void onNoMore();
        void onForceClickState();
    }

}
