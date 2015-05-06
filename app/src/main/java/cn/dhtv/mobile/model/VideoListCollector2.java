package cn.dhtv.mobile.model;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import cn.dhtv.mobile.Database.Contract;
import cn.dhtv.mobile.Database.VideoAccessor;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.VideoOverview;
import cn.dhtv.mobile.network.VideoClient;

/**
 * Created by Jack on 2015/4/29.
 */
public class VideoListCollector2 extends AbsListCollector  {
    private static final int STATE_PROCESSING = 1;
    private static final int STATE_PROCESSING_NET = 2;
    private static final int STATE_PROCESSING_DB = 3;
    private static final int STATE_IDLE = 0;
    private static final int STATE_ERROR_NET_PROBLEM_IO = -1;
    private static final int STATE_ERROR_NET_PROBLEM_JSON = -3;
    private static final int STATE_ERROR_NET_PROBLEM_NULL = -4;
    private static final int STATE_ERROR_DB_PROBLEM_NULL = -2;

    private static final int MODE_APPEND = 1;
    private static final int MODE_REFRESH = 2;
    private static final int MODE_INIT = 3;
    private static final int MODE_IDLE = 0;


    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private int mState = STATE_IDLE;
    private int mMode = MODE_IDLE;

    private ArrayList<VideoOverview> videoOverviews = new ArrayList<>();
    private ArrayList<VideoOverview> cache = new ArrayList<>();

    private VideoAccessor mVideoAccessor = new VideoAccessor();
    private VideoClient mVideoClient = new VideoClient();



    public VideoListCollector2(Category category, CallBacks callBacks) {
        super(category, callBacks);
    }

    @Override
    public void clear() {

    }

    @Override
    public void asyncFirstFetch() {
        if(isProcessing()){
            if(mCallBacks != null){
                mCallBacks.onFirstFetchFails(category, null);
                return;
            }
        }

        mState = STATE_PROCESSING;
        mMode = MODE_INIT;
        new VideoAsyncTask(VideoAsyncTask.MODE_TRY_NET_THEN_DB).execute();
    }

    @Override
    public void asyncAppend() {
        if(isProcessing()){
            if(mCallBacks != null){
                mCallBacks.onFirstFetchFails(category, null);
                return;
            }
        }

        mState = STATE_PROCESSING;
        mMode = MODE_APPEND;
        new VideoAsyncTask(VideoAsyncTask.MODE_TRY_DB_THEN_NET).execute();
    }

    @Override
    public void asyncRefresh() {
        if(isProcessing()){
            if(mCallBacks != null){
                mCallBacks.onFirstFetchFails(category, null);
                return;
            }
        }

        mState = STATE_PROCESSING;
        mMode = MODE_REFRESH;
        new VideoAsyncTask(VideoAsyncTask.MODE_TRY_NET_THEN_DB).execute();
    }



    @Override
    public Object getItem(int position) {
        return videoOverviews.get(position);
    }

    @Override
    public int viewType(int position) {
        return 0;
    }

    @Override
    public int size() {
        return videoOverviews.size();
    }

    @Override
    public int getDataId(int position) {
        return videoOverviews.get(position).getAvid();
    }

    private void endSync(){
        switch (mMode){
            case MODE_APPEND:
                endAppend();
                break;
            case MODE_REFRESH:
                endRefresh();
                break;
            case MODE_INIT:
                endFirstFetch();
                break;
        }
    }

    private void endAppend(){
        switch (mState){
            case STATE_PROCESSING:
                videoOverviews.addAll(cache);
                if(mCallBacks != null){
                    mCallBacks.onAppend(category,null);
                }
                reset();
                break;
            default:
                if(mCallBacks != null){
                    mCallBacks.onAppendFails(category, null);
                }
                reset();
                break;

        }
    }

    private void endRefresh(){
        switch (mState){
            case STATE_PROCESSING:
                videoOverviews.clear();
                videoOverviews.addAll(cache);
                if(mCallBacks != null){
                    mCallBacks.onRefresh(category,null);
                }
                reset();
                break;
            default:
                if(mCallBacks != null){
                    mCallBacks.onRefreshFails(category,null);
                }
                reset();
                break;

        }
    }

    private void endFirstFetch(){
        switch (mState){
            case STATE_PROCESSING_NET:
            case STATE_PROCESSING_DB:
            case STATE_PROCESSING:
                videoOverviews.clear();
                videoOverviews.addAll(cache);
                if(mCallBacks != null){
                    mCallBacks.onFirstFetch(category, null);
                }
                reset();
                break;


            default:
                if(mCallBacks != null){
                    mCallBacks.onFirstFetchFails(category, null);
                }
                reset();
                break;
        }
    }


    public boolean isProcessing(){
        if(mState != STATE_IDLE){
            return true;
        }else {
            return false;
        }
    }

    /*重置状态*/
    private void reset(){
        mState = STATE_IDLE;
        mMode = MODE_IDLE;
        cache.clear();
    }


    private class VideoAsyncTask extends AsyncTask<Void,Void,Void>{
        private static final int MODE_TRY_NET_THEN_DB = 1;
        private static final int MODE_TRY_DB_THEN_NET = 2;
        private static final int MODE_DB = 3;
        private static final int MODE_NET = 4;


        private ArrayList<VideoOverview> mList;
        private int mMode;

        private VideoAsyncTask(int mMode) {
            this.mMode = mMode;
        }

        @Override
        protected Void doInBackground(Void... params) {
            switch (mMode){
                case MODE_TRY_NET_THEN_DB:
                    tryNetThenDb();
                    break;
                case MODE_TRY_DB_THEN_NET:
                    tryDbThenNet();
                    break;
                case MODE_DB:
                    break;
                case MODE_NET:
                    break;

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            endSync();
        }

        private void tryNetThenDb(){
            mState = STATE_PROCESSING_NET;
            ArrayList<VideoOverview> list = null;
            /*try net*/
            try {
                list = mVideoClient.getVideoOverviews(category, 0);

            }catch (JSONException e){
                mState = STATE_ERROR_NET_PROBLEM_JSON;
            }catch (Exception e){
                mState = STATE_ERROR_NET_PROBLEM_IO;
            }

            if(list != null && list.size() == 0){
                mState = STATE_ERROR_NET_PROBLEM_NULL;
            }

            if(list !=null && list.size() > 0){

                /*存入数据库*/
                for (VideoOverview videoOverview : list){
                    mVideoAccessor.insertOrReplace(videoOverview);
                }

                mList = list;
                cache = list;
                return;
            }

            mState = STATE_PROCESSING_DB;//TODO
            /*try db*/
            list = mVideoAccessor.findVideos(category, 0,0);
            mList = list;
            cache = list;
            return;
        }

        private void tryDbThenNet(){
            ArrayList<VideoOverview> list = null;

            /*try db*/
            list = mVideoAccessor.findVideos(category, 0,0);
            if(list != null && list.size() > 0){
                mList = list;
                cache = list;
                return;
            }

            /*try net*/
            try {
                list = mVideoClient.getVideoOverviews(category, 0);
            }catch (JSONException e){
                mState = STATE_ERROR_NET_PROBLEM_JSON;
                return;
            }catch (IOException e){
                mState = STATE_ERROR_NET_PROBLEM_IO;
                return;
            }

            if(list == null || list.size() == 0){
                mState = STATE_ERROR_NET_PROBLEM_NULL;
                return;
            }

            cache = list;
            return;
        }

        private void Db(){

        }

        private void Net(){

        }
    }
}
