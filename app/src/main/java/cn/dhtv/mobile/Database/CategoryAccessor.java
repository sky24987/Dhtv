package cn.dhtv.mobile.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import java.util.ArrayList;
import java.util.List;

import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.inner.Live;

/**
 * Created by Jack on 2015/4/10.
 */
public class CategoryAccessor {

    public List<Category> getSubCategories(Category category){
        Cursor cursor = getDb().query(Contract.Category.TABLE_NAME, getProjection(), Contract.Category.COLUMN_NAME_UP_ID + "=?", new String[]{"" + category.getCatid()}, null, null, Contract.Category.COLUMN_NAME_CAT_ID);
        return toList(cursor);
    }

    public long insertOrReplace(Category category){
        ContentValues values = new ContentValues();
        values.put(Contract.Category.COLUMN_NAME_CAT_ID,category.getCatid());
        values.put(Contract.Category.COLUMN_NAME_UP_ID,category.getUpid());
        values.put(Contract.Category.COLUMN_NAME_TOP_ID,category.getTopid());
        values.put(Contract.Category.COLUMN_NAME_CAT_NAME,category.getCatname());
        values.put(Contract.Category.COLUMN_NAME_NAME,category.getName());
        values.put(Contract.Category.COLUMN_NAME_DESCRIPTION,category.getDescription());
        values.put(Contract.Category.COLUMN_NAME_M3U8,category.getLive().getM3u8());
        values.put(Contract.Category.COLUMN_NAME_RTMP,category.getLive().getRtmp());
        return getDb().replace(Contract.Category.TABLE_NAME,null,values);
    }


    private SQLiteDatabase getDb(){
         return DBHelper.getInstance().getWritableDatabase();
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

    public static List<Category> toList(Cursor cursor){
        List<Category> list = new ArrayList<Category>();
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
        live.setM3u8(cursor.getString(cursor.getColumnIndex(Contract.Category.COLUMN_NAME_M3U8)));
        live.setRtmp(cursor.getString(cursor.getColumnIndex(Contract.Category.COLUMN_NAME_RTMP)));
        category.setLive(live);
        return category;
    }

}
