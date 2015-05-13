package cn.dhtv.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by Jack on 2015/5/11.
 */
public class RatioFrameLayout extends FrameLayout{
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private float mRatio = 16.0f/9;

    public RatioFrameLayout(Context context) {
        super(context);
    }

    public RatioFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RatioFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*if(DEBUG){
//            Log.d(LOG_TAG,"widthMeasureSpec:"+widthMeasureSpec+"--heightMeasureSpec:"+heightMeasureSpec);
//            Log.d(LOG_TAG,"width:"+MeasureSpec.getSize(widthMeasureSpec)+"--height:"+MeasureSpec.getSize(heightMeasureSpec));
//            Log.d(LOG_TAG,"widthMode:"+MeasureSpec.getMode(widthMeasureSpec)+"--heightMode:"+MeasureSpec.getMode(heightMeasureSpec));
            Log.d(LOG_TAG,MeasureSpec.toString(widthMeasureSpec)+"--"+MeasureSpec.toString(heightMeasureSpec));
        }*/


        int width =  MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if(layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT && layoutParams.height != ViewGroup.LayoutParams.MATCH_PARENT){
            height = (int) (width / mRatio);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.getMode(widthMeasureSpec));
        }else if(layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT && layoutParams.width != ViewGroup.LayoutParams.MATCH_PARENT){
            width = (int) (height*mRatio);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,MeasureSpec.getMode(heightMeasureSpec));
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
