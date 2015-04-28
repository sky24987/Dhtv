package cn.dhtv.mobile;

import cn.dhtv.mobile.entity.Category;

/**
 * Created by Jack on 2015/4/14.
 */
public class Data {
    public static final String PREFERENCE_NAME_APP = "app";
    public static final String PREFERENCE_KEY_APP_INITIATED = "app_initiated";


    public static final String ACCOUNT_TYPE = "dhtv.cn";
    public static final String DUMMY_ACCOUNT = "dummyaccount";

    public static final long SYNC_PERIOD_ONE_DAY = 24L*60L*60L;

    public static final int PAGE_SIZE = 10;
    public static final int NEWS_PAGE_SIZE = PAGE_SIZE;

    public static final Category newsFatherCategory;
    public static final Category videoFatherCategory;
    public static final Category tvChannelFatherCategory;
    public static final Category audioChannelFatherCategory;
    static {
        newsFatherCategory = new Category();
        newsFatherCategory.setCatid(255);
        videoFatherCategory = new Category();
        videoFatherCategory.setCatid(257);
        tvChannelFatherCategory = new Category();
        tvChannelFatherCategory.setCatid(19);
        audioChannelFatherCategory = new Category();
        audioChannelFatherCategory.setCatid(24);
    }
}
