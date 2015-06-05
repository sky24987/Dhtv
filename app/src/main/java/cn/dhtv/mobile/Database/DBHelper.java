package cn.dhtv.mobile.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.dhtv.mobile.exception.NotSetUpException;

/**
 * Created by Jack on 2015/1/27.
 */
public class DBHelper extends SQLiteOpenHelper{
    private static DBHelper instance;
    private static Context context;

    private static final String DB_NAME = "dhtv";
    private static final int DB_VERSION = 2;

    public static DBHelper getInstance(){
        if(instance == null){
            if(context == null){
                throw new NotSetUpException();
            }
            instance = new DBHelper(context);
        }
        return instance;
    }


    public static void setUp(Context c){
        context = c.getApplicationContext();
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL.SQL_CREATE_CATEGORY);
        db.execSQL(SQL.SQL_CREATE_ARTICLE);
        db.execSQL(SQL.SQL_CREATE_BLOCK);
        db.execSQL(SQL.SQL_CREATE_VIDEO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+Contract.Article.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+Contract.Block.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+Contract.Video.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+Contract.Category.TABLE_NAME);
        db.execSQL(SQL.SQL_CREATE_CATEGORY);
        db.execSQL(SQL.SQL_CREATE_ARTICLE);
        db.execSQL(SQL.SQL_CREATE_BLOCK);
        db.execSQL(SQL.SQL_CREATE_VIDEO);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+Contract.Category.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+Contract.Article.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+Contract.Block.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+Contract.Video.TABLE_NAME);
        db.execSQL(SQL.SQL_CREATE_CATEGORY);
        db.execSQL(SQL.SQL_CREATE_ARTICLE);
        db.execSQL(SQL.SQL_CREATE_BLOCK);
        db.execSQL(SQL.SQL_CREATE_VIDEO);
    }
}
