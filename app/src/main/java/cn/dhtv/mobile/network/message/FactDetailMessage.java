package cn.dhtv.mobile.network.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.json.JSONException;

import java.io.IOException;

import cn.dhtv.mobile.Singletons;
import cn.dhtv.mobile.entity.FactDetail;

/**
 * Created by Jack on 2015/6/23.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FactDetailMessage {
    int code;
    String message;
    FactDetail factDetail = new FactDetail();

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FactDetail getFactDetail() {
        return factDetail;
    }

    public void setFactDetail(FactDetail factDetail) {
        this.factDetail = factDetail;
    }

    public static FactDetailMessage build(String json) throws IOException,JSONException{
        FactDetailMessage factDetailMessage = Singletons.getObjectMapper().readValue(json,FactDetailMessage.class);
        return factDetailMessage;
    }
}
