package cn.dhtv.mobile.model;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.dhtv.mobile.entity.NewsCat;
import cn.dhtv.mobile.entity.NewsOverview;

/**
 * Created by Jack on 2015/3/10.
 */
public class NewsListManager extends ListManager<NewsCat,NewsDataList> {
    private static final String LOG_TAG = "NewsListManager";
    private static final boolean DEBUG = true;

    public static final String URL = NewsCat.NEWS_URL;

    public NewsListManager() {
        super();
    }

    public NewsListManager(Context context) {
        super(context);
    }

    @Override
    public void setUp(){
        NewsCat newsCat1 = new NewsCat();
        newsCat1.setCatname("时政");
        newsCat1.setCatid(256);
        NewsCat newsCat2 = new NewsCat();
        newsCat2.setCatname("社会");
        newsCat2.setCatid(260);
        NewsCat newsCat3 = new NewsCat();
        newsCat3.setCatname("文化");
        newsCat3.setCatid(263);

        mCategories.add(newsCat1);
        mCategories.add(newsCat2);
        mCategories.add(newsCat3);
        for(NewsCat cat:mCategories){
            mDataListMap.put(cat,new NewsDataList(cat));
        }

    }

    @Override
    public void refresh(final NewsCat category, int flag) {
        if(isProcessing()){
            if(mCallBacks != null){
                mCallBacks.onRefreshFails(category,0);
            }
            return;
        }

        mProcessingList.add(category);

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                NewsDataList dataList = getDataList(category);
                ArrayList<NewsOverview> newsList = null;
                try {
                    newsList = mObjectMapper.readValue(response.getJSONObject("data").getJSONArray("list").toString(), new TypeReference<List<NewsOverview>>() {
                    });
                    if(DEBUG){
                        Log.d(LOG_TAG, newsList.toString());
                    }
                    dataList.replace(newsList);
                    dataList.resetState();
                    dataList.setCurrentPage(1);
                    dataList.setNewData(true);
                    if(mCallBacks != null){
                        mCallBacks.onRefresh(category,0);
                    }
                } catch (Exception e) {
                    if(mCallBacks != null){
                        mCallBacks.onRefreshFails(category,0);
                    }
                    e.printStackTrace();
                    Log.e(LOG_TAG, e.getMessage());
                }finally{
                    mProcessingList.remove(category);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG,error.getMessage());
                mProcessingList.remove(category);
                if(mCallBacks != null){
                    mCallBacks.onRefreshFails(category,0);
                }
            }
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,makeURL(category,1),null,responseListener,errorListener);
        mRequestQueue.add(jsonObjectRequest);
    }

    @Override
    public void append(final NewsCat category, int flag) {
        if(isProcessing(category)){
            if(mCallBacks != null){
                mCallBacks.onAppendFails(category,0);
            }
            return;
        }

        mProcessingList.add(category);

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                NewsDataList dataList = getDataList(category);
                ArrayList<NewsOverview> newsList = null;
                try {
                    newsList = mObjectMapper.readValue(response.getJSONObject("data").getJSONArray("list").toString(), new TypeReference<List<NewsOverview>>() {
                    });
                    if(DEBUG){
                        Log.d(LOG_TAG, newsList.toString());
                    }
                    dataList.append(newsList);
                    dataList.setCurrentPage(dataList.nextPage());
                    dataList.setNewData(true);
                    if(mCallBacks != null){
                        mCallBacks.onAppend(category,0);
                    }
                } catch (Exception e) {
                    if(mCallBacks != null){
                        mCallBacks.onAppendFails(category,0);
                    }
                    e.printStackTrace();
                    Log.e(LOG_TAG, e.getMessage());
                }finally{
                    mProcessingList.remove(category);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG,error.getMessage());
                mProcessingList.remove(category);
                if(mCallBacks != null){
                    mCallBacks.onAppendFails(category,0);
                }
            }
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,makeURL(category,getDataList(category).nextPage()),null,responseListener,errorListener);
        mRequestQueue.add(jsonObjectRequest);
    }

    @Override
    public void release() {
        mCategories.clear();
        mDataListMap.clear();
    }

    private String makeURL(NewsCat category,int page){
        return URL+"?"+"catid="+category.getCatid()+"&page="+page+"&size="+PAGE_SIZE;
    }
}
