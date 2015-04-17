package cn.dhtv.mobile.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.dhtv.mobile.R;

/**
 * Created by Jack on 2015/4/16.
 */
public class EmptyView extends FrameLayout {


    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private static final int STATE_IDLE = 0;
    private static final int STATE_PROCESSING = 1;

    private OnProcessingListener mOnProcessingListener;

    private TextView hint;
    private ProgressBar progressBar;
    private int mState = STATE_IDLE;


    public EmptyView(Context context) {
        super(context);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        hint = (TextView) findViewById(R.id.hint);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        setStateIdle();
    }

    public void setOnProcessingListener(OnProcessingListener onProcessingListener) {
        this.mOnProcessingListener = onProcessingListener;
    }

    public boolean isIdle(){
        return mState == STATE_IDLE;
    }

    public boolean isProcessing(){
        return mState == STATE_PROCESSING;
    }

    public void setStateIdle(){
        mState = STATE_IDLE;
        hint.setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);
    }

    public void setStateProcessing(){
        mState = STATE_PROCESSING;
        hint.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
        if(mOnProcessingListener !=null){
            mOnProcessingListener.onProcessing();
        }
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
       /* if(DEBUG){
            Log.d(LOG_TAG,"widthMeasureSpec:"+widthMeasureSpec+",heightMeasureSpec:"+heightMeasureSpec);
        }*/
        View view = (View) getParent();
        int myMeasuredWith = view.getMeasuredWidth()-view.getPaddingLeft() - getPaddingRight();
        int myMeasuredHeight = view.getMeasuredHeight() - view.getPaddingTop() - getPaddingBottom();
        super.onMeasure(MeasureSpec.makeMeasureSpec(myMeasuredWith,MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(myMeasuredHeight,MeasureSpec.EXACTLY));
    }

    public interface OnProcessingListener{
        void onProcessing();
    }
}
