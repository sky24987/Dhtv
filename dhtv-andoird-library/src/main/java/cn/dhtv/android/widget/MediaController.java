package cn.dhtv.android.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Formatter;
import java.util.Locale;

import cn.dhtv.android.R;

/**
 * Created by Jack on 2015/5/12.
 */
public class MediaController extends FrameLayout{


    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;




    private static final int PROGRESS_BAR_MAX = 1000;
    private static final int    sDefaultTimeout = 4000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;

    public static final int HEADER_MODE_AUTO = 1;
    public static final int HEADER_MODE_AWAYS_HIDE = 2;
    public static final int HEADER_MODE_AWAYS_SHOW = 3;


    private boolean mMotionCaptureDown = false;
    private boolean mMotionCaptureUp = false;
    private boolean mMotionCaptureCancel = false;

    private boolean mShowing = false;

    private android.widget.MediaController.MediaPlayerControl mPlayer;

    private boolean mShowFullScreenButton = true;
    private boolean mEnableSeekBar = true;
    private boolean mEnableCancelButton = true;
    private boolean mShowHeader = true;

    private int mHeaderMode =HEADER_MODE_AUTO;
    private boolean mFullScreen = false;


    private MediaControllerCallBacks mMediaControllerCallBacks;


    private View mAnchor;
    private View mRoot;
    private PopupWindow mWindow;


    private ImageButton mPlayButton;
    private ImageButton mFullScreenButton;
    private SeekBar mSeekBar;
    private TextView mCurrentTimeTextView, mTimeTextView;
    private TextView mTitle;
    private ImageButton mCancelButton,mShareButton;
    private View mHeader;


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
        /*ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(lp);*/
        setFocusable(true);

        initFloatingWindow();
        View view = makeControllerView(context);
        initView(view);
        this.addView(view/*,new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)*/);
        mRoot = this;
        mWindow.setContentView(mRoot);

        mShowing = false;

        setHeaderMode(mHeaderMode);
        /*setVisibility(GONE);*/

    }

    public void setAnchorView(View view){
        mAnchor = view;
        mAnchor.addOnLayoutChangeListener(mOnLayoutChangeListener);
//        mAnchor.setOnSystemUiVisibilityChangeListener(mOnSystemUiVisibilityChangeListener);
    }

    public void setMediaPlayer(android.widget.MediaController.MediaPlayerControl player){
        mPlayer = player;
        updatePlayState();
    }

    public void setMediaControllerCallBacks(MediaControllerCallBacks mediaControllerCallBacks) {
        this.mMediaControllerCallBacks = mediaControllerCallBacks;
    }

    public void setTitle(String title){
        mTitle.setText(title);
    }

    public void setHeaderMode(int mode){
        mHeaderMode = mode;
        switch (mHeaderMode){
            case HEADER_MODE_AUTO:
                if(mFullScreen){
                    showHeader();
                }else {
                    hideHeader();
                }
                break;
            case HEADER_MODE_AWAYS_HIDE:
                hideHeader();
                break;
            case HEADER_MODE_AWAYS_SHOW:
                showHeader();
                break;
        }
    }

    public void showHeader(){

        mHeader.setVisibility(VISIBLE);
        mShowHeader = true;
    }

    public void hideHeader(){

        mHeader.setVisibility(GONE);
        mShowHeader = false;
    }

    public void enableCancelButton(){
        mEnableCancelButton = true;
        mCancelButton.setVisibility(VISIBLE);
    }

    public void disableCancelButton(){
        mEnableCancelButton = false;
        mCancelButton.setVisibility(GONE);
    }

    public void enableFullScreenButton(){
        mShowFullScreenButton = true;
        if(mFullScreenButton != null) {
            mFullScreenButton.setVisibility(VISIBLE);
        }
    }

    public void disableFullScreenButton(){
        mShowFullScreenButton = false;
        if(mFullScreenButton != null) {
            mFullScreenButton.setVisibility(GONE);
        }
    }

    public void enableSeekBar(){
        mEnableSeekBar = true;
        if(mSeekBar != null){
            mSeekBar.setEnabled(true);
        }
    }

    public void disableSeekBar(){
        mEnableSeekBar = false;
        if(mSeekBar != null){
            mSeekBar.setEnabled(false);
        }
    }

    public void show(){
        show(sDefaultTimeout);
    }

    public void show(int timeout){

        if(!mShowing){
            setProgress();
            mShowing = true;
            /*setVisibility(VISIBLE);*/

            layoutWindow();
//            updateWindow();
        }

//        updatePlayState();

        mHandler.removeMessages(FADE_OUT);
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

            /*setVisibility(GONE);*/
            try {
                mHandler.removeMessages(SHOW_PROGRESS);
                mWindow.dismiss();

            }catch (IllegalArgumentException ex) {
                Log.w("MediaController", "already removed");
            }

            mShowing = false;

        }
    }

    /*private void updatePausePlay(){
        if(mPlayer == null){
            return;
        }

        if (mPlayer.isPlaying()) {
            mPlayButton.setImageResource(android.R.drawable.ic_media_play);
        } else {
            mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
        }
    }*/

    private int setProgress(){

        if(mPlayer == null){
            return 0;
        }

        updatePlayState();

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

    public ImageButton getScreenCfgButton(){
        return mFullScreenButton;
    }

    public void setFullScreen(boolean fullScreen){
        mFullScreen = fullScreen;
        if(mFullScreen){
            //TODO ȫ��
            switch (mHeaderMode){
                case HEADER_MODE_AUTO:
                    showHeader();
                    break;
            }
        }else {
            //TODO ��ȫ��
            switch (mHeaderMode){
                case HEADER_MODE_AUTO:
                    hideHeader();
                    break;
            }
        }
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

    private void initFloatingWindow(){
        mWindow = new PopupWindow(getContext());
        mWindow.setFocusable(false);
        mWindow.setBackgroundDrawable(null);
        mWindow.setOutsideTouchable(true);
//        mWindow.setWindowLayoutMode(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);



    }

    private void sizeWindow(){
        int width = mAnchor.getWidth();
        int height = mAnchor.getHeight();
//        mRoot.setLayoutParams(new ViewGroup.LayoutParams(width,height));
        mWindow.setWidth(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY));
        mWindow.setHeight(MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    private void locateWindow(){
        int[] location = new int[2];
        mAnchor.getLocationOnScreen(location);
        Rect anchorRect = new Rect(location[0], location[1], location[0] + mAnchor.getWidth(), location[1] + mAnchor.getHeight());
        mWindow.showAtLocation(mAnchor, Gravity.NO_GRAVITY, anchorRect.left, anchorRect.top);
        if(DEBUG){
            Log.d(LOG_TAG+".locateWindow","left:"+anchorRect.left+"top:"+anchorRect.top);
        }
    }

    private void layoutWindow(){
        sizeWindow();
        locateWindow();
    }


    private void updateWindow(){
        int width = mAnchor.getWidth();
        int height = mAnchor.getHeight();
        int[] location = new int[2];
        mAnchor.getLocationOnScreen(location);

        mWindow.update(location[0], location[1], MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY), true);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            /*case MotionEvent.ACTION_DOWN:
                show(0); // show until hide is called
                break;*/
            case MotionEvent.ACTION_UP:

                if(mMotionCaptureUp == false) {
                    show(sDefaultTimeout); // start timeout
                    mMotionCaptureUp = true;
                }

                break;
            case MotionEvent.ACTION_CANCEL:
                if(mMotionCaptureCancel == false){
                    hide();
                    mMotionCaptureCancel = true;
                }

                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(DEBUG){
            Log.d(LOG_TAG,"onInterceptTouchEvent");
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(DEBUG){
                    Log.d(LOG_TAG,"ACTION_DOWN");
                }

                mMotionCaptureUp = false;
                mMotionCaptureCancel = false;

                show(0); // show until hide is called
                break;
            case MotionEvent.ACTION_UP:
                if(DEBUG){
                    Log.d(LOG_TAG,"ACTION_UP");
                }

                if(mMotionCaptureUp == false){
                    show(sDefaultTimeout); // start timeout
                    mMotionCaptureUp = true;
                }

                break;
            case MotionEvent.ACTION_CANCEL:
                if(DEBUG){
                    Log.d(LOG_TAG,"ACTION_CANCEL");
                }

                if(mMotionCaptureCancel == false){
                    hide();
                    mMotionCaptureCancel = true;
                }
                break;
            default:
                break;
        }

        return false;
    }

    private void initView(View parent){
        mPlayButton = (ImageButton) parent.findViewById(R.id.button_play);
        mFullScreenButton = (ImageButton) parent.findViewById(R.id.button_screen_configure);
        mFullScreenButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaControllerCallBacks != null) {
                    mMediaControllerCallBacks.onFullScreenButtonClick(mFullScreenButton);
                }
            }
        });
        if(mShowFullScreenButton == false){
            mFullScreenButton.setVisibility(GONE);
        }
        mSeekBar = (SeekBar) parent.findViewById(R.id.seek_bar);
        if(mEnableSeekBar == false){
            mSeekBar.setEnabled(false);
        }
        mCurrentTimeTextView = (TextView) parent.findViewById(R.id.time_current);
        mTimeTextView = (TextView) parent.findViewById(R.id.time);

        mSeekBar.setMax(PROGRESS_BAR_MAX);
        mTimeTextView.setText(stringForTime(0));
        mCurrentTimeTextView.setText(stringForTime(0));

        mPlayButton.setOnClickListener(mPlayButtonListener);
        mSeekBar.setOnSeekBarChangeListener(mSeekListener);
        mHeader = parent.findViewById(R.id.header);
        mTitle = (TextView) parent.findViewById(R.id.title);
        mCancelButton = (ImageButton) parent.findViewById(R.id.cancel);
        mCancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediaControllerCallBacks != null){
                    mMediaControllerCallBacks.onCancelButtonClick(mCancelButton);
                }
            }
        });
        mShareButton = (ImageButton) parent.findViewById(R.id.share);
        mShareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediaControllerCallBacks != null){
                    mMediaControllerCallBacks.onShareButtonClick(mShareButton);
                }
            }
        });
    }

    private View makeControllerView(Context context){
        return LayoutInflater.from(context).inflate(R.layout.widget_video_controller,this,false);
    }



    /*@Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
//            if (uniqueDown) {
                hide();
//            }
            return true;
        }else {
            return super.dispatchKeyEvent(event);
        }
    }*/







    /*private void setWindowLayoutType(){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
//                mAnchor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                Method setWindowLayoutType = PopupWindow.class.getMethod("setWindowLayoutType", new Class[] { int.class });
                setWindowLayoutType.invoke(mWindow, WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG);
            } catch (Exception e) {
                Log.e("setWindowLayoutType", e.toString());
            }
//        }
    }*/

    private OnLayoutChangeListener mOnLayoutChangeListener = new OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if(mShowing){
                int[] location = new int[2];
                mAnchor.getLocationOnScreen(location);
                Rect anchorRect = new Rect(left, top, right, bottom);
                mWindow.update(location[0],location[1],MeasureSpec.makeMeasureSpec(anchorRect.width(),MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(anchorRect.height(), MeasureSpec.EXACTLY),true);

                /*updateWindow();*/

            }
        }
    };

    private View.OnClickListener mPlayButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mPlayer == null){
                return;
            }

            switchPlayMode();
        }
    };

    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }

            if(mPlayer == null){
                return;
            }

            long duration = mPlayer.getDuration();
            long newposition = (duration * progress) / PROGRESS_BAR_MAX;
            mPlayer.seekTo( (int) newposition);
            mCurrentTimeTextView.setText(stringForTime( (int) newposition));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

            show(3600000);

            mHandler.removeMessages(SHOW_PROGRESS);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            setProgress();
//            updatePlayState();
            show(sDefaultTimeout);
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    };

    private void switchPlayMode(){
        if(mPlayer == null){
            return;
        }

        if(mPlayer.isPlaying()){
            mPlayer.pause();
        }else if(mPlayer.getCurrentPosition() > 0){/*��ʱ�����ж��Ƿ�Ϊ�ɲ���״̬*/
            mPlayer.start();
        }else {/*��ʱ�����ж��Ƿ�Ϊ׼��״̬����MediaPlayer is preparing*/
            return;
        }

        updatePlayState();
    }



    private void updatePlayState(){
        if(mPlayer != null && mPlayer.isPlaying()){
            //TODO pause picture
            mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
        }else {
            //TODO play picture
            mPlayButton.setImageResource(android.R.drawable.ic_media_play);
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
                    if (mShowing &&mPlayer!=null/* && mPlayer.isPlaying()*/) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
            }
        }
    };

    public interface MediaControllerCallBacks{
        void onFullScreenButtonClick(View fullScreenButton);
        void onCancelButtonClick(View cancelButton);
        void onShareButtonClick(View shareButton);
    }








}
