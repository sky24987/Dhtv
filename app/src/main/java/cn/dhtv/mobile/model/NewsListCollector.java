package cn.dhtv.mobile.model;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import cn.dhtv.mobile.Database.ArticleAccessor;
import cn.dhtv.mobile.Database.BlockAccessor;
import cn.dhtv.mobile.Sync.ArticleSyncHelper;
import cn.dhtv.mobile.Sync.BlockSyncHelper;
import cn.dhtv.mobile.Sync.SyncHelperFactory;
import cn.dhtv.mobile.adapter.AbstractListAdapter;
import cn.dhtv.mobile.entity.Block;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.NewsOverview;
import cn.dhtv.mobile.model.AbsListCollector.CallBacks;
import cn.dhtv.mobile.network.BlockClient;
import cn.dhtv.mobile.util.Singletons;
import cn.dhtv.mobile.util.TextUtils;

/**
 * Created by Jack on 2015/3/17.
 */
public class NewsListCollector extends AbsListCollector{
    /*private static final String URL_NEWS = "http://api.dhtv.cn/mobile/article/";*/
    private static final int STATE_PROCESSING_APPEND = 1;
    private static final int STATE_PROCESSING_UPDATE = 2;
    private static final int STATE_PROCESSING_INIT = 3;
    private static final int STATE_IDLE = 0;
    private static final int STATE_ERROR_NET_PROBLEM = -1;
    private static final int STATE_ERROR_JSON_PROBLEM = -3;
    private static final int STATE_ERROR_DB_PROBLEM = -2;


    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;


    private ArticleSyncHelper mArticleSyncHelper = SyncHelperFactory.getInstance().newArticleSyncHelper();
    private BlockSyncHelper mBlockSyncHelper = SyncHelperFactory.getInstance().newBlockSyncHelper();
    private BlockAccessor mBlockAccessor = new BlockAccessor();
    private ArticleAccessor mArticleAccessor = new ArticleAccessor();
    private ExecutorService mExecutorService = Singletons.getExecutorService();


    private ArrayList<NewsOverview> newsOverviews = new ArrayList<>();
    private ArrayList<Block> blockArrayList = new ArrayList<>();
    private Cache mCache = new Cache();
    private Object mNetTag = new Object();

    private int mState = STATE_IDLE;



    public NewsListCollector(Category category,CallBacks callBacks) {
        this.category = category;
        this.mCallBacks = callBacks;
    }

    public NewsListCollector(Category category,Context context,RequestQueue requestQueue) {
        this(category, context, requestQueue,null);
    }

    public NewsListCollector(Category category,Context context,RequestQueue requestQueue,CallBacks callBacks) {
        this.context = context;
        this.category = category;
        this.mRequestQueue = requestQueue;
        this.mCallBacks = callBacks;
    }





    @Override
    public Object getItem(int position) {
        return newsOverviews.get(position);
    }

    @Override
    public int viewType(int position) {
        return 0;
    }

    @Override
    public int size() {
        return newsOverviews.size();
    }

    @Override
    public int getDataId(int position) {
        return newsOverviews.get(position).getAaid();
    }






    @Override
    public void clear() {
        newsOverviews.clear();
        resetState();
    }

    @Override
    public void asyncAppend() {
        if(isProcessing()){
            onAppendFails(null);
            return;
        }

        isProcessing = true;

        /*Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ArrayList<NewsOverview> newsList;
                try {
                    newsList = mObjectMapper.readValue(response.getJSONObject("data").getJSONArray("list").toString(), new TypeReference<List<NewsOverview>>() {
                    });
                    if(DEBUG){
                        Log.d(LOG_TAG, newsList.toString());
                    }
                    newsOverviews.addAll(newsList);
                    onAppend(null);
                } catch (Exception e) {
                    onAppendFails(null);
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
                onAppendFails(null);
            }
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,makeNewsURL(nextPage()),null,responseListener,errorListener);
        jsonObjectRequest.setTag(category);
        mRequestQueue.add(jsonObjectRequest);*/
        mArticleSyncHelper.syncFirstFromDB(category,nextPage(),mArticleSyncCallBacks);
    }

    @Override
    public void asyncRefresh() {
        if(isProcessing()){
            onRefreshFails(null);
            return;
        }

        isProcessing = false;
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ArrayList<NewsOverview> newsList;
                try {
                    newsList = mObjectMapper.readValue(response.getJSONObject("data").getJSONArray("list").toString(), new TypeReference<List<NewsOverview>>() {
                    });
                    if(DEBUG){
                        Log.d(LOG_TAG, newsList.toString());
                    }
                    newsOverviews.clear();
                    newsOverviews.addAll(newsList);
                    onRefresh(null);
                } catch (Exception e) {
                    onRefreshFails(null);
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
                onRefreshFails(null);
            }
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, TextUtils.makeNewsOverviewUrl(category,1),null,responseListener,errorListener);
        jsonObjectRequest.setTag(category);
        mRequestQueue.add(jsonObjectRequest);

    }



    private void asyncFetchBlocks(){

    }

    private void checkCache(){
        switch (mState){
            case STATE_PROCESSING_UPDATE:
                if(mCache.newsOverviews != null && mCache.blocks != null){
                    completeProcessing(mState);
                }
                break;
            case STATE_PROCESSING_INIT:
                if(mCache.newsOverviews != null && mCache.blocks != null){
                    completeProcessing(mState);
                }
                break;
            case STATE_PROCESSING_APPEND:
                if(mCache.newsOverviews != null){
                    completeProcessing(mState);
                }
                break;
        }
    }

    private void resetProcessing(){
        mCache.blocks = null;
        mCache.newsOverviews = null;
        mState = STATE_IDLE;
    }

    private void completeProcessing(int state){
        switch (state){
            case STATE_PROCESSING_UPDATE:
                blockArrayList.clear();
                newsOverviews.clear();
                blockArrayList.addAll(mCache.blocks);
                newsOverviews.addAll(mCache.newsOverviews);
                mExecutorService.submit(new DBUpDateBlockTask(mCache.blocks,category));
                mExecutorService.submit(new DBUpDateArticleTask(category, mCache.newsOverviews));
                resetProcessing();
                break;
            case STATE_PROCESSING_INIT:
                break;
            case STATE_PROCESSING_APPEND:
                break;
        }


    }



    private void cancelTask(){
        mRequestQueue.cancelAll(mNetTag);
        resetProcessing();
    }

    private class DBUpDateBlockTask implements Runnable{
        public ArrayList<Block> blocks;
        public Category category;

        private DBUpDateBlockTask(ArrayList<Block> blocks, Category category) {
            this.blocks = blocks;
            this.category = category;
        }

        @Override
        public void run() {
            mBlockAccessor.clear(category);
            for(Block block:blocks){
                mBlockAccessor.insertOrReplace(block);
            }
        }
    }

    private class DBUpDateArticleTask implements Runnable{
        public Category category;
        public ArrayList<NewsOverview> newsOverviews;

        private DBUpDateArticleTask(Category category, ArrayList<NewsOverview> newsOverviews) {
            this.category = category;
            this.newsOverviews = newsOverviews;
        }

        @Override
        public void run() {
            mArticleAccessor.clear(category);
            for(NewsOverview newsOverview:newsOverviews){
                mArticleAccessor.insertOrReplace(newsOverview);
            }
        }
    }



    private class BlockResponseListener implements Response.Listener<JSONObject>{
        @Override
        public void onResponse(JSONObject jsonObject) {
            try {
                ArrayList<Block> list = BlockClient.toList(jsonObject);
                mCache.blocks = list;
                checkCache();
            } catch (JSONException e) {
                mState = STATE_ERROR_JSON_PROBLEM;
                cancelTask();
                e.printStackTrace();
            } catch (IOException e) {
                mState = STATE_ERROR_NET_PROBLEM;
                cancelTask();
                e.printStackTrace();
            }
        }
    }

    private class ErrorListener implements Response.ErrorListener{
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            mState = STATE_ERROR_NET_PROBLEM;
            cancelTask();
        }
    }

    private class Cache{
        public ArrayList<NewsOverview> newsOverviews;
        public ArrayList<Block> blocks;

        public void clear(){
            newsOverviews = null;
            blocks = null;
        }
    }


    private ArticleSyncHelper.ArticleSyncCallBacks mArticleSyncCallBacks = new ArticleSyncHelper.ArticleSyncCallBacks() {
        @Override
        public void onSync(List<NewsOverview> list) {
            newsOverviews.addAll(list);
            onAppend(null);
        }

        @Override
        public void onError(int flag) {
            onAppendFails(null);
        }
    };

    private void onRefresh(SyncFlag syncFlag){


        currentPage = 1;
        if(mCallBacks != null){
            mCallBacks.onRefresh(category,null);
        }

        isProcessing = false;
    }

    private void onAppend(SyncFlag syncFlag){


        currentPage++;
        if(mCallBacks != null){
            mCallBacks.onAppend(category,null);
        }
        isProcessing = false;
    }

    private void onRefreshFails(SyncFlag syncFlag){



        if(mCallBacks != null){
            mCallBacks.onRefreshFails(category,null);
        }
        isProcessing = false;
    }

    private void onAppendFails(SyncFlag syncFlag){
        isProcessing = false;
        if(mCallBacks != null){
            mCallBacks.onAppendFails(category,null);
        }
    }



    /*private String makeNewsURL(int page){
        return URL_NEWS+"?"+"catid="+category.getCatid()+"&page="+page+"&size="+PAGE_SIZE;
    }*/
}
