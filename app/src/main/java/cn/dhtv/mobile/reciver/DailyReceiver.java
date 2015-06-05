package cn.dhtv.mobile.reciver;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashSet;

import cn.dhtv.mobile.AppLogic;
import cn.dhtv.mobile.Data;
import cn.dhtv.mobile.Contract;
import cn.dhtv.mobile.model.UpGrader;

public class DailyReceiver extends BroadcastReceiver {
    public DailyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if(intent.getAction().equals(Contract.ACTION_UPGRADE_CHOICE)){
            /*是否下载更新文件*/
            SharedPreferences sharedPreferences = context.getSharedPreferences(Data.PREFERENCE_NAME_APP,Context.MODE_PRIVATE);
            UpGrader.UpGradeInfo upGradeInfo = (UpGrader.UpGradeInfo) intent.getSerializableExtra("upGradeInfo");

            sharedPreferences.edit().putString("upGradeInfo", upGradeInfo.originalJson()).commit();

            /*直接默认下载*/
            if(!AppLogic.isApkExist(context,AppLogic.makeApkName(upGradeInfo.getVer()))) {
                AppLogic.requestDownloadApk(context, upGradeInfo.getLink(), upGradeInfo.getDesc(), AppLogic.makeApkName(upGradeInfo.getVer()));
            }
        }
    }
}
