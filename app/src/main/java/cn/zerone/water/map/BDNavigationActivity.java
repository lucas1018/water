package cn.zerone.water.map;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.clusterutil.clustering.ClusterItem;
import com.baidu.clusterutil.clustering.ClusterManager;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.navigation.service.ForegroundService;
import com.baidu.navigation.service.NormalUtils;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRoutePlanManager;
import com.baidu.navisdk.adapter.IBaiduNaviManager;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.zerone.water.R;
import cn.zerone.water.activity.BDExtGpsActivity;
import cn.zerone.water.activity.BDGuideActivity;
import cn.zerone.water.fragment.MasterArticleFragment;


public class BDNavigationActivity extends Activity implements OnMapLoadedCallback {

    private static final String APP_FOLDER_NAME = "CHUYU";
    static final String ROUTE_PLAN_NODE = "routePlanNode";
    private static final int NORMAL = 0;
    private static final int EXTERNAL = 1;

    private static final String[] authBaseArr = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private String mSDCardPath = null;
    private static final int authBaseRequestCode = 1;
    private boolean hasInitSuccess = false;
    private BNRoutePlanNode mStartNode = null;
    private double mCurrentLat;
    private double mCurrentLng;

    private double mDestinationLat;
    private double mDestinationLng;
    private String mPlaceName;
    private LocationManager mLocationManager;

    private TextView mNaviOK = null;
    private ImageView mNavi_sure_back = null; //返回
    MapView mMapView;
    BaiduMap mBaiduMap;
    MapStatus ms;
    private ClusterManager<MyItem> mClusterManager;

    private LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            mCurrentLat = location.getLatitude();
            mCurrentLng = location.getLongitude();
            Toast.makeText(BDNavigationActivity.this, mCurrentLat
                    + "--" + mCurrentLng, Toast.LENGTH_LONG).show();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.normal_activity_bdnavigation);

        // 开启前台服务防止应用进入后台gps挂掉
        startService(new Intent(this, ForegroundService.class));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<>();
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            }
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }

            if (permissions.size() != 0) {
                requestPermissionsForM(permissions);
            }
        }

        mNaviOK = findViewById(R.id.tv_NaviOK);
        mNavi_sure_back = findViewById(R.id.navi_sure_back);
        mMapView = (MapView) findViewById(R.id.desmapView);

        Intent it = getIntent();
        mDestinationLat = it.getDoubleExtra("lat",0.0);
        mDestinationLng = it.getDoubleExtra("lng",0.0);
        mPlaceName = it.getStringExtra("place");

        ms = new MapStatus.Builder().target(new LatLng(mDestinationLat, mDestinationLng)).zoom(12.0f).build();
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMapLoadedCallback(this);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
        // 定义点聚合管理类ClusterManager
        mClusterManager = new ClusterManager<MyItem>(this, mBaiduMap);

        // 添加Marker点
        addMarkers();

        // 设置地图监听，当地图状态发生改变时，进行点聚合运算
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
        // 设置maker点击时的响应
        mBaiduMap.setOnMarkerClickListener(mClusterManager);



        initListener();
        if (initDirs()) {
            initNavi();
        }
        initLocation();


    }

    private void initLocation() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (mLocationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission
                    .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                    .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000, 1000, mLocationListener);
        }
    }

    private void requestPermissionsForM(final ArrayList<String> per) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(per.toArray(new String[per.size()]), 1);
        }
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
        stopService(new Intent(this, ForegroundService.class));
    }

    /**
     * 向地图添加Marker点
     */
    public void addMarkers() {
        // 添加Marker点
        LatLng des = new LatLng(mDestinationLat, mDestinationLng);


        List<MyItem> items = new ArrayList<MyItem>();
        items.add(new MyItem(des));
        mClusterManager.addItems(items);

    }

    /**
     * 每个Marker点，包含Marker点坐标以及图标
     */
    public class MyItem implements ClusterItem {
        private final LatLng mPosition;

        public MyItem(LatLng latLng) {
            mPosition = latLng;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        @Override
        public BitmapDescriptor getBitmapDescriptor() {
            return BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_gcoding);
        }
    }

    @Override
    public void onMapLoaded() {
        // TODO Auto-generated method stub
        ms = new MapStatus.Builder().zoom(12.0f).build();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
    }


    private void initListener() {
        if(mNavi_sure_back != null){
            mNavi_sure_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    //Intent it =new Intent(BDNavigationActivity.this, PoiSearchActivity.class);
                    //startActivity(it);
                }
            });

        }

        if (mNaviOK != null) {
            mNaviOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
                        if (mCurrentLat == 0 && mCurrentLng == 0) {
                            return;
                        }
                        BNRoutePlanNode sNode = new BNRoutePlanNode.Builder()
                                .latitude(mCurrentLat)
                                .longitude(mCurrentLng)
                                .coordinateType(BNRoutePlanNode.CoordinateType.WGS84)
                                .build();
                        BNRoutePlanNode eNode = new BNRoutePlanNode.Builder()
                                .latitude(mDestinationLat)
                                .longitude(mDestinationLng)
                                .name(mPlaceName)
                                .description(mPlaceName)
                                .coordinateType(BNRoutePlanNode.CoordinateType.WGS84)
                                .build();
                        mStartNode = sNode;

                        routePlanToNavi(sNode, eNode, NORMAL);
                    }
                }
            });
        }

    }


    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private boolean hasBasePhoneAuth() {
        PackageManager pm = this.getPackageManager();
        for (String auth : authBaseArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager
                    .PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void initNavi() {
        // 申请权限
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (!hasBasePhoneAuth()) {
                this.requestPermissions(authBaseArr, authBaseRequestCode);
                return;
            }
        }

        if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
            hasInitSuccess = true;
            return;
        }

        BaiduNaviManagerFactory.getBaiduNaviManager().init(getApplicationContext(),
                mSDCardPath, APP_FOLDER_NAME, new IBaiduNaviManager.INaviInitListener() {

                    @Override
                    public void onAuthResult(int status, String msg) {
                        String result;
                        if (0 == status) {
                            result = "key校验成功!";
                        } else {
                            result = "key校验失败, " + msg;
                        }
                        Toast.makeText(BDNavigationActivity.this, result, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void initStart() {
                        Toast.makeText(BDNavigationActivity.this.getApplicationContext(),
                                "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void initSuccess() {
                        Toast.makeText(BDNavigationActivity.this.getApplicationContext(),
                                "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                        hasInitSuccess = true;
                        // 初始化tts
                        initTTS();
                        BaiduNaviManagerFactory.getBaiduNaviManager().enableOutLog(true);
                    }

                    @Override
                    public void initFailed(int errCode) {
                        Toast.makeText(BDNavigationActivity.this.getApplicationContext(),
                                "百度导航引擎初始化失败 " + errCode, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    private void initTTS() {
        // 使用内置TTS
        BaiduNaviManagerFactory.getTTSManager().initTTS(getApplicationContext(),
                getSdcardDir(), APP_FOLDER_NAME, NormalUtils.getTTSAppID());

    }

    private void routePlanToNavi(BNRoutePlanNode sNode, BNRoutePlanNode eNode, final int from) {
        List<BNRoutePlanNode> list = new ArrayList<>();
        list.add(sNode);
        list.add(eNode);

        //System.out.println("000000000***************************00000000");
        //System.out.println("000000000***************************00000000");

        BaiduNaviManagerFactory.getCommonSettingManager().setCarNum(this, "粤B66666");
        BaiduNaviManagerFactory.getRoutePlanManager().routeplanToNavi(
                list,
                IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT,
                null,
                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_START:
                                Toast.makeText(BDNavigationActivity.this.getApplicationContext(),
                                        "算路开始", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_SUCCESS:
                                Toast.makeText(BDNavigationActivity.this.getApplicationContext(),
                                        "算路成功", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_FAILED:
                                Toast.makeText(BDNavigationActivity.this.getApplicationContext(),
                                        "算路失败", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_TO_NAVI:
                                Toast.makeText(BDNavigationActivity.this.getApplicationContext(),
                                        "算路成功准备进入导航", Toast.LENGTH_SHORT).show();

                                Intent intent = null;
                                if (from == NORMAL) {
                                    intent = new Intent(BDNavigationActivity.this,
                                            BDGuideActivity.class);
                                } else if (from == EXTERNAL) {
                                    intent = new Intent(BDNavigationActivity.this,
                                            BDExtGpsActivity.class);
                                }

                                startActivity(intent);
                                break;
                            default:
                                // nothing
                                break;
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == authBaseRequestCode) {
            for (int ret : grantResults) {
                if (ret == 0) {
                    continue;
                } else {
                    Toast.makeText(BDNavigationActivity.this.getApplicationContext(),
                            "缺少导航基本的权限!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            initNavi();
        }
    }

}
