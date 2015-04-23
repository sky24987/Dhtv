package cn.dhtv.mobile.Sync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jack on 2015/4/14.
 */
public class SyncHelperFactory {
    public static SyncHelperFactory instance;

    private ExecutorService mExecutorService = Executors.newFixedThreadPool(3);


    public static SyncHelperFactory getInstance(){
        if(instance == null){
            instance = new SyncHelperFactory();
        }
        return instance;
    }

    public ArticleSyncHelper newArticleSyncHelper(){
        return new ArticleSyncHelper(mExecutorService);
    }

    public BlockSyncHelper newBlockSyncHelper(){
        return new BlockSyncHelper(mExecutorService);
    }
}
