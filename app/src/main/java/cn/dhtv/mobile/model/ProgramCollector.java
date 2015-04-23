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

import cn.dhtv.mobile.Sync.DataSyncHelper;
import cn.dhtv.mobile.adapter.AbstractListAdapter;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.Program;
import cn.dhtv.mobile.entity.VideoOverview;

/**
 * Created by Jack on 2015/3/26.
 */
public class ProgramCollector extends AbsListCollector {


    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private ArrayList<Category> programs = new ArrayList<>();

    public ProgramCollector(Category category, CallBacks callBacks) {
        super(category, callBacks);
    }

    @Override
    public void clear() {
        programs.clear();
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
                ArrayList<Program> list;
                if(DEBUG){
                    Log.d(LOG_TAG, "append request json:" + response);
                }
                try {
                    list = mObjectMapper.readValue(response.getJSONObject("data").getJSONArray("children").toString(), new TypeReference<List<Program>>() {
                    });
                    if(DEBUG){
                        Log.d(LOG_TAG, list.toString());
                    }
                    programs.addAll(list);
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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,makeProgramURL(nextPage()), null,responseListener,errorListener);
        jsonObjectRequest.setTag(category);
        mRequestQueue.add(jsonObjectRequest);
    }

    @Override
    public void asyncFirstFetch() {

    }

    @Override
    public void asyncRefresh() {
        if(isProcessing()){
            onRefreshFails(null);
            return;
        }

        isProcessing = false;
        /*Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ArrayList<Program> list;
                try {
                    list = mObjectMapper.readValue(response.getJSONObject("data").getJSONArray("children").toString(), new TypeReference<List<Program>>() {
                    });
                    if(DEBUG){
                        Log.d(LOG_TAG, list.toString());
                    }
                    programs.clear();
                    programs.addAll(list);
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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,makeProgramURL(1),null,responseListener,errorListener);
        jsonObjectRequest.setTag(category);
        mRequestQueue.add(jsonObjectRequest);*/

        DataSyncHelper.getInstance().SyncCategoryFirstFromDB(category,mCategorySyncCallBacks);

    }

    @Override
    public Object getItem(int position) {
        return programs.get(position);
    }

    @Override
    public int viewType(int position) {
        return 0;
    }

    @Override
    public int size() {
        return programs.size();
    }

    @Override
    public int getDataId(int position) {
        return programs.get(position).getCatid();
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

    private DataSyncHelper.CategorySyncCallBacks mCategorySyncCallBacks = new DataSyncHelper.CategorySyncCallBacks() {
        @Override
        public void onSync(List<Category> list) {
            programs.clear();
            programs.addAll(list);
            onRefresh(null);
        }

        @Override
        public void onError(int flag) {

        }
    };

    private String makeProgramURL(int page){
        return Category.URL+"?"+"catid="+category.getCatid()+"&page="+page+"&level=1";
    }
}
