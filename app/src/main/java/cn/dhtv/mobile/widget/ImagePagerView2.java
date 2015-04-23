package cn.dhtv.mobile.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.dhtv.android.adapter.BasePagerAdapter;
import cn.dhtv.mobile.R;

/**
 * Created by Jack on 2015/4/21.
 */
public class ImagePagerView2 extends FrameLayout implements ViewPager.OnPageChangeListener{
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = false;


    ViewPager mViewPager;
    ImagePagerAdapter mPagerAdapter;
    BasePagerAdapter.PageFactory mPageFactory;
    TextView mTextView;
    PictureIndicator mPictureIndicator;

    public ImagePagerView2(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOnPageChangeListener(this);

        mTextView = (TextView) findViewById(R.id.title);
        mPictureIndicator = (PictureIndicator) findViewById(R.id.picture_indicator);

    }

    public void setPageFactory(BasePagerAdapter.PageFactory pageFactory){
        if(DEBUG){
            Log.d(LOG_TAG, "setPageFactory");
        }
        mPageFactory = pageFactory;
        if(mPagerAdapter == null){
            mPagerAdapter = new ImagePagerAdapter(mPageFactory,null);

        }else{
            mPagerAdapter.setPageFactory(pageFactory);
        }

        mPagerAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                resetIndicator();
            }
        });
        mViewPager.setAdapter(mPagerAdapter);

        resetIndicator();

    }

    public ViewPager getViewPager(){
        return mViewPager;
    }

    private void resetIndicator(){
        mPictureIndicator.setUpIndicator(mPagerAdapter.getCount());
        if(mPagerAdapter.getCount() != 0){
            select(mViewPager.getCurrentItem());
        }
    }

    private void select(int position){
        if(position < 0){
            return;
        }
        mPictureIndicator.select(position);
        mTextView.setText(mPageFactory.getPageTitle(position));
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
       /* if(DEBUG){
            Log.d(LOG_TAG,"onPageScrolled:"+position);
        }*/
    }

    @Override
    public void onPageSelected(int position) {
        select(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(DEBUG){
            Log.d(LOG_TAG,"onPageScrollStateChanged:"+state);
        }
    }


    private class ImagePagerAdapter extends BasePagerAdapter{
        private ImagePagerAdapter(PageFactory pageFactory, PageHolder PageHolder) {
            super(pageFactory, PageHolder);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_UNCHANGED;
        }
    }


}
