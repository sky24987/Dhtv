package cn.dhtv.mobile.network;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.dhtv.mobile.Singletons;
import cn.dhtv.mobile.entity.Block;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.VideoOverview;
import cn.dhtv.mobile.util.TextUtils;

/**
 * Created by Jack on 2015/4/29.
 */
public class VideoClient {
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private OkHttpClient mOkHttpClient = Singletons.getOkHttpClient();

    public ArrayList<VideoOverview> getVideoOverviews(Category category,int beginId) throws JSONException,IOException{
        try {

            Request request = new Request.Builder()
                    .url(TextUtils.makeVideoUrl(category, beginId))
                    .build();
            Response response = mOkHttpClient.newCall(request).execute();
            JSONObject jsonObject = new JSONObject(response.body().string());
            response.body().close();
            return toList(jsonObject);

        }catch (Exception e){
            Log.d(LOG_TAG,e.toString());
            throw e;
        }
    }

    public static ArrayList<VideoOverview> toList(JSONObject jsonObject) throws JSONException,IOException {
        ArrayList<VideoOverview> list;
        list = Singletons.getObjectMapper().readValue(jsonObject.getJSONObject("data").getJSONArray("list").toString(), new TypeReference<List<VideoOverview>>() {
        });
        return list;
    }
}
