package cn.dhtv.mobile.network;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.dhtv.mobile.entity.Block;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.Singletons;
import cn.dhtv.mobile.util.TextUtils;

/**
 * Created by Jack on 2015/4/22.
 */
public class BlockClient {
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private ObjectMapper mObjectMapper = Singletons.getObjectMapper();
    private OkHttpClient mOkHttpClient = Singletons.getOkHttpClient();

    public ArrayList<Block> getBlockData(Category category) throws JSONException,IOException{
        Request request = new Request.Builder()
                .url(TextUtils.makeBlockQueryUrl(category))
                .build();
        Response response =  mOkHttpClient.newCall(request).execute();
        JSONObject jsonObject = new JSONObject(response.body().string());
        return toList(jsonObject);
    }



    public static ArrayList<Block> toList(JSONObject jsonObject) throws JSONException,IOException {
        ArrayList<Block> list;
        list = Singletons.getObjectMapper().readValue(jsonObject.getJSONArray("data").toString(), new TypeReference<List<Block>>() {
        });
        return list;
    }

}
