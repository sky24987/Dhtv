package cn.dhtv.mobile.network;

import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.dhtv.mobile.entity.NewsOverview;
import cn.dhtv.mobile.Singletons;

/**
 * Created by Jack on 2015/4/23.
 */
public class NewsClient {
    public static ArrayList<NewsOverview> toList(JSONObject jsonObject) throws JSONException,IOException {
        ArrayList<NewsOverview> list;
        list = Singletons.getObjectMapper().readValue(jsonObject.getJSONObject("data").getJSONArray("list").toString(), new TypeReference<List<NewsOverview>>() {
        });
        return list;
    }
}
