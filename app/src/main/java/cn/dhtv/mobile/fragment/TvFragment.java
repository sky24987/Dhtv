package cn.dhtv.mobile.fragment;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.IOException;
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
public class TvFragment extends Fragment implements MediaPlayer.OnPreparedListener{

    public final String TV_LIST_URL = "http://api.dhtv.cn/?mod=lookback&ac=tv_archiver";

    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    MediaPlayer mMediaPlayer;
    Program mProgram;
    ArrayList<TvOverview> tvList = new ArrayList<>();
    TvListAdapter mTvListAdapter = new TvListAdapter(tvList);
    TvOverview firstTv;
    boolean autoPaused = false;
    RequestQueue mRequestQueue = NetUtils.getRequestQueue();
    ObjectMapper mObjectMapper = new ObjectMapper();



    SurfaceView playView;
    RecyclerView listView;


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
    public void onCreate(Bundle savedInstanceState) {
        if(DEBUG){
            Log.d(LOG_TAG,"onCreate");
        }
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if(mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(this);
        }
        Intent intent = getActivity().getIntent();
        mProgram = (Program) intent.getSerializableExtra("program");
        asyncRequestProgramList(mProgram,tvList);

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
                    e.printStackTrace();
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
            firstTv = tvList.get(0);
            if(DEBUG){
                Log.d(LOG_TAG,"firstTv:"+firstTv);
            }
            try {
                if(DEBUG){
                    Log.d(LOG_TAG,"tvurl:"+firstTv.getTv_url());
                }
                mMediaPlayer.setDataSource(firstTv.getTv_url());
                mMediaPlayer.prepareAsync();
            }catch (IOException ioe){
                if(DEBUG){
                    Log.d(LOG_TAG,"mMediaPlayer exception");
                }
                Log.d(LOG_TAG,ioe.toString());
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if(DEBUG){
            Log.d(LOG_TAG,"onPrepared");
        }
        if(isResumed()){
            if(DEBUG){
                Log.d(LOG_TAG,"onPrepared and start");
            }
            mp.start();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(DEBUG){
            Log.d(LOG_TAG,"onCreateView");
        }
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_video_player, container, false);
        playView = (SurfaceView) view.findViewById(R.id.player);
        //listView = (RecyclerView) view.findViewById(R.id.list);
        SurfaceHolder holder = playView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if(DEBUG){
                    Log.d(LOG_TAG,"surfaceCreated");
                }
                mMediaPlayer.setDisplay(holder);
                if(isResumed() && autoPaused == true){
                    mMediaPlayer.start();
                    autoPaused = false;
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if(DEBUG){
                    Log.d(LOG_TAG,"surfaceDestroyed");
                }
                mMediaPlayer.setDisplay(null);
            }
        });
       // mMediaPlayer.setDisplay(holder);

        return view;
    }



    @Override
    public void onResume() {
        if(DEBUG){
            Log.d(LOG_TAG,"onResume");
        }
        super.onResume();
        /*if(autoPaused == true){
            mMediaPlayer.start();
            autoPaused = false;
        }*/
    }

    @Override
    public void onPause() {
        if(DEBUG){
            Log.d(LOG_TAG,"onPause");
        }
        super.onPause();
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
            autoPaused = true;
        }
    }

    @Override
    public void onDestroyView() {
        if(DEBUG){
            Log.d(LOG_TAG,"onDestroyView");
        }
        super.onDestroyView();
        mMediaPlayer.setDisplay(null);
        playView = null;
        listView = null;
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
}
