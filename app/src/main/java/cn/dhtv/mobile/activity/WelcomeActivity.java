package cn.dhtv.mobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import cn.dhtv.mobile.R;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout layout = (FrameLayout)getLayoutInflater().inflate(R.layout.activity_welcome,null);
        setContentView(layout);

        AlphaAnimation aa = new AlphaAnimation(0.3f,1.0f);
        aa.setDuration(2000);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                toNextActivity();
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        layout.setAnimation(aa);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void toNextActivity(){
        Intent intent = new Intent(WelcomeActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
