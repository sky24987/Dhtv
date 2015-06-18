package cn.dhtv.mobile.network;

import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.dhtv.mobile.Singletons;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.Comment;

/**
 * Created by Jack on 2015/6/16.
 */
public class CommentClient {
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    public static ArrayList<Comment> toList(JSONObject jsonObject) throws IOException,JSONException{
        ArrayList<Comment> list;
        list = Singletons.getObjectMapper().readValue(jsonObject.toString(), new TypeReference<List<Comment>>() {
        });
        return list;
    }
}
