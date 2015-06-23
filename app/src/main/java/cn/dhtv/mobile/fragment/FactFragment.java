package cn.dhtv.mobile.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Debug;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import cn.dhtv.android.adapter.BaseRecyclerViewAdapter;
import cn.dhtv.android.widget.BaseRecyclerView;
import cn.dhtv.mobile.R;
import cn.dhtv.mobile.Singletons;
import cn.dhtv.mobile.activity.LoginActivity;
import cn.dhtv.mobile.activity.user.FactDetailActivity;
import cn.dhtv.mobile.entity.Fact;
import cn.dhtv.mobile.model.User;
import cn.dhtv.mobile.model.UserCenter;
import cn.dhtv.mobile.network.message.FactMessage;
import cn.dhtv.mobile.network.volleyRequest.FactRequest;
import cn.dhtv.mobile.ui.adapter.CommentRecyclerViewAdapter;
import cn.dhtv.mobile.ui.adapter.FactRecyclerViewAdapter;
import cn.dhtv.mobile.ui.widget.EmptyView;
import cn.dhtv.mobile.util.TextUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FactFragment extends Fragment {
    private final static int REQUEST_CODE_LOGIN = 1;

    private final String LOG_TAG = getClass().getSimpleName();
    private final boolean DEBUG = true;

    private BaseRecyclerView mBaseRecyclerView;
    private FactRecyclerViewAdapter mRecyclerViewAdapter;
    private EmptyView mEmptyView;

    private ArrayList<Fact> mFactArrayList = new ArrayList<>();


    public static FactFragment newInstance() {
        FactFragment fragment = new FactFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public FactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    public void toFactDetailActivity(long msgid){
        Intent intent = new Intent(getActivity(), FactDetailActivity.class);
        intent.putExtra("msgid",msgid);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fact, container, false);
        mBaseRecyclerView = (BaseRecyclerView) view.findViewById(R.id.base_recycler_view);
        mBaseRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        mRecyclerViewAdapter = new FactRecyclerViewAdapter(mFactArrayList);
        mEmptyView = (EmptyView) inflater.inflate(R.layout.widget_empty_view, mBaseRecyclerView, false);
        mRecyclerViewAdapter.setEmptyView(new FactRecyclerViewAdapter.ViewHolder(mEmptyView, BaseRecyclerViewAdapter.ViewHolder.VIEW_TYPE_EMPTY));
        mBaseRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mBaseRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerViewAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(BaseRecyclerViewAdapter.ViewHolder vh, Object item, int position) {

            }

            @Override
            public void onItemClicked(View view) {
                FactRecyclerViewAdapter.ViewHolder viewHolder = (FactRecyclerViewAdapter.ViewHolder) mBaseRecyclerView.getChildViewHolder(view);
                Fact fact = viewHolder.fact;
                toFactDetailActivity(fact.getMsgid());
            }
        });
        mEmptyView.setOnProcessingListener(new EmptyView.OnProcessingListener() {
            @Override
            public void onProcessing() {
                User user = UserCenter.getInstance().retrieveUser(getActivity());
                if (user == null) {
                    mEmptyView.setStateFail();
                    return;
                }

                String url = TextUtils.makeFactUrl(user.getUid(), user.getToken(), 0, 0);
                FactRequest factRequest = new FactRequest(Request.Method.GET, url, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (DEBUG) {
                            Log.d(LOG_TAG, error.toString());
                        }
                        mEmptyView.setStateFail();
                    }
                }, new Response.Listener<FactMessage>() {
                    @Override
                    public void onResponse(FactMessage response) {


                        if (response.getCode() != 200) {
                            mEmptyView.setStateIdle();
                            return;
                        }

                        ArrayList<Fact> facts = response.getFactArrayList();
                        if (facts.size() == 0) {
                            mEmptyView.setStateIdle();
                            return;
                        }

                        mEmptyView.setStateIdle();
                        mFactArrayList.addAll(facts);
                        mRecyclerViewAdapter.notifyDataSetChanged();
                    }
                });

                Singletons.getRequestQueue().add(factRequest);
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
        if(DEBUG){
            Log.d(LOG_TAG,"onActivityResult");
        }
        if(requestCode == REQUEST_CODE_LOGIN){
            if(resultCode == Activity.RESULT_OK){
                if(mEmptyView.isFail()){
                    mEmptyView.setStateProcessing();
                }
            }
        }
    }

}
