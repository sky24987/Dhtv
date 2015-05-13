package cn.dhtv.mobile.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import cn.dhtv.mobile.R;
import cn.dhtv.mobile.fragment.VideoPlayerFragment;

public class VideoPlayerActivity extends ActionBarActivity {
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private VideoPlayerFragment mVideoPlayerFragment;
    private FragmentManager mFragmentManager;

    private Uri mVideoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }


        /*if(DEBUG){
            Log.d(LOG_TAG,"onCreate");
        }*/
        setContentView(R.layout.activity_video_player);
        mVideoUri = getIntent().getData();
        mFragmentManager = getFragmentManager();
        VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment) mFragmentManager.findFragmentById(R.id.fragment_video);
        videoPlayerFragment.setVideoURI(mVideoUri);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
