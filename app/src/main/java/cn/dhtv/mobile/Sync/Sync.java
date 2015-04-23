package cn.dhtv.mobile.Sync;

import java.util.concurrent.ExecutorService;

/**
 * Created by Jack on 2015/4/22.
 */
public class Sync {
    protected static final int MESSAGE_WHAT_SUCCESS = 1;
    protected static final int MESSAGE_WHAT_NULL = -1;
    protected static final int MESSAGE_WHAT_DATA_EXCEPTION = -2;
    protected static final int MESSAGE_WHAT_IO_EXCEPTION = -3;

    protected ExecutorService mExecutorService;

    public Sync(ExecutorService mExecutorService) {
        this.mExecutorService = mExecutorService;
    }
}
