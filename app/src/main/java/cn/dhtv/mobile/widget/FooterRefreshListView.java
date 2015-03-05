package cn.dhtv.mobile.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.dhtv.mobile.R;

/**
 * Created by Jack on 2015/3/5.
 */
public class FooterRefreshListView extends cn.dhtv.android.widget.FooterRefreshListView {
    private static final String LOG_TAG = "'app'.FooterRefreshListView";
    private static boolean DEBUG = true;

    private FooterRefreshListener mFooterRefreshListener;

    public FooterRefreshListView(Context context) {
        this(context,null);
    }

    public FooterRefreshListView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public FooterRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View refreshFooter = LayoutInflater.from(context).inflate(R.layout.widget_list_refresh_footer,null);
        refreshFooter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setRefreshFooterStatus(RefreshFooterStatus.REFRESHING);
            }
        });
        setRefreshFooter(refreshFooter);
        setOnRefreshFooterListener(new OnRefreshFooterListener() {
            @Override
            public void onClickable() {
                if(getRefreshFooter() != null){
                    getRefreshFooter().setClickable(true);
                    ((TextView)getRefreshFooter().findViewById(R.id.refresh_footer_hint)).setText(R.string.refresh_footer_clickable);
                    ((ProgressBar)getRefreshFooter().findViewById(R.id.progressBar)).setVisibility(GONE);
                }
            }

            @Override
            public void onRefreshing() {
                if(getRefreshFooter() != null){
                    getRefreshFooter().setClickable(false);
                    ((TextView)getRefreshFooter().findViewById(R.id.refresh_footer_hint)).setText(R.string.refresh_footer_refreshing);
                    ((ProgressBar)getRefreshFooter().findViewById(R.id.progressBar)).setVisibility(VISIBLE);
                    if(mFooterRefreshListener != null){
                        mFooterRefreshListener.onFooterRefreshing();
                    }
                }
            }

            @Override
            public void onNoMore() {
                if(getRefreshFooter() != null){
                    getRefreshFooter().setClickable(false);
                    ((TextView)getRefreshFooter().findViewById(R.id.refresh_footer_hint)).setText(R.string.refresh_footer_no_more);
                    ((ProgressBar)getRefreshFooter().findViewById(R.id.progressBar)).setVisibility(GONE);
                }
            }

            @Override
            public void onForceClickState() {
                if(getRefreshFooter() != null){
                    getRefreshFooter().setClickable(true);
                    ((TextView)getRefreshFooter().findViewById(R.id.refresh_footer_hint)).setText(R.string.refresh_footer_clickable);
                    ((ProgressBar)getRefreshFooter().findViewById(R.id.progressBar)).setVisibility(GONE);
                }
            }
        });

        setLastItemObserver(new LastItemObserver() {
            @Override
            public void onLastItemVisibleWhenScrolling() {
                if(getRefreshFooterStatus() != RefreshFooterStatus.REFRESHING && getRefreshFooterStatus() != RefreshFooterStatus.FORCE_CLICK_STATE){
                    setRefreshFooterStatus(RefreshFooterStatus.REFRESHING);
                }
            }

            @Override
            public void onLastItemInvisibleWhenScrolling() {
                if(getRefreshFooterStatus() == RefreshFooterStatus.FORCE_CLICK_STATE){
                    setRefreshFooterStatus(RefreshFooterStatus.CLICKABLE);
                }
            }

            @Override
            public void onLastItemVisible() {

            }
        });
    }

    public void setFooterRefreshListener(FooterRefreshListener mFooterRefreshListener) {
        this.mFooterRefreshListener = mFooterRefreshListener;
    }

    public interface FooterRefreshListener {
        void onFooterRefreshing();
    }
}
