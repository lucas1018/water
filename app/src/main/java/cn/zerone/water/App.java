package cn.zerone.water;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.service.LocationService;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.example.jpushdemo.ExampleUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.zerone.water.adapter.DeviceUuidFactory;
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
    public static String device_tokens;

    @Override
    public void onCreate() {
        super.onCreate();
       device_tokens = DeviceUuidFactory.GetDeviceUuidFactory(this)+"";
       device_tokens = device_tokens.replaceAll("-","");

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
     setTagAndAlias();


    }
    private void setTagAndAlias() {
        /**
         *这里设置了别名，在这里获取的用户登录的信息
         *并且此时已经获取了用户的userId,然后就可以用用户的userId来设置别名了
         **/
        //false状态为未设置标签与别名成功
        //if (UserUtils.getTagAlias(getHoldingActivity()) == false) {
        Set<String> tags = new HashSet<String>();
        //这里可以设置你要推送的人，一般是用户uid 不为空在设置进去 可同时添加多个
        if (ExampleUtil.getDeviceId(this) != null) {
            //tags.add(ExampleUtil.getDeviceId(this));//设置tag
            tags.add(device_tokens);
        }
//        tags.add("111111");//设置tag
        //上下文、别名【Sting行】、标签【Set型】、回调
//        JPushInterface.setAliasAndTags(this, ExampleUtil.getDeviceId(this), tags,
//                mAliasCallback);
        JPushInterface.setAliasAndTags(this, device_tokens, tags,
                mAliasCallback);
        // }


    }
    TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    //这里可以往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    //UserUtils.saveTagAlias(getHoldingActivity(), true);
                    logs = "Set tag and alias success极光推送别名设置成功";
                    Log.e("TAG", logs);
                    break;
                case 6002:
                    //极低的可能设置失败 我设置过几百回 出现3次失败 不放心的话可以失败后继续调用上面那个方面 重连3次即可 记得return 不要进入死循环了...
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.极光推送别名设置失败，60秒后重试";
                    Log.e("TAG", logs);
                    break;
                default:
                    logs = "极光推送设置失败，Failed with errorCode = " + code;
                    Log.e("TAG", logs);
                    break;
            }
        }
    };
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
