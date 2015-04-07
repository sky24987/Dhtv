package cn.dhtv.mobile;

import android.app.Application;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import cn.dhtv.mobile.model.NewsPageManager;
import cn.dhtv.mobile.model.ProgramPageManager;
import cn.dhtv.mobile.model.VideoPageManager;
import cn.dhtv.mobile.network.NetUtils;
import cn.dhtv.mobile.service.AudioService;

/**
 * Created by Jack on 2015/2/11.
 */
public class MyApplication extends Application {
    private  final String LOG_TAG = getClass().getSimpleName();
    private  final boolean DEBUG = true;

    private MediaPlayer mMediaPlayer;

    private NewsPageManager mNewsPageManager;
    private VideoPageManager mVideoPageManager;
    private ProgramPageManager mProgramPageManager;

    @Override
    public void onCreate() {
        super.onCreate();
        NetUtils.setup(this);
        mNewsPageManager = new NewsPageManager();
        mNewsPageManager.setUp();
        mVideoPageManager = new VideoPageManager();
        mVideoPageManager.setUp();
        mProgramPageManager = new ProgramPageManager();
        mProgramPageManager.setUp();
        if(DEBUG){
            Log.d(LOG_TAG,"onCreate()");
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        NetUtils.release();
        if(DEBUG){
            Log.d(LOG_TAG,"onTerminate()");
        }

    }

    public void startAudioService(){
        Intent intent = new Intent(this, AudioService.class);
        startService(intent);
    }

    public void stopAudioService(){
        Intent intent = new Intent(this, AudioService.class);
        stopService(intent);
    }

    public MediaPlayer getMediaPlayer(){
        if(mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();
        }
        mMediaPlayer.reset();
        return mMediaPlayer;
    }

    public void releaseMediaPlayer(){
        mMediaPlayer.release();
        mMediaPlayer = null;
    }


    public NewsPageManager getNewsPageManager(){
        return mNewsPageManager;
    }

    public VideoPageManager getVideoPageManager(){return  mVideoPageManager;}

    public ProgramPageManager getProgramPageManager() {
        return mProgramPageManager;
    }
}
