package cn.dhtv.mobile.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import cn.dhtv.mobile.R;


public class WebViewActivity extends ActionBarActivity {
    private String mContentTitle;
    private WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.activity_web_view,null);
        mWebView = (WebView) view.findViewById(R.id.web_reader);
        mWebView.getSettings().setJavaScriptEnabled(true);
        Intent intent = getIntent();
        Uri uri = intent.getData();
        mContentTitle = intent.getStringExtra("title");
        mWebView.loadUrl(uri.toString());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(view);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*getMenuInflater().inflate(R.menu.menu_web_view, menu);*/
        ActionBar actionBar = getSupportActionBar();
        if(mContentTitle != null) {
            actionBar.setTitle(mContentTitle);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
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

        if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
