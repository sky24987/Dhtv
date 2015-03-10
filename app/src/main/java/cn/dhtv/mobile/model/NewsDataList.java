package cn.dhtv.mobile.model;

import cn.dhtv.mobile.entity.NewsCat;
import cn.dhtv.mobile.entity.NewsOverview;

/**
 * Created by Jack on 2015/3/10.
 */
public class NewsDataList extends DataList<NewsCat,NewsOverview>{

    public NewsDataList(NewsCat catagory) {
        super(catagory);
    }
}
