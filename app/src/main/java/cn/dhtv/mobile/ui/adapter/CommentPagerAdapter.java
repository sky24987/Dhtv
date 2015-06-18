package cn.dhtv.mobile.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.dhtv.mobile.fragment.subFragment.CommentSubFragment;

/**
 * Created by Jack on 2015/6/16.
 */
public class CommentPagerAdapter extends FragmentStatePagerAdapter{
    String[] commentTypes;




    public CommentPagerAdapter(FragmentManager fm, String[] commentTypes) {
        super(fm);
        this.commentTypes = commentTypes;
    }

    @Override
    public Fragment getItem(int position) {
        return CommentSubFragment.newInstance(commentTypes[position]);
    }

    @Override
    public int getCount() {
        return commentTypes.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String commentType = commentTypes[position];
        if(commentType.equals("aaid")){
            return "新闻";
        }

        if(commentType.equals("avid")){
            return "视频";
        }

        return null;
    }
}
