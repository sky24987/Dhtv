package cn.dhtv.mobile.fragment.subFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;

import cn.dhtv.android.adapter.BaseRecyclerViewAdapter;
import cn.dhtv.android.widget.BaseRecyclerView;
import cn.dhtv.mobile.R;
import cn.dhtv.mobile.Singletons;
import cn.dhtv.mobile.entity.Comment;
import cn.dhtv.mobile.model.User;
import cn.dhtv.mobile.model.UserCenter;
import cn.dhtv.mobile.network.volleyRequest.CommentArrayListRequest;
import cn.dhtv.mobile.ui.adapter.CommentRecyclerViewAdapter;
import cn.dhtv.mobile.ui.widget.EmptyView;
import cn.dhtv.mobile.util.TextUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommentSubFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentSubFragment extends Fragment {
    private static final String ARG_COMMENT_TYPE = "COMMENT_TYPE";
    private String mCommentType;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BaseRecyclerView mBaseRecyclerView;
    private CommentRecyclerViewAdapter mCommentRecyclerViewAdapter;
    private EmptyView mEmptyView;

    private ArrayList<Comment> mCommentArrayList = new ArrayList<>();
    private RequestQueue mRequestQueue = Singletons.getRequestQueue();


    public static CommentSubFragment newInstance(String type) {
        CommentSubFragment fragment = new CommentSubFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COMMENT_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    public CommentSubFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCommentType = getArguments().getString(ARG_COMMENT_TYPE);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.page_recycle_view, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_view);
        mSwipeRefreshLayout.setEnabled(false);
        mBaseRecyclerView = (BaseRecyclerView) view.findViewById(R.id.recyclerView);
        mBaseRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        mCommentRecyclerViewAdapter = new CommentRecyclerViewAdapter(mCommentArrayList);
        mEmptyView = (EmptyView) inflater.inflate(R.layout.widget_empty_view,mBaseRecyclerView,false);
        mEmptyView.setOnProcessingListener(new EmptyView.OnProcessingListener() {
            @Override
            public void onProcessing() {
                User user = UserCenter.getInstance().retrieveUser(getActivity());
                if(user == null){
                    mEmptyView.setStateFail();
                    return;
                }
                String url = TextUtils.makeCommentUrl(user.getUid(),mCommentType);
                CommentArrayListRequest commentArrayListRequest = new CommentArrayListRequest(Request.Method.GET, url, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error instanceof ParseError){
                            mEmptyView.setStateIdle("暂无评论");
                            return;
                        }
                        mEmptyView.setStateFail();
                    }
                }, new Response.Listener<ArrayList<Comment>>() {
                    @Override
                    public void onResponse(ArrayList<Comment> response) {
                        if(response.size() == 0){
                            mEmptyView.setStateIdle();
                            return;
                        }

                        mCommentArrayList.addAll(response);
                        mEmptyView.setStateIdle();
                        mCommentRecyclerViewAdapter.notifyDataSetChanged();
                    }
                });

                mRequestQueue.add(commentArrayListRequest);
            }
        });
        mEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmptyView emptyView = (EmptyView) v;
                if(emptyView.isFail()) {
                    emptyView.setStateProcessing();
                }
            }
        });
        mCommentRecyclerViewAdapter.setEmptyView(new CommentRecyclerViewAdapter.ViewHolder(mEmptyView, BaseRecyclerViewAdapter.ViewHolder.VIEW_TYPE_EMPTY));
        mBaseRecyclerView.setAdapter(mCommentRecyclerViewAdapter);
        mEmptyView.setStateProcessing();
        return view;
    }


}
