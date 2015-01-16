package cn.dhtv.mobile.util;

import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jack on 2015/1/7.
 */
public class DataTest {
    public static List<String> newsTypeList = new ArrayList<>();
    public static HashMap<String,ArrayList<String>> newsListMap = new HashMap<>();
    public static String[] guoji = {"aaa","bbbb","ccc"};
    public static String[] guonei = {"国际","欧洲","哈哈"};
    public static String[] wenzhou = {"温州1","温州3","温州2"};

    static {
        newsTypeList.add("国际");
        newsTypeList.add("要闻");
        newsTypeList.add("温州");

        newsListMap.put(newsTypeList.get(0),new ArrayList<String>(Arrays.asList(guoji)));
        newsListMap.put(newsTypeList.get(1),new ArrayList<String>(Arrays.asList(guonei)));
        newsListMap.put(newsTypeList.get(2),new ArrayList<String>(Arrays.asList(wenzhou)));

    }
}
