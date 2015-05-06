package cn.dhtv.mobile.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by Jack on 2015/4/20.
 */
public class MyRatioViewPager extends ViewPager{
    private float mRatio = 16.0f/9;

    public MyRatioViewPager(Context context) {
        super(context);
    }

    public MyRatioViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width =  MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if(layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT){
            width = MeasureSpec.getSize(widthMeasureSpec);
            height = (int) (width / mRatio);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.getMode(widthMeasureSpec));
        }else if(layoutParams.height ==ViewGroup.LayoutParams.MATCH_PARENT){
            height = MeasureSpec.getSize(heightMeasureSpec);
            width = (int) (height*mRatio);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,MeasureSpec.getMode(heightMeasureSpec));
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
