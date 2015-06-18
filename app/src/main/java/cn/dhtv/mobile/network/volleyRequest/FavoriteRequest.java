package cn.dhtv.mobile.network.volleyRequest;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.fasterxml.jackson.core.type.TypeReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.dhtv.mobile.Singletons;
import cn.dhtv.mobile.entity.Favorite;
import cn.dhtv.mobile.network.message.FavoriteMessage;

/**
 * Created by Jack on 2015/6/17.
 */
public class FavoriteRequest extends Request<FavoriteMessage> {
    private Response.Listener<FavoriteMessage> mListener;

    public FavoriteRequest(int method, String url, Response.ErrorListener listener, Response.Listener<FavoriteMessage> mListener) {
        super(method, url, listener);
        this.mListener = mListener;
    }

    @Override
    protected Response<FavoriteMessage> parseNetworkResponse(NetworkResponse response) {
        FavoriteMessage favoriteMessage = new FavoriteMessage();
        try {
            JSONObject jsonObject = new JSONObject(new String(response.data));
            favoriteMessage.setCode(jsonObject.getInt("code"));
            favoriteMessage.setMessage(jsonObject.getString("message"));
            if(favoriteMessage.getCode() != 200){
                return Response.success(favoriteMessage,null);
            }

            ArrayList<Favorite> favorites = Singletons.getObjectMapper().readValue(jsonObject.getJSONObject("data").getJSONArray("list").toString(), new TypeReference<List<Favorite>>() {});
            favoriteMessage.setFavoriteArrayList(favorites);
            return Response.success(favoriteMessage,null);
        }catch (IOException e){
            return  Response.error(new ParseError());
        }catch (JSONException e){
            return  Response.error(new ParseError());
        }
    }

    @Override
    protected void deliverResponse(FavoriteMessage response) {
        mListener.onResponse(response);
    }
}
