package cn.dhtv.mobile.fragment;


import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
//import cn.dhtv.android.widget.MediaController;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import cn.dhtv.mobile.Database.Contract;
import cn.dhtv.mobile.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoPlayerFragment extends Fragment {
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private static final int AUTO_PAUSED = -1;
    private static final int AUTO_STOPPED = -2;
    private static final int AUTO_NULL = 0;



    private VideoView mVideoView;
    private View mVideoViewFather;
    private MediaController mMediaController;
    private View mVideoOverlay;
    private ProgressBar mProgressBar;



    private int mAutoState = AUTO_NULL;

    public static VideoPlayerFragment newInstance() {
        VideoPlayerFragment fragment = new VideoPlayerFragment();
        return fragment;
    }

    public VideoPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_video_player_only, container, false);
        mVideoViewFather = view;
        mVideoView = (VideoView) view.findViewById(R.id.video_view);
        mVideoView.setOnPreparedListener(mOnPreparedListener);
//        mMediaController = (MediaController) view.findViewById(R.id.mediaController);

//        mVideoView.setOnInfoListener(mOnInfoListener);
        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mMediaController.show();

                return true;
            }
        });
        mVideoOverlay = view.findViewById(R.id.video_overlay);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mMediaController.setMediaPlayer(mVideoView);
        mMediaController.setAnchorView(mVideoViewFather);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        /*if(DEBUG){
            Log.d(LOG_TAG,"onResume!!!!!");
        }*/
        if(mAutoState == AUTO_PAUSED){
            mAutoState = AUTO_NULL;
            mVideoView.start();

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        /*if(DEBUG){
            Log.d(LOG_TAG,"onPause!!!!!");
        }*/
        if(mVideoView.isPlaying()){
            mAutoState = AUTO_PAUSED;
            mVideoView.pause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mVideoView.stopPlayback();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mVideoView.getHolder().setSizeFromLayout();
    }

    public void setVideoPath(String path){
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri){
        mAutoState = AUTO_NULL;
        mVideoView.stopPlayback();
        mVideoView.setVideoURI(uri);
        mVideoView.start();
        mVideoOverlay.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void init(){
        mMediaController = new MediaController(getActivity(),false);
    }

    private MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if(what == MediaPlayer.MEDIA_INFO_BUFFERING_START){
                mProgressBar.setVisibility(View.VISIBLE);
                return true;
            }
            if(what == MediaPlayer.MEDIA_INFO_BUFFERING_END){
                mProgressBar.setVisibility(View.GONE);
            }
            return false;
        }
    };

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mVideoOverlay.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
        }
    };
}
