package cn.dhtv.mobile.network;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import cn.dhtv.mobile.network.BitmapCache;

/**
 * Created by Jack on 2015/3/10.
 */
public class NetUtils {
    private static RequestQueue mRequestQueue;
    private static ImageLoader mImageLoader;

    public static RequestQueue getRequestQueue(){
        if(mRequestQueue == null){
            throw new NotSetupException();
        }

        return mRequestQueue;
    }

    public static ImageLoader getImageLoader(){
        if(mImageLoader == null){
            throw new NotSetupException();
        }

        return mImageLoader;
    }

    public static RequestQueue getRequestQueue(Context context){
        setup(context);
        return mRequestQueue;
    }

    public static ImageLoader getImageLoader(Context context){
        setup(context);
        return mImageLoader;
    }


    public static void setup(Context context){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(context);
        }

        if(mImageLoader == null){
            mImageLoader = new ImageLoader(mRequestQueue,new BitmapCache());
        }
    }

    public static void release(){
        if(mRequestQueue != null){
            mRequestQueue.stop();
            mRequestQueue = null;
            mImageLoader = null;
        }
    }

    public static class NotSetupException extends Error{

    }
}
