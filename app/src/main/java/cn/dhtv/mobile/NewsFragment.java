package cn.dhtv.mobile;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.dhtv.mobile.adapter.NewsListAdapter;
import cn.dhtv.mobile.adapter.NewsPagerAdapter;
import cn.dhtv.mobile.entity.NewsCat;
import cn.dhtv.mobile.entity.NewsOverview;
import cn.dhtv.mobile.network.BitmapCache;
import cn.dhtv.mobile.util.DataTest;
import cn.dhtv.mobile.util.NewsDataManager;
import cn.dhtv.mobile.util.NewsManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends SectionFragment implements NewsService.CallBacks, ServiceConnection{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String LOG_TAG = "NewsFragment";
    public  final String title = "新闻";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private ViewPager mViewPager;
    private TabPageIndicator mTabPageIndicator;
    private NewsPagerAdapter mNewsPagerAdapter;
    private TypeReference<List<NewsOverview>> typeReference = new TypeReference<List<NewsOverview>>() { };

    private NewsDataManager mNewsDataManager = NewsDataManager.getInstance();
    private RequestQueue mNewsRequestQueue;
    private ImageLoader mImageLoader;
    private ObjectMapper mObjectMapper = new ObjectMapper();
    private NewsService.LocalBinder newsService;

    private NewsPagerAdapter.EventsListener newsPagerListener;
    private OnFragmentInteractionListener mListener;

    public NewsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initField();


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mNewsRequestQueue.start();
        Intent bindIntent = new Intent(getActivity(),NewsService.class);
        getActivity().bindService(bindIntent,this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        mNewsRequestQueue.stop();
        mNewsRequestQueue.cancelAll(getActivity());
        if(newsService != null){
            newsService.setCallbacks(null);
            getActivity().unbindService(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_news, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager_news);
        mViewPager.setEnabled(false);
        mTabPageIndicator =  (TabPageIndicator) view.findViewById(R.id.news_category);
        mTabPageIndicator.setEnabled(false);
        mNewsPagerAdapter = new NewsPagerAdapter(getActivity(), newsPagerListener,mImageLoader);
        mViewPager.setAdapter(mNewsPagerAdapter);
        initPagerIndicator();
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
    public void onNewsUpdate(int flag) {
        Log.d(LOG_TAG,"onNewsUpdate");
        if(mNewsDataManager.isUpdated()){
            Log.d(LOG_TAG,"mNewsPagerAdapter.notifyRefreshNews()");
            mNewsPagerAdapter.notifyRefreshNews();
            mNewsDataManager.setUpdated(false);//更新新闻页面后将背后新闻数据设置为非更新状态
        }else {
            mNewsPagerAdapter.setRefreshState(false);
            Toast.makeText(getActivity(),"没有更新的内容",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNewsUpdageFails(int flag) {
        switch (flag){
            case NewsService.REQUEST_FAIL_PROCESSING:

                mNewsPagerAdapter.setRefreshState(false);
                Toast.makeText(getActivity(),"正在获取新闻...",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onNewsAppend(NewsCat cat, Boolean hasMore, int flag) {

    }

    @Override
    public void onNewsAppendFails(NewsCat cat, int flag) {

    }

    private void initField(){

        newsPagerListener = new NewsPagerAdapter.EventsListener() {
            @Override
            public void onPullDown(NewsCat cat) {

            }

            @Override
            public void onPullUp(NewsCat cat) {

            }

            @Override
            public void onRefresh(NewsCat cat) {
                if(newsService != null){
                    newsService.updateNews();
                }
            }
        };

        mNewsRequestQueue = Volley.newRequestQueue(getActivity());
        mImageLoader =  new ImageLoader(mNewsRequestQueue,new BitmapCache());
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        newsService = (NewsService.LocalBinder)service;
        newsService.setCallbacks(this);
        mViewPager.setEnabled(true);
        mTabPageIndicator.setEnabled(true);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        newsService = null;
        mViewPager.setEnabled(false);
        mTabPageIndicator.setEnabled(false);
    }


    private void initFunction(){

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsFragment newInstance(String param1, String param2) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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

    private void initPagerIndicator(){
        mTabPageIndicator.setViewPager(mViewPager);
    }

    @Override
    public String getTitle() {
        return title;
    }
}
