package cn.dhtv.mobile.Database;

import android.provider.BaseColumns;

/**
 * Created by Jack on 2015/4/10.
 */
public final class Contract {
    public Contract() {
    }

    public static abstract class Category implements BaseColumns{
        public static final String TABLE_NAME = "category";
        public static final String COLUMN_NAME_CAT_ID = "catid";
        public static final String COLUMN_NAME_UP_ID = "upid";
        public static final String COLUMN_NAME_TOP_ID = "topid";
        public static final String COLUMN_NAME_CAT_NAME = "catname";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DESCRIPTION = "description";

        public static final String COLUMN_NAME_RTMP = "rtmp";
        public static final String COLUMN_NAME_M3U8 = "m3u8";
    }

    public static abstract class Article implements BaseColumns{
        public static final String TABLE_NAME = "article";
        public static final String COLUMN_NAME_AAID = "aaid";
        public static final String COLUMN_NAME_AID = "aid";
        public static final String COLUMN_NAME_UID = "uid";
        public static final String COLUMN_NAME_CAT_ID = "catid";
        public static final String COLUMN_NAME_USER_NAME = "username";
        public static final String COLUMN_NAME_FROM = "dfrom";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DATELINE = "dateline";
        public static final String COLUMN_NAME_SUMMARY = "summary";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_PIC_URL = "pic_url";
        public static final String COLUMN_NAME_CHECKED = "checked";
    }

   /* public static abstract class Live {
        public static final String TABLE_NAME = "Live";
        public static final String COLUMN_NAME_M3U8 = "m3u8";
        public static final String COLUMN_NAME_RTMP = "rtmp";
    }*/

}