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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import cn.dhtv.mobile.activity.ProgramDetailActivity;
import cn.dhtv.mobile.provider.MyContentProvider;
import cn.dhtv.mobile.ui.adapter.ItemViewDataSet;
import cn.dhtv.mobile.ui.adapter.ProgramListAdapter;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.model.AbsPageManager;
import cn.dhtv.mobile.model.ProgramPageManager;
import cn.dhtv.mobile.network.NetUtils;
import cn.dhtv.mobile.ui.adapter.ProgramRecyclerViewAdapter;
import cn.dhtv.mobile.ui.widget.EmptyView;
import cn.dhtv.mobile.ui.widget.FooterRefreshListView;
import cn.dhtv.mobile.ui.widget.FooterRefreshView;
import cn.dhtv.mobile.ui.widget.MySmartTabLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProgramFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProgramFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgramFragment extends SectionFragment implements BasePagerAdapter.PageFactory, AbsPageManager.CallBacks,android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>{
    private static final int LOADER_PROGRAM_CATEGORY = 1;

    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    public  final String title = "点播";
    private ViewPager mViewPager;
    private MySmartTabLayout mMySmartTabLayout;//private TabPageIndicator mTabPageIndicator;
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
        getLoaderManager().initLoader(LOADER_PROGRAM_CATEGORY, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mProgramPageManager = ((MyApplication)getActivity().getApplication()).getProgramPageManager();
        mImageLoader = NetUtils.getImageLoader(getActivity());

        View view =  inflater.inflate(R.layout.pager_with_tab, container, false);//View view =  inflater.inflate(R.layout.tab_pager, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mMySmartTabLayout = (MySmartTabLayout) view.findViewById(R.id.pager_title);//mTabPageIndicator =  (TabPageIndicator) view.findViewById(R.id.pager_title);
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
    public void onResume() {
        super.onResume();
        mProgramPageManager.setCallBacks(this);
//        mProgramPageManager.refresh();
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content").authority(MyContentProvider.AUTHORITY).appendPath(Contract.Category.TABLE_NAME).build();
        Log.d(LOG_TAG,uri.toString());
        String selection = Contract.Category.COLUMN_NAME_UP_ID+ " = ?";
        String[] selectionArgs = new String[]{""+ Data.tvChannelFatherCategory.getCatid()};
        return new CursorLoader(getActivity(),uri,null,selection,selectionArgs,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<Category> titles = (ArrayList<Category>) CategoryAccessor.toList(data);
        mProgramPageManager.change(titles);
        mPagerAdapter.notifyDataSetChanged();
        mMySmartTabLayout.setViewPager(mViewPager);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }




    @Override
    public void onFirstFetch(Category category, AbsPageManager.CallBackFlag flag) {

    }

    @Override
    public void onFirstFetchFails(Category category, AbsPageManager.CallBackFlag flag) {

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

        page.mSwipeRefreshLayout.setRefreshing(false);
        page.mProgramRecyclerViewAdapter.notifyDataSetChanged();
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
        ItemViewDataSet listViewDataList = mProgramPageManager.getList(category);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.page_recycle_view,null);
        MyPage page = new MyPage(category.getCatname(),view);
        page.category = category;
        page.mRecyclerView = (BaseRecyclerView) view.findViewById(R.id.recyclerView);
        page.mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_view);
        page.mSwipeRefreshLayout.setOnRefreshListener(page);

//        page.adapter = new ProgramListAdapter(listViewDataList,getActivity());
        page.mProgramRecyclerViewAdapter = new ProgramRecyclerViewAdapter(listViewDataList);
        page.mRecyclerView.setLayoutManager(page.layoutManager);
        page.mRecyclerView.setAdapter(page.mProgramRecyclerViewAdapter);
        page.mRecyclerView.setOnItemAttachDetachListener(page);
        page.emptyView = (EmptyView) inflater.inflate(R.layout.widget_empty_view,page.mRecyclerView,false);
        page.emptyView.setOnProcessingListener(page);
        page.emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmptyView emptyView = (EmptyView) v;
                if (emptyView.isIdle()) {
                    emptyView.setStateProcessing();
                }
            }
        });

        page.mProgramRecyclerViewAdapter.setEmptyView(new ProgramRecyclerViewAdapter.ViewHolder(page.emptyView, BaseRecyclerViewAdapter.ViewHolder.VIEW_TYPE_EMPTY));
        page.mProgramRecyclerViewAdapter.setOnItemClickListener(page);

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

    private void startProgram(Category category){
        Intent intent = new Intent(getActivity(), ProgramDetailActivity.class);
        intent.putExtra("program",category);
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

    private class MyPage extends BasePagerAdapter.Page implements FooterRefreshView.OnRefreshingListener,SwipeRefreshLayout.OnRefreshListener,EmptyView.OnProcessingListener,BaseRecyclerViewAdapter.OnItemClickListener,BaseRecyclerView.OnItemAttachDetachListener{
        public Category category;
        public BaseRecyclerView mRecyclerView;
        public EmptyView emptyView;
        public RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(),3, LinearLayoutManager.VERTICAL,false);
//        public ProgramListAdapter adapter;
        public ProgramRecyclerViewAdapter mProgramRecyclerViewAdapter;
        public SwipeRefreshLayout mSwipeRefreshLayout;

        MyPage(String title, View pageView) {
            super(title, pageView);
        }

        /*@Override
        public void onRefreshStarted(View view) {
            mProgramPageManager.refresh(category);
        }*/


        @Override
        public void onRefresh() {
            mProgramPageManager.refresh(category);
        }

        /*FooterRefreshView*/
        @Override
        public void onRefreshing() {
            mProgramPageManager.append(category);
        }

        /*EmptyView*/
        @Override
        public void onProcessing() {
            mProgramPageManager.refresh(category);
        }

        /*BaseRecyclerViewAdapter*/
        @Override
        public void onItemClicked(BaseRecyclerViewAdapter.ViewHolder vh, Object item, int position) {

        }

        @Override
        public void onItemClicked(View view) {
            ProgramRecyclerViewAdapter.ViewHolder viewHolder = (ProgramRecyclerViewAdapter.ViewHolder) mRecyclerView.getChildViewHolder(view);
            startProgram(viewHolder.category);
        }

        /*BaseRecyclerView*/
        @Override
        public void onItemAttachListener(View view) {
            if(view == emptyView){
               /* if(DEBUG){
                    Log.d(LOG_TAG,"emptyView attach");
                }*/
                emptyView.setStateProcessing();
            }
        }

        @Override
        public void onItemDetachListener(View view) {

        }
    }

}
