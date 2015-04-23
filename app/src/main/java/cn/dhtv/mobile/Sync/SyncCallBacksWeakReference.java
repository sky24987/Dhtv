package cn.dhtv.mobile.Sync;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Jack on 2015/4/22.
 */
public class SyncCallBacksWeakReference<R,T> extends WeakReference<R> implements SyncCallbacks<T> {
    protected SyncCallBacksWeakReference(R r) {
        super(r);
    }

    @Override
    public void onSync(ArrayList<T> list, int flag) {

    }

    @Override
    public void onError(int flag) {

    }
}
