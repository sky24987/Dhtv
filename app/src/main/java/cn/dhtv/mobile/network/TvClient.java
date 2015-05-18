package cn.dhtv.mobile.network;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.dhtv.mobile.Singletons;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.TvOverview;
import cn.dhtv.mobile.util.TextUtils;

/**
 * Created by Jack on 2015/5/11.
 */
public class TvClient {
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private OkHttpClient mOkHttpClient = Singletons.getOkHttpClient();

    public static ArrayList<TvOverview> toList(JSONObject jsonObject) throws JSONException,IOException{
        ArrayList<TvOverview> list = Singletons.getObjectMapper().readValue(jsonObject.getJSONArray("data").toString(), new TypeReference<List<TvOverview>>() {
        });
        return list;
    }

    public ArrayList<TvOverview> fetchTvs(Category category,int beginId) throws JSONException,IOException{
        try {
            String url = TextUtils.makeTvUrl(category, beginId);
            com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                    .url(url)
                    .build();
            com.squareup.okhttp.Response response = mOkHttpClient.newCall(request).execute();
            JSONObject jsonObject = new JSONObject(response.body().string());
            response.body().close();
            ArrayList<TvOverview> tvOverviews = toList(jsonObject);
            for (TvOverview tvOverview : tvOverviews) {
                tvOverview.setTv_pic(category);
                tvOverview.setTv_url(category);
            }
            return tvOverviews;
        }catch (Exception e){
            Log.d(LOG_TAG,e.toString());
            throw  e;
        }
    }
}
