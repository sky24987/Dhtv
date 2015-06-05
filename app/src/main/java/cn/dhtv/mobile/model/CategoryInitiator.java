package cn.dhtv.mobile.model;

import android.os.Handler;
import android.os.Message;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import cn.dhtv.mobile.Database.CategoryAccessor;
import cn.dhtv.mobile.Singletons;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.network.CategoryClient;
import cn.dhtv.mobile.network.NetUtils;
import cn.dhtv.mobile.Data;
import cn.dhtv.mobile.util.TextUtils;

/**
 * Created by Jack on 2015/4/27.
 */
public class CategoryInitiator {
    private static final int MESSAGE_SUCCESS_NEWS = 1;
    private static final int MESSAGE_SUCCESS_VIDEO = 2;
    private static final int MESSAGE_SUCCESS_TV_CHANNEL = 3;
    private static final int MESSAGE_SUCCESS_AUDIO_CHANNEL = 4;

    private static final int CATEGORY_TYPE_NEWS = MESSAGE_SUCCESS_NEWS;
    private static final int CATEGORY_TYPE_VIDEO = MESSAGE_SUCCESS_VIDEO;
    private static final int CATEGORY_TYPE_TV_CHANNEL = MESSAGE_SUCCESS_TV_CHANNEL;
    private static final int CATEGORY_TYPE_AUDIO_CHANNEL = MESSAGE_SUCCESS_AUDIO_CHANNEL;


    private CallBacks mCallBacks;

    private Object mNetTag = new Object();

    private String newsCatUrl = TextUtils.makeCategoryUrl(Data.newsFatherCategory,0);
    private String videoCatUrl = TextUtils.makeCategoryUrl(Data.videoFatherCategory,0);
    private String tvChannelCatUrl = TextUtils.makeCategoryUrl(Data.tvChannelFatherCategory,0);
    private String audioChannelCatUrl = TextUtils.makeCategoryUrl(Data.audioChannelFatherCategory,0);

    private boolean newsCategorySync = false;
    private boolean videoCategorySync = false;
    private boolean tvChannelCategorySync = false;
    private boolean audioChannelCategorySync = false;

    private ExecutorService mExecutorService = Singletons.getExecutorService();
    private RequestQueue mRequestQueue = Singletons.getRequestQueue();
    private CategoryAccessor mCategoryAccessor = new CategoryAccessor();






    public CategoryInitiator() {

    }

    public void asyncInit(){
        CategoryClient.CategoryArrayListRequest newsCategoryRequest = new CategoryClient.CategoryArrayListRequest(Request.Method.GET,newsCatUrl,new CategoryResponseListener(CATEGORY_TYPE_NEWS),new CategoryErrorListener(CATEGORY_TYPE_NEWS));
        CategoryClient.CategoryArrayListRequest videoCategoryRequest = new CategoryClient.CategoryArrayListRequest(Request.Method.GET,videoCatUrl,new CategoryResponseListener(CATEGORY_TYPE_VIDEO),new CategoryErrorListener(CATEGORY_TYPE_VIDEO));
        CategoryClient.CategoryArrayListRequest tvChannelCategoryRequest = new CategoryClient.CategoryArrayListRequest(Request.Method.GET,tvChannelCatUrl,new CategoryResponseListener(CATEGORY_TYPE_TV_CHANNEL),new CategoryErrorListener(CATEGORY_TYPE_TV_CHANNEL));
        CategoryClient.CategoryArrayListRequest audioChannelsCategoryRequest = new CategoryClient.CategoryArrayListRequest(Request.Method.GET,audioChannelCatUrl,new CategoryResponseListener(CATEGORY_TYPE_AUDIO_CHANNEL),new CategoryErrorListener(CATEGORY_TYPE_AUDIO_CHANNEL));
        newsCategoryRequest.setTag(mNetTag);
        videoCategoryRequest.setTag(mNetTag);
        tvChannelCategoryRequest.setTag(mNetTag);
        audioChannelsCategoryRequest.setTag(mNetTag);
        mRequestQueue.add(newsCategoryRequest);
        mRequestQueue.add(videoCategoryRequest);
        mRequestQueue.add(tvChannelCategoryRequest);
        mRequestQueue.add(audioChannelsCategoryRequest);
    }

    public void cancelAllTask(){
        mRequestQueue.cancelAll(mNetTag);
        mHandler.removeCallbacksAndMessages(null);
    }

    private void onError(){
        cancelAllTask();
        if(mCallBacks != null){
            mCallBacks.onFail(-1);
        }
    }

    private void onSuccess(){
        if(mCallBacks != null) {
            mCallBacks.onSuccess(1);
        }
    }

    private void checkComplete(){
        if(newsCategorySync &&
                videoCategorySync &&
                tvChannelCategorySync&&
                audioChannelCategorySync){
            onSuccess();
        }
    }

    public void setCallBacks(CallBacks mCallBacks) {
        this.mCallBacks = mCallBacks;
    }

    private class CategoryResponseListener implements Response.Listener<ArrayList<Category>>{
        private int categoryType;

        private CategoryResponseListener(int categoryType) {
            this.categoryType = categoryType;
        }

        @Override
        public void onResponse(ArrayList<Category> response) {

            mExecutorService.submit(new StoreCategoryTask(response,categoryType));
        }
    }

    private class CategoryErrorListener implements Response.ErrorListener{
        private int categoryType;

        private CategoryErrorListener(int categoryType) {
            this.categoryType = categoryType;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            onError();
        }
    }

    private class StoreCategoryTask implements Runnable{
        private ArrayList<Category> list;
        private int categoryType;

        private StoreCategoryTask(ArrayList<Category> list, int categoryType) {
            this.list = list;
            this.categoryType = categoryType;
        }

        @Override
        public void run() {
            for (Category category : list){
                mCategoryAccessor.insertOrReplace(category);
            }
            switch (categoryType){
                case CATEGORY_TYPE_NEWS:
                    mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_SUCCESS_NEWS));
                    break;
                case CATEGORY_TYPE_VIDEO:
                    mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_SUCCESS_VIDEO));
                    break;
                case CATEGORY_TYPE_TV_CHANNEL:
                    mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_SUCCESS_TV_CHANNEL));
                    break;
                case CATEGORY_TYPE_AUDIO_CHANNEL:
                    mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_SUCCESS_AUDIO_CHANNEL));
                    break;

            }

        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_SUCCESS_NEWS:
                    newsCategorySync = true;
                    checkComplete();
                    break;
                case MESSAGE_SUCCESS_VIDEO:
                    videoCategorySync = true;
                    checkComplete();
                    break;
                case MESSAGE_SUCCESS_TV_CHANNEL:
                    tvChannelCategorySync = true;
                    checkComplete();
                    break;
                case MESSAGE_SUCCESS_AUDIO_CHANNEL:
                    audioChannelCategorySync = true;
                    checkComplete();
                    break;

            }
        }
    };


    public interface CallBacks{
        void onSuccess(int flag);
        void onFail(int flag);
    }
}
