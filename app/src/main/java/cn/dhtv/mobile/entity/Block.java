package cn.dhtv.mobile.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by Jack on 2015/4/21.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Block {
    private int id;
    private int uid;
    private int commentnum;
    private String idtype;
    private String username;
    private String avatar;
    private String url;
    private String title;
    private String pic;
    private String summary;
    private String dateline;
    private String catname;

    private int fromBid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getCommentnum() {
        return commentnum;
    }

    public void setCommentnum(int commentnum) {
        this.commentnum = commentnum;
    }

    public String getIdtype() {
        return idtype;
    }

    public void setIdtype(String idtype) {
        this.idtype = idtype;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
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

    public String getCatname() {
        return catname;
    }

    public void setCatname(String catname) {
        this.catname = catname;
    }

    public int getFromBid() {
        return fromBid;
    }

    public void setFromBid(int fromBid) {
        this.fromBid = fromBid;
    }

    public static ArrayList<Block> injectFromBid(ArrayList<Block>  blocks,Category category){
        for (Block block : blocks){
            block.setFromBid(category.getBid());
        }
        return blocks;
    }
}
