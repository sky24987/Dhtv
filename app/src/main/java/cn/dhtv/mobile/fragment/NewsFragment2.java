package cn.dhtv.mobile.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
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
import cn.dhtv.mobile.adapter.NewsListAdapter;
import cn.dhtv.mobile.adapter.NewsListAdapter2;
import cn.dhtv.mobile.adapter.NewsPagerAdapter;
import cn.dhtv.mobile.entity.NewsCat;
import cn.dhtv.mobile.model.ListManager;
import cn.dhtv.mobile.model.NewsDataList;
import cn.dhtv.mobile.model.NewsListManager;
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
public class NewsFragment2 extends SectionFragment implements BasePagerAdapter.PageFactory,ListManager.CallBacks<NewsCat>{
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
    //private LayoutInflater mInflater;

    private NewsListManager mNewsListManager;
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
    public String getTitle() {
        return title;
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
        mNewsListManager = ((MyApplication)getActivity().getApplication()).getNewsListManager();
        mImageLoader = NetUtils.getImageLoader(getActivity());

        View view =  inflater.inflate(R.layout.fragment_news, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager_news);
        mTabPageIndicator =  (TabPageIndicator) view.findViewById(R.id.news_category);
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
        /*try {
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
    public int pageCount() {
        return mNewsListManager.getCategoryCount();
    }

    @Override
    public BasePagerAdapter.Page generatePage(int position) {
        NewsCat cat = mNewsListManager.getCategories().get(position);
        NewsDataList newsDataList = mNewsListManager.getDataList(cat);
        mNewsListManager.setCallBacks(this);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.news_page,null);
        NewsPage page = new NewsPage(cat.getCatname(),view);
        page.cat = cat;
        page.mPullToRefreshLayout = (PullToRefreshLayout)view.findViewById(R.id.refresh_view);
        page.newsList = (FooterRefreshListView) view.findViewById(R.id.news_list);
        page.newsList.setFooterRefreshListener(page);
        page.listAdapter = new NewsListAdapter2(cat, newsDataList, mImageLoader, getActivity());
        ActionBarPullToRefresh.from(getActivity()).theseChildrenArePullable(page.newsList).listener(page).setup(page.mPullToRefreshLayout);
        page.newsList.setAdapter(page.listAdapter);
        return page;
    }

    @Override
    public String getPageTitle(int position) {
        return mNewsListManager.getCategories().get(position).getCatname();
    }

    @Override
    public int getPagePosition(String title) {
        return 0;
    }

    @Override
    public void onRefresh(NewsCat category, int flag) {
        NewsPage page = (NewsPage) mPageHolder.get(category.getCatname());
        if(page == null){
            return;
        }

        page.listAdapter.notifyDataSetChanged();
        page.mPullToRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onAppend(NewsCat category, int flag) {
        NewsPage page = (NewsPage) mPageHolder.get(category.getCatname());
        if(page == null){
            return;
        }

        page.listAdapter.notifyDataSetChanged();
        page.newsList.setRefreshFooterStatus(cn.dhtv.android.widget.FooterRefreshListView.RefreshFooterStatus.CLICKABLE);

    }

    @Override
    public void onRefreshFails(NewsCat category, int flag) {
        NewsPage page = (NewsPage) mPageHolder.get(category.getCatname());
        if(page == null){
            return;
        }

        page.mPullToRefreshLayout.setRefreshing(false);
        Toast.makeText(getActivity(), "获取新闻失败...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAppendFails(NewsCat category, int flag) {
        NewsPage page = (NewsPage) mPageHolder.get(category.getCatname());
        if(page == null){
            return;
        }

        page.newsList.setRefreshFooterStatus(cn.dhtv.android.widget.FooterRefreshListView.RefreshFooterStatus.CLICKABLE);
        Toast.makeText(getActivity(), "添加新闻失败...", Toast.LENGTH_SHORT).show();
    }

    private class NewsPage extends BasePagerAdapter.Page implements OnRefreshListener,FooterRefreshListView.FooterRefreshListener{
        public NewsCat cat;
        public PullToRefreshLayout mPullToRefreshLayout;
        public FooterRefreshListView newsList;
        public View emptyView;
        public BaseAdapter listAdapter;
        NewsPage(String title, View pageView) {
            super(title, pageView);
        }

        @Override
        public void onRefreshStarted(View view) {
            mNewsListManager.refresh(cat,0);
        }

        @Override
        public void onFooterRefreshing() {
            mNewsListManager.append(cat,0);
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
