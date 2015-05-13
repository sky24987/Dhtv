package cn.dhtv.mobile.activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import cn.dhtv.android.adapter.BaseRecyclerViewAdapter;
import cn.dhtv.android.view.RatioFrameLayout;
import cn.dhtv.android.widget.BaseRecyclerView;
import cn.dhtv.mobile.R;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.entity.TvOverview;
import cn.dhtv.mobile.fragment.LiveTvFragment;
import cn.dhtv.mobile.fragment.VideoPlayerFragment;
import cn.dhtv.mobile.network.TvClient;
import cn.dhtv.mobile.ui.adapter.TvListAdapter;
import cn.dhtv.mobile.ui.adapter.TvRecyclerViewAdapter;

public class TVListActivity extends ActionBarActivity {
    private BaseRecyclerView mBaseRecyclerView;
    private TvRecyclerViewAdapter mTvRecyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RatioFrameLayout mRatioFrameLayout;


    private ArrayList<TvOverview> mTvOverviews = new ArrayList<>();
    private StateDate mStateDate = new StateDate();

    private FragmentManager mFragmentManager;

    Category category;

    private TvRecyclerViewAdapter.OnItemClickListener mOnItemClickListener = new BaseRecyclerViewAdapter.OnItemClickListener() {
        @Override
        public void onItemClicked(BaseRecyclerViewAdapter.ViewHolder vh, Object item, int position) {

        }

        @Override
        public void onItemClicked(View view) {
            TvRecyclerViewAdapter.ViewHolder viewHolder = (TvRecyclerViewAdapter.ViewHolder) mBaseRecyclerView.getChildViewHolder(view);
            TvOverview tvOverview = viewHolder.tvOverview;
            selectTv(tvOverview);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_list);
        init();

        mLayoutManager = new LinearLayoutManager(this, LinearLayout.VERTICAL,false);
        mRatioFrameLayout = (RatioFrameLayout) findViewById(R.id.ratio_frame_layout);
        mTvRecyclerViewAdapter = new TvRecyclerViewAdapter(mTvOverviews,mStateDate);
        mTvRecyclerViewAdapter.setOnItemClickListener(mOnItemClickListener);
        mBaseRecyclerView = (BaseRecyclerView) findViewById(R.id.recyclerView);
        mBaseRecyclerView.setLayoutManager(mLayoutManager);
        mBaseRecyclerView.setAdapter(mTvRecyclerViewAdapter);


        asyncFetchTvs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tvlist, menu);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            }else {
//
//            }

            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            if(actionBar != null){
                actionBar.show();
            }
            mRatioFrameLayout.getLayoutParams().height = FrameLayout.LayoutParams.WRAP_CONTENT;
            mRatioFrameLayout.getLayoutParams().width = FrameLayout.LayoutParams.MATCH_PARENT;
        }else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
//            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            }else {
//                View decorView = getWindow().getDecorView();
//                // Hide the status bar.
//                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//                decorView.setSystemUiVisibility(uiOptions);
//
//                // Remember that you should never show the action bar if the
//                // status bar is hidden, so hide that too if necessary.
//
//            }
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            if(actionBar != null){
                actionBar.hide();
            }
            mRatioFrameLayout.getLayoutParams().height = FrameLayout.LayoutParams.MATCH_PARENT;
            mRatioFrameLayout.getLayoutParams().width = FrameLayout.LayoutParams.MATCH_PARENT;
        }
    }

    private void init(){
        Intent intent = getIntent();
        category = (Category) intent.getSerializableExtra("program");
        mFragmentManager = getFragmentManager();
    }

    private void asyncFetchTvs(){
        FetchTVListTask fetchTVListTask = new FetchTVListTask(category,0);
        fetchTVListTask.execute();
    }

    private void selectTv(TvOverview tvOverview){
        if(mStateDate.firstTv == null ){
            mStateDate.firstTv = tvOverview;
        }

        if(mStateDate.selectedTv != tvOverview){
            mStateDate.selectedTv = tvOverview;
            mTvRecyclerViewAdapter.notifyDataSetChanged();
            playTv(tvOverview);
        }
    }

    private void playTv(TvOverview tvOverview){
        VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment) mFragmentManager.findFragmentById(R.id.fragment_video);
        videoPlayerFragment.setVideoURI(Uri.parse(tvOverview.getTv_url()));
    }


    public class StateDate{
        public TvOverview firstTv;
        public TvOverview selectedTv;
    }

    private class FetchTVListTask extends AsyncTask<Object,Object,ArrayList<TvOverview>>{
        private static final int STATE_SUCCESS = 1;
        private static final int STATE_JSON_EXCEPTION = -1;
        private static final int STATE_IO_EXCEPTION = -2;


        Category category;
        int beginId;

        int state;

        public FetchTVListTask(Category category, int beginId) {
            this.category = category;
            this.beginId = beginId;
        }

        @Override
        protected ArrayList<TvOverview> doInBackground(Object[] params) {
            TvClient tvClient = new TvClient();
            ArrayList<TvOverview> tvOverviews = null;
            try {
               tvOverviews = tvClient.fetchTvs(category, beginId);
            }catch (JSONException e){
                state = STATE_JSON_EXCEPTION;
                return null;
            }catch (IOException e){
                state = STATE_IO_EXCEPTION;
                return null;
            }

            state = STATE_SUCCESS;
            return tvOverviews;
        }

        @Override
        protected void onPostExecute(ArrayList<TvOverview> tvOverviews) {
            if(state == STATE_SUCCESS){
                mTvOverviews.clear();
                mTvOverviews.addAll(tvOverviews);
                if(mTvOverviews.size() > 0) {
                    selectTv(mTvOverviews.get(0));
                }
            }
        }


    }
}
