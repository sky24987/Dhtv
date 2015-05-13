package cn.dhtv.android.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Formatter;
import java.util.Locale;

import cn.dhtv.android.R;

/**
 * Created by Jack on 2015/5/12.
 */
public class MediaController extends FrameLayout{
    private static final int PROGRESS_BAR_MAX = 1000;
    private static final int    sDefaultTimeout = 4000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;

    private boolean mShowing = false;

    private android.widget.MediaController.MediaPlayerControl mPlayer;

    private ImageButton mPlayButton;
    private ImageButton mScreenCfgButton;
    private SeekBar mSeekBar;
    private TextView mCurrentTimeTextView, mTimeTextView;


    StringBuilder mFormatBuilder = new StringBuilder();
    Formatter  mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());



    public MediaController(Context context) {
        this(context, null);
    }

    public MediaController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        /*View view =*/ LayoutInflater.from(context).inflate(R.layout.widget_video_controller,this,true);
        mPlayButton = (ImageButton) findViewById(R.id.button_play);
        mScreenCfgButton = (ImageButton) findViewById(R.id.button_screen_configure);
        mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        mCurrentTimeTextView = (TextView) findViewById(R.id.time_current);
        mTimeTextView = (TextView) findViewById(R.id.time);

        mSeekBar.setMax(PROGRESS_BAR_MAX);
        mTimeTextView.setText(stringForTime(0));
        mCurrentTimeTextView.setText(stringForTime(0));

        mShowing = false;
        setVisibility(GONE);

    }

    public void setMediaPlayer(android.widget.MediaController.MediaPlayerControl player){
        mPlayer = player;
        updatePausePlay();
    }

    public void show(){
        show(sDefaultTimeout);
    }

    public void show(int timeout){
        if(!mShowing){
            setProgress();
            mShowing = true;
            setVisibility(VISIBLE);

        }

        updatePausePlay();

        // cause the progress bar to be updated even if mShowing
        // was already true.  This happens, for example, if we're
        // paused with the progress bar showing the user hits play.
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        Message msg = mHandler.obtainMessage(FADE_OUT);
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }

    }

    public void hide(){
        if(mShowing){
            mShowing = false;
            setVisibility(GONE);
            mHandler.removeMessages(SHOW_PROGRESS);
        }
    }

    private void updatePausePlay(){
        if(mPlayer == null){
            return;
        }

        if (mPlayer.isPlaying()) {
            mPlayButton.setImageResource(android.R.drawable.ic_media_play);
        } else {
            mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    private int setProgress(){
        if(mPlayer == null){
            return 0;
        }

        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();
        if (mSeekBar != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                mSeekBar.setProgress( (int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            mSeekBar.setSecondaryProgress(percent * 10);
        }

        if (mTimeTextView != null)
            mTimeTextView.setText(stringForTime(duration));
        if (mCurrentTimeTextView != null)
            mCurrentTimeTextView.setText(stringForTime(position));

        return position;
    }

    public boolean isShowing() {
        return mShowing;
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int pos;
            switch (msg.what) {
                case FADE_OUT:
                    hide();
                    break;
                case SHOW_PROGRESS:
                    pos = setProgress();
                    if (mShowing &&mPlayer!=null && mPlayer.isPlaying()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
            }
        }
    };








}
