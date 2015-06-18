package cn.dhtv.mobile.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

import cn.dhtv.mobile.R;
import cn.dhtv.mobile.model.User;
import cn.dhtv.mobile.model.UserCenter;
import cn.dhtv.mobile.ui.adapter.CommentPagerAdapter;
import cn.dhtv.mobile.ui.widget.MySmartTabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentFragment extends Fragment {
    private static  final int REQUEST_CODE_LOGIN = 1;

    private ViewPager mViewPager;
    private MySmartTabLayout mMySmartTabLayout;

    private String[] commentTypes = {"aaid","avid"};


    public static CommentFragment newInstance() {
        CommentFragment fragment = new CommentFragment();

        return fragment;
    }

    public CommentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           //TODO
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.pager_with_tab, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mMySmartTabLayout = (MySmartTabLayout) view.findViewById(R.id.pager_title);
        mViewPager.setAdapter(new CommentPagerAdapter(getChildFragmentManager(), commentTypes));
        mMySmartTabLayout.setViewPager(mViewPager);
        User user = UserCenter.getInstance().retrieveUser(getActivity());
        if(user == null){
            UserCenter.toLoginActivity(getActivity(),REQUEST_CODE_LOGIN);
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_LOGIN){
            if(resultCode == Activity.RESULT_OK){
                refreshContent();
            }
        }
    }

    public void refreshContent(){
        //TODO nothing
    }
}
