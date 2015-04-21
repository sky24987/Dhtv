package cn.dhtv.mobile.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.exoplayer.dash.mpd.SegmentBase;

import cn.dhtv.mobile.R;

/**
 * Created by Jack on 2015/4/21.
 */
public class PictureIndicator extends LinearLayout{
    private int mSelectedDiameter;
    private int mIndicatorMargin = 0 ;

    private int mCount;
    private int mCurrentPosition;
    private ImageView[] imageViews;
    private ViewPager mViewPager;



    public PictureIndicator(Context context) {
        this(context,null);
    }

    public PictureIndicator(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public PictureIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        final TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.ImageIndicator);
        mIndicatorMargin = a.getDimensionPixelSize(R.styleable.ImageIndicator_ii_indicator_margin,mIndicatorMargin);

        a.recycle();

    }

    public void setUpIndicator(int count){
        detachViewPager();
        setIndicatorCount(count);
    }


    private void detachViewPager(){
        if(mViewPager != null){
            mViewPager = null;
            mViewPager.setOnPageChangeListener(null);
        }
    }

    private void setIndicatorCount(int count){
        clearIndicators();
        mCount = count;
        imageViews = new ImageView[mCount];
        for(int i = 0; i < imageViews.length; ++ i){
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.drawable.indicator_dot);
            LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.setMargins(mIndicatorMargin,0,mIndicatorMargin,0);
            imageViews[i] = imageView;
            addView(imageView, lp);
        }
    }

    public void select(int position){
        mCurrentPosition = position;
        for(int i = 0; i < imageViews.length;++i){
            imageViews[i].setSelected(false);
        }
        imageViews[mCurrentPosition].setSelected(true);
    }

    public void setViewPager(ViewPager viewPager){
        if(viewPager == null || viewPager.getAdapter() == null){
            return;
        }
        clearIndicators();
        mViewPager = viewPager;
        mViewPager.setOnPageChangeListener(new DefaultPageChangeListener());

        setIndicatorCount(mViewPager.getAdapter().getCount());
        select(mViewPager.getCurrentItem());

    }

    private void clearIndicators(){
        mCount = 0;
        imageViews = null;
        removeAllViews();
    }

    private class DefaultPageChangeListener implements ViewPager.OnPageChangeListener{
       @Override
       public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

       }

       @Override
       public void onPageSelected(int position) {
            select(position);
       }

       @Override
       public void onPageScrollStateChanged(int state) {

       }
   }

}
