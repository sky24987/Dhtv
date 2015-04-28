package cn.dhtv.mobile.Sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import cn.dhtv.mobile.Database.CategoryAccessor;
import cn.dhtv.mobile.Database.Contract;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.network.CategoryClient;
import cn.dhtv.mobile.Data;

/**
 * Created by Jack on 2015/4/28.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    ContentResolver mContentResolver;
    CategoryClient mCategoryClient = new CategoryClient();
    CategoryAccessor mCategoryAccessor = new CategoryAccessor();

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        try {
            ArrayList<Category> newsCategories = mCategoryClient.fetchCategories(Data.newsFatherCategory);
            ArrayList<Category> videoCategories = mCategoryClient.fetchCategories(Data.tvChannelFatherCategory);
            ArrayList<Category> tvChannelCategories = mCategoryClient.fetchCategories(Data.tvChannelFatherCategory);
            ArrayList<Category> audioChannelCategories = mCategoryClient.fetchCategories(Data.audioChannelFatherCategory);

            ArrayList<Category> list = new ArrayList<>();
            list.addAll(newsCategories);
            list.addAll(videoCategories);
            list.addAll(tvChannelCategories);
            list.addAll(audioChannelCategories);

            String selection = Contract.Category.COLUMN_NAME_UP_ID +" = ? or ? or ? or ?";
            String[] selectionArgs = new String[]{
                    ""+Data.newsFatherCategory.getCatid(),
                    ""+Data.tvChannelFatherCategory.getCatid(),
                    ""+Data.tvChannelFatherCategory.getCatid(),
                    ""+Data.audioChannelFatherCategory.getCatid()};

            SQLiteDatabase db = mCategoryAccessor.getDb();
            db.beginTransaction();
            db.delete(Contract.Category.TABLE_NAME,selection,selectionArgs);
            for(Category category:list){
                mCategoryAccessor.insertOrReplace(category);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            /*Log.d(LOG_TAG,"onPerformSync");
            Log.d(LOG_TAG,""+list.size());*/
        }catch (JSONException e){

        }catch (IOException e){

        }
    }
}
