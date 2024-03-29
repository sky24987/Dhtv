package cn.dhtv.mobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import cn.dhtv.mobile.Data;
import cn.dhtv.mobile.fragment.AudioFragment;
import cn.dhtv.mobile.fragment.LiveTvFragment;
import cn.dhtv.mobile.fragment.NavigationDrawerFragment;
import cn.dhtv.mobile.R;

import cn.dhtv.mobile.fragment.NewsFragment;
import cn.dhtv.mobile.fragment.ProgramFragment;
import cn.dhtv.mobile.fragment.SectionFragment;
import cn.dhtv.mobile.fragment.VideoFragment;


public class HomeActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,SectionFragment.BaseCallbacks{
    private final String LOG_TAG = getClass().getName();

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen titleView. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private String mSectionTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "create activety");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        /*fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();*/
        if(position == 0){
            fragmentManager.beginTransaction().replace(R.id.container, NewsFragment.newInstance(null, null)).commit();
        }else if(position == 1){
            fragmentManager.beginTransaction().replace(R.id.container, VideoFragment.newInstance(0)).commit();
        }else if(position == 2){
            fragmentManager.beginTransaction().replace(R.id.container, ProgramFragment.newInstance()).commit();
        }else if(position == 3){
            fragmentManager.beginTransaction().replace(R.id.container, AudioFragment.newInstance()).commit();
        }else if(position == 4){
            fragmentManager.beginTransaction().replace(R.id.container, LiveTvFragment.newInstance()).commit();
        }else if(position == 5){
            fragmentManager.beginTransaction().replace(R.id.container, VideoFragment.newInstance(Data.weiVideoId)).commit();
        }
        else{
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                    .commit();
        }
    }

    public void onSectionAttached(SectionFragment section) {
        if(section == null){
            return;
        }
//        mTitle = section.getTitle();
        mSectionTitle = section.getTitle();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mSectionTitle);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.home, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_user){
            Intent intent = new Intent(this,UserCenterActivity.class);
            startActivity(intent);
            return true;
        }



        if (id == R.id.action_settings) {

            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    private void startLoginActivity(){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((HomeActivity) activity).onSectionAttached(
                    null);
        }
    }

}
