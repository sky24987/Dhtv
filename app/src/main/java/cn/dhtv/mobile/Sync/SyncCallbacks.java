package cn.dhtv.mobile.Sync;

import java.util.ArrayList;

/**
 * Created by Jack on 2015/4/22.
 */
public interface SyncCallbacks<T> {
    public static final int SYNC_SUCCESS = 1;
    public static final int SYNC_ERROR_NULL_DATA = -1;
    public static final int SYNC_ERROR_DATA_EXCEPTION = -2;
    public static final int SYNC_ERROR_IO_EXCEPTION = -3;

    void onSync(ArrayList<T> list,int flag);
    void onError(int flag);
}
