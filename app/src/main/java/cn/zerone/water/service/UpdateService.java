package cn.zerone.water.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.zerone.water.activity.SystemUpdateActivity;
import cn.zerone.water.utils.ApkUtils;
import cn.zerone.water.utils.StorageUtils;

public class UpdateService extends IntentService {
    private static final int BUFFER_SIZE = 10 * 1024; // 8k ~ 32K
    private static final String TAG = "UpdateService";


    public UpdateService() {
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        NotificationHelper notificationHelper =  new NotificationHelper(this);
        String urlStr = intent.getStringExtra(SystemUpdateActivity.APK_DOWNLOAD_URL);
        InputStream in = null;
        FileOutputStream out = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.setConnectTimeout(10 * 1000);
            urlConnection.setReadTimeout(10 * 1000);
            urlConnection .setRequestProperty("Accept-Encoding", "identity");
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
          //  conn.setRequestProperty(“Accept-Encoding”, “identity”);
           // urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
           // urlConnection.setRequestProperty("Accept-Encoding", "identity");

            urlConnection.connect();
           long bytetotal = urlConnection.getContentLength();
//           if (bytetotal<0){
//               bytetotal = 10*1024*1024;
//           }
          //  long bytetotal = 10*1024*1024;
            Log.e("total-->",bytetotal+"");
            long bytesum = 0;
            int byteread = 0;
            in = urlConnection.getInputStream();
            File dir = StorageUtils.getCacheDirectory(this);
            String apkName = urlStr.substring(urlStr.lastIndexOf("/") + 1, urlStr.length());
            File apkFile = new File(dir, apkName);
            out = new FileOutputStream(apkFile);
            byte[] buffer = new byte[BUFFER_SIZE];

            int oldProgress = 0;

            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread);

                Log.e("bytesum-->",bytesum+"");
                int progress = (int) (bytesum * 100L / bytetotal);
                // 如果进度与之前进度相等，则不更新，如果更新太频繁，否则会造成界面卡顿
                if (progress != oldProgress) {
                    notificationHelper.updateProgress(progress);
                }
                oldProgress = progress;
            }
            // 下载完成
            notificationHelper.cancel();
            ApkUtils.installAPk(this, apkFile);

        } catch (Exception e) {
            Log.e(TAG, "download apk file error:" + e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {

                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {

                }
            }
        }
    }



}
