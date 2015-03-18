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
    public static List<String> newsTypeList = new ArrayList<String>();
    public static HashMap<String,ArrayList<String>> newsListMap = new HashMap<String, ArrayList<String>>();
    public static String[] guoji = {"aaa","bbbb","ccc"};
    public static String[] guonei = {"国际","欧洲","哈哈"};
    public static String[] wenzhou = {"温州1","温州3","温州2"};

    static {
        newsTypeList.add("国际");
        newsTypeList.add("要闻");
        newsTypeList.add("温州");
        newsTypeList.add("国际");
        newsTypeList.add("要闻");
        newsTypeList.add("温州");

        newsListMap.put(newsTypeList.get(0),new ArrayList<String>(Arrays.asList(guoji)));
        newsListMap.put(newsTypeList.get(1),new ArrayList<String>(Arrays.asList(guonei)));
        newsListMap.put(newsTypeList.get(2),new ArrayList<String>(Arrays.asList(wenzhou)));
        newsListMap.put(newsTypeList.get(3),new ArrayList<String>(Arrays.asList(guoji)));
        newsListMap.put(newsTypeList.get(4),new ArrayList<String>(Arrays.asList(guonei)));
        newsListMap.put(newsTypeList.get(5),new ArrayList<String>(Arrays.asList(wenzhou)));

    }

    public static void main(String[] args){
        String s1 = "aaa";
        String s2 = "aaa";
        ArrayList arrayList = new ArrayList();
        arrayList.add(s1);
        System.out.print(arrayList.contains(s2));

    }
}
