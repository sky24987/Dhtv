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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.viewpagerindicator.TabPageIndicator;

import cn.dhtv.android.adapter.BasePagerAdapter;
import cn.dhtv.mobile.MyApplication;
import cn.dhtv.mobile.R;
import cn.dhtv.mobile.adapter.AbstractListAdapter;
import cn.dhtv.mobile.adapter.NewsListAdapter;
import cn.dhtv.mobile.adapter.NewsListAdapter2;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.NewsCat;
import cn.dhtv.mobile.model.AbsPageManager;
import cn.dhtv.mobile.model.NewsDataList;
import cn.dhtv.mobile.model.NewsListManager;
import cn.dhtv.mobile.model.NewsPageManager;
import cn.dhtv.mobile.network.NetUtils;
import cn.dhtv.mobile.widget.FooterRefreshListView;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsFragment2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewsFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment2 extends SectionFragment implements BasePagerAdapter.PageFactory, AbsPageManager.CallBacks{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public  final String title = "新闻";
    private ViewPager mViewPager;
    private TabPageIndicator mTabPageIndicator;
    private BasePagerAdapter mPagerAdapter;
    private BasePagerAdapter.PageHolder mPageHolder;

    private NewsPageManager mNewsPageManager;
    private ImageLoader mImageLoader;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewsFragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsFragment2 newInstance(String param1, String param2) {
        NewsFragment2 fragment = new NewsFragment2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NewsFragment2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mNewsPageManager = ((MyApplication)getActivity().getApplication()).getNewsPageManager();
        mImageLoader = NetUtils.getImageLoader(getActivity());

        View view =  inflater.inflate(R.layout.page, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mTabPageIndicator =  (TabPageIndicator) view.findViewById(R.id.pager_title);
        mPagerAdapter = new BasePagerAdapter(this,null);
        mPageHolder = mPagerAdapter.getPageHolder();
        mViewPager.setAdapter(mPagerAdapter);
        mTabPageIndicator.setViewPager(mViewPager);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
       /* try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh(Category category, AbsPageManager.CallBackFlag flag) {
        NewsPage page = (NewsPage) mPageHolder.get(category.getCatname());
        if(page == null){
            return;
        }

        page.listAdapter.notifyDataSetChanged();
        page.mPullToRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onAppend(Category category, AbsPageManager.CallBackFlag flag) {
        NewsPage page = (NewsPage) mPageHolder.get(category.getCatname());
        if(page == null){
            return;
        }

        page.listAdapter.notifyDataSetChanged();
        page.newsList.setRefreshFooterStatus(cn.dhtv.android.widget.FooterRefreshListView.RefreshFooterStatus.CLICKABLE);
    }

    @Override
    public void onRefreshFails(Category category, AbsPageManager.CallBackFlag flag) {
        NewsPage page = (NewsPage) mPageHolder.get(category.getCatname());
        if(page == null){
            return;
        }

        page.mPullToRefreshLayout.setRefreshing(false);
        Toast.makeText(getActivity(), "获取新闻失败...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAppendFails(Category category, AbsPageManager.CallBackFlag flag) {
        NewsPage page = (NewsPage) mPageHolder.get(category.getCatname());
        if(page == null){
            return;
        }

        page.newsList.setRefreshFooterStatus(cn.dhtv.android.widget.FooterRefreshListView.RefreshFooterStatus.FORCE_CLICK_STATE);
        Toast.makeText(getActivity(), "添加新闻失败...", Toast.LENGTH_SHORT).show();
    }




    @Override
    public int pageCount() {
        return mNewsPageManager.getPageCount();
    }

    @Override
    public BasePagerAdapter.Page generatePage(int position) {
        Category category = mNewsPageManager.getCategory(position);
        AbstractListAdapter.ListViewDataList listViewDataList = mNewsPageManager.getList(category);
        mNewsPageManager.setCallBacks(this);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.news_page,null);
        NewsPage page = new NewsPage(category.getCatname(),view);
        page.category = category;
        page.mPullToRefreshLayout = (PullToRefreshLayout)view.findViewById(R.id.refresh_view);
        page.newsList = (FooterRefreshListView) view.findViewById(R.id.news_list);
        page.newsList.setFooterRefreshListener(page);
        page.listAdapter = new NewsListAdapter2(category, listViewDataList, mImageLoader, getActivity());
        ActionBarPullToRefresh.from(getActivity()).theseChildrenArePullable(page.newsList).listener(page).setup(page.mPullToRefreshLayout);
        page.newsList.setAdapter(page.listAdapter);
        return page;
    }

    @Override
    public String getPageTitle(int position) {
        return mNewsPageManager.getCategory(position).getCatname();
    }

    @Override
    public int getPagePosition(BasePagerAdapter.Page page) {
        NewsPage newsPage = (NewsPage) page;
        Category category = newsPage.category;
        int position = mNewsPageManager.indexof(category);
        if(position >= 0){
            return position;
        }else {
            return PagerAdapter.POSITION_NONE;
        }
    }


    @Override
    public String getTitle() {
        return title;
    }

    private class NewsPage extends BasePagerAdapter.Page implements OnRefreshListener,FooterRefreshListView.FooterRefreshListener{
        public Category category;
        public PullToRefreshLayout mPullToRefreshLayout;
        public FooterRefreshListView newsList;
        public View emptyView;
        public BaseAdapter listAdapter;
        NewsPage(String title, View pageView) {
            super(title, pageView);
        }

        @Override
        public void onRefreshStarted(View view) {
            mNewsPageManager.refresh(category);
        }

        @Override
        public void onFooterRefreshing() {
            mNewsPageManager.append(category);
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

}
