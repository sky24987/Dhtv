package cn.dhtv.mobile;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



public abstract class SectionFragment extends Fragment {

    private final String title = "title unspecified";
    abstract public String getTitle();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((BaseCallbacks)activity).onSectionAttached(this);
    }

    interface BaseCallbacks {
        void onSectionAttached(SectionFragment fragment);
    }
}
