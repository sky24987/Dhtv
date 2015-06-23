package cn.dhtv.mobile.activity.user;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;

import cn.dhtv.mobile.R;
import cn.dhtv.mobile.Singletons;
import cn.dhtv.mobile.entity.FactDetail;
import cn.dhtv.mobile.model.User;
import cn.dhtv.mobile.model.UserCenter;
import cn.dhtv.mobile.network.OkHttpUtils;
import cn.dhtv.mobile.network.message.FactDetailMessage;
import cn.dhtv.mobile.network.volleyRequest.FactDetailRequest;
import cn.dhtv.mobile.ui.widget.EmptyView;
import cn.dhtv.mobile.util.TextUtils;

public class FactDetailActivity extends ActionBarActivity {
    private long mMsgid;
    private EmptyView mEmptyView;
    private TextView mContentView;

    private FactDetail mFactDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup parent = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_fact_detail,null);
        mContentView = (TextView) parent.findViewById(R.id.content);
        Intent intent = getIntent();
        mMsgid = intent.getLongExtra("msgid",0);
        mEmptyView = (EmptyView) LayoutInflater.from(this).inflate(R.layout.widget_empty_view,parent,false);
        parent.addView(mEmptyView);
        mEmptyView.setOnProcessingListener(new EmptyView.OnProcessingListener() {
            @Override
            public void onProcessing() {
                User user = UserCenter.getInstance().retrieveUser(FactDetailActivity.this);
                String url = TextUtils.makeFactDetailUrl(user.getUid(), user.getToken(), mMsgid);
                FactDetailRequest request = new FactDetailRequest(com.android.volley.Request.Method.GET, url, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mEmptyView.setStateFail();
                    }
                }, new com.android.volley.Response.Listener<FactDetailMessage>() {
                    @Override
                    public void onResponse(FactDetailMessage response) {
                        if(response.getCode() != 200){
                            mEmptyView.setStateFail();
                            return;
                        }

                        mFactDetail = response.getFactDetail();
                        mContentView.setText(mFactDetail.getContent());
                        mEmptyView.setStateIdle();
                        mEmptyView.setVisibility(View.GONE);
                    }
                });

                Singletons.getRequestQueue().add(request);

            }
        });
        mEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmptyView.setStateProcessing();
            }
        });
        setContentView(parent);
        mEmptyView.setStateProcessing();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fact_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
