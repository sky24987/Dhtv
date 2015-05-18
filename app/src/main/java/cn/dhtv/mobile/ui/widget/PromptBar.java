package cn.dhtv.mobile.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import org.w3c.dom.Text;

import cn.dhtv.mobile.R;

/**
 * Created by Jack on 2015/5/18.
 */
public class PromptBar extends CardView {
    private final int SHOW_TIME = 2400;

    private boolean mShowing = false;
    private TextView mTextView;

    private Animation mFadeIn;
    private Animation mFadeOut;
    private Runnable mFadeOutRunnable = new Runnable() {
        @Override
        public void run() {
//            mFadeOut.cancel();
            startAnimation(mFadeOut);

        }
    };



    public PromptBar(Context context) {
        this(context, null);
    }

    public PromptBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PromptBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mFadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.prompt_fade_in);
        mFadeOut = AnimationUtils.loadAnimation(getContext(),R.anim.prompt_fade_out);
        mFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mShowing = true;
                setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                int a = 0;
            }
        });
        mFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mShowing = false;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTextView = (TextView) findViewById(R.id.hint);
    }

    public void show(String hint){
        mTextView.setText(hint);
        removeCallbacks(mFadeOutRunnable);
        if(mShowing == false) {
//            mFadeIn.cancel();
            startAnimation(mFadeIn);
        }
        postDelayed(mFadeOutRunnable, SHOW_TIME);
    }

    public void hide(){
        removeCallbacks(mFadeOutRunnable);
        startAnimation(mFadeOut);
    }
}
