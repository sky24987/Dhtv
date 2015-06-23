package cn.dhtv.mobile.network;

import com.squareup.okhttp.Request;

import cn.dhtv.mobile.util.TextUtils;

/**
 * Created by Jack on 2015/6/23.
 */
public class OkHttpUtils {
    public static Request makeFactDetailRequest(long uid,String token,long msgid){
        Request request = new Request.Builder()
                .url(TextUtils.makeFactDetailUrl(uid, token, msgid))
                .build();
        return request;
    }
}
