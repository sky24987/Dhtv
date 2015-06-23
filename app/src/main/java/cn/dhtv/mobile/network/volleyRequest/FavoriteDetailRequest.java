package cn.dhtv.mobile.network.volleyRequest;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONException;

import java.io.IOException;

import cn.dhtv.mobile.network.message.FactMessage;
import cn.dhtv.mobile.network.message.FavoriteDetailMessage;
import cn.dhtv.mobile.network.message.FavoriteMessage;

/**
 * Created by Jack on 2015/6/23.
 */
public class FavoriteDetailRequest extends Request<FavoriteDetailMessage> {
    private Response.Listener<FavoriteDetailMessage> mListener;

    public FavoriteDetailRequest(int method, String url, Response.ErrorListener listener, Response.Listener<FavoriteDetailMessage> mListener) {
        super(method, url, listener);
        this.mListener = mListener;
    }

    @Override
    protected Response<FavoriteDetailMessage> parseNetworkResponse(NetworkResponse response) {
        FavoriteDetailMessage favoriteDetailMessage;
        try {
            favoriteDetailMessage = FavoriteDetailMessage.build(new String(response.data));
            return Response.success(favoriteDetailMessage,null);
        }catch (IOException e){
            return Response.error(new ParseError());
        }catch (JSONException e){
            return Response.error(new ParseError());
        }
    }

    @Override
    protected void deliverResponse(FavoriteDetailMessage response) {
        mListener.onResponse(response);
    }
}
