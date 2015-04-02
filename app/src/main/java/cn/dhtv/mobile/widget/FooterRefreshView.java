package cn.dhtv.mobile.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
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
    private Status status = Status.CLICKABLE;

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
        switch (status){
            case CLICKABLE:
                status = Status.CLICKABLE;
                textHint.setText(R.string.refresh_footer_clickable);
                mProgressBar.setVisibility(GONE);
                break;
            case REFRESHING:
                status = Status.REFRESHING;
                textHint.setText(R.string.refresh_footer_refreshing);
                mProgressBar.setVisibility(VISIBLE);
                if(mRefreshingListener != null){
                    mRefreshingListener.onRefreshing();
                }
                break;
            case FORCE_CLICK:
                status = Status.FORCE_CLICK;
                textHint.setText(R.string.refresh_footer_clickable);
                mProgressBar.setVisibility(GONE);
                break;
            case NO_MORE:
                status = Status.NO_MORE;
                textHint.setText(R.string.refresh_footer_no_more);
                mProgressBar.setVisibility(GONE);
                break;
        }
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
