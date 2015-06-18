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

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView);
        float ratio = a.getFloat(R.styleable.RatioImageView_ratio, -1);
        if(ratio > 0){
            mRatio = ratio;
        }else {
            int widthWeight = a.getInt(R.styleable.RatioImageView_ratio_width_weight,0);
            int heightWeight = a.getInt(R.styleable.RatioImageView_ratio_height_weight,0);

            if(widthWeight > 0 && heightWeight > 0){
                mRatio = ((float)widthWeight)/heightWeight;
            }
        }
        a.recycle();

    }

    public float getRatio() {
        return mRatio;
    }

    public void setRatio(float ratio) {
        this.mRatio = ratio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*int width =  MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);


        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if(layoutParams.width != ViewGroup.LayoutParams.WRAP_CONTENT){
            width = MeasureSpec.getSize(widthMeasureSpec);
            height = (int) (width / mRatio);
            setMeasuredDimension(width,height);
//            super.onMeasure(MeasureSpec.makeMeasureSpec(width,MeasureSpec.getMode(widthMeasureSpec)),MeasureSpec.makeMeasureSpec(height,MeasureSpec.getMode(heightMeasureSpec)));
            return;
        }else if(layoutParams.height !=ViewGroup.LayoutParams.WRAP_CONTENT){
            height = MeasureSpec.getSize(heightMeasureSpec);
            width = (int) (height*mRatio);
            setMeasuredDimension(width,height);
//            super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.getMode(widthMeasureSpec)), MeasureSpec.makeMeasureSpec(height, MeasureSpec.getMode(heightMeasureSpec)));
            return;
        }else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
*/

        int layoutWidth = getLayoutParams().width;
        int layoutHeight = getLayoutParams().height;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if(layoutWidth == 0){
            width = (int) (height*mRatio);
            setMeasuredDimension(width,height);
//            super.onMeasure(MeasureSpec.makeMeasureSpec(width,heightMode),heightMeasureSpec);
            return;
        }else if(layoutHeight == 0){
            height = (int) (width / mRatio);
            setMeasuredDimension(width,height);
//            super.onMeasure(widthMeasureSpec,MeasureSpec.makeMeasureSpec(height,widthMode));
            return;
        }else {
            super.onMeasure(widthMeasureSpec,heightMeasureSpec);
            return;
        }

    }
}
