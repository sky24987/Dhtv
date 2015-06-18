package cn.dhtv.mobile.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Date;

import cn.dhtv.mobile.Contract;
import cn.dhtv.mobile.Data;
import cn.dhtv.mobile.Singletons;
import cn.dhtv.mobile.model.UpGrader;
import cn.dhtv.mobile.util.TimeUtils;

public class DailyService extends Service {
    private boolean processing = false;

    public DailyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        synchronized (this){
            if (isProcessing()){
                return START_NOT_STICKY;
            }else {
                setProcessing(true);
            }
        }



        checkUpgrade();
        return START_NOT_STICKY;
    }

    private void checkUpgrade(){
        Singletons.getExecutorService().execute(new CheckUpgradeTask());
    }

    private  boolean isProcessing(){
        return processing;
    }

    private  void setProcessing(boolean processing){
        this.processing = processing;
    }

    private class CheckUpgradeTask implements Runnable{
        @Override
        public void run() {


            SharedPreferences sharedPreferences = getSharedPreferences(Data.PREFERENCE_NAME_APP, MODE_PRIVATE);
            long lastCheckUpgradeTimestamp = sharedPreferences.getLong(Data.PREFERENCE_KEY_APP_LAST_CHECK_UPGRADE_TIMESTAMP, 0);
            long userCancelUpgradeTimestamp = sharedPreferences.getLong(Data.PREFERENCE_KEY_APP_USER_CANCEL_UPGRADE_TIMESTAMP,0);
            Date lastCheckUpgradeTime = lastCheckUpgradeTimestamp == 0 ? null : /*null*/new Date(lastCheckUpgradeTimestamp);
            Date userCancelUpgradeTime = userCancelUpgradeTimestamp == 0 ? null : /*null*/new Date(userCancelUpgradeTimestamp);
            if(TimeUtils.reachCheckAppUpgradeGap(lastCheckUpgradeTime) && TimeUtils.reachRecheckAppUpgradeGap(userCancelUpgradeTime)){
                UpGrader.UpGradeInfo upGradeInfo = UpGrader.check(Data.myAppInfo.getVersionCode(),1,Data.myAppInfo.getDeviceId());
                if(upGradeInfo != null && upGradeInfo.isSuccess()){
                    Intent localIntent = new Intent(Contract.ACTION_UPGRADE_CHOICE);
                    localIntent.setData(Uri.parse(upGradeInfo.getLink()));
                    localIntent.putExtra("title", upGradeInfo.getTitle());
                    localIntent.putExtra("link",upGradeInfo.getLink());
                    localIntent.putExtra("ver",upGradeInfo.getVer());
                    localIntent.putExtra("desc",upGradeInfo.getDesc());
                    localIntent.putExtra("upGradeInfo", upGradeInfo);
                    LocalBroadcastManager.getInstance(DailyService.this).sendBroadcast(localIntent);
                    DailyService.this.sendBroadcast(localIntent);
                }
                sharedPreferences.edit().putLong(Data.PREFERENCE_KEY_APP_LAST_CHECK_UPGRADE_TIMESTAMP,System.currentTimeMillis()).commit();
            }

            synchronized (DailyService.this){
                setProcessing(false);
            }
        }
    }


}
