package cn.dhtv.mobile.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.viewpagerindicator.TabPageIndicator;

import cn.dhtv.android.adapter.BasePagerAdapter;
import cn.dhtv.mobile.MyApplication;
import cn.dhtv.mobile.R;
import cn.dhtv.mobile.adapter.AbstractListAdapter;
import cn.dhtv.mobile.adapter.ItemViewDataSet;
import cn.dhtv.mobile.adapter.VideoListAdapter;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.model.AbsPageManager;
import cn.dhtv.mobile.widget.FooterRefreshListView;
import cn.dhtv.mobile.model.VideoPageManager;
import cn.dhtv.mobile.network.NetUtils;
import cn.dhtv.mobile.widget.MySmartTabLayout;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VideoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends SectionFragment implements BasePagerAdapter.PageFactory, AbsPageManager.CallBacks{

    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    public  final String title = "视频";
    private ViewPager mViewPager;
    private MySmartTabLayout mMySmartTabLayout;//private TabPageIndicator mTabPageIndicator;
    private BasePagerAdapter mPagerAdapter;
    private BasePagerAdapter.PageHolder mPageHolder;

    private VideoPageManager mVideoPageManager;
    private ImageLoader mImageLoader;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment VideoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoFragment newInstance() {
        VideoFragment fragment = new VideoFragment();
        return fragment;
    }

    public VideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mVideoPageManager = ((MyApplication)getActivity().getApplication()).getVideoPageManager();

        mImageLoader = NetUtils.getImageLoader(getActivity());

        View view =  inflater.inflate(R.layout.pager_with_tab, container, false);//View view =  inflater.inflate(R.layout.tab_pager, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
       mMySmartTabLayout = (MySmartTabLayout) view.findViewById(R.id.pager_title);// mTabPageIndicator =  (TabPageIndicator) view.findViewById(R.id.pager_title);
        mPagerAdapter = new BasePagerAdapter(this,null);
        mPageHolder = mPagerAdapter.getPageHolder();
        mViewPager.setAdapter(mPagerAdapter);
       mMySmartTabLayout.setViewPager(mViewPager);//mTabPageIndicator.setViewPager(mViewPager);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();
        mVideoPageManager.setCallBacks(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mVideoPageManager.setCallBacks(null);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void onRefresh(Category category, AbsPageManager.CallBackFlag flag) {
        MyPage page = (MyPage) mPageHolder.get(category.getCatname());
        if(page == null){
            return;
        }

        page.listAdapter.notifyDataSetChanged();
        page.mPullToRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onAppend(Category category, AbsPageManager.CallBackFlag flag) {
        MyPage page = (MyPage) mPageHolder.get(category.getCatname());
        if(page == null){
            return;
        }
        page.listAdapter.notifyDataSetChanged();

        page.listView.setRefreshFooterStatus(FooterRefreshListView.RefreshFooterStatus.CLICKABLE);
    }

    @Override
    public void onRefreshFails(Category category, AbsPageManager.CallBackFlag flag) {
        MyPage page = (MyPage) mPageHolder.get(category.getCatname());
        if(page == null){
            return;
        }

        page.mPullToRefreshLayout.setRefreshing(false);
        Toast.makeText(getActivity(), "获取视频失败...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAppendFails(Category category, AbsPageManager.CallBackFlag flag) {
        MyPage page = (MyPage) mPageHolder.get(category.getCatname());
        if(page == null){
            return;
        }

        page.listView.setRefreshFooterStatus(FooterRefreshListView.RefreshFooterStatus.FORCE_CLICK_STATE);
        Toast.makeText(getActivity(), "添加视频失败...", Toast.LENGTH_SHORT).show();
    }




    @Override
    public int pageCount() {
        return mVideoPageManager.getCategoryCount();
    }

    @Override
    public BasePagerAdapter.Page generatePage(int position) {
        Category category = mVideoPageManager.getCategory(position);
        ItemViewDataSet listViewDataList = mVideoPageManager.getList(category);


        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.page,null);
        MyPage page = new MyPage(category.getCatname(),view);
        page.category = category;
        page.mPullToRefreshLayout = (PullToRefreshLayout)view.findViewById(R.id.refresh_view);
        page.listView = (FooterRefreshListView) view.findViewById(R.id.list);
        page.listView.setFooterRefreshListener(page);
        page.listAdapter = new VideoListAdapter(category, listViewDataList, mImageLoader, getActivity());
        ActionBarPullToRefresh.from(getActivity()).theseChildrenArePullable(page.listView).listener(page).setup(page.mPullToRefreshLayout);
        page.listView.setAdapter(page.listAdapter);

        return page;
    }

    @Override
    public String getPageTitle(int position) {
        return mVideoPageManager.getCategory(position).getCatname();
    }

    @Override
    public int getPagePosition(BasePagerAdapter.Page page) {
        MyPage myPage = (MyPage) page;
        Category category = myPage.category;
        int position = mVideoPageManager.indexof(category);
        if(position >= 0){
            return position;
        }else {
            return PagerAdapter.POSITION_NONE;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    private class MyPage extends BasePagerAdapter.Page implements OnRefreshListener,FooterRefreshListView.FooterRefreshListener{
        public Category category;
        public PullToRefreshLayout mPullToRefreshLayout;
        public FooterRefreshListView listView;
        public View emptyView;
        public BaseAdapter listAdapter;
        MyPage(String title, View pageView) {
            super(title, pageView);
        }

        @Override
        public void onRefreshStarted(View view) {
            mVideoPageManager.refresh(category);
        }

        @Override
        public void onFooterRefreshing() {
            mVideoPageManager.append(category);
        }
    }
}
