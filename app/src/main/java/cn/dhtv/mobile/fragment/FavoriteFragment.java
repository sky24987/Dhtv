package cn.dhtv.mobile.fragment;


import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;

import cn.dhtv.android.adapter.BaseRecyclerViewAdapter;
import cn.dhtv.android.widget.BaseRecyclerView;
import cn.dhtv.mobile.R;
import cn.dhtv.mobile.Singletons;
import cn.dhtv.mobile.activity.LoginActivity;
import cn.dhtv.mobile.entity.Favorite;
import cn.dhtv.mobile.model.User;
import cn.dhtv.mobile.model.UserCenter;
import cn.dhtv.mobile.network.message.FavoriteMessage;
import cn.dhtv.mobile.network.volleyRequest.FavoriteRequest;
import cn.dhtv.mobile.ui.adapter.FactRecyclerViewAdapter;
import cn.dhtv.mobile.ui.adapter.FavoriteRecyclerViewAdapter;
import cn.dhtv.mobile.ui.widget.EmptyView;
import cn.dhtv.mobile.util.TextUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoriteFragment extends Fragment {
    private final static int REQUEST_CODE_LOGIN = 1;

    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private BaseRecyclerView mBaseRecyclerView;
    private FavoriteRecyclerViewAdapter mRecyclerViewAdapter;
    private EmptyView mEmptyView;

    private ArrayList<Favorite> mFavoriteArrayList = new ArrayList<>();

    public static FavoriteFragment newInstance() {
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        mBaseRecyclerView = (BaseRecyclerView) view.findViewById(R.id.recyclerView);
        mBaseRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        mRecyclerViewAdapter = new FavoriteRecyclerViewAdapter(mFavoriteArrayList);
        mEmptyView = (EmptyView) inflater.inflate(R.layout.widget_empty_view, mBaseRecyclerView, false);
        mRecyclerViewAdapter.setEmptyView(new FavoriteRecyclerViewAdapter.ViewHolder(mEmptyView, BaseRecyclerViewAdapter.ViewHolder.VIEW_TYPE_EMPTY));
        mBaseRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mBaseRecyclerView.setAdapter(mRecyclerViewAdapter);
        mEmptyView.setOnProcessingListener(new EmptyView.OnProcessingListener() {
            @Override
            public void onProcessing() {
                User user = UserCenter.getInstance().retrieveUser(getActivity());
                if (user == null) {
                    mEmptyView.setStateFail("登录后重试");
                    return;
                }

                String url = TextUtils.makeFavoriteUrl(user.getUid(), user.getToken(), 0, 0);
                FavoriteRequest favoriteRequest = new FavoriteRequest(Request.Method.GET, url, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mEmptyView.setStateFail();
                    }
                }, new Response.Listener<FavoriteMessage>() {
                    @Override
                    public void onResponse(FavoriteMessage response) {
                        if (response.getCode() != 200) {
                            mEmptyView.setStateIdle();
                            return;
                        }

                        ArrayList<Favorite> favorites = response.getFavoriteArrayList();
                        if (favorites.size() == 0) {
                            mEmptyView.setStateIdle();
                            return;
                        }

                        mFavoriteArrayList.addAll(favorites);
                        mEmptyView.setStateIdle();
                        mRecyclerViewAdapter.notifyDataSetChanged();
                    }
                });

                Singletons.getRequestQueue().add(favoriteRequest);

            }
        });

        mEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmptyView.setStateProcessing();
            }
        });

        User user = UserCenter.getInstance().retrieveUser(getActivity());
        if(user == null){
            toLoginActivity();

        }

        mEmptyView.setStateProcessing();

        return view;

    }

    private void toLoginActivity(){
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivityForResult(intent, REQUEST_CODE_LOGIN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_LOGIN){
            if(resultCode == Activity.RESULT_OK){
                if(mEmptyView.isFail()){
                    mEmptyView.setStateProcessing();
                }
            }
        }
    }


}
