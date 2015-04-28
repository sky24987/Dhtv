package cn.dhtv.mobile.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.dhtv.android.adapter.BaseRecyclerViewAdapter;
import cn.dhtv.mobile.MyApplication;
import cn.dhtv.mobile.R;
import cn.dhtv.mobile.Sync.DataSyncHelper;
import cn.dhtv.mobile.ui.adapter.FMAdapter;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.network.NetUtils;
import cn.dhtv.mobile.service.AudioService;


public class AudioFragment extends SectionFragment implements ServiceConnection,AudioService.CallBacks,BaseRecyclerViewAdapter.OnItemClickListener{
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;


    public  final String title = "新闻";

    private boolean autoPaused = true;//是否手动暂停
    private boolean audioBound = false;
    private boolean audioPrepared = false;
    private boolean fmToBeSet = false;//是否有一个频道已经设置

    private boolean playButtonClickable = true;
    private boolean playButtonOn = false;

    private AudioService.LocalBinder mAudioBinder;
    private Category fmCategory;
    private ArrayList<Category> fmProgramList = new ArrayList<>();
    private StateData mStateData = new StateData();
    private FMAdapter mFmAdapter = new FMAdapter(fmProgramList,mStateData);
    private View.OnClickListener radioButtonClickListener;



    private RequestQueue mRequestQueue = NetUtils.getRequestQueue();
    private ObjectMapper mObjectMapper = new ObjectMapper();

    ImageView mImageView;
    ImageView mPlayButton;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    public AudioFragment() {
        fmCategory = new Category();
        fmCategory.setCatid(24);
        fmCategory.setCatname("广播");
    }

    public static AudioFragment newInstance() {
        AudioFragment fragment = new AudioFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        ((MyApplication)(getActivity().getApplication())).startAudioService();
        requestFm();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_audio,container,false);
        initView(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(getActivity(),AudioService.class);
        getActivity().bindService(intent,this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        //autoPause();
        getActivity().unbindService(this);
        audioBound = false;
        mAudioBinder.setCallBacks(null);
        mAudioBinder = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MyApplication)(getActivity().getApplication())).stopAudioService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }

    @Override
    public void onItemClicked(BaseRecyclerViewAdapter.ViewHolder vh, Object item, int position) {
        Category fmCategory = (Category) item;
        selectFm(fmCategory);
        mFmAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClicked(View view) {
       FMAdapter.ViewHolder viewHolder = (FMAdapter.ViewHolder) mRecyclerView.getChildViewHolder(view);
        Category fmCategory =(Category) viewHolder.item;
        selectFm(fmCategory);
        mFmAdapter.notifyDataSetChanged();
    }

    private DataSyncHelper.CategorySyncCallBacks mCategorySyncCallBacks = new DataSyncHelper.CategorySyncCallBacks() {
        @Override
        public void onSync(List<Category> list) {
            fmProgramList.clear();
            fmProgramList.addAll(list);
            onFmGot();
        }

        @Override
        public void onError(int flag) {

        }
    };

    private void onFmGot(){
        if(fmProgramList.size() == 0){
            return;
        }

        Category fm = fmProgramList.get(0);
        if(mStateData.firstTm == null){
            mStateData.firstTm = fm;
            selectFm(fm);
        }
        mFmAdapter.notifyDataSetChanged();
    }

    private void buttonOn(){
        playButtonOn = true;
        refreshButtonState();
    }

    private void buttonOff(){
        playButtonOn = false;
        refreshButtonState();
    }

    private void selectFm(Category fmCategory){
        if(mStateData.selectedFm == null || mStateData.selectedFm.getCatid() != fmCategory.getCatid()) {
            mStateData.selectedFm = fmCategory;

            if(audioBound == true) {
                setFm();
                fmToBeSet = false;
            }else {
                fmToBeSet = true;
            }
        }
    }

    private void queryAudioPrepared(){
        if(isAudioPlayable()){
            audioPrepared();
        }
    }

    private void audioPrepared(){
        if(playButtonOn == true){
            play();
        }else {
            pause();
        }
    }

    private void setFm(){
        if(DEBUG){
            Log.d(LOG_TAG,"setFm");
        }

        fmToBeSet = false;

        if(mStateData.selectedFm != null){
            try {
                setAudio(mStateData.selectedFm.getLive().getM3u8());
            }catch (IOException e){
                //TODO
            }

            buttonOn();
            return;
        }else if(mStateData.firstTm != null){
            mStateData.selectedFm = mStateData.firstTm;
            try{
                setAudio(mStateData.selectedFm.getLive().getM3u8());
            }catch (IOException e){
                //TODO
            }

            buttonOn();
            return;
        }else {
            return;
        }
    }

    private void play(){

        mAudioBinder.play();
    }

    private void pause(){

        mAudioBinder.pause();
    }


    private void setAudio(String url) throws IOException{
        audioPrepared = false;

        mAudioBinder.resetAudio(url);


    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if(DEBUG){
            Log.d(LOG_TAG,"onServiceConnected");
        }
        mAudioBinder = (AudioService.LocalBinder) service;
        mAudioBinder.setCallBacks(this);
        onAudioConnected();
    }


    @Override
    public void onServiceDisconnected(ComponentName name) {

        mAudioBinder = null;
        audioBound = false;
    }

    @Override
    public void onAudioPrepared() {
        audioPrepared = true;
        //autoPlay();
        queryAudioPrepared();
    }

    private void requestFm(){
        /*Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ArrayList<Program> list;
                try {
                    list = mObjectMapper.readValue(response.getJSONObject("data").getJSONArray("children").toString(), new TypeReference<List<Category>>() {
                    });
                    if(DEBUG){
                        Log.d(LOG_TAG, list.toString());
                    }
                    fmProgramList.clear();
                    fmProgramList.addAll(list);
                    onFmGot();
                } catch (Exception e) {

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

            }
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, TextUtils.makeCategoryUrl(fmCategory, 1), null,responseListener,errorListener);
        jsonObjectRequest.setTag(fmCategory);
        mRequestQueue.add(jsonObjectRequest);*/
        DataSyncHelper.getInstance(getActivity()).SyncCategoryFirstFromDB(fmCategory,mCategorySyncCallBacks);
    }

    private void onAudioConnected(){
        if(DEBUG){
            Log.d(LOG_TAG,"onAudioConnected");
        }

        audioBound = true;
        if(fmToBeSet == true){
            setFm();
        }

        //autoPlay();
    }

    private void init(){
        mFmAdapter.setOnItemClickListener(this);
        radioButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean selected = v.isSelected();
                if(selected == true){
                    buttonOff();
                    refreshButtonState();
                    refreshPlayState();
                }else {
                    buttonOn();
                    refreshButtonState();
                    refreshPlayState();
                }
            }
        };
    }

    private void initView(View view){
        mLayoutManager =  new GridLayoutManager(getActivity(),3,LinearLayoutManager.VERTICAL,false);
        mImageView = (ImageView) view.findViewById(R.id.fm);
        mPlayButton = mImageView;
        mPlayButton.setOnClickListener(radioButtonClickListener);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setAdapter(mFmAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private boolean isAudioPlayable(){
        return audioBound && audioPrepared;
    }

    private void autoPlay(){
        if(playButtonOn ==true && isAudioPlayable() && autoPaused == true){
            play();
            autoPaused = false;
        }
    }

    private void autoPause(){
        if(playButtonOn == false && isAudioPlayable() && autoPaused == false){
            pause();
            autoPaused = true;
        }
    }

    private void refreshPlayState(){
        if(playButtonOn == true){
            if(isAudioPlayable()){
                play();
            }
        }else {
            if(isAudioPlayable()){
                pause();
            }
        }
    }

    private void refreshButtonState(){
        //TODO
        if(playButtonOn == true){
            mPlayButton.setSelected(true);
        }else {
            mPlayButton.setSelected(false);
        }
    }

    @Override
    public String getTitle() {
        return title;
    }

    public class StateData{
        public Category firstTm;
        public Category selectedFm;
    }
}
