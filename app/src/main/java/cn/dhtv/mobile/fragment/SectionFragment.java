package cn.dhtv.mobile.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;


public abstract class SectionFragment extends Fragment {

    protected final String title = "title unspecified";
    abstract public String getTitle();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((BaseCallbacks)activity).onSectionAttached(this);
    }

    public interface BaseCallbacks {
        void onSectionAttached(SectionFragment fragment);
    }
}
