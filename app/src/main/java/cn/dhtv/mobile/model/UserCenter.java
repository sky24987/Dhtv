package cn.dhtv.mobile.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.dhtv.mobile.Data;
import cn.dhtv.mobile.Singletons;
import cn.dhtv.mobile.activity.LoginActivity;
import cn.dhtv.mobile.util.TextUtils;

/**
 * Created by Jack on 2015/6/10.
 */
public class UserCenter {
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    public static UserCenter userCenter = new UserCenter();

    private OkHttpClient mOkHttpClient = Singletons.getOkHttpClient();


    private User mUser;

    private Call registerCall;

    private UserCenter(){}

    public static UserCenter getInstance(){
        return userCenter;
    }

    public static void toLoginActivity(Activity activity,int requestCode){
        Intent intent = new Intent(activity,LoginActivity.class);
        activity.startActivityForResult(intent,requestCode);

    }

    public void logout(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Data.PREFERENCE_NAME_USER, Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(Data.PREFERENCE_KEY_USER_NAME)
                .remove(Data.PREFERENCE_KEY_USER_TOKEN)
                .remove(Data.PREFERENCE_KEY_USER_UID)
                .commit();
        return;
    }

    public LoginMessage login(String userName,String password) throws IOException,JSONException {
        String loginUrl = TextUtils.URL_LOGIN;
        RequestBody requestBody = new FormEncodingBuilder()
                .add("username",userName)
                .add("password",password)
                .build();
        Request request = new Request.Builder()
                .post(requestBody)
                .url(loginUrl)
                .build();
        Response response =  mOkHttpClient.newCall(request).execute();
        JSONObject jsonObject = new JSONObject(response.body().string());
        response.body().close();
        if(DEBUG){
            Log.d(LOG_TAG,jsonObject.toString());
        }

        LoginMessage loginMessage = new LoginMessage(userName, jsonObject);
        if(loginMessage.isSuccess()){
            setUser(loginMessage.getUser());
        }
        return loginMessage;
    }

    public RegisterMessage register(String userName,String email,String password,String repeatPassword,String mobilePhone,String SMSCode) throws IOException,JSONException{
        try {
            String loginUrl = TextUtils.URL_REGISTER;
            RequestBody requestBody = new FormEncodingBuilder()
                    .add("un", userName)
                    .add("em", email)
                    .add("pd", password)
                    .add("pdt", repeatPassword)
                    .add("mobile", mobilePhone)
                    .add("sms-captcha", SMSCode)
                    .build();
            Request request = new Request.Builder()
                    .post(requestBody)
                    .url(loginUrl)
                    .build();

            Call call = mOkHttpClient.newCall(request);
            registerCall = call;
            Response response =  call.execute();
            JSONObject jsonObject = new JSONObject(response.body().string());
            response.body().close();
            if(DEBUG){
                Log.d(LOG_TAG,jsonObject.toString());
            }
       /* System.out.println(response.body().string());*/

            RegisterMessage registerMessage = new RegisterMessage(userName, jsonObject);
            return  registerMessage;
        }finally {
            registerCall = null;
        }

    }

    public void cancelRegister(){
        if(registerCall != null){
            registerCall.cancel();
        }
    }


    public SMSCodeRequestMessage requestSMSCode(String mobilePhone) throws IOException,JSONException{
        String url = TextUtils.URL_SMS_CODE;
        RequestBody requestBody = new FormEncodingBuilder()
                .add("mobile", mobilePhone)
                .build();
        Request request = new Request.Builder()
                .post(requestBody)
                .url(url)
                .build();
        Response response =  mOkHttpClient.newCall(request).execute();
        SMSCodeRequestMessage smsCodeRequestMessage =  SMSCodeRequestMessage.build(response.body().string());
        return smsCodeRequestMessage;
    }

    public void storeUser(Context context,User user){
        mUser = user;
        SharedPreferences sharedPreferences = context.getSharedPreferences(Data.PREFERENCE_NAME_USER, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(Data.PREFERENCE_KEY_USER_NAME,user.getName()).putLong(Data.PREFERENCE_KEY_USER_UID,user.getUid()).putString(Data.PREFERENCE_KEY_USER_TOKEN,user.getToken()).commit();
    }

    public User retrieveUser(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Data.PREFERENCE_NAME_USER, Context.MODE_PRIVATE);
        String name;
        String token;
        long uid;
        name = sharedPreferences.getString(Data.PREFERENCE_KEY_USER_NAME,null);
        token = sharedPreferences.getString(Data.PREFERENCE_KEY_USER_TOKEN,null);
        uid = sharedPreferences.getLong(Data.PREFERENCE_KEY_USER_UID,-1);
        if(name == null || token == null || uid < 0){
            return null;
        }

        User user = new User(name, uid);
        user.setToken(token);
        return user;
    }

    public void setUser(User user){
        mUser = user;
    }

    public static class LoginMessage {
        User mUser;

        boolean success = false;
        int code;
        long uid;
        String token;
        String message;

        public LoginMessage(String userName,JSONObject jsonObject){

            try {
                code = jsonObject.getInt("code");
            }catch (JSONException e){
                success = false;
            }
            if(code == 200){
                success = true;
                try {
                    uid = jsonObject.getLong("uid");
                    token = jsonObject.getString("token");
                    mUser = new User(userName,uid);
                    mUser.setToken(token);
                }catch (JSONException e){
                    //TODO
                }



            }else {
                success = false;
                try {
                    message = jsonObject.getString("message");
                }catch (JSONException e){
                    //TODO
                }
            }

        }

        public int getCode() {
            return code;
        }

        public long getUid() {
            return uid;
        }

        public String getToken() {
            return token;
        }

        public String getMessage() {
            return message;
        }

        public boolean isSuccess(){
            return success;
        }

        public User getUser(){
            return mUser;
        }
    }

    public static class RegisterMessage{
        User user;
        boolean success;
        int code;
        long uid;
        String token;
        String message;

        RegisterMessage(String userName,JSONObject jsonObject){
            try {
                code = jsonObject.getInt("code");

            }catch (JSONException e){
                success = false;
            }
            if(code == 219){
                success = true;
                try {
                    uid = jsonObject.getInt("uid");
                    token = jsonObject.getString("token");
                    message = jsonObject.getString("message");
                    user = new User(userName,uid);
                    user.setToken(token);
                }catch (JSONException e){
                    //TODO
                }
            }else {
                success = false;
                try {
                    message = jsonObject.getString("message");
                }catch (JSONException e){
                    //TODO
                }
            }
        }

        public User getUser() {
            return user;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SMSCodeRequestMessage{
        int code;
        String message;

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

        public boolean isSuccess(){
            return code == 200;
        }

        public static SMSCodeRequestMessage build(String jsonString) throws JSONException,IOException{
            return Singletons.getObjectMapper().readValue(jsonString,SMSCodeRequestMessage.class);
        }
    }

    public static void main(String args[]){

        try {
            /*UserCenter.getInstance().requestSMSCode("15558895507");*/
            UserCenter.getInstance().register("dfdfdf7","123456@126.com","df1256","df1256","","");
        }catch (Exception e){

        }




    }
}
