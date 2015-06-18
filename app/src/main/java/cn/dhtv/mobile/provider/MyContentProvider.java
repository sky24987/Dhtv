package cn.dhtv.mobile.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import cn.dhtv.mobile.Database.Contract;
import cn.dhtv.mobile.Database.DBOpenHelper;

public class MyContentProvider extends ContentProvider {
    public static final String AUTHORITY = "cn.dhtv.mobile.provider";
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int MATCH_ID_QUERY_SUB_CATEGORY = 1;
    private static final int MATCH_ID_CATEGORY = 2;

    private static final String CONTENT_TYPE_CATEGORY = "vnd.android.cursor.dir/vnd."+AUTHORITY+"."+ Contract.Category.TABLE_NAME;
    private static final String CONTENT_TYPE_CATEGORY_ITEM = "vnd.android.cursor.item/vnd."+AUTHORITY+"."+ Contract.Category.TABLE_NAME;

    static {
        sUriMatcher.addURI(AUTHORITY, Contract.Category.TABLE_NAME,MATCH_ID_CATEGORY);
        sUriMatcher.addURI(AUTHORITY, Contract.Category.TABLE_NAME+"/up_id/#",MATCH_ID_QUERY_SUB_CATEGORY);
    }





    public MyContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        switch (sUriMatcher.match(uri)){
            case MATCH_ID_QUERY_SUB_CATEGORY:
            case MATCH_ID_CATEGORY:
                return CONTENT_TYPE_CATEGORY;
        }

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        switch (sUriMatcher.match(uri)){
            case MATCH_ID_CATEGORY:
                DBOpenHelper.getInstance().getWritableDatabase().replace(Contract.Category.TABLE_NAME,null,values);
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content cn.dhtv.mobile.provider on startup.

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        Cursor cursor;
        switch (sUriMatcher.match(uri)){
            case MATCH_ID_QUERY_SUB_CATEGORY:
                cursor = DBOpenHelper.getInstance().getReadableDatabase().query(Contract.Category.TABLE_NAME,null, Contract.Category.COLUMN_NAME_UP_ID+"=?",new String[]{uri.getLastPathSegment()},null,null, Contract.Category.COLUMN_NAME_CAT_ID);
                return cursor;

            case MATCH_ID_CATEGORY:
                cursor = DBOpenHelper.getInstance().getReadableDatabase().query(Contract.Category.TABLE_NAME,projection,selection,selectionArgs,null,null,Contract.Category.COLUMN_NAME_CAT_ID);
                return cursor;
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
