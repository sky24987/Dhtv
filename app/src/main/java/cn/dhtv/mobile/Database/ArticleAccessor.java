package cn.dhtv.mobile.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.NewsOverview;
import cn.dhtv.mobile.Data;

/**
 * Created by Jack on 2015/4/13.
 */
public class ArticleAccessor {
    private static final int DEFAULT_SIZE = Data.NEWS_PAGE_SIZE;

    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    public List<NewsOverview> findArticles(Category category,int size){
        if(size == 0){
            size = DEFAULT_SIZE;
        }
        String selection =Contract.Article.COLUMN_NAME_CAT_ID +" = "+category.getCatid();
        Cursor cursor = getDb().query(Contract.Article.TABLE_NAME,null,selection,null,null,null, Contract.Article.COLUMN_NAME_AAID+" desc",""+size);
        List<NewsOverview> list = toList(cursor);
        cursor.close();
        return list;
    }

    public List<NewsOverview> findArticles(Category category,int minId,int size){
        if(DEBUG){
            Log.d(LOG_TAG,"findArticles from db");
        }
        if(size == 0){
            size = DEFAULT_SIZE;
        }
        String selection = Contract.Article.COLUMN_NAME_CAT_ID +" = "+category.getCatid() + " and "+
                Contract.Article.COLUMN_NAME_AAID + " < " + minId;
        Cursor cursor = null;
        try {
           cursor = getDb().query(Contract.Article.TABLE_NAME,null,selection,null,null,null, Contract.Article.COLUMN_NAME_AAID+" desc",""+size);

        }catch (Error error){
            Log.e(LOG_TAG,error.toString());
        }
        List<NewsOverview> list = toList(cursor);
        cursor.close();
        return list;
    }

    public long insertOrReplace(NewsOverview newsOverview){
        /*if(DEBUG){
            Log.d(LOG_TAG,"insertOrReplace");
        }*/
        ContentValues values = new ContentValues();
        values.put(Contract.Article.COLUMN_NAME_AID,newsOverview.getAid());
        values.put(Contract.Article.COLUMN_NAME_AAID,newsOverview.getAaid());
        values.put(Contract.Article.COLUMN_NAME_CAT_ID,newsOverview.getCatid());
        values.put(Contract.Article.COLUMN_NAME_SUMMARY,newsOverview.getSummary());
        values.put(Contract.Article.COLUMN_NAME_TITLE,newsOverview.getTitle());
        values.put(Contract.Article.COLUMN_NAME_DATELINE,newsOverview.getDateline());
        values.put(Contract.Article.COLUMN_NAME_FROM,newsOverview.getFrom());
        values.put(Contract.Article.COLUMN_NAME_UID, newsOverview.getUid());
        values.put(Contract.Article.COLUMN_NAME_URL, newsOverview.getUrl());
        values.put(Contract.Article.COLUMN_NAME_PIC_URL, newsOverview.getPic_url());
        values.put(Contract.Article.COLUMN_NAME_USER_NAME,newsOverview.getUsername());
        values.put(Contract.Article.COLUMN_NAME_CHECKED, newsOverview.isChecked());
        return getDb().replace(Contract.Article.TABLE_NAME,null,values);
    }

    public void clear(Category category){
        getDb().beginTransaction();
        getDb().delete(Contract.Article.TABLE_NAME, Contract.Article.COLUMN_NAME_CAT_ID+" = ?",new String[]{""+category.getCatid()});
        getDb().setTransactionSuccessful();
        getDb().endTransaction();
    }

    private SQLiteDatabase getDb(){
        return DBOpenHelper.getInstance().getWritableDatabase();
    }

    /*public static String[] getProjection(){
        return new String[]{
                Contract.Article._ID,
                Contract.Article.COLUMN_NAME_AID,
                Contract.Article.COLUMN_NAME_CAT_ID,
                Contract.Article.COLUMN_NAME_SUMMARY,
                Contract.Article.COLUMN_NAME_TITLE,
                Contract.Article.COLUMN_NAME_DATELINE,
                Contract.Article.COLUMN_NAME_URL,
                Contract.Article.COLUMN_NAME_AAID,
                Contract.Article.COLUMN_NAME_FROM,
                Contract.Article.COLUMN_NAME_PIC_URL,
                Contract.Article.COLUMN_NAME_USER_NAME,
                Contract.Article.COLUMN_NAME_CHECKED
        };
    }*/


    public static List<NewsOverview> toList(Cursor cursor){
        List<NewsOverview> list = new ArrayList<>();
        while (cursor.moveToNext()){
            list.add(toArticle(cursor));
        }
        return list;
    }

    public static NewsOverview toArticle(Cursor cursor){
        NewsOverview newsOverview = new NewsOverview();
        newsOverview.setAaid(cursor.getInt(cursor.getColumnIndex(Contract.Article.COLUMN_NAME_AAID)));
        newsOverview.setAid(cursor.getInt(cursor.getColumnIndex(Contract.Article.COLUMN_NAME_AID)));
        newsOverview.setCatid(cursor.getInt(cursor.getColumnIndex(Contract.Article.COLUMN_NAME_CAT_ID)));
        newsOverview.setTitle(cursor.getString(cursor.getColumnIndex(Contract.Article.COLUMN_NAME_TITLE)));
        newsOverview.setUrl(cursor.getString(cursor.getColumnIndex(Contract.Article.COLUMN_NAME_URL)));
        newsOverview.setSummary(cursor.getString(cursor.getColumnIndex(Contract.Article.COLUMN_NAME_SUMMARY)));
        newsOverview.setPic_url(cursor.getString(cursor.getColumnIndex(Contract.Article.COLUMN_NAME_PIC_URL)));
        newsOverview.setDateline(cursor.getString(cursor.getColumnIndex(Contract.Article.COLUMN_NAME_DATELINE)));
        newsOverview.setFrom(cursor.getString(cursor.getColumnIndex(Contract.Article.COLUMN_NAME_FROM)));
        newsOverview.setUid(cursor.getInt(cursor.getColumnIndex(Contract.Article.COLUMN_NAME_UID)));
        newsOverview.setUsername(cursor.getString(cursor.getColumnIndex(Contract.Article.COLUMN_NAME_USER_NAME)));
        newsOverview.setChecked(cursor.getInt(cursor.getColumnIndex(Contract.Article.COLUMN_NAME_CHECKED)));
        return newsOverview;
    }
}
