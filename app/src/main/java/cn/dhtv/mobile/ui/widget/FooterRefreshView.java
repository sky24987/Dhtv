package cn.dhtv.mobile.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.dhtv.mobile.R;

/**
 * Created by Jack on 2015/4/1.
 */
public class FooterRefreshView extends RelativeLayout {
    private ProgressBar mProgressBar;
    private TextView textHint;
    private OnRefreshingListener mRefreshingListener;
    private Status mStatus = Status.CLICKABLE;

    public FooterRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        textHint = (TextView) findViewById(R.id.refresh_footer_hint);
        setStatus(Status.CLICKABLE);
    }

    public void setStatus(Status status){
        if(mStatus == status){
            return;
        }
        mStatus = status;
        switch (status){
            case CLICKABLE:
                textHint.setText(R.string.refresh_footer_clickable);
                mProgressBar.setVisibility(GONE);
                break;
            case REFRESHING:
                textHint.setText(R.string.refresh_footer_refreshing);
                mProgressBar.setVisibility(VISIBLE);
                if(mRefreshingListener != null){
                    mRefreshingListener.onRefreshing();
                }
                break;
            case FORCE_CLICK:
                textHint.setText(R.string.refresh_footer_clickable);
                mProgressBar.setVisibility(GONE);
                break;
            case NO_MORE:
                textHint.setText(R.string.refresh_footer_no_more);
                mProgressBar.setVisibility(GONE);
                break;
        }
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setRefreshingListener(OnRefreshingListener refreshingListener) {
        this.mRefreshingListener = refreshingListener;
    }

    public static enum  Status{
        CLICKABLE,REFRESHING,FORCE_CLICK,NO_MORE
    }

    public interface OnRefreshingListener{
        void onRefreshing();
    }
}
