package cn.dhtv.mobile.model;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.dhtv.mobile.adapter.AbstractListAdapter;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.NewsOverview;
import cn.dhtv.mobile.entity.VideoOverview;

/**
 * Created by Jack on 2015/3/20.
 */
public class VideoListCollector extends AbsListCollector {
    private static final String URL_VIDEO = "http://api.dhtv.cn/mobile/video/";

    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private ArrayList<VideoOverview> videoOverviews = new ArrayList<>();

    public VideoListCollector(Category category,CallBacks callBacks) {
        super(category, callBacks);
    }

    @Override
    public void clear() {
        videoOverviews.clear();
    }

    @Override
    public void asyncAppend() {
        if(isProcessing()){
            onAppendFails(null);
            return;
        }

        isProcessing = false;

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ArrayList<VideoOverview> list;
                if(DEBUG){
                    Log.d(LOG_TAG, "append request json:"+response);
                }
                try {
                    list = mObjectMapper.readValue(response.getJSONObject("data").getJSONArray("list").toString(), new TypeReference<List<VideoOverview>>() {
                    });
                    if(DEBUG){
                        Log.d(LOG_TAG, list.toString());
                    }
                    videoOverviews.addAll(list);
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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,makeVideoURL(nextPage()), null,responseListener,errorListener);
        jsonObjectRequest.setTag(category);
        mRequestQueue.add(jsonObjectRequest);
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
                ArrayList<VideoOverview> list;
                try {
                    list = mObjectMapper.readValue(response.getJSONObject("data").getJSONArray("list").toString(), new TypeReference<List<VideoOverview>>() {
                    });
                    if(DEBUG){
                        Log.d(LOG_TAG, list.toString());
                    }
                    videoOverviews.clear();
                    videoOverviews.addAll(list);
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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,makeVideoURL(1),null,responseListener,errorListener);
        jsonObjectRequest.setTag(category);
        mRequestQueue.add(jsonObjectRequest);
    }






    @Override
    public Object getItem(int position) {
        return videoOverviews.get(position);
    }

    @Override
    public AbstractListAdapter.ViewType viewType(int position) {
        return null;
    }

    @Override
    public int size() {
        return videoOverviews.size();
    }

    @Override
    public int getDataId(int position) {
        return videoOverviews.get(position).getAvid();
    }



    private void onRefresh(SyncFlag syncFlag){
        isProcessing = false;

        currentPage = 1;
        if(mCallBacks != null){
            mCallBacks.onRefresh(category,null);
        }
    }

    private void onAppend(SyncFlag syncFlag){
        isProcessing = false;

        currentPage++;
        if(mCallBacks != null){
            mCallBacks.onAppend(category,null);
        }
    }

    private void onRefreshFails(SyncFlag syncFlag){
        isProcessing = false;


        if(mCallBacks != null){
            mCallBacks.onRefreshFails(category,null);
        }
    }

    private void onAppendFails(SyncFlag syncFlag){
        isProcessing = false;
        if(mCallBacks != null){
            mCallBacks.onAppendFails(category,null);
        }
    }


    private String makeVideoURL(int page){
        return URL_VIDEO+"?"+"catid="+category.getCatid()+"&page="+page+"&size="+PAGE_SIZE;
    }
}
