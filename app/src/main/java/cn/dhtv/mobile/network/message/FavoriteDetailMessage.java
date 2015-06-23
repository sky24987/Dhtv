package cn.dhtv.mobile.network.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import org.json.JSONException;

import java.io.IOException;

import cn.dhtv.mobile.Singletons;
import cn.dhtv.mobile.entity.Favorite;
import cn.dhtv.mobile.entity.FavoriteDetail;

/**
 * Created by Jack on 2015/6/23.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FavoriteDetailMessage {
    int code;
    String message;
    FavoriteDetail favoriteDetail = new FavoriteDetail();

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

    public FavoriteDetail getFavoriteDetail() {
        return favoriteDetail;
    }

    @JsonSetter(value = "data")
    public void setFavoriteDetail(FavoriteDetail favoriteDetail) {
        this.favoriteDetail = favoriteDetail;
    }

    public static FavoriteDetailMessage build(String json) throws IOException,JSONException{
        FavoriteDetailMessage favoriteDetailMessage = Singletons.getObjectMapper().readValue(json,FavoriteDetailMessage.class);
        return favoriteDetailMessage;
    }

}
