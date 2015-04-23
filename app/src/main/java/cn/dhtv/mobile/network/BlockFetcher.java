package cn.dhtv.mobile.network;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.dhtv.mobile.entity.Block;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.NewsOverview;
import cn.dhtv.mobile.util.TextUtils;

/**
 * Created by Jack on 2015/4/21.
 */
public class BlockFetcher {
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;
    
    private ObjectMapper mObjectMapper = new ObjectMapper();
    private RequestQueue mRequestQueue = NetUtils.getRequestQueue();

    public void asyncGetBlockData(Category category,WeakReference<CallBacks> callBacksWeakReference){
        BlockResponseListener blockResponseListener = new BlockResponseListener(callBacksWeakReference);
        BlockErrorListener blockErrorListener = new BlockErrorListener(callBacksWeakReference);
        String url = TextUtils.makeBlockQueryUrl(category);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,blockResponseListener,blockErrorListener);
        mRequestQueue.add(jsonObjectRequest);
    }

    private void onReceive(ArrayList<Block> blocks,WeakReference<CallBacks> callBacksWeakReference, int flag){
        CallBacks callBacks = callBacksWeakReference.get();
        if(callBacks != null){
            callBacks.onReceive(blocks,flag);
        }
    }

    private void onError(WeakReference<CallBacks> callBacksWeakReference,int flag){
        CallBacks callBacks = callBacksWeakReference.get();
        if(callBacks != null){
            callBacks.onError(flag);
        }
    }

    private class BlockResponseListener implements Response.Listener<JSONObject>{
        WeakReference<CallBacks> mCallBacksWeakReference;

        private BlockResponseListener(WeakReference<CallBacks> mCallBacksWeakReference) {
            this.mCallBacksWeakReference = mCallBacksWeakReference;
        }

        @Override
        public void onResponse(JSONObject response) {
            ArrayList<Block> list;
            try{
                list = mObjectMapper.readValue(response.getJSONArray("data").toString(), new TypeReference<List<Block>>() {
                });
                onReceive(list,mCallBacksWeakReference,0);

            }catch (JsonParseException e){
                Log.e(LOG_TAG,e.toString());

            }catch (JsonMappingException e){
                Log.e(LOG_TAG,e.toString());

            }catch (JSONException e){
                Log.e(LOG_TAG,e.toString());

            }catch (IOException e){
                Log.e(LOG_TAG,e.toString());

            }
        }
    }

    private class BlockErrorListener implements Response.ErrorListener{
        WeakReference<CallBacks> mCallBacksWeakReference;

        private BlockErrorListener(WeakReference<CallBacks> mCallBacksWeakReference) {
            this.mCallBacksWeakReference = mCallBacksWeakReference;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            onError(mCallBacksWeakReference, 0);
        }
    }


    public interface CallBacks{
        void onReceive(List<Block> blocks,int flag);
        void onError(int flag);
    }
}
