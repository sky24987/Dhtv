package cn.dhtv.mobile;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import cn.dhtv.mobile.network.BitmapCache;

/**
 * Created by Jack on 2015/4/22.
 */
public class Singletons {
    private static Context appContext;

    private static ObjectMapper objectMapper;
    private static OkHttpClient okHttpClient;
    private static RequestQueue requestQueue;
    private static RequestQueue imageRequestQueue;
    private static ImageLoader imageLoader;

    private static ExecutorService executorService = Executors.newFixedThreadPool(3);
    private static ExecutorService dbExecutorService = Executors.newSingleThreadExecutor();

    public static void setUp(Context context){
        appContext = context.getApplicationContext();
        init(appContext);

    }

    private static void init(Context context){
        requestQueue = Volley.newRequestQueue(appContext);
        imageRequestQueue = requestQueue;
        imageLoader = new ImageLoader(imageRequestQueue,new BitmapCache());
    }

    public static RequestQueue getRequestQueue(){
        return requestQueue;
    }

    public static RequestQueue getImageRequestQueue(){
        return imageRequestQueue;
    }

    public static ImageLoader getImageLoader(){
        return imageLoader;
    }



    public static ObjectMapper getObjectMapper(){
        if(objectMapper == null){
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }

    public static OkHttpClient getOkHttpClient(){
        if(okHttpClient == null){
            okHttpClient = new OkHttpClient();
            okHttpClient.setConnectTimeout(12,TimeUnit.SECONDS);
            okHttpClient.setReadTimeout(5,TimeUnit.SECONDS);
            okHttpClient.setWriteTimeout(3,TimeUnit.SECONDS);
            okHttpClient.setRetryOnConnectionFailure(true);

        }
        return okHttpClient;
    }

    public static ExecutorService getExecutorService(){
        return executorService;
    }

    public static ExecutorService getDBExecutor(){
        return dbExecutorService;
    }

}
