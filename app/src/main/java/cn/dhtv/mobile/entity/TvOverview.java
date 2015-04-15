package cn.dhtv.mobile.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by Jack on 2015/3/31.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TvOverview {
    public static final String URL = "http://data.wztv.cn/tv/";

    private int tvid;
    private int commentnum;
    private String dir;
    private String title;
    private String eptm;
    private String tv_url;
    private String tv_pic;

    private boolean checked = false;//是否已经阅读过

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setTv_url(Program program){
        tv_url = URL+program.getUpid()+"/"+dir+".mp4";
    }

    @JsonSetter(value = "dir")
    public void jsonSetDir(String dir){
        this.dir = dir;
    }

    public int getTvid() {
        return tvid;
    }

    public void setTvid(int tvid) {
        this.tvid = tvid;
    }

    public int getCommentnum() {
        return commentnum;
    }

    public void setCommentnum(int commentnum) {
        this.commentnum = commentnum;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEptm() {
        return eptm;
    }

    public void setEptm(String eptm) {
        this.eptm = eptm;
    }

    public String getTv_url() {
        return tv_url;
    }

    /*public void setTv_url(String tv_url) {
        this.tv_url = tv_url;
    }*/

    public String getTv_pic() {
        return tv_pic;
    }

    public void setTv_pic(String tv_pic) {
        this.tv_pic = tv_pic;
    }
}
