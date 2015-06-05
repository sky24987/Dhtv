package cn.dhtv.mobile.util;

import android.text.format.DateFormat;

import com.android.volley.toolbox.StringRequest;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.dhtv.mobile.Data;
import cn.dhtv.mobile.entity.Category;

/**
 * Created by Jack on 2015/3/20.
 */
public class TextUtils {
    public static final int PAGE_SIZE = 10;

    public static final String URL_CATEGORY = Category.URL;
    public static final String URL_NEWS = "http://api.dhtv.cn/mobile/article/";
    public static final String URL_BLOCK = "http://api.dhtv.cn/mobile/block/";
    public static final String URL_VIDEO = "http://api.dhtv.cn/mobile/video/";
    public static final String URL_TV = "http://api.dhtv.cn/?mod=lookback&ac=tv_archiver";

    public static final String URL_RES = "http://data.wztv.cn/";
    public static final String URL_RES_IMG = "http://tv.dhtv.cn/img/";
    public static final String URL_RES_TV = URL_RES+"tv/";

    public static final String URL_UPGRADE = "http://api.dhtv.cn/mobile/update/";

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat();



    public static String makeCategoryUrl(Category category,int page){
        return URL_CATEGORY +"?level=1"+"&catid="+category.getCatid()+"&page="+page/*+"&size="+PAGE_SIZE*/;
    }

    public static String makeNewsOverviewUrl(Category category,int page){
        return URL_NEWS+"?"+"catid="+category.getCatid()+"&page="+page+"&size="+ Data.TOTAL_PAGE_SIZE_NEWS;
    }

    public static String makeBlockQueryUrl(Category category){
        return URL_BLOCK+"?"+"bid="+category.getBid();
    }

    public static String makeVideoUrl(Category category,int beginId){
        return URL_VIDEO+"?"+"catid="+category.getCatid()+"&page="+beginId+"&size="+Data.TOTAL_PAGE_SIZE_NEWS;
    }

    public static String makeTvUrl(Category category,int beginId){
        return URL_TV+"&page="+beginId+"&id="+category.getUpid()+"&catid="+category.getCatid();
    }

    public static String makeCheckUpgradeUrl(int ver,int appid,String uuid){
        return URL_UPGRADE+"?ver="+ver+"&appid="+appid+"&uuid="+uuid+"&devicetype=android";
    }









}
