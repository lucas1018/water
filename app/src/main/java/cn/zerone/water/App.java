package cn.zerone.water;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.service.LocationService;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.example.jpushdemo.Logger;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import cn.jpush.android.api.JPushInterface;
import cn.zerone.water.http.Requests;
import cn.zerone.water.model.EngineeringStation;
import cn.zerone.water.utils.MD5Utils;



/**
 * Created by zero on 2018/11/29.
 */

public class App extends Application {
    public static String username = null;
    public static String userId = null;
    public static String pwd = null;
    public static String device_tokens = null;
    public static boolean isUploadGps = false;
    public static BDLocation lastDBLocation = null;
    public static String baseUrl = "http://124.237.77.232:50180/";
    public static SharedPreferences sharedPreferences = null;
    private static Context context;
    private long lastUpdateGps = System.currentTimeMillis();
    boolean isTest =false;

    public LocationService locationService;
    public EngineeringStation  engineeringStation = null;
    private static final String TAG = "JIGUANG-Example";
    public final static String mPreUrl = "http://47.105.187.185:8011";

    @Override
    public void onCreate() {
        Logger.d(TAG, "[ExampleApplication] onCreate");
        super.onCreate();
        device_tokens = JPushInterface.ACTION_REGISTRATION_ID;
        JPushInterface.setDebugMode(true); //正式环境时去掉此行代码
        JPushInterface.init(this);


        context = this;
        Fresco.initialize(this);
        locationService = new LocationService(getApplicationContext());
        sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        if (username == "") {
            username = null;
        }
        try{
            ZXingLibrary.initDisplayOpinion(this);
        }catch (Exception e){
            e.printStackTrace();
        }

        //百度地图
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
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
                        Requests.uploadGps(App.userId, bdLocation.getLatitude(), bdLocation.getLongitude());
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

    public void saveUserId(String userId) {
        sharedPreferences.edit().putString("userId", userId).commit();
    }
}
