package cn.dhtv.mobile.model;

/**
 * Created by Jack on 2015/6/10.
 */
public class User {
    private final String name;
    private final long uid;
    private String token;

    public User(String name,long uid){
        this.name = name;
        this.uid = uid;
    }

    public void setToken(String token){
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getToken() {
        return token;
    }
}
