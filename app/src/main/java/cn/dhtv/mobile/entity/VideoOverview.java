package cn.dhtv.mobile.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by Jack on 2015/3/6.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoOverview {
    public static final String URL = "http://data.wztv.cn/mobile/";

    private int avid;
    private int vid;
    private int catid;
    private int uid;
    private int duration;

    private String username;
    private String title;
    private String summary;
    private String dateline;
    private String url;
    private String pic;
    private String video;
    private String dir;

    private boolean checked =false;//是否已经阅读过

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @JsonIgnore
    public void setChecked(int checked){
        if(checked == 1){
            this.checked = true;
        }else {
            this.checked = false;
        }
    }

    @JsonSetter(value = "dir")
    public void jsonSetDir(String dir){
        this.dir = dir;
        this.pic = URL+dir+".jpg";
        this.video = URL+dir+".mp4";
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public int getAvid() {
        return avid;
    }

    public void setAvid(int avid) {
        this.avid = avid;
    }

    public int getVid() {
        return vid;
    }

    public void setVid(int vid) {
        this.vid = vid;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
