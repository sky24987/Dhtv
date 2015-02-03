package cn.dhtv.mobile.entity;

import android.util.JsonReader;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.json.JSONException;
import org.json.JSONObject;
import java.sql.Date;

/**
 * Created by Jack on 2015/1/20.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsOverview {
    private int aaid;
    private int aid;
    private int catid;
    private int uid;
    private String username;
    private String from;
    private String title;
    private String dateline;
    private String summary;
    private String url;
    private String pic_url;
    private Pic[] pic;

    public Pic[] getPic() {
        return pic;
    }

    public void setPic(Pic[] pic) {
        this.pic = pic;
    }

    public int getAaid() {
        return aaid;
    }

    public void setAaid(int aaid) {
        this.aaid = aaid;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getCatid() {
        return catid;
    }

    public void setCatid(int catid) {
        this.catid = catid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public NewsOverview(){

    }

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
        // do something: put to a Map; log a warning, whatever
    }

    /*public static NewsOverview fromJson(JSONObject jsonObject){
        NewsOverview newsOverview = new NewsOverview();
        try {
            newsOverview.setAaid(jsonObject.getInt("aaid"));
            newsOverview.setAid(jsonObject.getInt("aid"));
            newsOverview.setCatid(jsonObject.getInt("catid"));
            //newsOverview.setDateline(new Date(jsonObject.getString("dateline")));
            newsOverview.setFrom(jsonObject.getString("from"));
            newsOverview.setSummary(jsonObject.getString("summary"));
            newsOverview.setTitle(jsonObject.getString("title"));
            newsOverview.setFrom(jsonObject.getString("from"));
        }catch (JSONException e){
            //TODO:
            return null;
        }
        return newsOverview;
    }*/

    public static class Pic {
        public static final String PIC_URL_PREFEX = "http://img.dhtv.cn/app/";
        private String src;
        private int thumb;

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public int getThumb() {
            return thumb;
        }

        public void setThumb(int thumb) {
            this.thumb = thumb;
        }
    }
}
