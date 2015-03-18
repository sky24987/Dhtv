package cn.dhtv.mobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import cn.dhtv.mobile.R;

public class WelcomeActivity extends Activity {
    private FrameLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayout = (FrameLayout)getLayoutInflater().inflate(R.layout.activity_welcome,null);
        setContentView(mLayout);



    }

    @Override
    protected void onResume() {
        super.onResume();
        AlphaAnimation aa = new AlphaAnimation(0.3f,1.0f);
        aa.setDuration(2000);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                toNextActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mLayout.setAnimation(aa);
    }

    private void toNextActivity(){
        Intent intent = new Intent(WelcomeActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
