package cn.dhtv.mobile.Sync;

import android.os.Handler;
import android.os.Message;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import cn.dhtv.mobile.Database.BlockAccessor;
import cn.dhtv.mobile.entity.Block;
import cn.dhtv.mobile.entity.Category;
import cn.dhtv.mobile.network.BlockClient;

/**
 * Created by Jack on 2015/4/22.
 */
public class BlockSyncHelper extends Sync{
    private BlockAccessor mBlockAccessor = new BlockAccessor();
    private BlockClient mBlockClient = new BlockClient();
    private Object mTag = new Object();




    public BlockSyncHelper(ExecutorService mExecutorService) {
        super(mExecutorService);
    }
    
    public void syncFromNet(Category category,SyncCallbacks<Block> blockSyncCallbacks){
        mExecutorService.submit(new SyncFromNetTask(category, blockSyncCallbacks));
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            MessageObject obj = (MessageObject) msg.obj;
            SyncCallbacks<Block> blockSyncCallbacks = (SyncCallbacks<Block>) obj.obj1;
            switch (msg.what){
                case MESSAGE_WHAT_SUCCESS:
                    ArrayList<Block> list = (ArrayList<Block>) obj.list;
                    if(blockSyncCallbacks instanceof SyncCallBacksWeakReference){
                        blockSyncCallbacks = ((SyncCallBacksWeakReference<SyncCallbacks<Block>,Block>)blockSyncCallbacks).get();
                    }
                    if(blockSyncCallbacks != null){
                        blockSyncCallbacks.onSync(list,SyncCallbacks.SYNC_SUCCESS);
                    }
                    break;
                case MESSAGE_WHAT_NULL:
                    if(blockSyncCallbacks instanceof SyncCallBacksWeakReference){
                        blockSyncCallbacks = ((SyncCallBacksWeakReference<SyncCallbacks<Block>,Block>)blockSyncCallbacks).get();
                    }
                    if(blockSyncCallbacks != null){
                        blockSyncCallbacks.onError(SyncCallbacks.SYNC_ERROR_NULL_DATA);
                    }
                    break;
                case MESSAGE_WHAT_DATA_EXCEPTION:
                    if(blockSyncCallbacks instanceof SyncCallBacksWeakReference){
                        blockSyncCallbacks = ((SyncCallBacksWeakReference<SyncCallbacks<Block>,Block>)blockSyncCallbacks).get();
                    }
                    if(blockSyncCallbacks != null){
                        blockSyncCallbacks.onError(SyncCallbacks.SYNC_ERROR_DATA_EXCEPTION);
                    }
                    break;
                case MESSAGE_WHAT_IO_EXCEPTION:
                    if(blockSyncCallbacks instanceof SyncCallBacksWeakReference){
                        blockSyncCallbacks = ((SyncCallBacksWeakReference<SyncCallbacks<Block>,Block>)blockSyncCallbacks).get();
                    }
                    if(blockSyncCallbacks != null){
                        blockSyncCallbacks.onError(SyncCallbacks.SYNC_ERROR_IO_EXCEPTION);
                    }
                    break;

            }
        }
    };


    
    private class SyncFromNetTask implements Runnable{
        private Category mCategory;
        private SyncCallbacks<Block> mBlockSyncCallbacks;

        private SyncFromNetTask(Category mCategory, SyncCallbacks<Block> mBlockSyncCallbacks) {
            this.mCategory = mCategory;
            this.mBlockSyncCallbacks = mBlockSyncCallbacks;
        }

        @Override
        public void run() {
            ArrayList<Block> list;
            try {
                list = mBlockClient.getBlockData(mCategory);
            }catch (JSONException e){
                MessageObject obj = new MessageObject();
                //obj.list = list;
                obj.obj1 = mBlockSyncCallbacks;
                mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_WHAT_DATA_EXCEPTION,obj));
                return;
            }catch (IOException e){
                MessageObject obj = new MessageObject();
                //obj.list = list;
                obj.obj1 = mBlockSyncCallbacks;
                mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_WHAT_IO_EXCEPTION,obj));
                return;
            }
            
            if(list.size() == 0){
                MessageObject obj = new MessageObject();
                //obj.list = list;
                obj.obj1 = mBlockSyncCallbacks;
                mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_WHAT_NULL,obj));
                return;
            }
            
            mExecutorService.submit(new RefreshBlockTask(mCategory, list));

            MessageObject obj = new MessageObject();
            obj.list = list;
            obj.obj1 = mBlockSyncCallbacks;
            mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_WHAT_SUCCESS,obj));
            return;
    
        }
    }
    
    private class RefreshBlockTask implements Runnable{
        Category mCategory;
        List<Block> list;

        private RefreshBlockTask(Category mCategory, List<Block> list) {
            this.mCategory = mCategory;
            this.list = list;
        }

        @Override
        public void run() {
            mBlockAccessor.clear(mCategory);
            for (Block block:list){
                mBlockAccessor.insertOrReplace(block);
            }
        }
    }

     /*public void syncFirstFromDB(Category category,SyncCallbacks<Block> blockSyncCallbacks){
        mExecutorService.submit(new SyncFirstFromDBTask(category, blockSyncCallbacks));
    }*/

    /*private class SyncFirstFromDBTask implements Runnable{
        private Category mCategory;
        private SyncCallbacks<Block> mBlockSyncCallbacks;

        private SyncFirstFromDBTask(Category mCategory, SyncCallbacks<Block> mBlockSyncCallbacks) {
            this.mCategory = mCategory;
            this.mBlockSyncCallbacks = mBlockSyncCallbacks;
        }

        @Override
        public void run() {
            ArrayList<Block> list;

            list = mBlockAccessor.findBlocks(mCategory);
            if(list.size() == 0){
                try {
                    list = mBlockClient.getBlockData(mCategory);
                    if(list.size() == 0){
                        MessageObject obj = new MessageObject();
                        //obj.list = list;
                        obj.obj1 = mBlockSyncCallbacks;
                        mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_WHAT_NULL,obj));
                        return;
                    }

                    MessageObject obj = new MessageObject();
                    obj.list = list;
                    obj.obj1 = mBlockSyncCallbacks;
                    mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_WHAT_SUCCESS,obj));
                    return;

                }catch (JSONException e){
                    MessageObject obj = new MessageObject();
                    //obj.list = list;
                    obj.obj1 = mBlockSyncCallbacks;
                    mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_WHAT_DATA_EXCEPTION,obj));
                    return;

                }catch (IOException e){
                    MessageObject obj = new MessageObject();
                    //obj.list = list;
                    obj.obj1 = mBlockSyncCallbacks;
                    mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_WHAT_IO_EXCEPTION,obj));
                    return;
                }

            }

        }
    }*/
    
   /* private class SyncFirstFromNetTask implements Runnable{
        private Category mCategory;
        private SyncCallbacks<Block> mBlockSyncCallbacks;

        private SyncFirstFromNetTask(Category mCategory, SyncCallbacks<Block> mBlockSyncCallbacks) {
            this.mCategory = mCategory;
            this.mBlockSyncCallbacks = mBlockSyncCallbacks;
        }

        @Override
        public void run() {
            ArrayList<Block> list;
            try {
                list = mBlockClient.getBlockData(mCategory);
            }catch (Exception e){
                list = mBlockAccessor.findBlocks(mCategory);
                if(list.size() == 0){
                    MessageObject obj = new MessageObject();
                    //obj.list = list;
                    obj.obj1 = mBlockSyncCallbacks;
                    mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_WHAT_NULL,obj));
                    return;
                }
                
                
            }
            
        }
    }*/

}
