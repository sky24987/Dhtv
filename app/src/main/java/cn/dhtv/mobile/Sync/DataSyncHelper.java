package cn.dhtv.mobile.Sync;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.dhtv.mobile.Database.ArticleAccessor;
import cn.dhtv.mobile.Database.CategoryAccessor;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.network.NetUtils;
import cn.dhtv.mobile.util.TextUtils;

/**
 * Created by Jack on 2015/4/9.
 */
public class DataSyncHelper {
    //TODO 从网络获取的Category记得存到内存

    public static final int FLAG_SYNC_CATEGORY_SUCCESS = 1;

    private static final int MESSAGE_WHAT_CATEGORY_SUCCESS = 1;

    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private static DataSyncHelper instance;

    private RequestQueue mRequestQueue;
    private Handler mCategoryHandler;
    private Handler mHandler;
    private Context mContext;
    private ObjectMapper mObjectMapper = new ObjectMapper();
    private ArticleAccessor mArticleAccessor = new ArticleAccessor();
    private CategoryAccessor mCategoryAccessor = new CategoryAccessor();
    private ExecutorService mExecutorService = Executors.newFixedThreadPool(3);





    public DataSyncHelper(Context context) {
        mContext = context.getApplicationContext();
        mRequestQueue = NetUtils.getRequestQueue(context);//需要在NetUtils setup()之后调用
        init();

    }

    public static DataSyncHelper getInstance(Context context){

        if(instance == null){
            instance =  new DataSyncHelper(context);
        }
        return instance;
    }

    public static DataSyncHelper getInstance(){
        if(instance == null){

        }
        return instance;
    }

    public static void setUp(Context context){
        getInstance(context);
    }



    public void SyncCategory(Category category,final CategorySyncCallBacks callBacks){


       /* Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ArrayList<Category> list;
                try {
                    list = mObjectMapper.readValue(response.getJSONObject("data").getJSONArray("children").toString(), new TypeReference<List<Category>>() {
                    });
                    if(DEBUG){
                        Log.d(LOG_TAG, list.toString());
                    }
                    onCategorySync(list,callBacks,FLAG_SYNC_CATEGORY_SUCCESS);
                } catch (Exception e) {

                    e.printStackTrace();
                    Log.e(LOG_TAG, e.getMessage());
                }finally{

                }
            }
        };


        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG,error.getMessage());

            }
        };

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, TextUtils.makeCategoryUrl(category, 1), null,responseListener,errorListener);
        jsonObjectRequest.setTag(category);
        mRequestQueue.add(jsonObjectRequest);*/

        fetchCategory(category, new WeakReference<CategorySyncCallBacks>(callBacks));
    }


    public void SyncCategoryFirstFromDB(Category category,CategorySyncCallBacks categorySyncCallBacksWeakReference){
        mExecutorService.submit(new GetCategoryFirstFromDBTask(category,new WeakReference<CategorySyncCallBacks>(categorySyncCallBacksWeakReference)));
    }

    private void onCategorySync(List<Category> list,WeakReference<CategorySyncCallBacks> categorySyncCallBacksWeakReference,int flag){
        CategorySyncCallBacks callBacks = categorySyncCallBacksWeakReference.get();
        if(callBacks != null) {
            callBacks.onSync(list);
        }
    }

    private void onCategorySyncError(List<Category> list,WeakReference<CategorySyncCallBacks> callBacks,int flag){

    }

    private void init(){
        mCategoryHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MESSAGE_WHAT_CATEGORY_SUCCESS:
                        MessageObject messageObject = (MessageObject) msg.obj;
                        onCategorySync(messageObject.list, (WeakReference<CategorySyncCallBacks>) messageObject.obj1, FLAG_SYNC_CATEGORY_SUCCESS);
                }
            }
        };
    }

    private void fetchCategory(Category father,WeakReference<CategorySyncCallBacks> categorySyncCallBacksWeakReference){
        Response.Listener<JSONObject> responseListener = new CategoryResponseListener(categorySyncCallBacksWeakReference);
        Response.ErrorListener errorListener = new CategoryResponseErrorListener(categorySyncCallBacksWeakReference);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, TextUtils.makeCategoryUrl(father, 1), null,responseListener,errorListener);
        jsonObjectRequest.setTag(categorySyncCallBacksWeakReference);
        mRequestQueue.add(jsonObjectRequest);
        return;
    }








    private class CategoryResponseListener implements Response.Listener<JSONObject>{
        WeakReference<CategorySyncCallBacks> mCategorySyncCallBacks;

        private CategoryResponseListener(WeakReference<CategorySyncCallBacks> categorySyncCallBacks) {
            this.mCategorySyncCallBacks = categorySyncCallBacks;
        }

        @Override
        public void onResponse(JSONObject response) {
            ArrayList<Category> list;
            try {
                list = mObjectMapper.readValue(response.getJSONObject("data").getJSONArray("children").toString(), new TypeReference<List<Category>>() {
                });
                if(DEBUG){
                    Log.d(LOG_TAG, list.toString());
                }
                onCategorySync(list,mCategorySyncCallBacks,FLAG_SYNC_CATEGORY_SUCCESS);//得到数据后处理
                mExecutorService.submit(new StoreCategoryTask(list));//异步将数据存到数据库
            } catch (Exception e) {

                e.printStackTrace();
                Log.e(LOG_TAG, e.getMessage());
            }finally{

            }
        }
    }

    private class CategoryResponseErrorListener implements Response.ErrorListener{
        WeakReference<CategorySyncCallBacks> mCategorySyncCallBacks;

        private CategoryResponseErrorListener(WeakReference<CategorySyncCallBacks> mCategorySyncCallBacks) {
            this.mCategorySyncCallBacks = mCategorySyncCallBacks;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(LOG_TAG,error.getMessage());

        }
    }

    private class GetCategoryFirstFromDBTask implements Runnable{
        Category father;
        WeakReference<CategorySyncCallBacks> mCategorySyncCallBacks;

        private GetCategoryFirstFromDBTask(Category father,WeakReference<CategorySyncCallBacks> callBacks) {
            this.father = father;
            mCategorySyncCallBacks = callBacks;
        }

        @Override
        public void run() {

            List<Category> list = mCategoryAccessor.getSubCategories(father);
            if(list.size() == 0){
               /* Response.Listener<JSONObject> responseListener = new CategoryResponseListener(mCategorySyncCallBacks);
                Response.ErrorListener errorListener = new CategoryResponseErrorListener(mCategorySyncCallBacks);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, TextUtils.makeCategoryUrl(father, 1), null,responseListener,errorListener);
                jsonObjectRequest.setTag(mCategorySyncCallBacks);
                mRequestQueue.add(jsonObjectRequest);*/
                fetchCategory(father, mCategorySyncCallBacks);//从网络获取Category
                return;
            }else {
                MessageObject obj = new MessageObject();
                obj.list = list;
                obj.obj1 = mCategorySyncCallBacks;
                mCategoryHandler.sendMessage(mCategoryHandler.obtainMessage(MESSAGE_WHAT_CATEGORY_SUCCESS,obj));

                return;
            }

        }
    }

    private class StoreCategoryTask implements Runnable{
        List<Category> list;

        private StoreCategoryTask(List<Category> list) {
            this.list = list;
        }

        @Override
        public void run() {
            for (Category category : list){
                mCategoryAccessor.insertOrReplace(category);
            }
        }
    }

    private static class MessageObject{
        public List list;
        public Object obj1;
        public Object obj2;
    }

    public interface CategorySyncCallBacks{
        void onSync(List<Category> list);
        void onError(int flag);
    }


}
