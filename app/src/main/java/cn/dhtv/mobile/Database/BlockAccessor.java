package cn.dhtv.mobile.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import cn.dhtv.mobile.entity.Block;
import cn.dhtv.mobile.entity.Category;

/**
 * Created by Jack on 2015/4/21.
 */
public class BlockAccessor {


    public ArrayList<Block> findBlocks(Category category){
//        Cursor cursor = getDb().query(Contract.Block.TABLE_NAME, null,null, null, null, null, null,""+3);
        String selection = Contract.Block.COLUMN_NAME_FROM_BID+" = ?";
        Cursor cursor = getDb().query(Contract.Block.TABLE_NAME, null, selection, new String[]{""+category.getBid()}, null, null, Contract.Block.COLUMN_NAME_ID);
        return toList(cursor);
    }



    public long insertOrReplace(Block block){
        ContentValues values = new ContentValues();
        values.put(Contract.Block.COLUMN_NAME_ID,block.getId());
        values.put(Contract.Block.COLUMN_NAME_UID,block.getUid());
        values.put(Contract.Block.COLUMN_NAME_COMMENT_NUM,block.getCommentnum());
        values.put(Contract.Block.COLUMN_NAME_TITLE,block.getTitle());
        values.put(Contract.Block.COLUMN_NAME_SUMMARY,block.getSummary());
        values.put(Contract.Block.COLUMN_NAME_URL,block.getUrl());
        values.put(Contract.Block.COLUMN_NAME_PIC,block.getPic());
        values.put(Contract.Block.COLUMN_NAME_DATELINE,block.getDateline());
        values.put(Contract.Block.COLUMN_NAME_CATNAME,block.getCatname());
        values.put(Contract.Block.COLUMN_NAME_AVATAR,block.getAvatar());
        values.put(Contract.Block.COLUMN_NAME_ID_TYPE,block.getIdtype());
        values.put(Contract.Block.COLUMN_NAME_USER_NAME,block.getUsername());

        values.put(Contract.Block.COLUMN_NAME_FROM_BID,block.getFromBid());
        long a = getDb().replace(Contract.Block.TABLE_NAME,null,values);
        return a;
//        return getDb().insert(Contract.Block.TABLE_NAME,null,values);
    }


    public void clear(Category category){
        getDb().beginTransaction();
        getDb().delete(Contract.Block.TABLE_NAME, Contract.Block.COLUMN_NAME_FROM_BID+" = ?",new String[]{""+category.getBid()});
        getDb().setTransactionSuccessful();
        getDb().endTransaction();
    }



    private SQLiteDatabase getDb(){
        return DBOpenHelper.getInstance().getWritableDatabase();
    }

    public static ArrayList<Block> toList(Cursor cursor){
        ArrayList<Block> list = new ArrayList<Block>();
        while (cursor.moveToNext()){
            list.add(toBlock(cursor));
        }
        return list;
    }

    public static Block toBlock(Cursor cursor){
        Block block = new Block();
        block.setId(cursor.getInt(cursor.getColumnIndex(Contract.Block.COLUMN_NAME_ID)));
        block.setUid(cursor.getInt(cursor.getColumnIndex(Contract.Block.COLUMN_NAME_UID)));
        block.setCommentnum(cursor.getInt(cursor.getColumnIndex(Contract.Block.COLUMN_NAME_COMMENT_NUM)));
        block.setTitle(cursor.getString(cursor.getColumnIndex(Contract.Block.COLUMN_NAME_TITLE)));
        block.setSummary(cursor.getString(cursor.getColumnIndex(Contract.Block.COLUMN_NAME_SUMMARY)));
        block.setUrl(cursor.getString(cursor.getColumnIndex(Contract.Block.COLUMN_NAME_URL)));
        block.setPic(cursor.getString(cursor.getColumnIndex(Contract.Block.COLUMN_NAME_PIC)));
        block.setCatname(cursor.getString(cursor.getColumnIndex(Contract.Block.COLUMN_NAME_CATNAME)));
        block.setDateline(cursor.getString(cursor.getColumnIndex(Contract.Block.COLUMN_NAME_DATELINE)));
        block.setIdtype(cursor.getString(cursor.getColumnIndex(Contract.Block.COLUMN_NAME_ID_TYPE)));
        block.setAvatar(cursor.getString(cursor.getColumnIndex(Contract.Block.COLUMN_NAME_AVATAR)));
        block.setUsername(cursor.getString(cursor.getColumnIndex(Contract.Block.COLUMN_NAME_USER_NAME)));

        block.setFromBid(cursor.getInt(cursor.getColumnIndex(Contract.Block.COLUMN_NAME_FROM_BID)));
        return block;
    }

    /*public static ArrayList<Block> toBlockList(Cursor cursor){
        ArrayList<Block> list = new ArrayList<>();
        while (cursor.moveToNext()){
            list.add(toBlock(cursor));
        }
        return list;
    }*/


}
