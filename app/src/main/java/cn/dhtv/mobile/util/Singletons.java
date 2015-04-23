package cn.dhtv.mobile.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jack on 2015/4/22.
 */
public class Singletons {
    private static ObjectMapper objectMapper;
    private static OkHttpClient okHttpClient;
    private static ExecutorService executorService = Executors.newFixedThreadPool(3);



    public static ObjectMapper getObjectMapper(){
        if(objectMapper == null){
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }

    public static OkHttpClient getOkHttpClient(){
        if(okHttpClient == null){
            okHttpClient = new OkHttpClient();
        }
        return okHttpClient;
    }

    public static ExecutorService getExecutorService(){
        return executorService;
    }

}
