package cn.dhtv.mobile;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import cn.dhtv.mobile.network.BitmapCache;
import cn.dhtv.mobile.util.VideoDataManager;

/**
 * Created by Jack on 2015/2/11.
 */
public class MyApplication extends Application {
    private VideoDataManager mVideoDataManager;

    private RequestQueue requestQueue;//Volley网络请求队列
    private RequestQueue imgRequestQuene;//图片请求队列
    private ImageLoader mImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        mVideoDataManager = new VideoDataManager();
        requestQueue =  Volley.newRequestQueue(this);//Volley网络请求队列
        imgRequestQuene = Volley.newRequestQueue(this);//图片请求队列
        mImageLoader = new ImageLoader(imgRequestQuene,new BitmapCache());

        requestQueue.start();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        requestQueue.stop();
        imgRequestQuene.stop();
    }

    public VideoDataManager getVideoDataManager() {
        return mVideoDataManager;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
