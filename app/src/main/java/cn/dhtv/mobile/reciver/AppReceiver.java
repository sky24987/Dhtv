package cn.dhtv.mobile.reciver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;

import cn.dhtv.mobile.Contract;
import cn.dhtv.mobile.Data;

public class AppReceiver extends BroadcastReceiver {
    public AppReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        if(intentFilter.matchAction(intent.getAction())){
        /*安装文件下载完毕*/
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,0);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            Cursor cursor = downloadManager.query(query);
            if(cursor.moveToFirst()){
                if(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL){
                    String apkUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    Intent localIntent = new Intent(Contract.ACTION_APK_INSTALL);
                    localIntent.setData(Uri.parse(apkUri));
                    LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);


                    /*Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                            .setDataAndType(Uri.parse(apkUri),
                                    "application/vnd.android.package-archive");
                    promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(promptInstall);*/
                }
            }
        }
    }

}
