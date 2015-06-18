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
import cn.dhtv.mobile.entity.Block;
import cn.dhtv.mobile.entity.Comment;
import cn.dhtv.mobile.entity.Fact;
import cn.dhtv.mobile.network.message.FactMessage;

/**
 * Created by Jack on 2015/6/16.
 */
public class FactRequest extends Request<FactMessage> {
    private Response.Listener<FactMessage> mListener;

    public FactRequest(int method, String url, Response.ErrorListener listener, Response.Listener<FactMessage> mListener) {
        super(method, url, listener);
        this.mListener = mListener;
    }

    @Override
    protected Response<FactMessage> parseNetworkResponse(NetworkResponse response) {
        FactMessage factMessage = new FactMessage();
        try {
            JSONObject jsonObject = new JSONObject(new String(response.data));
            factMessage.setCode(jsonObject.getInt("code"));
            factMessage.setMessage(jsonObject.getString("message"));
            if(factMessage.getCode() != 200){
                return Response.success(factMessage,null);
            }

            ArrayList<Fact> facts = Singletons.getObjectMapper().readValue(jsonObject.getJSONObject("data").getJSONArray("list").toString(), new TypeReference<List<Fact>>() {});
            factMessage.setFactArrayList(facts);
            return Response.success(factMessage,null);
        }catch (JSONException e){
            return  Response.error(new ParseError());
        }catch (IOException e){
            return  Response.error(new ParseError());
        }
    }

    @Override
    protected void deliverResponse(FactMessage response) {
        mListener.onResponse(response);
    }
}
