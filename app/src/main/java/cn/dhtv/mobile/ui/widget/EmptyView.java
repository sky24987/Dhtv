package cn.dhtv.mobile.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.dhtv.mobile.R;

/**
 * Created by Jack on 2015/4/16.
 */
public class EmptyView extends RelativeLayout {


    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private static final int STATE_IDLE = 0;
    private static final int STATE_PROCESSING = 1;
    private static final int STATE_ACTIVE = 2;
    private static final int STATE_FAIL = -1;

    private OnProcessingListener mOnProcessingListener;

    private TextView hint;
    private ProgressBar progressBar;
    private int mState = STATE_IDLE;


    public EmptyView(Context context) {
        this(context, null);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setStateProcessing();
            }
        });
        setStateActive();
    }

    @Override
    protected void onFinishInflate() {
        hint = (TextView) findViewById(R.id.hint);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

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

    public boolean isActive(){
        return mState == STATE_ACTIVE;
    }

    public boolean isFail(){
        return mState == STATE_FAIL;
    }

    public void setStateIdle(){
        mState = STATE_IDLE;
        setClickable(false);
        hint.setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);
    }

    public void setStateActive(){
        mState = STATE_ACTIVE;
        setClickable(false);
    }

    public void setStateProcessing(){
        mState = STATE_PROCESSING;
        setClickable(false);
        hint.setText(getContext().getString(R.string.empty_view_hint));
        hint.setVisibility(VISIBLE);
        progressBar.setVisibility(VISIBLE);
        if(mOnProcessingListener !=null){
            mOnProcessingListener.onProcessing();
        }
    }

    public void setStateFail(){
        mState = STATE_FAIL;
        setClickable(true);
        hint.setText(getContext().getString(R.string.empty_view_fail));
        hint.setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);
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
