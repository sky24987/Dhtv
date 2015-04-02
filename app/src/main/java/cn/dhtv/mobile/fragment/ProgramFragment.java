package cn.dhtv.mobile.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.viewpagerindicator.TabPageIndicator;

import cn.dhtv.android.adapter.BasePagerAdapter;
import cn.dhtv.mobile.MyApplication;
import cn.dhtv.mobile.R;
import cn.dhtv.mobile.adapter.AbstractListAdapter;
import cn.dhtv.mobile.adapter.ProgramListAdapter;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.model.AbsPageManager;
import cn.dhtv.mobile.model.ProgramPageManager;
import cn.dhtv.mobile.model.VideoPageManager;
import cn.dhtv.mobile.network.NetUtils;
import cn.dhtv.mobile.widget.FooterRefreshListView;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProgramFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProgramFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgramFragment extends SectionFragment implements BasePagerAdapter.PageFactory, AbsPageManager.CallBacks{

    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    public  final String title = "点播";
    private ViewPager mViewPager;
    private TabPageIndicator mTabPageIndicator;
    private BasePagerAdapter mPagerAdapter;
    private BasePagerAdapter.PageHolder mPageHolder;

    private ProgramPageManager mProgramPageManager;
    private ImageLoader mImageLoader;

    private OnFragmentInteractionListener mListener;


    public static ProgramFragment newInstance() {
        ProgramFragment fragment = new ProgramFragment();
        return fragment;
    }

    public ProgramFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mProgramPageManager = ((MyApplication)getActivity().getApplication()).getProgramPageManager();
        mImageLoader = NetUtils.getImageLoader(getActivity());

        View view =  inflater.inflate(R.layout.tab_pager, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mTabPageIndicator =  (TabPageIndicator) view.findViewById(R.id.pager_title);
        mPagerAdapter = new BasePagerAdapter(this,null);
        mPageHolder = mPagerAdapter.getPageHolder();
        mViewPager.setAdapter(mPagerAdapter);
        mTabPageIndicator.setViewPager(mViewPager);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        mProgramPageManager.setCallBacks(this);
        mProgramPageManager.refresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        mProgramPageManager.setCallBacks(null);
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
        if(DEBUG){
            Log.d(LOG_TAG,"onRefresh");
        }
        MyPage page = (MyPage) mPageHolder.get(category.getCatname());
        if(page == null){
            return;
        }
        page.adapter.notifyDataSetChanged();
    }

    @Override
    public void onAppend(Category category, AbsPageManager.CallBackFlag flag) {

    }

    @Override
    public void onRefreshFails(Category category, AbsPageManager.CallBackFlag flag) {

    }

    @Override
    public void onAppendFails(Category category, AbsPageManager.CallBackFlag flag) {

    }

    @Override
    public int pageCount() {
        return mProgramPageManager.getCategoryCount();
    }

    @Override
    public BasePagerAdapter.Page generatePage(int position) {
        Category category = mProgramPageManager.getCategory(position);
        AbstractListAdapter.ListViewDataList listViewDataList = mProgramPageManager.getList(category);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.page_recycle_view,null);
        MyPage page = new MyPage(category.getCatname(),view);
        page.category = category;
        page.mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        page.adapter = new ProgramListAdapter(listViewDataList,getActivity());
        page.mRecyclerView.setLayoutManager(page.layoutManager);
        page.mRecyclerView.setAdapter(page.adapter);
        return page;
    }

    @Override
    public String getPageTitle(int position) {
        return mProgramPageManager.getCategory(position).getCatname();
    }

    @Override
    public int getPagePosition(BasePagerAdapter.Page page) {
        MyPage myPage = (MyPage) page;
        Category category = myPage.category;
        int position = mProgramPageManager.indexof(category);
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
        public RecyclerView mRecyclerView;
        public View emptyView;
        public RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(),3, LinearLayoutManager.VERTICAL,false);
        public ProgramListAdapter adapter;
        MyPage(String title, View pageView) {
            super(title, pageView);
        }

        @Override
        public void onRefreshStarted(View view) {
            mProgramPageManager.refresh(category);
        }

        @Override
        public void onFooterRefreshing() {
            mProgramPageManager.append(category);
        }




    }

}
