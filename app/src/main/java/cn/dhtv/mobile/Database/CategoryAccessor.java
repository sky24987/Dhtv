package cn.dhtv.mobile.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;
import java.util.ArrayList;

import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.inner.Live;

/**
 * Created by Jack on 2015/4/10.
 */
public class CategoryAccessor {

    public ArrayList<Category> getSubCategories(Category category){
        Cursor cursor = getDb().query(Contract.Category.TABLE_NAME, null, Contract.Category.COLUMN_NAME_UP_ID + "=?", new String[]{"" + category.getCatid()}, null, null, Contract.Category.COLUMN_NAME_CAT_ID);
        return toList(cursor);
    }

    public long insertOrReplace(Category category){
        ContentValues values = makeContentValues(category);
        /*
        values.put(Contract.Category.COLUMN_NAME_CAT_ID,category.getCatid());
        values.put(Contract.Category.COLUMN_NAME_UP_ID,category.getUpid());
        values.put(Contract.Category.COLUMN_NAME_TOP_ID,category.getTopid());
        values.put(Contract.Category.COLUMN_NAME_CAT_NAME,category.getCatname());
        values.put(Contract.Category.COLUMN_NAME_NAME,category.getName());
        values.put(Contract.Category.COLUMN_NAME_DESCRIPTION,category.getDescription());
        values.put(Contract.Category.COLUMN_NAME_BID,category.getBid());
        values.put(Contract.Category.COLUMN_NAME_M3U8,category.getLive().getM3u8());
        values.put(Contract.Category.COLUMN_NAME_RTMP,category.getLive().getRtmp());*/
        return getDb().replace(Contract.Category.TABLE_NAME, null, values);
    }

    public ContentValues makeContentValues(Category category){
        ContentValues values = new ContentValues();
        values.put(Contract.Category.COLUMN_NAME_CAT_ID,category.getCatid());
        values.put(Contract.Category.COLUMN_NAME_UP_ID,category.getUpid());
        values.put(Contract.Category.COLUMN_NAME_TOP_ID,category.getTopid());
        values.put(Contract.Category.COLUMN_NAME_CAT_NAME,category.getCatname());
        values.put(Contract.Category.COLUMN_NAME_NAME,category.getName());
        values.put(Contract.Category.COLUMN_NAME_DESCRIPTION,category.getDescription());
        values.put(Contract.Category.COLUMN_NAME_BID,category.getBid());
        values.put(Contract.Category.COLUMN_NAME_M3U8,category.getLive().getM3u8());
        values.put(Contract.Category.COLUMN_NAME_RTMP,category.getLive().getRtmp());
        values.put(Contract.Category.COLUMN_NAME_UPDATE_TIME,category.getUpdateTime() == null ? 0 :category.getUpdateTime().getTime());
        return values;
    }

    public void clear(Category category){
        getDb().beginTransaction();
        getDb().delete(Contract.Category.TABLE_NAME, Contract.Category.COLUMN_NAME_UP_ID+" = ?",new String[]{""+category.getCatid()});
        getDb().setTransactionSuccessful();
        getDb().endTransaction();
    }


    public SQLiteDatabase getDb(){
         return DBOpenHelper.getInstance().getWritableDatabase();
    }

    public static String[] getProjection(){
        return new String[]{
                Contract.Category._ID,
                Contract.Category.COLUMN_NAME_CAT_ID,
                Contract.Category.COLUMN_NAME_UP_ID,
                Contract.Category.COLUMN_NAME_TOP_ID,
                Contract.Category.COLUMN_NAME_NAME,
                Contract.Category.COLUMN_NAME_CAT_NAME,
                Contract.Category.COLUMN_NAME_DESCRIPTION,
                Contract.Category.COLUMN_NAME_RTMP,
                Contract.Category.COLUMN_NAME_M3U8
                };
    }

    public static ArrayList<Category> toList(Cursor cursor){
        ArrayList<Category> list = new ArrayList<Category>();
        while (cursor.moveToNext()){
            list.add(toCategory(cursor));
        }
        return list;
    }

    public static Category toCategory(Cursor cursor){
        Category category = new Category();
        Live live = new Live();
        category.setCatid(cursor.getInt(cursor.getColumnIndex(Contract.Category.COLUMN_NAME_CAT_ID)));
        category.setUpid(cursor.getInt(cursor.getColumnIndex(Contract.Category.COLUMN_NAME_UP_ID)));
        category.setCatname(cursor.getString(cursor.getColumnIndex(Contract.Category.COLUMN_NAME_CAT_NAME)));
        category.setName(cursor.getString(cursor.getColumnIndex(Contract.Category.COLUMN_NAME_NAME)));
        category.setBid(cursor.getInt(cursor.getColumnIndex(Contract.Category.COLUMN_NAME_BID)));
        live.setM3u8(cursor.getString(cursor.getColumnIndex(Contract.Category.COLUMN_NAME_M3U8)));
        live.setRtmp(cursor.getString(cursor.getColumnIndex(Contract.Category.COLUMN_NAME_RTMP)));
        category.setLive(live);
        long time = cursor.getLong(cursor.getColumnIndex(Contract.Category.COLUMN_NAME_UPDATE_TIME));
        category.setUpdateTime(time == 0 ? null : new Date(time));
        return category;
    }

}
