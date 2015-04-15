package cn.dhtv.mobile.util;

import cn.dhtv.mobile.entity.Category;

/**
 * Created by Jack on 2015/3/20.
 */
public class TextUtils {
    public static final int PAGE_SIZE = 10;

    public static final String CATEGORY_URL = Category.URL;
    public static final String URL_NEWS = "http://api.dhtv.cn/mobile/article/";

    public static String makeCategoryUrl(Category category,int page){
        return CATEGORY_URL+"?level=1"+"&catid="+category.getCatid()+"&page="+page+"&size="+PAGE_SIZE;
    }

    public static String makeNewsOverviewUrl(Category category,int page){
        return URL_NEWS+"?"+"catid="+category.getCatid()+"&page="+page+"&size="+PAGE_SIZE;
    }

}
