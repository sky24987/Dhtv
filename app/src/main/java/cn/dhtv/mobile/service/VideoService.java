package cn.dhtv.mobile.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.dhtv.mobile.MyApplication;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.NewsCat;
import cn.dhtv.mobile.entity.NewsOverview;
import cn.dhtv.mobile.entity.VideoOverview;
import cn.dhtv.mobile.util.VideoDataManager;

public class VideoService extends Service {
    private static final boolean DEBUG = true;
    private static final String LOG_TAG = "VideoService";

    private static final int UPDATE_SUCCESS = 1;
    private static final int UPDATE_FAIL = -1;
    private static final int APPEND_SUCCESS = 2;
    private static final int APPEND_FAIL = -2;

    private LocalBinder localBinder = new LocalBinder();
    private CallBacks mCallBacks;

    private RequestQueue requestQueue;
    private VideoDataManager mVideoDataManager;
    private ObjectMapper mObjectMapper = new ObjectMapper();
    private ArrayList<Category> processingList = new ArrayList<>();

    private Handler updateHandler;
    private Handler fetchHandler;

    public VideoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    public boolean isProcessing(Category category){
        int index = processingList.indexOf(category);
        if(index >= 0){
            return true;
        }else {
            return false;
        }
    }

    public void update(Category cat){
        if(isProcessing(cat)){
            mCallBacks.notifyAppendFail(cat,0);
            return;
        }
        processingList.add(cat);

        final int catId = cat.getCatid();
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Message msg = new Message();
                msg.what = UPDATE_SUCCESS;
                msg.obj = response;
                msg.arg1 = catId;
                updateHandler.sendMessage(msg);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Message msg = new Message();
                msg.what = UPDATE_FAIL;
                msg.arg1 = catId;
                updateHandler.sendMessage(msg);
            }
        };

        //TODO..........
    }

    private void init(){
        MyApplication application = (MyApplication)getApplication();
        //TODO..........requestQueue = application.getRequestQueue();
        //TODO..........mVideoDataManager = application.getVideoDataManager();

        updateHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case UPDATE_SUCCESS :
                        Category cat = mVideoDataManager.getCat(msg.arg1);
                        if(cat != null){
                            JSONObject jsonObject = (JSONObject) msg.obj;
                            ArrayList<VideoOverview> list;
                            try{
                                list = mObjectMapper.readValue(jsonObject.getJSONObject("data").getJSONArray("list").toString(), new TypeReference<List<VideoOverview>>() {});
                                mVideoDataManager.getDataList(cat.getCatid()).refresh(list);
                                mCallBacks.notifyUpdate(cat,0);
                                if(DEBUG){
                                    Log.v(LOG_TAG,list.toString());
                                }

                            }catch (Exception e){
                                mCallBacks.notifyUpdateFail(cat,0);
                                if(DEBUG){
                                    Log.d(LOG_TAG,e.toString());
                                }
                            }
                        }
                        break;
                    case UPDATE_FAIL:
                        //TODO
                }
            }
        };

        fetchHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case APPEND_SUCCESS :
                        Category cat = mVideoDataManager.getCat(msg.arg1);
                        if(cat != null){
                            JSONObject jsonObject = (JSONObject) msg.obj;
                            ArrayList<VideoOverview> list;
                            try{
                                list = mObjectMapper.readValue(jsonObject.getJSONObject("data").getJSONArray("list").toString(), new TypeReference<List<VideoOverview>>() {});
                                mVideoDataManager.getDataList(cat.getCatid()).appendList(list);
                                mCallBacks.notifyUpdate(cat, 0);
                                if(DEBUG){
                                    Log.v(LOG_TAG,list.toString());
                                }

                            }catch (Exception e){
                                mCallBacks.notifyAppendFail(cat, 0);
                                if(DEBUG){
                                    Log.d(LOG_TAG,e.toString());
                                }
                            }
                        }
                        break;
                    case UPDATE_FAIL:
                        //TODO
                }
            }

        };



    }

    public class LocalBinder extends Binder {
        public VideoService getService(){
            return VideoService.this;
        }

        public void update(Category cat){}

        public void requestMore(Category cat){}

        public void isProcessing(Category cat){}

        public void setCallbacks(CallBacks callbacks){mCallBacks = callbacks;}
    }

    public interface CallBacks{
        void notifyUpdate(Category category,int flag);
        void notifyUpdateFail(Category category,int flag);
        void notifyAppend(Category category,int flag);
        void notifyAppendFail(Category category,int flag);
    }
}
