package cn.dhtv.mobile;

import android.app.Application;
import android.util.Log;

import cn.dhtv.mobile.model.NewsPageManager;
import cn.dhtv.mobile.model.VideoPageManager;
import cn.dhtv.mobile.network.NetUtils;

/**
 * Created by Jack on 2015/2/11.
 */
public class MyApplication extends Application {
    private  final String LOG_TAG = getClass().getSimpleName();
    private  final boolean DEBUG = true;

    //private NewsListManager mNewsListManager;
    private NewsPageManager mNewsPageManager;
    private VideoPageManager mVideoPageManager;

    @Override
    public void onCreate() {
        super.onCreate();
        NetUtils.setup(this);
//        mNewsListManager = new NewsListManager(this);
//        mNewsListManager.setUp();
        mNewsPageManager = new NewsPageManager();
        mNewsPageManager.setUp();
        mVideoPageManager = new VideoPageManager();
        mVideoPageManager.setUp();
        if(DEBUG){
            Log.d(LOG_TAG,"onCreate()");
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
//        mNewsListManager.release();
        NetUtils.release();
        if(DEBUG){
            Log.d(LOG_TAG,"onTerminate()");
        }

    }

    public NewsPageManager getNewsPageManager(){
        return mNewsPageManager;
    }

    public VideoPageManager getVideoPageManager(){return  mVideoPageManager;}

    /*public NewsListManager getNewsListManager() {
        return mNewsListManager;
    }*/


}
