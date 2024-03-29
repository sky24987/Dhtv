package cn.dhtv.mobile.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import cn.dhtv.mobile.R;
import cn.dhtv.mobile.fragment.NavigationDrawerFragment;
import cn.dhtv.mobile.*;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;


public class WebViewActivity extends ActionBarActivity implements PlatformActionListener {
    private String url;
    private String title;
    private String summary;
    private String picUrl;

    private ViewGroup container;
    private String mContentTitle;
    private WebView mWebView;
    private WebChromeClient mWebChromeClient;
    private WebViewClient mWebViewClient;
    private ContentLoadingProgressBar mContentLoadingProgressBar;
    private ViewGroup mErrorView;
    private TextView mErrorMessage;
    private Button mRetryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        init();
        View view = getLayoutInflater().inflate(R.layout.activity_web_view,null);
        container = (ViewGroup) view;
        mContentLoadingProgressBar = (ContentLoadingProgressBar) view.findViewById(R.id.progress_bar);
        mErrorView = (ViewGroup) view.findViewById(R.id.error_view);
        mErrorMessage = (TextView) view.findViewById(R.id.error_message);
        mRetryButton = (Button) view.findViewById(R.id.button_retry);
        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl(url);
            }
        });
        mWebView = (WebView) view.findViewById(R.id.web_reader);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.setWebViewClient(mWebViewClient);
        Intent intent = getIntent();
        Uri uri = intent.getData();
        url = uri.toString();
        mContentTitle = intent.getStringExtra("title");
        title = intent.getStringExtra(Intent.EXTRA_TITLE);
        summary = intent.getStringExtra(Intent.EXTRA_TEXT);
        picUrl = intent.getStringExtra(Contract.INTENT_EXTRA_IMG_URL);
        mWebView.loadUrl(url);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(view);


    }

    private void init(){
        mWebChromeClient = new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

            }



        };
        mWebViewClient = new WebViewClient(){


            @Override
            public void onLoadResource(WebView view, String url) {

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mContentLoadingProgressBar.show();
                mErrorView.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mContentLoadingProgressBar.hide();
            }



            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                mWebView.loadUrl("about:blank");
                mContentLoadingProgressBar.hide();
                switch (errorCode){
                    case ERROR_HOST_LOOKUP:
                    case ERROR_IO:
                        showErrorView(getString(R.string.error_message_io),true);
                        break;
                    case ERROR_CONNECT:
                        showErrorView(getString(R.string.error_message_connect),true);
                        break;
                    case ERROR_FILE_NOT_FOUND:
                        showErrorView(getString(R.string.error_message_file_not_found),false);
                        break;
                    case ERROR_FILE:
                        break;
                    case ERROR_UNKNOWN:
                        showErrorView(getString(R.string.error_message_unknown),true);
                        break;
                }
            }


        };

    }

    private void showErrorView(String errorMessage,boolean canRetray){
        mErrorMessage.setText(errorMessage);
        if(canRetray){
            mRetryButton.setVisibility(View.VISIBLE);
        }else {
            mRetryButton.setVisibility(View.GONE);
        }

        mErrorView.setVisibility(View.VISIBLE);
    }

    private void hideErrorView(){
        mErrorView.setVisibility(View.GONE);
    }


    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web_view, menu);

        ActionBar actionBar = getSupportActionBar();
        if(mContentTitle != null) {
            actionBar.setTitle(mContentTitle);

        }
        return true;


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        container.removeAllViews();
        mWebView.loadUrl("about:blank");
        mWebView.clearCache(true);
        mWebView.clearHistory();
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

        if(id == R.id.action_share){
            showShare();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showShare() {

        ShareSDK.initSDK(this);

        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);

        // text是分享文本，所有平台都需要这个字段
        oks.setText(summary);

        oks.setImageUrl(picUrl);

        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(url);


        oks.show(this);
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {

    }

    @Override
    public void onCancel(Platform platform, int i) {

    }
}
