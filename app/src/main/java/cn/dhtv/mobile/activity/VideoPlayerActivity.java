package cn.dhtv.mobile.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import cn.dhtv.android.widget.MediaController;
import cn.dhtv.mobile.Contract;
import cn.dhtv.mobile.R;
import cn.dhtv.mobile.fragment.VideoPlayerFragment;

public class VideoPlayerActivity extends ActionBarActivity {
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private VideoPlayerFragment mVideoPlayerFragment;
    private FragmentManager mFragmentManager;
    private MediaController mMediaController;

    private Uri mVideoUri;
    private String mVideoTitle;//视频标题
    private String mSummary;//视频简介
    private String mPic_url;//图片地址
    private String mWeb_url;//网页地址

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
        mVideoTitle = getIntent().getStringExtra(Intent.EXTRA_TITLE);
        mSummary = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        mPic_url = getIntent().getStringExtra(Contract.INTENT_EXTRA_IMG_URL);
        mWeb_url = getIntent().getStringExtra(Intent.EXTRA_ORIGINATING_URI);
        mFragmentManager = getFragmentManager();
        VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment) mFragmentManager.findFragmentById(R.id.fragment_video);
        mMediaController = videoPlayerFragment.getMediaController();
        mMediaController.disableFullScreenButton();
        mMediaController.setHeaderMode(MediaController.HEADER_MODE_AWAYS_SHOW);
        mMediaController.setTitle(mVideoTitle);
        mMediaController.setMediaControllerCallBacks(new MediaController.MediaControllerCallBacks() {
            @Override
            public void onFullScreenButtonClick(View fullScreenButton) {

            }

            @Override
            public void onCancelButtonClick(View cancelButton) {
                finish();
            }
        });
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
