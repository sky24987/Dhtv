package cn.dhtv.mobile.fragment;


import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.MediaController;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.exoplayer.VideoSurfaceView;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.dhtv.mobile.R;
import cn.dhtv.mobile.adapter.TvListAdapter;
import cn.dhtv.mobile.entity.Program;
import cn.dhtv.mobile.entity.TvOverview;
import cn.dhtv.mobile.network.NetUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TvFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TvFragment extends Fragment implements MediaPlayer.OnPreparedListener,TvListAdapter.OnItemClickListener,MediaPlayer.OnErrorListener{

    public final String TV_LIST_URL = "http://api.dhtv.cn/?mod=lookback&ac=tv_archiver";

    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    RequestQueue mRequestQueue = NetUtils.getRequestQueue();
    ObjectMapper mObjectMapper = new ObjectMapper();

    Program mProgram;
    StateDate mStateDate = new StateDate();
    boolean surfacePrepared = false;
    boolean autoPaused = false;

    MediaPlayer mMediaPlayer;
    MediaController mMediaController;
    ArrayList<TvOverview> tvList = new ArrayList<>();
    TvListAdapter mTvListAdapter = new TvListAdapter(tvList,mStateDate);

    WeakReference<Activity> a;




    VideoSurfaceView playView;
    RecyclerView listView;
    Button button;


    public static TvFragment newInstance() {
        TvFragment fragment = new TvFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public TvFragment() {
        // Required empty public constructor
    }



    @Override
    public void OnItemClicked(TvListAdapter.ViewHolder viewHolder) {
        selectTv(viewHolder.tvOverview);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(DEBUG){
            Log.d(LOG_TAG,"onAttach");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(DEBUG){
            Log.d(LOG_TAG,"onCreate");
        }
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mMediaController.setMediaPlayer(new MediaController.MediaPlayerControl() {
            @Override
            public void start() {
                play();
            }

            @Override
            public void pause() {

            }

            @Override
            public int getDuration() {
                return 0;
            }

            @Override
            public int getCurrentPosition() {
                return 0;
            }

            @Override
            public void seekTo(int pos) {

            }

            @Override
            public boolean isPlaying() {
                return false;
            }

            @Override
            public int getBufferPercentage() {
                return 0;
            }

            @Override
            public boolean canPause() {
                return false;
            }

            @Override
            public boolean canSeekBackward() {
                return false;
            }

            @Override
            public boolean canSeekForward() {
                return false;
            }

            @Override
            public int getAudioSessionId() {
                return 0;
            }
        });
        mTvListAdapter.setmOnItemClickListener(this);
       /* if(mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnErrorListener(this);
        }*/


        Intent intent = getActivity().getIntent();
        mProgram = (Program) intent.getSerializableExtra("program");
        asyncRequestProgramList(mProgram,tvList);


        a = new WeakReference<Activity>(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(DEBUG){
            Log.d(LOG_TAG,"onCreateView");
        }
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_video_player, container, false);
        button = (Button) view.findViewById(R.id.test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = a.get();
                if(activity != null){
                    Log.d(LOG_TAG,"first activity:"+activity.toString());
                }else {
                    Log.d(LOG_TAG,"first activity is destroyed");
                }
            }
        });
        playView = (VideoSurfaceView) view.findViewById(R.id.player);
        playView.setVideoWidthHeightRatio(16.0f/9);//设置视频宽高比
        playView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediaPlayer.isPlaying()){
                    pause();
                }else {
                    play();
                }

            }
        });
        listView = (RecyclerView) view.findViewById(R.id.list);
        listView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        listView.setAdapter(mTvListAdapter);
        //mMediaController  = new MediaController(getActivity());
        playView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (DEBUG) {
                    Log.d(LOG_TAG, "surfaceCreated");
                }
                surfacePrepared = true;
                if (mMediaPlayer != null) {
                    if (mMediaPlayer.isPlaying()) {
                        pause();
                        autoPaused = true;
                    }
                    mMediaPlayer.setDisplay(holder);

                    if (isResumed() && autoPaused == true) {
                        play();
                        autoPaused = false;
                    }
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (DEBUG) {
                    Log.d(LOG_TAG, "surfaceDestroyed");
                }
                surfacePrepared = false;
                if (mMediaPlayer != null) {
                    mMediaPlayer.pause();
                    autoPaused = true;
                    mMediaPlayer.setDisplay(null);
                }
            }
        });
        // mMediaPlayer.setDisplay(holder);

        return view;
    }

    /**
     *
     * @param program
     * @param tvList 用于保存结果列表
     */
    private void asyncRequestProgramList(Program program,final ArrayList<TvOverview> tvList){
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ArrayList<TvOverview> list;
                try {
                    if(DEBUG){
                        Log.d(LOG_TAG,"response:"+response);
                    }
                    list = mObjectMapper.readValue(response.getJSONArray("data").toString(), new TypeReference<List<TvOverview>>() {
                    });
                    if(DEBUG){
                        Log.d(LOG_TAG, list.toString());
                    }
                    for(TvOverview tvOverview:list){
                        tvOverview.setTv_url(mProgram);
                    }
                    tvList.clear();
                    tvList.addAll(list);
                    onTvListGot();
                } catch (Exception e) {
                    //TODO
                    Log.e(LOG_TAG, e.getMessage());
                }finally{

                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG,error.getMessage());
               //TODO
            }
        };
        String url = makeTvListURL(1,mProgram.getUpid(),mProgram.getCatid());
        if(DEBUG){
            Log.d(LOG_TAG,url);
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url, null,responseListener,errorListener);

        jsonObjectRequest.setTag(mProgram);
        mRequestQueue.add(jsonObjectRequest);
    }

    private String makeTvListURL(int page,int id,int catid){
        return TV_LIST_URL+"&page="+page+"&id="+id+"&catid="+catid;
    }

    private void onTvListGot(){
        if(DEBUG){
            Log.d(LOG_TAG,"onTvListGot");
        }
        mTvListAdapter.notifyDataSetChanged();
        if(tvList.size() > 0){
            if(mStateDate.firstTv == null){
                mStateDate.firstTv = tvList.get(0);
                selectTv(mStateDate.firstTv);
            }

        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if(mp != mMediaPlayer){
            return;
        }

        if(DEBUG){
            Log.d(LOG_TAG,"onMediaplayerPrepared");
        }
        if(isResumed() && surfacePrepared == true){
            play();
        }
    }

    private void play(){
        if(DEBUG){
            Log.d(LOG_TAG,"play video");
        }
        try {
            mMediaPlayer.start();
        }catch (Exception e){
            Log.e(LOG_TAG,"play video error:"+e);
        }

    }

    private void pause(){
        if(DEBUG){
            Log.d(LOG_TAG,"pause video");
        }

        try {
            mMediaPlayer.pause();
        }catch (Exception e){
            Log.e(LOG_TAG,"pause video error:"+e);
        }

    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(LOG_TAG,"mediaplayer errorlistener:-what-"+what+"-extra-"+extra);
        return false;
    }

    @Override
    public void onResume() {
        if(DEBUG){
            Log.d(LOG_TAG,"onResume");
        }
        super.onResume();
        if( surfacePrepared == true && autoPaused == true){
            play();
            autoPaused = false;
        }
    }

    @Override
    public void onPause() {
        if(DEBUG){
            Log.d(LOG_TAG,"onPause");
        }
        super.onPause();
        if(mMediaPlayer.isPlaying()){
            pause();
            autoPaused = true;
        }
    }

    @Override
    public void onDestroyView() {
        if(DEBUG){
            Log.d(LOG_TAG,"onDestroyView");
        }
        super.onDestroyView();
        if(mMediaPlayer != null){
            mMediaPlayer.setDisplay(null);
        }
        listView.setAdapter(null);//取消原Adapter隐含的间接的对Recycler的引用，使Recycler和Recycler关联的activity都能够被GC回收
        playView = null;
        listView = null;
        button = null;
        surfacePrepared = false;
    }

    @Override
    public void onDestroy() {
        if(DEBUG){
            Log.d(LOG_TAG,"onDestroy");
        }
        super.onDestroy();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    private void selectTv(TvOverview tvOverview){

        if(mStateDate.selectedTv != null && mStateDate.selectedTv.getTvid() == tvOverview.getTvid()){
            if(DEBUG){
                Log.d(LOG_TAG,"repetitiveSelectTv:"+tvOverview.getTitle());
            }
            mStateDate.selectedTv = tvOverview;
            return;
        }

        if(DEBUG){
            Log.d(LOG_TAG,"selectTv:"+tvOverview.getTitle());
        }
        mStateDate.selectedTv = tvOverview;
        setTv2(mStateDate.selectedTv);
        mTvListAdapter.notifyDataSetChanged();
    }

    private void setTv(TvOverview tvOverview){
        if(DEBUG){
            Log.d(LOG_TAG,"setTv");
        }
        autoPaused = false;
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
        }
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(tvOverview.getTv_url());
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            if(DEBUG){
                Log.d(LOG_TAG,"mMediaPlayer exception");
            }
            Log.d(LOG_TAG,e.toString());
        }
    }

    private void setTv2(TvOverview tvOverview){
        if(mMediaPlayer != null){
            mMediaPlayer.setOnErrorListener(null);
            mMediaPlayer.setOnPreparedListener(null);
            mMediaPlayer.setDisplay(null);
            mMediaPlayer.release();
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        if(surfacePrepared == true){
            mMediaPlayer.setDisplay(playView.getHolder());
        }
        try {
            mMediaPlayer.setDataSource(tvOverview.getTv_url());
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            if(DEBUG){
                Log.d(LOG_TAG,"mMediaPlayer exception");
            }
            Log.d(LOG_TAG,e.toString());
        }
    }


    public class StateDate{
        public TvOverview firstTv;
        public TvOverview selectedTv;
    }
}
