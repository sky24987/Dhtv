package cn.dhtv.mobile;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import cn.dhtv.mobile.model.NewsListManager;
import cn.dhtv.mobile.network.BitmapCache;
import cn.dhtv.mobile.network.NetUtils;
import cn.dhtv.mobile.util.VideoDataManager;

/**
 * Created by Jack on 2015/2/11.
 */
public class MyApplication extends Application {
    private NewsListManager mNewsListManager;
    //private VideoDataManager mVideoDataManager;

    @Override
    public void onCreate() {
        super.onCreate();
        NetUtils.setup(this);
        mNewsListManager = new NewsListManager(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        NetUtils.release();
    }

    public NewsListManager getNewsListManager() {
        return mNewsListManager;
    }
}
