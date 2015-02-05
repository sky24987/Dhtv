package cn.dhtv.mobile;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import cn.dhtv.mobile.entity.NewsCat;
import cn.dhtv.mobile.entity.NewsOverview;
import cn.dhtv.mobile.util.NewsDataManager;
import cn.dhtv.mobile.util.NewsManager;

public class NewsService extends Service {
    public static final String LOG_TAG = "NewsService";
    
    /*
    新闻请求状态，用于通知类外
     */
    public static final int REQUEST_FAIL_PROCESSING = -1;

    /*
    新闻获取状态，类内使用
     */
    private static final int UPDATE_SUCCESS = 1;
    private static final int UPDATE_FAIL = -1;
    private static final int APPEND_SUCCESS = 2;
    private static final int APPEND_FAIL = -2;

    private boolean processing  = false;//是否正在获取新闻，同一时间只处理一次请求
    private int processCount = 0;//记录网络请求数，用于判断何时所有请求都已完成

    private LocalBinder localBinder = new LocalBinder();
    private NewsDataManager mNewsDataManager = NewsDataManager.getInstance();
    private RequestQueue requestQueue;//Volley网络请求队列
    private ObjectMapper mObjectMapper = new ObjectMapper();
    private CallBacks mCallBacks;

    //private Timer updateTimer;//更新新闻列表定时器;
    private Handler updateHandler;
    private Handler fetchHandler;
    public NewsService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        updateHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == UPDATE_SUCCESS){
                    NewsCat cat = mNewsDataManager.getNewsCat(msg.arg1);
                    if(cat == null){

                    }else{
                        JSONObject jsonObject = (JSONObject) msg.obj;
                        ArrayList<NewsOverview> newsList = null;
                        try {
                            newsList = mObjectMapper.readValue(jsonObject.getJSONObject("data").getJSONArray("list").toString(), new TypeReference<List<NewsOverview>>() {
                            });

                            Log.d(LOG_TAG, newsList.toString());
                            mNewsDataManager.refreshData(cat,newsList);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(LOG_TAG, e.getMessage());
                        }finally {
                            processCount--;
                            Log.d(LOG_TAG,"processcount: "+processCount);
                            if(processCount == 0){
                                if(mCallBacks != null){
                                    Log.d(LOG_TAG,"onNewsUpdate");
                                    mCallBacks.onNewsUpdate(0);//TODO:参数0暂时无用，占位
                                }else {
                                    Log.d(LOG_TAG,"service callbacks is null");
                                }
                                processing = false;//processCount == 0时所有操作都已完成
                            }
                        }
                    }
                }else if(msg.what == UPDATE_FAIL){
                    try{

                    }finally {
                        if(processCount == 0){
                            if(mCallBacks != null){
                                mCallBacks.onNewsUpdageFails(0);//TODO:参数0暂时无用，占位
                            }
                            processing = false;//processCount == 0时所有操作都已完成
                        }
                    }

                }else{

                }

            }
        };

        fetchHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == APPEND_SUCCESS){
                    NewsCat cat = mNewsDataManager.getNewsCat(msg.arg1);
                    if(cat == null){

                    }else {
                        JSONObject jsonObject = (JSONObject) msg.obj;
                        ArrayList<NewsOverview> newsList = null;
                        try{
                            newsList = mObjectMapper.readValue(jsonObject.getJSONObject("data").getJSONArray("list").toString(), new TypeReference<List<NewsOverview>>() {
                            });
                            Log.d(LOG_TAG, newsList.toString());
                            mNewsDataManager.appendNews(cat,newsList);
                        }catch (Exception e){
                            Log.d(LOG_TAG,e.toString());
                        }finally {
                            if(mCallBacks != null){
                                mCallBacks.onNewsAppend(cat,true,0);//TODO:0暂无意义，占位
                            }else {
                                Log.d(LOG_TAG,"service callbacks is null");
                            }
                        }
                    }
                }
            }
        };

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requestQueue.stop();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateNews(){
        if(processing == true){
            mCallBacks.onNewsUpdageFails(REQUEST_FAIL_PROCESSING);
            return;
        }
        processing = true;

        for(final NewsCat cat : mNewsDataManager.getNewsCats()){
            final int catId = cat.getCatid();
            processCount++;

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
            String requestString = mNewsDataManager.makeRequestURL(cat,1);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,requestString,null,responseListener,errorListener);
            requestQueue.add(jsonObjectRequest);
        }
    }

    private void requestMoreNews(final NewsCat cat){
        final int catId = cat.getCatid();
        if(processing == true){
            mCallBacks.onNewsUpdageFails(REQUEST_FAIL_PROCESSING);
            return;
        }
        processing = true;

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Message msg = new Message();
                msg.what = APPEND_SUCCESS;
                msg.obj = response;
                msg.arg1 = catId;
                fetchHandler.sendMessage(msg);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Message msg = new Message();
                msg.what = APPEND_FAIL;
                msg.arg1 = catId;
                fetchHandler.sendMessage(msg);
            }
        };

        String requestString = mNewsDataManager.makeRequestURL(cat,1);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,requestString,null,responseListener,errorListener);
        requestQueue.add(jsonObjectRequest);
    }

    public boolean isProcessing() {
        return processing;
    }

    private void setProcessing(boolean processing) {
        this.processing = processing;
    }

    public int getProcessCount() {
        return processCount;
    }

    private void setProcessCount(int processCount) {
        this.processCount = processCount;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    public class LocalBinder extends Binder{
        public NewsService getService(){
            return NewsService.this;
        }
        public void updateNews(){NewsService.this.updateNews();}
        public void requestMoreNews(NewsCat cat){NewsService.this.requestMoreNews(cat);}
        public boolean isProcessing(){return processing;}
        public void setCallbacks(CallBacks callbacks){mCallBacks = callbacks;}
    }

    public interface CallBacks{
        void onNewsUpdate(int flag);
        void onNewsUpdageFails(int flag);
        void onNewsAppend(NewsCat cat,Boolean hasMore,int flag);
        void onNewsAppendFails(NewsCat cat,int flag);
    }
}
