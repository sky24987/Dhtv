package cn.dhtv.mobile.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;

/**
 * Created by Jack on 2015/4/7.
 */
public class AudioService extends Service implements MediaPlayer.OnPreparedListener{
    private LocalBinder mLocalBinder = new LocalBinder();
    private CallBacks mCallBack;
    private MediaPlayer mMediaPlayer;


    private boolean audioPrepared = false;

    @Override
    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }

    public void resetAudio (String url) throws IOException{
        if(mMediaPlayer != null){
            mMediaPlayer.setOnErrorListener(null);
            mMediaPlayer.setOnPreparedListener(null);
            mMediaPlayer.setDisplay(null);
            mMediaPlayer.release();
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(url);
        mMediaPlayer.setOnPreparedListener(this);
        audioPrepared = false;
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mMediaPlayer != null){
            mMediaPlayer.setOnErrorListener(null);
            mMediaPlayer.setOnPreparedListener(null);
            mMediaPlayer.setDisplay(null);
            mMediaPlayer.release();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        audioPrepared = true;
        if(mCallBack != null){
            mCallBack.OnAudioPrepared();
        }
    }

    public void setCallBack(CallBacks callBack) {
        this.mCallBack = callBack;
    }

    public class LocalBinder extends Binder{
        public void resetAudio(String url) throws IOException{
            AudioService.this.resetAudio(url);
        }

        public void play(){
            mMediaPlayer.start();
        }

        public void pause(){
            mMediaPlayer.pause();
        }

        public void setCallBacks(CallBacks callBacks){
            mCallBack = callBacks;
        }

        public boolean isPlaying(){
            return mMediaPlayer.isPlaying();
        }

        public boolean isAudioPrepared(){
            return audioPrepared;
        }

    }

    public interface CallBacks {
        void OnAudioPrepared();
    }
}
