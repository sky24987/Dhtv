package cn.dhtv.mobile.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.dhtv.mobile.Data;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.NewsOverview;
import cn.dhtv.mobile.entity.VideoOverview;

/**
 * Created by Jack on 2015/4/29.
 */
public class VideoAccessor {
    private static final int DEFAULT_SIZE = Data.VIDEO_PAGE_SIZE;

    public ArrayList<VideoOverview> findVideos(Category category,int minId,int size){
        if(size == 0){
            size = DEFAULT_SIZE;
        }
        String selection =Contract.Video.COLUMN_NAME_CAT_ID +" = "+category.getCatid();
        Cursor cursor = getDb().query(Contract.Video.TABLE_NAME,null,selection,null,null,null, Contract.Video.COLUMN_NAME_AVID+" desc",""+size);
        ArrayList<VideoOverview> list = toList(cursor);
        cursor.close();
        return list;
    }

    public long insertOrReplace(VideoOverview videoOverview){
        ContentValues contentValues = makeContentValues(videoOverview);
        return getDb().replace(Contract.Video.TABLE_NAME,null,contentValues);
//        return getDb().insert(Contract.Video.TABLE_NAME,null,contentValues);
    }

    public static ContentValues makeContentValues(VideoOverview videoOverview){
        ContentValues values = new ContentValues();
        values.put(Contract.Video.COLUMN_NAME_AVID,videoOverview.getAvid());
        values.put(Contract.Video.COLUMN_NAME_VID,videoOverview.getVid());
        values.put(Contract.Video.COLUMN_NAME_CAT_ID,videoOverview.getCatid());
        values.put(Contract.Video.COLUMN_NAME_DURATION,videoOverview.getDuration());
        values.put(Contract.Video.COLUMN_NAME_TITLE,videoOverview.getTitle());
        values.put(Contract.Video.COLUMN_NAME_SUMMARY,videoOverview.getSummary());
        values.put(Contract.Video.COLUMN_NAME_DATELINE,videoOverview.getDateline());
        values.put(Contract.Video.COLUMN_NAME_PIC,videoOverview.getPic());
        values.put(Contract.Video.COLUMN_NAME_DIR,videoOverview.getDir());
        values.put(Contract.Video.COLUMN_NAME_UID,videoOverview.getUid());
        values.put(Contract.Video.COLUMN_NAME_URL,videoOverview.getUrl());
        values.put(Contract.Video.COLUMN_NAME_USER_NAME,videoOverview.getUsername());
        values.put(Contract.Video.COLUMN_NAME_CHECKED,videoOverview.isChecked());

        return values;
    }

    private SQLiteDatabase getDb(){
        return DBHelper.getInstance().getWritableDatabase();
    }

    public static ArrayList<VideoOverview> toList(Cursor cursor){
        ArrayList<VideoOverview> list = new ArrayList<>();
        while (cursor.moveToNext()){
            list.add(toVideo(cursor));
        }
        return list;
    }

    public static VideoOverview toVideo(Cursor cursor){
        VideoOverview videoOverview = new VideoOverview();
        videoOverview.setAvid(cursor.getInt(cursor.getColumnIndex(Contract.Video.COLUMN_NAME_AVID)));
        videoOverview.setVid(cursor.getInt(cursor.getColumnIndex(Contract.Video.COLUMN_NAME_VID)));
        videoOverview.setCatid(cursor.getInt(cursor.getColumnIndex(Contract.Video.COLUMN_NAME_CAT_ID)));
        videoOverview.setDuration(cursor.getInt(cursor.getColumnIndex(Contract.Video.COLUMN_NAME_DURATION)));
        videoOverview.setTitle(cursor.getString(cursor.getColumnIndex(Contract.Video.COLUMN_NAME_TITLE)));
        videoOverview.setSummary(cursor.getString(cursor.getColumnIndex(Contract.Video.COLUMN_NAME_SUMMARY)));
        videoOverview.setUrl(cursor.getString(cursor.getColumnIndex(Contract.Video.COLUMN_NAME_URL)));
        videoOverview.setPic(cursor.getString(cursor.getColumnIndex(Contract.Video.COLUMN_NAME_PIC)));
        videoOverview.setDir(cursor.getString(cursor.getColumnIndex(Contract.Video.COLUMN_NAME_DIR)));
        videoOverview.setDateline(cursor.getString(cursor.getColumnIndex(Contract.Video.COLUMN_NAME_DATELINE)));
        videoOverview.setUsername(cursor.getString(cursor.getColumnIndex(Contract.Video.COLUMN_NAME_USER_NAME)));
        videoOverview.setChecked(cursor.getInt(cursor.getColumnIndex(Contract.Video.COLUMN_NAME_CHECKED)));
        return videoOverview;
    }
}
