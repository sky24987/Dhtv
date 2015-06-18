package cn.dhtv.mobile.model;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executor;

import cn.dhtv.mobile.Database.CategoryAccessor;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.network.CategoryClient;
import cn.dhtv.mobile.util.TimeUtils;

/**
 * Created by Jack on 2015/5/22.
 */
public class ProgramCollector extends AbsListCollector {
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private static final int MODE_APPEND = 1;
    private static final int MODE_REFRESH = 2;
    private static final int MODE_INIT = 3;
    private static final int MODE_IDLE = 0;

    private static final int APPROACH_DB = 1;
    private static final int APPROACH_NET = 2;
    private static final int APPROACH_DB_NET = 3;
    private static final int APPROACH_NET_DB = 4;
    private static final int APPROACH_NOT_SPECIFIC = 0;

    private static final int STATE_IDLE = 0;
    private static final int STATE_ERROR_NULL = -1;
    private static final int STATE_ERROR_IO = -2;
    private static final int STATE_ERROR_JSON = -3;
    private static final int STATE_SUCCESS = 1;
//    private static final int STATE_ERROR_DB = -4;


    private int mMode = MODE_IDLE;
    private int mApproach = APPROACH_NOT_SPECIFIC;
    private int mNetState = STATE_IDLE;
    private int mDBState = STATE_IDLE;

    private ArrayList<Category> programs = new ArrayList<>();
    private CategoryAccessor mCategoryAccessor = new CategoryAccessor();
    private CategoryClient mCategoryClient = new CategoryClient();
    private Executor asyncTaskExecutor = AsyncTask.THREAD_POOL_EXECUTOR;

    public ProgramCollector(Category category, CallBacks callBacks) {
        super(category, callBacks);
    }

    @Override
    public void clear() {
        programs.clear();
    }

    @Override
    public void asyncFirstFetch() {
        if(isProcessing()){
            if(mCallBacks != null){
                mCallBacks.onFirstFetchFails(category, null);
                return;
            }
        }

        mMode = MODE_INIT;
        if(TimeUtils.reachUpdateTimeGap(category.getUpdateTime())){
            mApproach = APPROACH_NET_DB;
        }else {
            mApproach = APPROACH_DB_NET;
        }

        new SyncProgramAsyncTask().executeOnExecutor(asyncTaskExecutor);


    }

    @Override
    public void asyncAppend() {

    }

    @Override
    public void asyncRefresh() {
        if(isProcessing()){
            if(mCallBacks != null){
                mCallBacks.onRefreshFails(category, null);
                return;
            }
        }

        /*if(!TimeUtils.reachRefreshTimeGap(category.getUpdateTime())){
            if(mCallBacks != null){
                mCallBacks.onRefreshFails(category,null);
            }
        }*/

        mMode = MODE_REFRESH;
        mApproach = APPROACH_NET;
        new SyncProgramAsyncTask().executeOnExecutor(asyncTaskExecutor);

    }





    @Override
    public Object getItem(int position) {
        return programs.get(position);
    }

    @Override
    public int viewType(int position) {
        return 0;
    }

    @Override
    public int size() {
        return programs.size();
    }

    @Override
    public int getDataId(int position) {
        return programs.get(position).getCatid();
    }


    public boolean isProcessing(){
        if(mMode != MODE_IDLE){
            return true;
        }else {
            return false;
        }
    }

    private void reset(){
        mMode = MODE_IDLE;
        mApproach = APPROACH_NOT_SPECIFIC;
        mNetState = STATE_IDLE;
        mDBState = STATE_IDLE;
    }

    private boolean isSuccessful(){
        if(mDBState == STATE_SUCCESS || mNetState == STATE_SUCCESS){
            return true;
        }

        return false;
    }


    private class SyncProgramAsyncTask extends AsyncTask<Object,Object,ArrayList<Category>>{
        private ArrayList<Category> result;

        @Override
        protected ArrayList<Category> doInBackground(Object... params) {
            if(mApproach == APPROACH_NET_DB){
                if(tryNet()){
                    return result;
                }

                if(tryDb()){
                    return result;
                }

                return null;
            }

            if(mApproach == APPROACH_DB_NET){
                if(tryDb()){
                    return result;
                }

                if(tryNet()){
                    return result;
                }

                return null;
            }

            if(mApproach == APPROACH_NET){
                if(tryNet()){
                    return result;
                }

                return null;
            }

            if(mApproach == APPROACH_DB){
                if(tryDb()){
                    return result;
                }

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Category> categories) {
            if(isSuccessful()){
                switch (mMode){
                    case MODE_INIT:
                        programs.clear();
                        programs.addAll(categories);
                        if(mCallBacks != null){
                            mCallBacks.onFirstFetch(category,null);
                        }
                        reset();
                        break;
                    case MODE_APPEND:
                        reset();
                        break;
                    case MODE_REFRESH:
                        programs.clear();
                        programs.addAll(categories);
                        if(mCallBacks != null){
                            mCallBacks.onRefresh(category,null);
                        }
                        reset();
                        break;
                    default:
                        reset();
                }
            }else {
                switch (mMode){
                    case MODE_INIT:
                        if(mCallBacks != null){
                            mCallBacks.onFirstFetchFails(category,null);
                        }
                        reset();
                        break;
                    case MODE_APPEND:
                        reset();
                        break;
                    case MODE_REFRESH:
                        if(mCallBacks != null){
                            mCallBacks.onRefreshFails(category,null);
                        }
                        reset();
                        break;

                }
            }
        }

        private boolean tryNet(){
            try {
                ArrayList<Category> categories = mCategoryClient.fetchCategories(category);
                if(categories.size() == 0){
                    mNetState = STATE_ERROR_NULL;
                    return false;
                }
                mNetState = STATE_SUCCESS;
                result = categories;
                mCategoryAccessor.clear(category);
                for(Category category:categories){
                    mCategoryAccessor.insertOrReplace(category);
                }
                return true;
            }catch (JSONException e){
                mNetState = STATE_ERROR_JSON;
                return false;
            }catch (IOException e){
                mNetState = STATE_ERROR_IO;
                return false;
            }
        }

        private boolean tryDb(){
            try {
                ArrayList<Category> categories = mCategoryAccessor.getSubCategories(category);
                if(categories.size() == 0){
                    mDBState = STATE_ERROR_NULL;
                    return false;
                }

                mDBState = STATE_SUCCESS;
                result = categories;

                return true;
            }catch (Exception e){
                mDBState = STATE_ERROR_IO;
                return false;
            }
        }
    }
}
