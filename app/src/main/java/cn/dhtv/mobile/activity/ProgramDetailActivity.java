package cn.dhtv.mobile.activity;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import cn.dhtv.mobile.R;
import cn.dhtv.mobile.fragment.TvFragment;

public class ProgramDetailActivity extends ActionBarActivity {
    TvFragment mVideoPlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_detail);
        FragmentManager fm = getSupportFragmentManager();
                mVideoPlayerFragment = (TvFragment)fm.findFragmentByTag("video player");
        if(mVideoPlayerFragment == null){
            mVideoPlayerFragment = TvFragment.newInstance();
            fm.beginTransaction().replace(R.id.container,mVideoPlayerFragment,"video player").commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_program_detail, menu);
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
