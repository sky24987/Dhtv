package cn.dhtv.mobile.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;


import java.util.ArrayList;

import cn.dhtv.android.adapter.BasePagerAdapter;
import cn.dhtv.android.adapter.BaseRecyclerViewAdapter;
import cn.dhtv.android.widget.BaseRecyclerView;
import cn.dhtv.mobile.Data;
import cn.dhtv.mobile.Database.CategoryAccessor;
import cn.dhtv.mobile.Database.Contract;
import cn.dhtv.mobile.MyApplication;
import cn.dhtv.mobile.R;
import cn.dhtv.mobile.Singletons;
import cn.dhtv.mobile.activity.VideoPlayerActivity;
import cn.dhtv.mobile.provider.MyContentProvider;
import cn.dhtv.mobile.ui.adapter.ItemViewDataSet;
import cn.dhtv.mobile.ui.adapter.NewsRecyclerViewAdapter;

import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.model.AbsPageManager;
import cn.dhtv.mobile.ui.adapter.VideoRecyclerViewAdapter;
import cn.dhtv.mobile.ui.widget.EmptyView;
import cn.dhtv.mobile.ui.widget.FooterRefreshListView;
import cn.dhtv.mobile.model.VideoPageManager;
import cn.dhtv.mobile.network.NetUtils;
import cn.dhtv.mobile.ui.widget.FooterRefreshView;
import cn.dhtv.mobile.ui.widget.MySmartTabLayout;
import cn.dhtv.mobile.ui.widget.PromptBar;
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
public class VideoFragment2 extends SectionFragment implements BasePagerAdapter.PageFactory, AbsPageManager.CallBacks,android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_VIDEO_CATEGORY = 1;


    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private final Category videoFather = Data.videoFatherCategory;

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
    public static VideoFragment2 newInstance() {
        VideoFragment2 fragment = new VideoFragment2();
        return fragment;
    }

    public VideoFragment2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(LOADER_VIDEO_CATEGORY,null,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mVideoPageManager = ((MyApplication)getActivity().getApplication()).getVideoPageManager();
        mVideoPageManager.setCallBacks(this);

        mImageLoader = Singletons.getImageLoader();

        View view =  inflater.inflate(R.layout.pager_with_tab, container, false);//View view =  inflater.inflate(R.layout.tab_pager, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mMySmartTabLayout = (MySmartTabLayout) view.findViewById(R.id.pager_title);// mTabPageIndicator =  (TabPageIndicator) view.findViewById(R.id.pager_title);
        mPagerAdapter = new BasePagerAdapter(this,null);
        mPagerAdapter.setOnPageHolderActListener(new BasePagerAdapter.OnPageHolderActListener() {
            @Override
            public void onReceiveNewPage(BasePagerAdapter.Page page) {

            }

            @Override
            public void onProvidePage(BasePagerAdapter.Page page) {

            }

            @Override
            public void onDropPage(BasePagerAdapter.Page page) {
                ((MyPage)page).mSwipeRefreshLayout.clearAnimation();
            }
        });
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
        /*mVideoPageManager.setCallBacks(this);*/
    }

    @Override
    public void onPause() {
        super.onPause();
        /*mVideoPageManager.setCallBacks(null);*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content").authority(MyContentProvider.AUTHORITY).appendPath(Contract.Category.TABLE_NAME).build();
        Log.d(LOG_TAG, uri.toString());
        String selection = Contract.Category.COLUMN_NAME_UP_ID+ " = ?";
        String[] selectionArgs = new String[]{""+videoFather.getCatid()};
        return new CursorLoader(getActivity(),uri,null,selection,selectionArgs,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<Category> titles = (ArrayList<Category>) CategoryAccessor.toList(data);
        mVideoPageManager.change(titles);
        mPagerAdapter.notifyDataSetChanged();
        mMySmartTabLayout.setViewPager(mViewPager);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onFirstFetch(Category category, AbsPageManager.CallBackFlag flag) {
        MyPage page = (MyPage) mPageHolder.get(category.getCatname());
        if(page == null){
            return;
        }

        page.emptyView.setStateIdle();
        page.mVideoRecyclerViewAdapter.notifyDataSetChanged();
        page.promptBar.show(getString(R.string.prompt_bar_refresh_success));
    }

    @Override
    public void onFirstFetchFails(Category category, AbsPageManager.CallBackFlag flag) {
        MyPage page = (MyPage) mPageHolder.get(category.getCatname());
        if(page == null){
            return;
        }

        page.emptyView.setStateFail();
//        page.promptBar.show(getString(R.string.prompt_bar_refresh_fail));
    }

    @Override
    public void onRefresh(Category category, AbsPageManager.CallBackFlag flag) {
        MyPage page = (MyPage) mPageHolder.get(category.getCatname());
        if(page == null){
            return;
        }

        page.mSwipeRefreshLayout.setRefreshing(false);
        page.mVideoRecyclerViewAdapter.notifyDataSetChanged();
        page.promptBar.show(getString(R.string.prompt_bar_refresh_success));
    }

    @Override
    public void onAppend(Category category, AbsPageManager.CallBackFlag flag) {
        MyPage page = (MyPage) mPageHolder.get(category.getCatname());
        if(page == null){
            return;
        }

        page.mVideoRecyclerViewAdapter.notifyDataSetChanged();
        if(page.footerRefreshView.getStatus() == FooterRefreshView.Status.REFRESHING){
            page.footerRefreshView.setStatus(FooterRefreshView.Status.CLICKABLE);
        }

    }

    @Override
    public void onRefreshFails(Category category, AbsPageManager.CallBackFlag flag) {
        MyPage page = (MyPage) mPageHolder.get(category.getCatname());
        if(page == null){
            return;
        }

        page.mSwipeRefreshLayout.setRefreshing(false);
        page.promptBar.show(getString(R.string.prompt_bar_refresh_fail));
    }

    @Override
    public void onAppendFails(Category category, AbsPageManager.CallBackFlag flag) {
        MyPage page = (MyPage) mPageHolder.get(category.getCatname());
        if(page == null){
            return;
        }

        if(flag == AbsPageManager.CallBackFlag.DB_NULL){
            page.footerRefreshView.setStatus(FooterRefreshView.Status.NO_MORE);
        }else {
            page.footerRefreshView.setStatus(FooterRefreshView.Status.FORCE_CLICK);
        }
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
        View view = inflater.inflate(R.layout.page_recycle_view,null);
        MyPage page = new MyPage(category.getCatname(),view);
        page.category = category;
//        page.mPullToRefreshLayout = (PullToRefreshLayout)view.findViewById(R.id.refresh_view);
        page.mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_view);
        page.mSwipeRefreshLayout.setOnRefreshListener(page);
        page.promptBar = (PromptBar) view.findViewById(R.id.promptBar);

        page.mBaseRecyclerView = (BaseRecyclerView) view.findViewById(R.id.recyclerView);
        page.mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        page.mVideoRecyclerViewAdapter = new VideoRecyclerViewAdapter(listViewDataList);
        page.mBaseRecyclerView.setLayoutManager(page.mLayoutManager);
        page.mBaseRecyclerView.setAdapter(page.mVideoRecyclerViewAdapter);
        page.mBaseRecyclerView.setOnItemAttachDetachListener(page);
        page.emptyView = (EmptyView) inflater.inflate(R.layout.widget_empty_view,page.mBaseRecyclerView,false);
        page.emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmptyView emptyView = (EmptyView) v;
                if(emptyView.isFail()) {
                    emptyView.setStateProcessing();
                }
            }
        });
        page.emptyView.setOnProcessingListener(page);
        page.footerRefreshView = (FooterRefreshView) inflater.inflate(R.layout.widget_refresh_footer,page.mBaseRecyclerView,false);
        page.footerRefreshView.setRefreshingListener(page);
        page.mVideoRecyclerViewAdapter.setOnItemClickListener(page);
        page.mVideoRecyclerViewAdapter.setEmptyView(new VideoRecyclerViewAdapter.ViewHolder(page.emptyView, BaseRecyclerViewAdapter.ViewHolder.VIEW_TYPE_EMPTY));
        page.mVideoRecyclerViewAdapter.addFooterView(new VideoRecyclerViewAdapter.ViewHolder(page.footerRefreshView, BaseRecyclerViewAdapter.ViewHolder.VIEW_TYPE_FOOTER));


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


    public void startVideoPlayerActivity(Uri uri){
        Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
        intent.setData(uri);
        getActivity().startActivity(intent);

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


    private class MyPage extends BasePagerAdapter.Page implements OnRefreshListener,SwipeRefreshLayout.OnRefreshListener,BaseRecyclerView.OnItemAttachDetachListener,EmptyView.OnProcessingListener,BaseRecyclerViewAdapter.OnItemClickListener,FooterRefreshView.OnRefreshingListener{
        public Category category;
//        public PullToRefreshLayout mPullToRefreshLayout;
//        public FooterRefreshListView listView;
        public EmptyView emptyView;
//        public BaseAdapter listAdapter;
        public PromptBar promptBar;
        public BaseRecyclerView mBaseRecyclerView;
        public SwipeRefreshLayout mSwipeRefreshLayout;
        public RecyclerView.LayoutManager mLayoutManager;
        public FooterRefreshView footerRefreshView;
        public VideoRecyclerViewAdapter mVideoRecyclerViewAdapter;
        MyPage(String title, View pageView) {
            super(title, pageView);
        }

        @Override
        public void onRefreshStarted(View view) {
            mVideoPageManager.refresh(category);
        }


        /*SwipeRefreshLayout*/
        @Override
        public void onRefresh() {
            footerRefreshView.reset();
            mVideoPageManager.refresh(category);
        }


        /*BaseRecyclerView*/
        @Override
        public void onItemAttachListener(View view) {
            if(view == emptyView){
               /* if(DEBUG){
                    Log.d(LOG_TAG,"emptyView attach");
                }*/
                if(emptyView.isActive()) {
                    emptyView.setStateProcessing();
                }
            }

            if(view == footerRefreshView){
                if(footerRefreshView.getStatus() == FooterRefreshView.Status.CLICKABLE){
                    //baseRecyclerView.smoothScrollToPosition(newsRecyclerViewAdapter.getItemCount());
                    //footerRefreshView.requestFocus();
                    if(mVideoPageManager.isProcessing(category)){
                        return;
                    }
                    footerRefreshView.setStatus(FooterRefreshView.Status.REFRESHING);
                }
            }
        }

        @Override
        public void onItemDetachListener(View view) {
            if(view == emptyView){
                emptyView.setStateActive();
            }

            if(view == footerRefreshView){
                if(footerRefreshView.getStatus() != FooterRefreshView.Status.NO_MORE && footerRefreshView.getStatus() != FooterRefreshView.Status.REFRESHING){
                    footerRefreshView.setStatus(FooterRefreshView.Status.CLICKABLE);
                }
            }

        }

        @Override
        public void onItemClicked(BaseRecyclerViewAdapter.ViewHolder vh, Object item, int position) {

        }

        @Override
        public void onItemClicked(View view) {
            VideoRecyclerViewAdapter.ViewHolder viewHolder = (VideoRecyclerViewAdapter.ViewHolder) mBaseRecyclerView.getChildViewHolder(view);
            Uri videoUri = Uri.parse(viewHolder.videoUrl);
            startVideoPlayerActivity(videoUri);
        }

        /*EmptyView*/
        @Override
        public void onProcessing() {
            mVideoPageManager.firstFetch(category);
        }

        /*FooterRefreshView*/
        @Override
        public void onRefreshing() {
            mVideoPageManager.append(category);
        }
    }
}
