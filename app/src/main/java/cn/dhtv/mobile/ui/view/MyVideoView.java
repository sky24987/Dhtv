package cn.dhtv.mobile.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.VideoView;

/**
 * Created by Jack on 2015/5/13.
 */
public class MyVideoView extends VideoView{
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    public MyVideoView(Context context) {
        super(context);
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(DEBUG){
            Log.d(LOG_TAG,MeasureSpec.toString(widthMeasureSpec)+"--"+MeasureSpec.toString(heightMeasureSpec));
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
