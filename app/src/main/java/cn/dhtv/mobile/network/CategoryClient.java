package cn.dhtv.mobile.network;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.fasterxml.jackson.core.type.TypeReference;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.dhtv.mobile.Singletons;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.network.exception.NullDataVolleyError;
import cn.dhtv.mobile.util.TextUtils;

/**
 * Created by Jack on 2015/4/27.
 */
public class CategoryClient {
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private OkHttpClient mOkHttpClient = Singletons.getOkHttpClient();



    public static ArrayList<Category> toList(JSONObject jsonObject) throws JSONException,IOException {
        ArrayList<Category> list;
        list = Singletons.getObjectMapper().readValue(jsonObject.getJSONObject("data").getJSONArray("children").toString(), new TypeReference<List<Category>>() {
        });
        return list;
    }

    public ArrayList<Category> fetchCategories(Category father) throws JSONException,IOException{
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(TextUtils.makeCategoryUrl(father,0))
                .build();
        com.squareup.okhttp.Response response =  mOkHttpClient.newCall(request).execute();
        JSONObject jsonObject = new JSONObject(response.body().string());
        response.body().close();
        return toList(jsonObject);
    }

    public static class CategoryArrayListRequest extends Request<ArrayList<Category>> {
        private Response.Listener<ArrayList<Category>> mListener;

        public CategoryArrayListRequest(int method, String url, Response.Listener<ArrayList<Category>> listener,Response.ErrorListener errorListener) {
            super(method, url, errorListener);
            mListener = listener;
        }

        @Override
        protected Response<ArrayList<Category>> parseNetworkResponse(NetworkResponse response) {
            try {
                JSONObject jsonObject = new JSONObject(new String(response.data));
                ArrayList<Category> list = toList(jsonObject);
                if(list.size() > 0){
                    return Response.success(list,null);
                }else {
                    return Response.error(new NullDataVolleyError());
                }
            }catch (JSONException e){
                return Response.error(new ParseError());
            }catch (IOException e){
                return Response.error(new NetworkError());
            }


        }

        @Override
        protected void deliverResponse(ArrayList<Category> response) {
            mListener.onResponse(response);
        }
    }
}
