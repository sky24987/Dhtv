package cn.dhtv.mobile.Database;

import java.util.concurrent.Executor;

import cn.dhtv.mobile.Singletons;
import cn.dhtv.mobile.entity.Category;

/**
 * Created by Jack on 2015/5/21.
 */
public class DBUtils {
    public static ArticleAccessor articleAccessor = new ArticleAccessor();
    public static BlockAccessor blockAccessor = new BlockAccessor();
    public static CategoryAccessor categoryAccessor = new CategoryAccessor();
    private static Executor executor = Singletons.getDBExecutor();

    public static void update(Category category){
        categoryAccessor.insertOrReplace(category);
    }

    public static void insert(Category category){
        categoryAccessor.insertOrReplace(category);
    }

    public static void asyncUpdate(final Category category){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                update(category);
            }
        });
    }
}
