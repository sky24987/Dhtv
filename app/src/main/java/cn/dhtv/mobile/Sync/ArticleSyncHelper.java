package cn.dhtv.mobile.Sync;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import cn.dhtv.mobile.Database.ArticleAccessor;
import cn.dhtv.mobile.Singletons;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.NewsOverview;
import cn.dhtv.mobile.network.NetUtils;
import cn.dhtv.mobile.Data;
import cn.dhtv.mobile.util.TextUtils;

/**
 * Created by Jack on 2015/4/14.
 */
public class ArticleSyncHelper {
    public static final int FLAG_SYNC_SUCCESS = 1;
    private static final int MESSAGE_WHAT_SUCCESS = 1;

    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private ArticleAccessor mArticleAccessor = new ArticleAccessor();
    private ExecutorService mExecutorService;

    private ObjectMapper mObjectMapper = new ObjectMapper();
    private RequestQueue mRequestQueue = Singletons.getRequestQueue();



    public ArticleSyncHelper(ExecutorService mExecutorService) {
        this.mExecutorService = mExecutorService;
    }

    public void syncFromNet(Category category,int minAid,ArticleSyncCallBacks articleSyncCallBacks){
        syncFromNet(category,minAid, new WeakReference<ArticleSyncCallBacks>(articleSyncCallBacks));
    }

    public void syncFromNet(Category category,int minAid,WeakReference<ArticleSyncCallBacks> articleSyncCallBacksWeakReference){
        fetch(category, minAid, articleSyncCallBacksWeakReference);
    }

    public void syncFromDB(Category category,int minAid,ArticleSyncCallBacks articleSyncCallBacks){
        syncFromDB(category, minAid, new WeakReference<ArticleSyncCallBacks>(articleSyncCallBacks));
    }

    public void syncFromDB(Category category,int minAid,WeakReference<ArticleSyncCallBacks> articleSyncCallBacksWeakReference){
        mExecutorService.submit(new GetFromDBTask(category, minAid, articleSyncCallBacksWeakReference));
    }

    public void syncFirstFromDB(Category category,int minAid,ArticleSyncCallBacks articleSyncCallBacks){
        syncFirstFromDB(category, minAid, new WeakReference<ArticleSyncCallBacks>(articleSyncCallBacks));
    }

    public void syncFirstFromDB(Category category,int minAid,WeakReference<ArticleSyncCallBacks> articleSyncCallBacksWeakReference){
        mExecutorService.submit(new GetFirstFromDBTask(category, minAid, articleSyncCallBacksWeakReference));
    }





    private void onSync(List<NewsOverview> list,WeakReference<ArticleSyncCallBacks> articleSyncCallBacksWeakReference,int flag){
        if(DEBUG){
            Log.d(LOG_TAG,"onSync list:"+list.toString());
        }
        ArticleSyncCallBacks callBacks = articleSyncCallBacksWeakReference.get();
        if(callBacks != null){
            Log.d(LOG_TAG,"callback!");
            callBacks.onSync(list);
        }else {
            Log.d(LOG_TAG,"callback reference lost");
        }
    }




    private void fetch(Category category,int minAid,WeakReference<ArticleSyncCallBacks> articleSyncCallBacksWeakReference){
        Response.Listener<JSONObject> responseListener = new ArticleResponseListener(articleSyncCallBacksWeakReference);
        Response.ErrorListener errorListener = new ArticleResponseErrorListener(articleSyncCallBacksWeakReference);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, TextUtils.makeNewsOverviewUrl(category, minAid), null,responseListener,errorListener);
        jsonObjectRequest.setTag(articleSyncCallBacksWeakReference);
        mRequestQueue.add(jsonObjectRequest);
        return;
    }

    private void fetchAndStore(Category category,int minAid,WeakReference<ArticleSyncCallBacks> articleSyncCallBacksWeakReference){
        Response.Listener<JSONObject> responseListener = new ArticleStoreResponseListener(articleSyncCallBacksWeakReference);
        Response.ErrorListener errorListener = new ArticleResponseErrorListener(articleSyncCallBacksWeakReference);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, TextUtils.makeNewsOverviewUrl(category, minAid), null,responseListener,errorListener);
        jsonObjectRequest.setTag(articleSyncCallBacksWeakReference);
        mRequestQueue.add(jsonObjectRequest);
        return;
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case  MESSAGE_WHAT_SUCCESS :
                    MessageObject obj = (MessageObject) msg.obj;
                    List<NewsOverview> list = obj.list;
                    WeakReference<ArticleSyncCallBacks> articleSyncCallBacksWeakReference = (WeakReference<ArticleSyncCallBacks>) obj.obj1;
                    onSync(list, articleSyncCallBacksWeakReference, MESSAGE_WHAT_SUCCESS);
            }
        }
    };


    private class ArticleStoreResponseListener implements Response.Listener<JSONObject>{
        WeakReference<ArticleSyncCallBacks> mArticleSyncCallBacks;

        private ArticleStoreResponseListener(WeakReference<ArticleSyncCallBacks> mArticleSyncCallBacks) {
            this.mArticleSyncCallBacks = mArticleSyncCallBacks;
        }

        @Override
        public void onResponse(JSONObject response) {
            ArrayList<NewsOverview> list;
            try {
                list = mObjectMapper.readValue(response.getJSONObject("data").getJSONArray("list").toString(), new TypeReference<List<NewsOverview>>() {
                });
                if(DEBUG){
                    Log.d(LOG_TAG, list.toString());
                }
                onSync(list, mArticleSyncCallBacks, FLAG_SYNC_SUCCESS);//得到数据后处理
                mExecutorService.submit(new StoreTask(list));//存到数据库
            } catch (Exception e) {

                e.printStackTrace();
                Log.e(LOG_TAG, e.getMessage());
            }finally{

            }
        }
    }

    private class ArticleResponseListener implements Response.Listener<JSONObject>{
        WeakReference<ArticleSyncCallBacks> mArticleSyncCallBacks;

        private ArticleResponseListener(WeakReference<ArticleSyncCallBacks> mArticleSyncCallBacks) {
            this.mArticleSyncCallBacks = mArticleSyncCallBacks;
        }

        @Override
        public void onResponse(JSONObject response) {
            ArrayList<NewsOverview> list;
            try {
                list = mObjectMapper.readValue(response.getJSONObject("data").getJSONArray("list").toString(), new TypeReference<List<NewsOverview>>() {
                });
                if(DEBUG){
                    Log.d(LOG_TAG, list.toString());
                }
                onSync(list, mArticleSyncCallBacks, FLAG_SYNC_SUCCESS);//得到数据后处理

            } catch (Exception e) {

                e.printStackTrace();
                Log.e(LOG_TAG, e.getMessage());
            }finally{

            }
        }
    }

    private class ArticleResponseErrorListener implements Response.ErrorListener{
        WeakReference<ArticleSyncCallBacks> mArticleSyncCallBacks;

        private ArticleResponseErrorListener(WeakReference<ArticleSyncCallBacks> mArticleSyncCallBacks) {
            this.mArticleSyncCallBacks = mArticleSyncCallBacks;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(LOG_TAG,error.getMessage());

        }
    }

    private class StoreTask implements Runnable{
        List<NewsOverview> list;

        private StoreTask(List<NewsOverview> list) {
            this.list = list;
        }

        @Override
        public void run() {
            for(NewsOverview newsOverview:list){
                mArticleAccessor.insertOrReplace(newsOverview);
            }
        }
    }

    private class GetFromDBTask implements Runnable{
        Category category;
        int minAid;
        WeakReference<ArticleSyncCallBacks> articleSyncCallBacksWeakReference;

        private GetFromDBTask(Category category, int minAid, WeakReference<ArticleSyncCallBacks> articleSyncCallBacksWeakReference) {
            this.category = category;
            this.minAid = minAid;
            this.articleSyncCallBacksWeakReference = articleSyncCallBacksWeakReference;
        }

        @Override
        public void run() {
            List<NewsOverview> list = mArticleAccessor.findArticles(category, minAid, Data.NEWS_PAGE_SIZE);
            MessageObject obj = new MessageObject();
            obj.list = list;
            obj.obj1 = articleSyncCallBacksWeakReference;
            mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_WHAT_SUCCESS,obj));
            //onSync(list, articleSyncCallBacksWeakReference, FLAG_SYNC_SUCCESS);
            return;
        }
    }

    private class GetFirstFromDBTask implements Runnable{
        Category category;
        int minAid;
        WeakReference<ArticleSyncCallBacks> articleSyncCallBacksWeakReference;

        private GetFirstFromDBTask(Category category, int minAid, WeakReference<ArticleSyncCallBacks> articleSyncCallBacksWeakReference) {
            this.category = category;
            this.minAid = minAid;
            this.articleSyncCallBacksWeakReference = articleSyncCallBacksWeakReference;
        }

        @Override
        public void run() {
            List<NewsOverview> list = mArticleAccessor.findArticles(category, minAid, Data.NEWS_PAGE_SIZE);
            if(DEBUG){
                Log.d(LOG_TAG,"db list size:"+list.size());
            }
            if(list.size() < Data.NEWS_PAGE_SIZE){
                if(DEBUG){
                    Log.d(LOG_TAG,"fetch and store:"+list.size());
                }
                fetchAndStore(category, minAid, articleSyncCallBacksWeakReference);
            }else {
                MessageObject obj = new MessageObject();
                obj.list = list;
                obj.obj1 = articleSyncCallBacksWeakReference;
                mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_WHAT_SUCCESS,obj));
                //onSync(list, articleSyncCallBacksWeakReference, FLAG_SYNC_SUCCESS);
            }

        }
    }


    public interface ArticleSyncCallBacks{
        void onSync(List<NewsOverview> list);
        void onError(int flag);
    }
}
