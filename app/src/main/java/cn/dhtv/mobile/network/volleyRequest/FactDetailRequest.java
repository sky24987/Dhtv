package cn.dhtv.mobile.network.volleyRequest;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONException;

import java.io.IOException;

import cn.dhtv.mobile.network.message.FactDetailMessage;
import cn.dhtv.mobile.network.message.FactMessage;

/**
 * Created by Jack on 2015/6/23.
 */
public class FactDetailRequest extends Request<FactDetailMessage> {
    private Response.Listener<FactDetailMessage> mListener;

    public FactDetailRequest(int method, String url, Response.ErrorListener listener, Response.Listener<FactDetailMessage> mListener) {
        super(method, url, listener);
        this.mListener = mListener;
    }

    @Override
    protected Response<FactDetailMessage> parseNetworkResponse(NetworkResponse response) {
        FactDetailMessage factDetailMessage;
        try {
            factDetailMessage = FactDetailMessage.build(new String(response.data));
            return Response.success(factDetailMessage,null);
        }catch (IOException e){
            return Response.error(new ParseError());
        }catch (JSONException e){
            return Response.error(new ParseError());
        }

    }

    @Override
    protected void deliverResponse(FactDetailMessage response) {
        mListener.onResponse(response);
    }
}
