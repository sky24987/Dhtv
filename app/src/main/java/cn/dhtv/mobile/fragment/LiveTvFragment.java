package cn.dhtv.mobile.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

import cn.dhtv.android.adapter.BaseRecyclerViewAdapter;
import cn.dhtv.android.widget.BaseRecyclerView;
import cn.dhtv.mobile.R;
import cn.dhtv.mobile.ui.adapter.ChannelAdapter;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.Sync.DataSyncHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LiveTvFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiveTvFragment extends SectionFragment {
    public  final String title = "直播";

    private VideoView mVideoView;
    private BaseRecyclerView mBaseRecyclerView;
    private GridLayoutManager mLayoutManager;

    private Category tvRoot;
    private ArrayList<Category> tvs = new ArrayList<>();
    private DataState mDataState = new DataState();
    private ChannelAdapter mChannelAdapter = new ChannelAdapter(tvs,mDataState);




    public static LiveTvFragment newInstance() {
        LiveTvFragment fragment = new LiveTvFragment();

        return fragment;
    }

    public LiveTvFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_live_tv, container, false);
        initView(view);
        asyncRequestTv();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mVideoView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mVideoView.stopPlayback();
    }


    private void init(){
        tvRoot = new Category();
        tvRoot.setCatid(19);
        tvRoot.setCatname("电视");
        mChannelAdapter.setOnItemClickListener(mOnItemClickListener);
    }

    private void initView(View view){
        mVideoView = (VideoView) view.findViewById(R.id.video_view);
        mBaseRecyclerView = (BaseRecyclerView) view.findViewById(R.id.recyclerView);
        mLayoutManager = new GridLayoutManager(getActivity(),4,LinearLayoutManager.VERTICAL,false);
        mBaseRecyclerView.setLayoutManager(mLayoutManager);
        mBaseRecyclerView.setAdapter(mChannelAdapter);
    }

    private void asyncRequestTv(){
        DataSyncHelper.getInstance(getActivity()).SyncCategory(tvRoot,mCategorySyncCallBacks);
    }

    private void onTvGet(){
        if(mDataState.firstSelected == null && tvs.size()>0){
            mDataState.firstSelected = tvs.get(0);
            selectTv(mDataState.firstSelected);
        }
    }

    private void selectTv(Category category){
        if(mDataState.selected == null || mDataState.selected.getCatid() != category.getCatid()){
            mDataState.selected = category;
            mChannelAdapter.notifyDataSetChanged();
            mVideoView.setVideoPath(category.getLive().getM3u8());
            //mVideoView.setVideoPath("http://data.wztv.cn/tv/17/201504/26_1428401975.mp4");
        }
    }

    private DataSyncHelper.CategorySyncCallBacks mCategorySyncCallBacks = new DataSyncHelper.CategorySyncCallBacks() {
        @Override
        public void onSync(List<Category> list) {
            tvs.clear();
            tvs.addAll(list);
            onTvGet();
        }

        @Override
        public void onError(int flag) {

        }
    };

    private BaseRecyclerViewAdapter.OnItemClickListener mOnItemClickListener = new BaseRecyclerViewAdapter.OnItemClickListener() {
        @Override
        public void onItemClicked(BaseRecyclerViewAdapter.ViewHolder vh, Object item, int position) {

        }

        @Override
        public void onItemClicked(View view) {
            ChannelAdapter.ViewHolder viewHolder = (ChannelAdapter.ViewHolder) mBaseRecyclerView.getChildViewHolder(view);
            Category channel = (Category) viewHolder.item;
            selectTv(channel);
        }
    };

    @Override
    public String getTitle() {
        return title;
}

    public class DataState{
        public Category firstSelected;
        public Category selected;
    }

}
