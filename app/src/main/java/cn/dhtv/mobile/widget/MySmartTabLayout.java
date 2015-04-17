package cn.dhtv.mobile.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

/**
 * Created by Jack on 2015/4/17.
 */
public class MySmartTabLayout extends SmartTabLayout{
    public MySmartTabLayout(Context context) {
        super(context);
    }

    public MySmartTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySmartTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final boolean lockedExpanded = widthMode == MeasureSpec.EXACTLY;
        setFillViewport(lockedExpanded);*/

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }
}
