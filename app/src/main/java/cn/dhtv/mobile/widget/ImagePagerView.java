package cn.dhtv.mobile.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.dhtv.android.adapter.BasePagerAdapter;
import cn.dhtv.mobile.R;

/**
 * Created by Jack on 2015/3/25.
 */
public class ImagePagerView extends FrameLayout implements ViewPager.OnPageChangeListener{
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = false;

    ViewPager mViewPager;
    ImagePagerAdapter mPagerAdapter;
    BasePagerAdapter.PageFactory mPageFactory;
    TextView mTextView;
    LinearLayout dotContainer;
    ImageView[] dots;
    Context mContext;
    public ImagePagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOnPageChangeListener(this);
        mTextView = (TextView) findViewById(R.id.title);
        dotContainer = (LinearLayout) findViewById(R.id.dot_container);
    }

    public void setPageFactory(BasePagerAdapter.PageFactory pageFactory){
        if(DEBUG){
            Log.d(LOG_TAG,"setPageFactory");
        }
        mPageFactory = pageFactory;
        if(mPagerAdapter == null){
            mPagerAdapter = new ImagePagerAdapter(mPageFactory,null);
            mViewPager.setAdapter(mPagerAdapter);
        }else{
            mPagerAdapter.setPageFactory(pageFactory);
            mPagerAdapter.notifyDataSetChanged();
        }
        dots = new ImageView[mPageFactory.pageCount()];
        dotContainer.removeAllViews();
        for(int i=0;i<dots.length;++i){
            dots[i] = new ImageView(mContext);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dots[i].setLayoutParams(lp);
            dots[i].setImageResource(R.drawable.dot);
            dots[i].setAdjustViewBounds(true);
            dotContainer.addView(dots[i]);

        }
        if(DEBUG){
            Log.d(LOG_TAG,"dotContainer child size:"+dotContainer.getChildCount());
        }
        onPageSelected(0);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(DEBUG){
            Log.d(LOG_TAG,"onPageScrolled:"+position);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if(DEBUG){
            Log.d(LOG_TAG,"onPageSelected:"+position);
        }

        for(int i=0;i<dots.length;++i){
            dots[i].setImageResource(R.drawable.dot);
        }
        dots[position].setImageResource(R.drawable.dot_on);

        mTextView.setText(mPageFactory.getPageTitle(position));
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
