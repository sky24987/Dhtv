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
        public static final String COLUMN_NAME_BID = "bid";
        public static final String COLUMN_NAME_RTMP = "rtmp";
        public static final String COLUMN_NAME_M3U8 = "m3u8";
        public static final String COLUMN_NAME_UPDATE_TIME = "updateTime";

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

    public static abstract class Video implements BaseColumns{
        public static final String TABLE_NAME = "video";
        public static final String COLUMN_NAME_AVID = "avid";
        public static final String COLUMN_NAME_VID = "vid";
        public static final String COLUMN_NAME_UID = "uid";
        public static final String COLUMN_NAME_CAT_ID = "catid";
        public static final String COLUMN_NAME_DURATION = "duration";
        public static final String COLUMN_NAME_USER_NAME = "username";
//        public static final String COLUMN_NAME_FROM = "dfrom";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DATELINE = "dateline";
        public static final String COLUMN_NAME_SUMMARY = "summary";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_PIC = "pic";
        public static final String COLUMN_NAME_VIDEO = "video";
        public static final String COLUMN_NAME_DIR = "dir";
        public static final String COLUMN_NAME_CHECKED = "checked";
    }

    public static abstract class Block implements BaseColumns{
        public static final String TABLE_NAME = "block";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_UID = "uid";
        public static final String COLUMN_NAME_COMMENT_NUM = "commentnum";
        public static final String COLUMN_NAME_ID_TYPE = "idtype";
        public static final String COLUMN_NAME_USER_NAME = "username";
        public static final String COLUMN_NAME_AVATAR = "avatar";
        public static final String COLUMN_NAME_URL  = "url";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_PIC = "pic";
        public static final String COLUMN_NAME_SUMMARY = "summary";
        public static final String COLUMN_NAME_DATELINE = "dateline";
        public static final String COLUMN_NAME_CATNAME = "catname";

        public static final String COLUMN_NAME_FROM_BID = "fromBid";
    }

   /* public static abstract class Live {
        public static final String TABLE_NAME = "Live";
        public static final String COLUMN_NAME_M3U8 = "m3u8";
        public static final String COLUMN_NAME_RTMP = "rtmp";
    }*/

}
