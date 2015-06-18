package cn.dhtv.mobile.network.volleyRequest;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.Comment;
import cn.dhtv.mobile.network.CommentClient;

/**
 * Created by Jack on 2015/6/16.
 */
public class CommentArrayListRequest extends Request<ArrayList<Comment>> {
    private Response.Listener<ArrayList<Comment>> mListener;

    public CommentArrayListRequest(int method, String url, Response.ErrorListener listener, Response.Listener<ArrayList<Comment>> mListener) {
        super(method, url, listener);
        this.mListener = mListener;
    }

    @Override
    protected Response<ArrayList<Comment>> parseNetworkResponse(NetworkResponse response) {
        try {
            JSONObject jsonObject = new JSONObject(new String(response.data));
            ArrayList<Comment> list = CommentClient.toList(jsonObject);
            return Response.success(list,null);
        }catch (IOException e){
            return Response.error(new ParseError());
        }catch (JSONException e){
            return Response.error(new ParseError());
        }
    }

    @Override
    protected void deliverResponse(ArrayList<Comment> response) {
        mListener.onResponse(response);
    }
}
