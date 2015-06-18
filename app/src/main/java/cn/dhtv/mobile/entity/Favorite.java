package cn.dhtv.mobile.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Jack on 2015/6/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Favorite {
    long favid;
    long id;
    String title;
    String idtype;
    String description;
    String dateline;

    public long getFavid() {
        return favid;
    }

    public void setFavid(long favid) {
        this.favid = favid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIdtype() {
        return idtype;
    }

    public void setIdtype(String idtype) {
        this.idtype = idtype;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }
}
