package cn.dhtv.mobile;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import cn.dhtv.mobile.util.DataTest;
import cn.dhtv.mobile.util.NewsManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
public class NewsFragment extends SectionFragment {
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
    private PagerTitleStrip mPagerTitleStrip;
    private TabPageIndicator mTabPageIndicator;
    private NewsPagerAdapter mNewsPagerAdapter;
    private ArrayList<String> mNewsTypeList;
    private TypeReference<List<NewsOverview>> typeReference = new TypeReference<List<NewsOverview>>() { };

    private NewsManager newsManager = NewsManager.getInstance();
    private RequestQueue mNewsRequestQueue;
    private Handler mNewsHandler;
    private ObjectMapper mObjectMapper = new ObjectMapper();

    private NewsPagerAdapter.EventsListener newsPagerListener;
    private OnFragmentInteractionListener mListener;





    public NewsFragment() {
        //mObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

        mNewsHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                JSONObject jsonObject = (JSONObject) msg.obj;
                ArrayList<NewsOverview> newsList = null;
                try {
                    newsList = mObjectMapper.readValue(jsonObject.getJSONObject("data").getJSONArray("list").toString(),new TypeReference<List<NewsOverview>>(){});
                    Log.d("NewsFragment",newsList.toString());
                    newsManager.appendFront(msg.arg1,newsList);
                    mNewsPagerAdapter.notifyNewsListChange(newsManager.getCat(1));

                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("NewsFragment",e.getMessage());
                }



                Log.d("NewsFragment",((JSONObject)msg.obj).toString());
            }
        };

        newsPagerListener = new NewsPagerAdapter.EventsListener() {
            @Override
            public void onPullDown(NewsCat cat) {

            }

            @Override
            public void onPullUp(NewsCat cat) {

            }
        };

    }

    public void updateNews(final NewsCat cat){
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Message msg = new Message();
                //Bundle data = new Bundle();
                //data.put
                msg.obj = response;
                msg.arg1 = 260;
                mNewsHandler.sendMessage(msg);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };
        String requestString = NewsManager.makeRequestURL(cat,1);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,requestString,null,responseListener,errorListener);
        mNewsRequestQueue.add(jsonObjectRequest);
    }

    /*public void appendNewsToEnd(NewsCat cat){
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };


    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNewsRequestQueue = Volley.newRequestQueue(getActivity());
        mNewsRequestQueue.start();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mNewsRequestQueue.stop();
        mNewsRequestQueue = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_news, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager_news);
        //mPagerTitleStrip = (PagerTitleStrip) view.findViewById(R.id.pager_tab_news);
        mTabPageIndicator =  (TabPageIndicator) view.findViewById(R.id.news_category);
        view.findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNews(newsManager.getCat(1));
            }
        });
        mNewsPagerAdapter = new NewsPagerAdapter(getActivity(), newsPagerListener);
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
