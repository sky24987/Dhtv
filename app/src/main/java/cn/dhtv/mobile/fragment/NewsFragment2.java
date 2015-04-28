package cn.dhtv.mobile.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.dhtv.android.adapter.BasePagerAdapter;
import cn.dhtv.android.adapter.BaseRecyclerViewAdapter;
import cn.dhtv.android.widget.BaseRecyclerView;
import cn.dhtv.mobile.Database.CategoryAccessor;
import cn.dhtv.mobile.Database.Contract;
import cn.dhtv.mobile.MyApplication;
import cn.dhtv.mobile.R;
import cn.dhtv.mobile.activity.WebViewActivity;
import cn.dhtv.mobile.ui.adapter.ItemViewDataSet;
import cn.dhtv.mobile.ui.adapter.NewsRecyclerViewAdapter;
import cn.dhtv.mobile.entity.Block;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.model.AbsPageManager;
import cn.dhtv.mobile.model.NewsListCollector2;
import cn.dhtv.mobile.model.NewsPageManager;
import cn.dhtv.mobile.network.NetUtils;
import cn.dhtv.mobile.Data;
import cn.dhtv.mobile.ui.widget.EmptyView;

import cn.dhtv.mobile.ui.widget.FooterRefreshView;
import cn.dhtv.mobile.ui.widget.ImagePagerView2;
import cn.dhtv.mobile.ui.widget.MySmartTabLayout;
import cn.dhtv.mobile.provider.MyContentProvider;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment2 extends SectionFragment implements BasePagerAdapter.PageFactory, AbsPageManager.CallBacks, android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int LOADER_NEWS_CATEGORY = 1;

    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public  final String title = "新闻";

    private final Category newsFather = Data.newsFatherCategory;

    private ViewPager mViewPager;
    private MySmartTabLayout mMySmartTabLayout;//    private TabPageIndicator mTabPageIndicator;


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
     * @return A new instance of fragment NewsFragment.
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
        getLoaderManager().initLoader(LOADER_NEWS_CATEGORY,null,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mNewsPageManager = ((MyApplication)getActivity().getApplication()).getNewsPageManager();
        mImageLoader = NetUtils.getImageLoader(getActivity());

        View view = inflater.inflate(R.layout.pager_with_tab, container, false);//View view =  inflater.inflate(R.layout.tab_pager, container, false);

        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mMySmartTabLayout = (MySmartTabLayout) view.findViewById(R.id.pager_title);//        mTabPageIndicator =  (TabPageIndicator) view.findViewById(R.id.pager_title);

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
        mMySmartTabLayout.setViewPager(mViewPager);//        mTabPageIndicator.setViewPager(mViewPager);

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
        /*if(DEBUG){
            Log.d(LOG_TAG,"getActivity null? "+(getActivity() == null));
        }*/
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
    public void onResume() {
        super.onResume();
        mNewsPageManager.setCallBacks(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mNewsPageManager.setCallBacks(null);
    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content").authority(MyContentProvider.AUTHORITY).appendPath(Contract.Category.TABLE_NAME).build();
        Log.d(LOG_TAG,uri.toString());
        String selection = Contract.Category.COLUMN_NAME_UP_ID+ " = ?";
        String[] selectionArgs = new String[]{""+newsFather.getCatid()};
        return new CursorLoader(getActivity(),uri,null,selection,selectionArgs,null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        ArrayList<Category> titles = (ArrayList<Category>) CategoryAccessor.toList(data);
        mNewsPageManager.change(titles);
        mPagerAdapter.notifyDataSetChanged();
        mMySmartTabLayout.setViewPager(mViewPager);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    @Override
    public void onFirstFetch(Category category, AbsPageManager.CallBackFlag flag) {
        MyPage myPage = null;
        for(BasePagerAdapter.Page page : mPageHolder.getPages()) {
            MyPage page1 = (MyPage) page;
            if(page1.category == category){
                myPage = page1;
                break;
            }
        }

        if(myPage == null){
            return;
        }


        myPage.emptyView.setStateIdle();
        myPage.newsRecyclerViewAdapter.notifyDataSetChanged();
        myPage.imagePagerView.getViewPager().getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onFirstFetchFails(Category category, AbsPageManager.CallBackFlag flag) {

    }

    @Override
    public void onRefresh(Category category, AbsPageManager.CallBackFlag flag) {
        MyPage page = (MyPage) mPageHolder.get(category.getCatname());
        if(page == null){
            return;
        }

        page.newsRecyclerViewAdapter.notifyDataSetChanged();
        page.imagePagerView.getViewPager().getAdapter().notifyDataSetChanged();
        page.mSwipeRefreshLayout.setRefreshing(false);
//        page.listAdapter.notifyDataSetChanged();
//        page.mPullToRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onAppend(Category category, AbsPageManager.CallBackFlag flag) {
        /*if(DEBUG){
            Log.d(LOG_TAG,"onAppend:"+category.getCatname());
        }*/
        MyPage page = (MyPage) mPageHolder.get(category.getCatname());
        if(page == null){
            return;
        }
       /* if(DEBUG){
            Log.d(LOG_TAG,"page found");
        }*/

        page.newsRecyclerViewAdapter.notifyDataSetChanged();
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
        //page.mPullToRefreshLayout.setRefreshing(false);
        Toast.makeText(getActivity(), "获取新闻失败...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAppendFails(Category category, AbsPageManager.CallBackFlag flag) {
        MyPage page = (MyPage) mPageHolder.get(category.getCatname());
        if(page == null){
            return;
        }

//        page.listView.setRefreshFooterStatus(cn.dhtv.android.widget.FooterRefreshListView.RefreshFooterStatus.FORCE_CLICK_STATE);
        Toast.makeText(getActivity(), "添加新闻失败...", Toast.LENGTH_SHORT).show();
    }

    private void startWebActivity(String url){
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.setAction("android.intent.action.VIEW");
        intent.setDataAndType(Uri.parse(url),"text/html");
        getActivity().startActivity(intent);
    }




    @Override
    public int pageCount() {
        return mNewsPageManager.getPageCount();
    }

    @Override
    public BasePagerAdapter.Page generatePage(int position) {
        final Category category = mNewsPageManager.getCategory(position);
        ItemViewDataSet itemViewDataSet = mNewsPageManager.getList(category);


        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.page_recycle_view,null);
        MyPage page = new MyPage(category.getCatname(),view);
        page.category = category;
        page.blocks = ((NewsListCollector2)mNewsPageManager.getList(category)).getBlockArrayList();


//        page.mPullToRefreshLayout = (PullToRefreshLayout)view.findViewById(R.id.refresh_view);
        page.mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_view);
        page.mSwipeRefreshLayout.setOnRefreshListener(page);
        page.baseRecyclerView = (BaseRecyclerView) view.findViewById(R.id.recyclerView);
//        page.baseRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getResources().getDrawable(R.drawable.shape_divider_line),false,false));

        page.layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        page.newsRecyclerViewAdapter = new NewsRecyclerViewAdapter(itemViewDataSet);
        page.baseRecyclerView.setLayoutManager(page.layoutManager);
        page.baseRecyclerView.setAdapter(page.newsRecyclerViewAdapter);
        page.baseRecyclerView.setOnItemAttachDetachListener(page);
        page.emptyView = (EmptyView) inflater.inflate(R.layout.widget_empty_view,page.baseRecyclerView,false);
        page.emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmptyView emptyView = (EmptyView) v;
                if(emptyView.isIdle()) {
                    emptyView.setStateProcessing();
                }
            }
        });
        page.emptyView.setOnProcessingListener(page);
        page.footerRefreshView = (FooterRefreshView) inflater.inflate(R.layout.widget_refresh_footer,page.baseRecyclerView,false);
        page.footerRefreshView.setRefreshingListener(page);
        page.imagePagerView = (ImagePagerView2) inflater.inflate(R.layout.widget_pager_image,null);
        page.imagePagerView.setPageFactory(page);
        NewsRecyclerViewAdapter.ViewHolder headerViewHolder = new NewsRecyclerViewAdapter.ViewHolder(page.imagePagerView, BaseRecyclerViewAdapter.ViewHolder.VIEW_TYPE_HEADER);
        headerViewHolder.setIsRecyclable(false);
        page.newsRecyclerViewAdapter.addHeaderView(headerViewHolder);
        page.newsRecyclerViewAdapter.setEmptyView(new NewsRecyclerViewAdapter.ViewHolder(page.emptyView, BaseRecyclerViewAdapter.ViewHolder.VIEW_TYPE_EMPTY));
        page.newsRecyclerViewAdapter.addFooterView(new NewsRecyclerViewAdapter.ViewHolder(page.footerRefreshView, BaseRecyclerViewAdapter.ViewHolder.VIEW_TYPE_FOOTER));
        page.newsRecyclerViewAdapter.setOnItemClickListener(page);







//        page.listView = (FooterRefreshListView) view.findViewById(R.id.news_list);
//        page.listView.setFooterRefreshListener(page);
//        page.listAdapter = new NewsListAdapter(category, listViewDataList, mImageLoader, getActivity());

//        ActionBarPullToRefresh.from(getActivity()).theseChildrenArePullable(page.baseRecyclerView).listener(page).setup(page.mPullToRefreshLayout);
//        page.listView.setAdapter(page.listAdapter);

//        page.listView.addHeaderView(page.imagePagerView);
        return page;
    }

    @Override
    public String getPageTitle(int position) {
        return mNewsPageManager.getCategory(position).getCatname();
    }

    @Override
    public int getPagePosition(BasePagerAdapter.Page page) {
        MyPage myPage = (MyPage) page;
        Category category = myPage.category;
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

    private class MyPage extends BasePagerAdapter.Page implements OnRefreshListener,FooterRefreshView.OnRefreshingListener,BasePagerAdapter.PageFactory,BaseRecyclerViewAdapter.OnItemClickListener,BaseRecyclerView.OnItemAttachDetachListener,SwipeRefreshLayout.OnRefreshListener,EmptyView.OnProcessingListener{
        public Category category;

        public ArrayList<Block> blocks;

        public SwipeRefreshLayout mSwipeRefreshLayout;
        public PullToRefreshLayout mPullToRefreshLayout;
        //        public FooterRefreshListView listView;
        public BaseRecyclerView baseRecyclerView;
        public RecyclerView.LayoutManager layoutManager;
        public ImagePagerView2 imagePagerView;
        public EmptyView emptyView;
        //        public BaseAdapter listAdapter;
        public FooterRefreshView footerRefreshView;
        public NewsRecyclerViewAdapter newsRecyclerViewAdapter;
        MyPage(String title, View pageView) {
            super(title, pageView);
        }

        @Override
        public void onRefreshStarted(View view) {
            mNewsPageManager.refresh(category);
        }


        /*FooterRefreshView*/
        @Override
        public void onRefreshing() {
            mNewsPageManager.append(category);
//            mNewsPageManager.firstFetch(category);
        }

        /*SwipeRefreshLayout*/
        @Override
        public void onRefresh() {

            mNewsPageManager.refresh(category);
            //mSwipeRefreshLayout.setRefreshing(true);
        }

        /*EmptyView*/
        @Override
        public void onProcessing() {
            mNewsPageManager.firstFetch(category);
        }

        @Override
        public int pageCount() {
            return blocks.size();
        }

        @Override
        public BasePagerAdapter.Page generatePage(int position) {
            Block block = blocks.get(position);
            NetworkImageView netImageView = new NetworkImageView(getActivity());
            netImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            netImageView.setDefaultImageResId(R.drawable.default_image);
            netImageView.setImageUrl(block.getPic(), mImageLoader);
            netImageView.setTag(block.getUrl());
            netImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startWebActivity((String) v.getTag());
                }
            });
            BasePagerAdapter.Page page = new BasePagerAdapter.Page("",netImageView);
            return page;
        }

        @Override
        public String getPageTitle(int position) {
            Block block = blocks.get(position);
            return block.getTitle();
        }

        @Override
        public int getPagePosition(BasePagerAdapter.Page page) {
            return 0;
        }



        @Override
        public void onItemClicked(BaseRecyclerViewAdapter.ViewHolder vh, Object item, int position) {

        }

        @Override
        public void onItemClicked(View view) {
            NewsRecyclerViewAdapter.ViewHolder viewHolder = (NewsRecyclerViewAdapter.ViewHolder) baseRecyclerView.getChildViewHolder(view);


            startWebActivity(viewHolder.url);
            /*Intent intent = new Intent(getActivity(), WebViewActivity.class);
            intent.setAction("android.intent.action.VIEW");
            intent.setDataAndType(Uri.parse(viewHolder.url),"text/html");
            getActivity().startActivity(intent);*/
        }

        @Override
        public void onItemAttachListener(View view) {
            if(view == imagePagerView){
                return;
            }

            if(view == emptyView){
               /* if(DEBUG){
                    Log.d(LOG_TAG,"emptyView attach");
                }*/
                emptyView.setStateProcessing();
            }

            if(view == footerRefreshView){
                if(footerRefreshView.getStatus() == FooterRefreshView.Status.CLICKABLE){
                    //baseRecyclerView.smoothScrollToPosition(newsRecyclerViewAdapter.getItemCount());
                    footerRefreshView.setStatus(FooterRefreshView.Status.REFRESHING);
                }
            }
        }

        @Override
        public void onItemDetachListener(View view) {
            if(view == imagePagerView){
                emptyView.isIdle();
            }


            if(view == emptyView){
                /*if(DEBUG){
                    Log.d(LOG_TAG,"emptyView detach");
                }*/
                emptyView.setStateIdle();
            }

            if(view == footerRefreshView){
                if(footerRefreshView.getStatus() != FooterRefreshView.Status.NO_MORE && footerRefreshView.getStatus() != FooterRefreshView.Status.REFRESHING){
                    footerRefreshView.setStatus(FooterRefreshView.Status.CLICKABLE);
                }
            }
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
