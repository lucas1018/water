package cn.zerone.water.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;

/**
 * create by halfcup
 * on 2019/1/30
 */
public class StorageUtils {

    /**
     * 获取应用的缓存目录
     */
    public static File getCacheDirectory(Context context) {
        File appCacheDir = context.getCacheDir();
        if (appCacheDir == null) {
            Log.w("StorageUtils", "Can't define system cache directory! The app should be re-installed.");
        }
        return appCacheDir;
    }
}
