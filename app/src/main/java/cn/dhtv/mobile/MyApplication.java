package cn.dhtv.mobile;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.preference.Preference;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.squareup.picasso.Picasso;

import cn.dhtv.mobile.Database.DBHelper;
import cn.dhtv.mobile.Sync.DataSyncHelper;
import cn.dhtv.mobile.model.NewsPageManager;
import cn.dhtv.mobile.model.ProgramPageManager;
import cn.dhtv.mobile.model.VideoPageManager;
import cn.dhtv.mobile.network.NetUtils;
import cn.dhtv.mobile.service.AudioService;

/**
 * Created by Jack on 2015/2/11.
 */
public class MyApplication extends Application {
    private  final String LOG_TAG = getClass().getSimpleName();
    private  final boolean DEBUG = true;

    private int versionCode;

    private MediaPlayer mMediaPlayer;

    private NewsPageManager mNewsPageManager;
    private VideoPageManager mVideoPageManager;
    private ProgramPageManager mProgramPageManager;

    @Override
    public void onCreate() {
        super.onCreate();
        init();



        Singletons.setUp(this);

        Picasso.with(this).setIndicatorsEnabled(true);
//        Fresco.initialize(this);
//        NetUtils.setup(this);
        DBHelper.setUp(this);
        DataSyncHelper.setUp(this);
        mNewsPageManager = new NewsPageManager();
//        mNewsPageManager.setUp();
        mVideoPageManager = new VideoPageManager();
//        mVideoPageManager.setUp();
        mProgramPageManager = new ProgramPageManager();
//        mProgramPageManager.setUp();
        if(DEBUG){
            Log.d(LOG_TAG,"onCreate()");
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        NetUtils.release();
        if(DEBUG){
            Log.d(LOG_TAG,"onTerminate()");
        }

    }

   /* public void asyncInitCategories(){
        mNewsPageManager.clear();
        mVideoPageManager.clear();
        mProgramPageManager.clear();
        Singletons.getExecutorService().submit(new FindCategoryTask(CATEGORY_TYPE_NEWS));
    }*/


    public void startAudioService(){
        Intent intent = new Intent(this, AudioService.class);
        startService(intent);
    }

    public void stopAudioService(){
        Intent intent = new Intent(this, AudioService.class);
        stopService(intent);
    }

    public MediaPlayer getMediaPlayer(){
        if(mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();
        }
        mMediaPlayer.reset();
        return mMediaPlayer;
    }

    public void releaseMediaPlayer(){
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    public void init(){
        SharedPreferences sharedPreferences = getSharedPreferences(Data.PREFERENCE_NAME_APP, MODE_PRIVATE);

        versionCode = sharedPreferences.getInt(Data.PREFERENCE_KEY_APP_VERSION_CODE,0);
        if(versionCode == 0){
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                versionCode = packageInfo.versionCode;
            }catch (PackageManager.NameNotFoundException e){
                versionCode = 0;
            }
        }

        String deviceId = sharedPreferences.getString(Data.PREFERENCE_KEY_APP_DEVICE_ID,"0");
        if(deviceId.equals("0")){
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            deviceId = telephonyManager.getDeviceId();
        }
        sharedPreferences.edit().putInt(Data.PREFERENCE_KEY_APP_VERSION_CODE,versionCode);
        sharedPreferences.edit().putString(Data.PREFERENCE_KEY_APP_DEVICE_ID, deviceId)
                .commit();

        Data.myAppInfo.setVersionCode(versionCode);
        Data.myAppInfo.setDeviceId(deviceId);
    }


    public NewsPageManager getNewsPageManager(){
        return mNewsPageManager;
    }

    public VideoPageManager getVideoPageManager(){return  mVideoPageManager;}

    public ProgramPageManager getProgramPageManager() {
        return mProgramPageManager;
    }

/*
    private static final int CATEGORY_TYPE_NEWS = 1;
    private static final int CATEGORY_TYPE_VIDEO = 2;
    private static final int CATEGORY_TYPE_TV_CHANNEL = 3;
    private static final int CATEGORY_TYPE_AUDIO_CHANNEL = 4;
    private CategoryAccessor mCategoryAccessor = new CategoryAccessor();
    private class FindCategoryTask implements Runnable{
        private int categoryType;

        private FindCategoryTask(int categoryType) {
            this.categoryType = categoryType;
        }

        @Override
        public void run() {
            ArrayList<Category> list;
            switch (categoryType){
                case CATEGORY_TYPE_NEWS:
                   list = (ArrayList<Category>) mCategoryAccessor.getSubCategories(Data.newsFatherCategory);
                    mInitCategoriesHandler.sendMessage(mInitCategoriesHandler.obtainMessage(MESSAGE_CATEGORY_SUCCESS_NEWS,list));
                    break;
                case CATEGORY_TYPE_VIDEO:
                    list = (ArrayList<Category>) mCategoryAccessor.getSubCategories(Data.audioChannelFatherCategory);
                    mInitCategoriesHandler.sendMessage(mInitCategoriesHandler.obtainMessage(MESSAGE_CATEGORY_SUCCESS_VIDEO,list));
                    break;
                case CATEGORY_TYPE_TV_CHANNEL:
                    list = (ArrayList<Category>) mCategoryAccessor.getSubCategories(Data.tvChannelFatherCategory);
                    mInitCategoriesHandler.sendMessage(mInitCategoriesHandler.obtainMessage(MESSAGE_CATEGORY_SUCCESS_TV_CHANNEL,list));
                    break;
                case CATEGORY_TYPE_AUDIO_CHANNEL:
                    list = (ArrayList<Category>) mCategoryAccessor.getSubCategories(Data.audioChannelFatherCategory);
                    mInitCategoriesHandler.sendMessage(mInitCategoriesHandler.obtainMessage(MESSAGE_CATEGORY_SUCCESS_AUDIO_CHANNEL,list));
                    break;

            }
        }
    }

    private static final int MESSAGE_CATEGORY_SUCCESS_NEWS = 1;
    private static final int MESSAGE_CATEGORY_SUCCESS_VIDEO = 2;
    private static final int MESSAGE_CATEGORY_SUCCESS_TV_CHANNEL = 3;
    private static final int MESSAGE_CATEGORY_SUCCESS_AUDIO_CHANNEL = 4;
    private Handler mInitCategoriesHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_CATEGORY_SUCCESS_NEWS:
                    mNewsPageManager.setUp((ArrayList<Category>) msg.obj);
                    break;
                case MESSAGE_CATEGORY_SUCCESS_VIDEO:
                    break;
                case MESSAGE_CATEGORY_SUCCESS_TV_CHANNEL:
                    break;
                case MESSAGE_CATEGORY_SUCCESS_AUDIO_CHANNEL:
                    break;
            }
        }
    };*/
}
