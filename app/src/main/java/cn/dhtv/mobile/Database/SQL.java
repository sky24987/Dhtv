package cn.dhtv.mobile.Database;

import cn.dhtv.mobile.Database.Contract;

/**
 * Created by Jack on 2015/4/10.
 */
public class SQL {
    public static final String TEXT_TYPE = " TEXT ";
    public static final String INTEGER_TYPE = " INTEGER ";
    public static final String REAL_TYPE = " REAL ";
    public static final String BOOLEAN_TYPE = " BOOLEAN ";


    public static final String PRIMARY_KEY = " PRIMARY_KEY ";
    public static final String NOT_NULL = " NOT NULL ";
    public static final String UNIQUE = " UNIQUE ";
    public static final String COMMA_SEP = ",";





    public static final String SQL_CREATE_ARTICLE = "CREATE TABLE "+ Contract.Article.TABLE_NAME+" ("+
            Contract.Article._ID + INTEGER_TYPE+PRIMARY_KEY+COMMA_SEP+
            Contract.Article.COLUMN_NAME_AAID+INTEGER_TYPE+NOT_NULL+UNIQUE+COMMA_SEP+
            Contract.Article.COLUMN_NAME_CAT_ID+INTEGER_TYPE+NOT_NULL+COMMA_SEP+
            Contract.Article.COLUMN_NAME_AID+INTEGER_TYPE+COMMA_SEP+
            Contract.Article.COLUMN_NAME_TITLE+TEXT_TYPE+COMMA_SEP+
            Contract.Article.COLUMN_NAME_SUMMARY+TEXT_TYPE+COMMA_SEP+
            Contract.Article.COLUMN_NAME_DATELINE+TEXT_TYPE+COMMA_SEP+
            Contract.Article.COLUMN_NAME_FROM+TEXT_TYPE+COMMA_SEP+
            Contract.Article.COLUMN_NAME_URL+TEXT_TYPE+COMMA_SEP+
            Contract.Article.COLUMN_NAME_PIC_URL+TEXT_TYPE+COMMA_SEP+
            Contract.Article.COLUMN_NAME_UID+INTEGER_TYPE+COMMA_SEP+
            Contract.Article.COLUMN_NAME_CHECKED + BOOLEAN_TYPE+COMMA_SEP+
            Contract.Article.COLUMN_NAME_USER_NAME+TEXT_TYPE+
            ");";

    public static final String SQL_CREATE_VIDEO = "CREATE TABLE "+Contract.Video.TABLE_NAME+" ("+
            Contract.Video._ID+INTEGER_TYPE+PRIMARY_KEY+COMMA_SEP+
            Contract.Video.COLUMN_NAME_AVID+INTEGER_TYPE+NOT_NULL+UNIQUE+COMMA_SEP+
            Contract.Video.COLUMN_NAME_VID+INTEGER_TYPE+COMMA_SEP+
            Contract.Video.COLUMN_NAME_CAT_ID+INTEGER_TYPE+NOT_NULL+COMMA_SEP+
            Contract.Video.COLUMN_NAME_TITLE+TEXT_TYPE+COMMA_SEP+
            Contract.Video.COLUMN_NAME_SUMMARY+TEXT_TYPE+COMMA_SEP+
            Contract.Video.COLUMN_NAME_DATELINE+TEXT_TYPE+COMMA_SEP+
            Contract.Video.COLUMN_NAME_UID+INTEGER_TYPE+COMMA_SEP+
            Contract.Video.COLUMN_NAME_CHECKED + BOOLEAN_TYPE+COMMA_SEP+
            Contract.Video.COLUMN_NAME_USER_NAME+TEXT_TYPE+COMMA_SEP+
            Contract.Video.COLUMN_NAME_DIR+TEXT_TYPE+COMMA_SEP+
            Contract.Video.COLUMN_NAME_PIC+TEXT_TYPE+COMMA_SEP+
            Contract.Video.COLUMN_NAME_URL+TEXT_TYPE+COMMA_SEP+
            Contract.Video.COLUMN_NAME_DURATION+INTEGER_TYPE+
            ");";


    public static final String SQL_CREATE_CATEGORY = "CREATE TABLE "+Contract.Category.TABLE_NAME+" ("+
            Contract.Category._ID+INTEGER_TYPE+PRIMARY_KEY+COMMA_SEP+
            Contract.Category.COLUMN_NAME_CAT_ID+INTEGER_TYPE+NOT_NULL+UNIQUE+COMMA_SEP+
            Contract.Category.COLUMN_NAME_UP_ID+INTEGER_TYPE+COMMA_SEP+
            Contract.Category.COLUMN_NAME_TOP_ID+INTEGER_TYPE+COMMA_SEP+
            Contract.Category.COLUMN_NAME_CAT_NAME+TEXT_TYPE+COMMA_SEP+
            Contract.Category.COLUMN_NAME_DESCRIPTION+TEXT_TYPE+COMMA_SEP+
            Contract.Category.COLUMN_NAME_NAME+TEXT_TYPE+COMMA_SEP+
            Contract.Category.COLUMN_NAME_BID+INTEGER_TYPE+COMMA_SEP+
            Contract.Category.COLUMN_NAME_M3U8+TEXT_TYPE+COMMA_SEP+
            Contract.Category.COLUMN_NAME_RTMP+TEXT_TYPE+
            ");";

    public static final String SQL_CREATE_BLOCK = "CREATE TABLE "+ Contract.Block.TABLE_NAME+" ("+
            Contract.Block._ID+INTEGER_TYPE+PRIMARY_KEY+COMMA_SEP+
            Contract.Block.COLUMN_NAME_ID+INTEGER_TYPE+NOT_NULL+UNIQUE+COMMA_SEP+
            Contract.Block.COLUMN_NAME_UID+INTEGER_TYPE+COMMA_SEP+
            Contract.Block.COLUMN_NAME_COMMENT_NUM+INTEGER_TYPE+COMMA_SEP+
            Contract.Block.COLUMN_NAME_TITLE+TEXT_TYPE+COMMA_SEP+
            Contract.Block.COLUMN_NAME_SUMMARY+TEXT_TYPE+COMMA_SEP+
            Contract.Block.COLUMN_NAME_URL+TEXT_TYPE+COMMA_SEP+
            Contract.Block.COLUMN_NAME_PIC+TEXT_TYPE+COMMA_SEP+
            Contract.Block.COLUMN_NAME_CATNAME+TEXT_TYPE+COMMA_SEP+
            Contract.Block.COLUMN_NAME_ID_TYPE+TEXT_TYPE+COMMA_SEP+
            Contract.Block.COLUMN_NAME_AVATAR+TEXT_TYPE+COMMA_SEP+
            Contract.Block.COLUMN_NAME_DATELINE+TEXT_TYPE+COMMA_SEP+
            Contract.Block.COLUMN_NAME_USER_NAME+TEXT_TYPE+COMMA_SEP+
            Contract.Block.COLUMN_NAME_FROM_BID+INTEGER_TYPE+NOT_NULL+
            ");";




    /*public static void main(String[] args){
        System.out.println(SQL_CREATE_ARTICLE);
        System.out.println(SQL_CREATE_CATEGORY);

    }*/

}
