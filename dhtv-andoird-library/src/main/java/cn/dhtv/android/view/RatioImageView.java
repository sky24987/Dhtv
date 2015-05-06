package cn.dhtv.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import cn.dhtv.android.R;

/**
 * Created by Jack on 2015/4/30.
 */
public class RatioImageView extends ImageView {
    private float mRatio = 16.0f/9;

    public RatioImageView(Context context) {
        super(context);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /*int widthWeight = attrs.getAttributeIntValue(R.attr.ratio_width_weight,0);
        int heightWeight = attrs.getAttributeIntValue(R.attr.ratio_height_weight,0);*/

        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.RatioImageView);
        int widthWeight = a.getInt(R.styleable.RatioImageView_ratio_width_weight,0);
        int heightWeight = a.getInt(R.styleable.RatioImageView_ratio_height_weight,0);
        a.recycle();
        if(widthWeight > 0 && heightWeight > 0){
            mRatio = ((float)widthWeight)/heightWeight;
        }
    }

    public float getRatio() {
        return mRatio;
    }

    public void setRatio(float ratio) {
        this.mRatio = ratio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width =  MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if(layoutParams.width != ViewGroup.LayoutParams.WRAP_CONTENT){
            width = MeasureSpec.getSize(widthMeasureSpec);
            height = (int) (width / mRatio);
            setMeasuredDimension(width,height);
            return;
        }else if(layoutParams.height !=ViewGroup.LayoutParams.WRAP_CONTENT){
            height = MeasureSpec.getSize(heightMeasureSpec);
            width = (int) (height*mRatio);
            setMeasuredDimension(width,height);
            return;
        }else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }


    }
}
