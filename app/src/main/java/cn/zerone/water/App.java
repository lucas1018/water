package cn.zerone.water;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.service.LocationService;
import com.example.jpushdemo.Logger;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.zerone.water.activity.DialogActivity;
import cn.zerone.water.activity.MainActivity;
import cn.zerone.water.http.Requests;
import cn.zerone.water.model.DaoMaster;
import cn.zerone.water.model.DaoSession;
import cn.zerone.water.model.EngineeringStation;
import cn.zerone.water.model.SystemMessage;
import cn.zerone.water.model.SystemMessageDao;
import cn.zerone.water.utils.MD5Utils;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;



/**
 * Created by zero on 2018/11/29.
 */

public class App extends Application {
    public static java.lang.String username = null;
    public static java.lang.String token = null;
    public static java.lang.Integer userId = null;
    public static boolean isUploadGps = false;
    public static BDLocation lastDBLocation = null;
    public static String baseUrl = "http://124.237.77.232:50180/";
    public static SharedPreferences sharedPreferences = null;
    private static Context context;
    private long lastUpdateGps = System.currentTimeMillis();
    boolean isTest =false;
    private LocationService locationService;
    public EngineeringStation  engineeringStation = null;
    private static final String TAG = "JIGUANG-Example";
    @Override
    public void onCreate() {
        Logger.d(TAG, "[ExampleApplication] onCreate");
        super.onCreate();
        JPushInterface.setDebugMode(true); //正式环境时去掉此行代码
        JPushInterface.init(this);

        context = this;
        Fresco.initialize(this);
        locationService = new LocationService(getApplicationContext());
        sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        if (token == "") {
            token = null;
        }
        username = sharedPreferences.getString("username", "");
        if (username == "") {
            username = null;
        }
        try{
            ZXingLibrary.initDisplayOpinion(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void clearLoginToken() {
        token = "";
        sharedPreferences.edit().putString("token", "").commit();
    }

    public void mapInit() {
        locationService.registerListener(new BDAbstractLocationListener() {
            @Override
            public void onLocDiagnosticMessage(int i, int i1, String s) {
                super.onLocDiagnosticMessage(i, i1, s);
                System.out.println("onLocDiagnosticMessage"+s);
            }

            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                try{
                lastDBLocation = bdLocation;
                if (isUploadGps && (System.currentTimeMillis() - lastUpdateGps > 15 * 1000)) {
                    lastUpdateGps = System.currentTimeMillis();
                    System.out.println("上传 GPS中");
                    try {
                        Requests.uploadGps(App.token, bdLocation.getLatitude(), bdLocation.getLongitude());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("未上传GPS");
                }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        locationService.start();;
    }

    SystemMessageDao systemMessageDao = null;

    public void clearEngineeringStation() {
        try{
            engineeringStation = null;
            SharedPreferences sp = getSharedPreferences(MD5Utils.encode2hex(App.username), Context.MODE_PRIVATE);
            sp.edit().putString("engineeringStation", "").commit();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void saveEngineeringStation(EngineeringStation engineeringStation) {
       try{
           this. engineeringStation = engineeringStation;
           System.out.println("saveEngineeringStation:"+engineeringStation);
           SharedPreferences sp = getSharedPreferences(MD5Utils.encode2hex(App.username), Context.MODE_PRIVATE);
           sp.edit().putString("engineeringStation", engineeringStation.toString()).commit();
       }catch (Exception e){
           e.printStackTrace();
       }


    }

    class GetSystemMessages implements  Observer<JSONObject>{
        public List<SystemMessage> tempMessages = new ArrayList<>();

        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(JSONObject objects) {
            try {
                Boolean isUploadGPS = objects.getJSONObject("CommonJSon").getBoolean("IsUploadGPS");
                App.isUploadGps = isUploadGPS;
            } catch (Exception e) {
            }
            if (objects.getJSONArray("MsgArry") != null) {
                JSONArray msgArry = objects.getJSONArray("MsgArry");
                for (int i = 0; i < msgArry.size(); i++) {
                    JSONObject o = msgArry.getJSONObject(i);
                    String title = o.getString("Title");
                    String sgin = o.getString("SIGN");
                    String content = o.getString("Msg");
                    String level = o.getString("Levels");
                    long createTime = o.getLong("AddTime") * 1000;
                    SystemMessage systemMessage = new SystemMessage(content, sgin + "：" + title + "[" + getLevel(level) + "]",level, createTime);
                    tempMessages.add(systemMessage);
                }
            }
            if(isTest){
                tempMessages = new ArrayList<>();
                SystemMessage systemMessage = new SystemMessage("asdasdasdasd", "asdasdasd" + "：" + "wqeqweqweqwe" + "[" + getLevel("2") + "]","2", System.currentTimeMillis()/1000);
                isTest =false;
                tempMessages.add(systemMessage);
            }
        }

        @Override
        public void onError(Throwable e) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Requests.getSystemMessages(new GetSystemMessages(), App.token);
                }
            },10*1000);
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
            try{
                for (int i = 0; i < tempMessages.size(); i++) {
                    String message = null;
                    SystemMessage systemMessage = tempMessages.get(i);
                    int size = systemMessageDao.queryBuilder().where(SystemMessageDao.Properties.CreateTime.eq(systemMessage.getCreateTime())).build().list().size();
                    if (size == 0) {
                        App.sharedPreferences.edit().putBoolean("isRead", true).commit();
                        Toast.makeText(getApplicationContext(), "有新短消息", 0).show();
                        systemMessageDao.save(systemMessage);
                        if (MainActivity.activity != null) {
                            MainActivity.activity.setNotifyReadView(true);
                        }
                        if ("2".equals(systemMessage.getLevel())) {
                            message = systemMessage.getContent();
                            showMessageDialog(systemMessage.getTitle() ,message);
                        }
                    } else {
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Requests.getSystemMessages(new GetSystemMessages(), App.token);
                }
            },10*1000);
            ;
        }
    }
    Handler handler = new Handler(Looper.getMainLooper());
    public void tokenInit() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(getApplicationContext(), username);
        SQLiteDatabase db = devOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        systemMessageDao = daoSession.getSystemMessageDao();
        Requests.getSystemMessages(new GetSystemMessages(), App.token);
        try{
            SharedPreferences sp = getSharedPreferences(MD5Utils.encode2hex(App.username), Context.MODE_PRIVATE);
            String engineeringStationString = sp.getString("engineeringStation", null);
            engineeringStation = JSON.parseObject(engineeringStationString, EngineeringStation.class);
        }catch (Exception e){
        }

    }

    public static void showMessageDialog(String title,String message) {
        Intent intent = new Intent(App.context, DialogActivity.class);
        intent.putExtra("message",message);
        intent.putExtra("title",title);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.context.startActivity(intent);
    }

    private String getLevel(String level) {
        if ("2".equals(level)) {
            return "紧急通知";
        } else if ("1".equals(level)) {
            return "通知";
        } else {
            return "其他通知";
        }
    }

    public void saveToken(String token) {
        sharedPreferences.edit().putString("token", token).commit();
    }

    public void saveUsername(String username) {
        sharedPreferences.edit().putString("username", username).commit();
    }
}
