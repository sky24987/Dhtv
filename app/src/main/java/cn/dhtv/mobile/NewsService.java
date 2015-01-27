package cn.dhtv.mobile;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.Timer;

import cn.dhtv.mobile.util.NewsManager;

public class NewsService extends Service {
    private LocalBinder localBinder = new LocalBinder();
    private NewsManager newsManager = NewsManager.getInstance();
    private RequestQueue requestQueue;//Volley网络请求队列
    //private Timer updateTimer;//更新新闻列表定时器;
    private Handler handler;
    public NewsService() {
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requestQueue.stop();
        requestQueue = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    public class LocalBinder extends Binder{
        public NewsService getService(){
            return NewsService.this;
        }
    }
}
