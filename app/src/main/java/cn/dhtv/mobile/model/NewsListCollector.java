package cn.dhtv.mobile.model;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.dhtv.mobile.Sync.ArticleSyncHelper;
import cn.dhtv.mobile.Sync.SyncHelperFactory;
import cn.dhtv.mobile.adapter.AbstractListAdapter;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.NewsOverview;
import cn.dhtv.mobile.model.AbsListCollector.CallBacks;

/**
 * Created by Jack on 2015/3/17.
 */
public class NewsListCollector extends AbsListCollector{
    private static final String URL_NEWS = "http://api.dhtv.cn/mobile/article/";

    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;


    private ArticleSyncHelper mArticleSyncHelper = SyncHelperFactory.getInstance().newArticleSyncHelper();

    private ArrayList<NewsOverview> newsOverviews = new ArrayList<>();

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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,makeNewsURL(1),null,responseListener,errorListener);
        jsonObjectRequest.setTag(category);
        mRequestQueue.add(jsonObjectRequest);

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


    private String makeNewsURL(int page){
        return URL_NEWS+"?"+"catid="+category.getCatid()+"&page="+page+"&size="+PAGE_SIZE;
    }
}
