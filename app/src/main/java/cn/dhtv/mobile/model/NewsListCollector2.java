package cn.dhtv.mobile.model;

import android.os.Handler;
import android.os.Message;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import cn.dhtv.mobile.Database.ArticleAccessor;
import cn.dhtv.mobile.Database.BlockAccessor;
import cn.dhtv.mobile.Sync.ArticleSyncHelper;
import cn.dhtv.mobile.Sync.BlockSyncHelper;
import cn.dhtv.mobile.Sync.SyncHelperFactory;
import cn.dhtv.mobile.entity.Block;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.NewsOverview;
import cn.dhtv.mobile.network.BlockClient;
import cn.dhtv.mobile.network.NewsClient;
import cn.dhtv.mobile.Singletons;
import cn.dhtv.mobile.util.TextUtils;

/**
 * Created by Jack on 2015/4/23.
 */
public class NewsListCollector2 extends AbsListCollector {
    private static final int STATE_PROCESSING_APPEND = 1;
    private static final int STATE_PROCESSING_UPDATE = 2;
    private static final int STATE_PROCESSING_INIT = 3;
    private static final int STATE_IDLE = 0;
    private static final int STATE_ERROR_NET_PROBLEM = -1;
    private static final int STATE_ERROR_JSON_PROBLEM = -3;
    private static final int STATE_ERROR_DB_PROBLEM_NULL = -2;

    private static final int MODE_APPEND = 1;
    private static final int MODE_REFRESH = 2;
    private static final int MODE_INIT = 3;
    private static final int MODE_IDLE = 0;

    private static final int MESSAGE_SUCCESS = 1;
    private static final int MESSAGE_NULL = 0;



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
    private int mMode;

    public NewsListCollector2(Category category, CallBacks callBacks) {
        super(category, callBacks);
    }

    @Override
    public void clear() {

    }

    @Override
    public void asyncAppend() {
        mMode = MODE_APPEND;
        if(mState != STATE_IDLE){
            onFails();
            resetProcessing();
            return;
        }

        mState = STATE_PROCESSING_APPEND;
        mExecutorService.submit(new DBFindArticleTask(category, 0,true));
    }


    @Override
    public void asyncFirstFetch(){
        mMode = MODE_INIT;
        if(mState != STATE_IDLE){
            onFails();
            resetProcessing();
            return;
        }

        mState = STATE_PROCESSING_INIT;

        JsonObjectRequest newsRequest = new JsonObjectRequest(Request.Method.GET, TextUtils.makeNewsOverviewUrl(category, 1),null,new ArticleResponseListener(),new ErrorListener());
        JsonObjectRequest blockRequest = new JsonObjectRequest(Request.Method.GET, TextUtils.makeBlockQueryUrl(category),null,new BlockResponseListener(),new ErrorListener());
        newsRequest.setTag(mNetTag);
        blockRequest.setTag(mNetTag);
        mRequestQueue.add(newsRequest);
        mRequestQueue.add(blockRequest);
    }

    @Override
    public void asyncRefresh() {
        mMode = MODE_REFRESH;
        if(mState != STATE_IDLE){
            onFails();
            resetProcessing();
            return;
        }

        mState = STATE_PROCESSING_UPDATE;

        JsonObjectRequest newsRequest = new JsonObjectRequest(Request.Method.GET, TextUtils.makeNewsOverviewUrl(category, 1),null,new ArticleResponseListener(),new ErrorListener());
        JsonObjectRequest blockRequest = new JsonObjectRequest(Request.Method.GET, TextUtils.makeBlockQueryUrl(category),null,new BlockResponseListener(),new ErrorListener());
        newsRequest.setTag(mNetTag);
        blockRequest.setTag(mNetTag);
        mRequestQueue.add(newsRequest);
        mRequestQueue.add(blockRequest);
    }

    private void onFails(){
        switch (mMode){
            case MODE_REFRESH:
                if(mCallBacks != null){
                    mCallBacks.onRefreshFails(category, null);
                }
                break;
            case MODE_APPEND:
                if(mCallBacks != null){
                    mCallBacks.onAppendFails(category, null);
                }
                break;
            case MODE_INIT:
                if(mCallBacks != null){
                    mCallBacks.onFirstFetchFails(category, null);
                }
                break;
            case MODE_IDLE:
                break;
        }
    }

    private void onSync(){
        switch (mMode){
            case MODE_REFRESH:
                if(mCallBacks != null){
                    mCallBacks.onRefresh(category, null);
                }
                break;
            case MODE_APPEND:
                if(mCallBacks != null){
                    mCallBacks.onAppend(category, null);
                }
                break;
            case MODE_INIT:
                if(mCallBacks != null){
                    mCallBacks.onFirstFetch(category, null);
                }
                break;
            case MODE_IDLE:
                break;
        }
    }



    public ArrayList<Block> getBlockArrayList(){
        return blockArrayList;
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
        return 0;
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
        mCache.clear();
        mState = STATE_IDLE;
    }

    private void completeProcessing(int state){
        switch (state){
            case STATE_PROCESSING_UPDATE:
                blockArrayList.clear();
                newsOverviews.clear();
                blockArrayList.addAll(mCache.blocks);
                newsOverviews.addAll(mCache.newsOverviews);
                if(mCache.newsOrigin == Cache.FROM_NET){
                    mExecutorService.submit(new DBUpDateArticleTask(category, mCache.newsOverviews));
                }
                if(mCache.blockOrigin == Cache.FROM_NET) {
                    mExecutorService.submit(new DBUpDateBlockTask(mCache.blocks,category));
                }
                onSync();
                resetProcessing();
                break;
            case STATE_PROCESSING_INIT:
                //TODO
                blockArrayList.clear();
                newsOverviews.clear();
                blockArrayList.addAll(mCache.blocks);
                newsOverviews.addAll(mCache.newsOverviews);
                if(mCache.newsOrigin == Cache.FROM_NET){
                    mExecutorService.submit(new DBUpDateArticleTask(category, mCache.newsOverviews));
                }
                if(mCache.blockOrigin == Cache.FROM_NET) {
                    mExecutorService.submit(new DBUpDateBlockTask(mCache.blocks,category));
                }
                onSync();
                resetProcessing();
                break;
            case STATE_PROCESSING_APPEND:
                newsOverviews.addAll(mCache.newsOverviews);
                if(mCache.newsOrigin == Cache.FROM_NET){
                    mExecutorService.submit(new DBUpDateArticleTask(category, mCache.newsOverviews));
                }
                onSync();
                resetProcessing();
                break;
        }


    }

    private void cancelTask(){
        mRequestQueue.cancelAll(mNetTag);
        mBlockHandler.removeMessages(MESSAGE_SUCCESS);
        mBlockHandler.removeMessages(MESSAGE_NULL);
        mArticleHandler.removeMessages(MESSAGE_SUCCESS);
        mArticleHandler.removeMessages(MESSAGE_NULL);

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

    private class DBFindArticleTask implements Runnable{
        private Category category;
        private int minId;
        private boolean attemptNetWork = false;

        private DBFindArticleTask(Category category, int minId, boolean attemptNetWork) {
            this.category = category;
            this.minId = minId;
            this.attemptNetWork = attemptNetWork;
        }

        @Override
        public void run() {
            ArrayList<NewsOverview> list =  (ArrayList<NewsOverview>) mArticleAccessor.findArticles(category, minId);
            if(list.size() == 0){
                if(attemptNetWork){
                    JsonObjectRequest newsRequest = new JsonObjectRequest(Request.Method.GET, TextUtils.makeNewsOverviewUrl(category, 1),null,new ArticleResponseListener(),new ErrorListener());
                    newsRequest.setTag(mNetTag);
                    mRequestQueue.add(newsRequest);
                }else {
                    mArticleHandler.sendMessage(mArticleHandler.obtainMessage(MESSAGE_NULL));
                }
            }else {
                mArticleHandler.sendMessage(mArticleHandler.obtainMessage(MESSAGE_SUCCESS,list));
            }
        }
    }

    private class DBFindBlockTask implements Runnable{
        private Category category;
        private boolean attemptNetWork = false;

        private DBFindBlockTask(Category category, boolean attemptNetWork) {
            this.category = category;
            this.attemptNetWork = attemptNetWork;
        }

        @Override
        public void run() {
            ArrayList<Block> list = mBlockAccessor.findBlocks(category);
            if(list.size() == 0){
                if(attemptNetWork){
                    JsonObjectRequest blockRequest = new JsonObjectRequest(Request.Method.GET, TextUtils.makeBlockQueryUrl(category),null,new BlockResponseListener(),new ErrorListener());
                    blockRequest.setTag(mNetTag);
                    mRequestQueue.add(blockRequest);
                }else {
                    mBlockHandler.sendMessage(mBlockHandler.obtainMessage(MESSAGE_NULL));
                }
            }else {
                mBlockHandler.sendMessage(mBlockHandler.obtainMessage(MESSAGE_SUCCESS,list));
            }
        }
    }

    /*private class DBFindBlockTask implements Runnable{
        private Category category;
        private boolean attemptNet = false;

        private DBFindBlockTask(Category category, boolean attemptNet) {
            this.category = category;
            this.attemptNet = attemptNet;
        }

        @Override
        public void run() {
            mBlockAccessor.findBlocks(category);
        }
    }*/

    private Handler mArticleHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case  MESSAGE_SUCCESS:
                    mCache.newsOverviews = (ArrayList<NewsOverview>) msg.obj;
                    mCache.newsOrigin = Cache.FROM_DB;
                    checkCache();
                    break;
                case MESSAGE_NULL:
                    mState = STATE_ERROR_DB_PROBLEM_NULL;
                    cancelTask();
                    onFails();
                    resetProcessing();
                    break;

            }
        }
    };

    private Handler mBlockHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case  MESSAGE_SUCCESS:
                    mCache.blocks = (ArrayList<Block>) msg.obj;
                    mCache.blockOrigin = Cache.FROM_DB;
                    checkCache();
                    break;
                case MESSAGE_NULL:
                    mState = STATE_ERROR_DB_PROBLEM_NULL;
                    cancelTask();
                    onFails();
                    resetProcessing();
                    break;
            }
        }
    };

    private class ArticleResponseListener implements Response.Listener<JSONObject>{
        @Override
        public void onResponse(JSONObject jsonObject) {
            try {
                ArrayList<NewsOverview> list = NewsClient.toList(jsonObject);
                mCache.newsOverviews = list;
                mCache.newsOrigin = Cache.FROM_NET;
                checkCache();
            } catch (JSONException e) {
                mState = STATE_ERROR_JSON_PROBLEM;
                cancelTask();
                onFails();
                resetProcessing();
                e.printStackTrace();
            } catch (IOException e) {

                if(mMode == MODE_INIT){
                    /*如果为初始刷新状态，遇到网络失败，则尝试从数据库取*/
                    cancelTask();
                    mExecutorService.submit(new DBFindArticleTask(category, 0, false));
                    mExecutorService.submit(new DBFindBlockTask(category,false));

                }


                mState = STATE_ERROR_NET_PROBLEM;
                cancelTask();
                onFails();
                resetProcessing();
                e.printStackTrace();
            }
        }
    }

    private class BlockResponseListener implements Response.Listener<JSONObject>{
        @Override
        public void onResponse(JSONObject jsonObject) {
            try {
                ArrayList<Block> list = BlockClient.toList(jsonObject);
                mCache.blocks = list;
                mCache.blockOrigin = Cache.FROM_NET;
                checkCache();
            } catch (JSONException e) {
                if(mMode == MODE_INIT){
                    /*如果为初始刷新状态，遇到网络失败，则尝试从数据库取*/
                    cancelTask();
                    mExecutorService.submit(new DBFindArticleTask(category, 0, false));
                    mExecutorService.submit(new DBFindBlockTask(category,false));

                }


                mState = STATE_ERROR_JSON_PROBLEM;
                cancelTask();
                onFails();
                resetProcessing();
                e.printStackTrace();
            } catch (IOException e) {
                mState = STATE_ERROR_NET_PROBLEM;
                cancelTask();
                onFails();
                resetProcessing();
                e.printStackTrace();
            }
        }
    }

    private class ErrorListener implements Response.ErrorListener{
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            mState = STATE_ERROR_NET_PROBLEM;
            cancelTask();
            onFails();
            resetProcessing();
        }
    }


    private class Cache{
        public static final int FROM_NET = 1;
        public static final int FROM_DB = 2;

        public ArrayList<NewsOverview> newsOverviews;
        public ArrayList<Block> blocks;

        public int newsOrigin;
        public int blockOrigin;

        public void clear(){
            newsOverviews = null;
            blocks = null;
            newsOrigin = 0;
            blockOrigin= 0;
        }
    }
}
