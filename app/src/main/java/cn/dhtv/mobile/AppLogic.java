package cn.dhtv.mobile;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

/**
 * Created by Jack on 2015/6/4.
 */
public class AppLogic {
    private static final String APK_SUB_DIR = "";
    public static final String AKP_NAME = Data.APK_NAME;
    public static long requestDownloadApk(Context context,String link, String desc,String filename){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(link));
        request.setTitle("东海网")
                .setDescription(desc)
                .setDestinationUri(Uri.fromFile(new File(getApkDir(),filename)))
                /*.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, filename)*/
                /*.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, APK_SUB_DIR + filename)*/
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        return downloadManager.enqueue(request);
    }

    public static boolean isApkExist(Context context, String filename){
//        File apkPath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

        File apk = new File(getApkDir(),filename);

        return apk.exists();
    }

    public static String makeApkName(String apkName,String ver){
        return apkName+"-"+ver+".apk";
    }

    public static String makeApkName(String ver){
        return makeApkName(AKP_NAME,ver);
    }

    public static File getApkDir(){
        File apkPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File apk = new File(apkPath.getAbsolutePath()+APK_SUB_DIR);
        if(apk.exists() == false){
            apk.mkdir();
        }
        return apk;
    }

    public static void clearApkFile(){

    }

    public static void requestApkInstall(Context context,String apkName){
//        File apkPath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File apk = new File(getApkDir(),apkName);
        Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(Uri.fromFile(apk),
                        "application/vnd.android.package-archive");
        promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(promptInstall);
    }


}
