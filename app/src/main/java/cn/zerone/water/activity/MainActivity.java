package cn.zerone.water.activity;


import android.os.Bundle;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.fragment.HomeFragment;
import cn.zerone.water.fragment.LiveFragment;
import cn.zerone.water.fragment.MyselfFragment;
import cn.zerone.water.fragment.NoticeFragment;
import cn.zerone.water.fragment.TaskListFragment;
import cn.zerone.water.utils.BottomNavigationViewHelper;
import io.reactivex.annotations.NonNull;

public class MainActivity extends AppCompatActivity {
    public static MainActivity activity;
    private ImageView home;

    List<String> mViewList = new ArrayList<String>();//顶部用于循环的布局集合
    Fragment[] fragments = new Fragment[]{new NoticeFragment(), new TaskListFragment(), new HomeFragment(), new LiveFragment(), new MyselfFragment()};
    //切换底部导航

    public synchronized void changeTab(int index, String url) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < 5; ++i) {
            if (i == index) {
                transaction.show(fragments[i]);
            } else {
                transaction.hide(fragments[i]);
            }
        }
        if (index == 2) {
            home.setImageResource(R.drawable.home_blue);
        } else {
            home.setImageResource(R.drawable.home_gray);
        }
        transaction.commit();

    }

    public void setNotifyReadView(final boolean isShow) {
        if (activity != null)
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isShow) {
                        badge.setVisibility(View.VISIBLE);
                    } else {
                        App.sharedPreferences.edit().putBoolean("isRead", false).commit();
                        badge.setVisibility(View.GONE);
                    }
                }
            });
    }

    public void setMsgReadView(final boolean isShow) {
        if (activity != null)
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isShow) {
                        msgBadge.setVisibility(View.VISIBLE);
                        App.sharedPreferences.edit().putBoolean("isMsgRead", true).commit();
                    } else {
                        App.sharedPreferences.edit().putBoolean("isMsgRead", false).commit();
                        msgBadge.setVisibility(View.GONE);
                    }
                }
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_notify:
                    setNotifyReadView(false);
                    changeTab(0, null);
                    return true;
                case R.id.navigation_task:
                    changeTab(1, null);
                    return true;
                case R.id.navigation_home:
                    changeTab(2, null);
                    return true;
                case R.id.navigation_broadcast:
                    setMsgReadView(false);
                    changeTab(3, null);
                    return true;
                case R.id.navigation_myself:
                    changeTab(4, null);
                    return true;
            }
            return false;
        }
    };
    BottomNavigationView navigation = null;
    View badge = null;
    View msgBadge = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        home = (ImageView) findViewById(R.id.navigation_center_image);

        ((App) getApplication()).mapInit();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, fragments[0]);
        transaction.add(R.id.fragment_container, fragments[1]);
        transaction.add(R.id.fragment_container, fragments[2]);
        transaction.add(R.id.fragment_container, fragments[3]);
        transaction.add(R.id.fragment_container, fragments[4]);
        transaction.commit();
        changeTab(2, null);
       // setTagAndAlias();
        MainActivity.activity = this;
        int a = navigation.getChildCount();
        int b = navigation.getMaxItemCount();
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);
        navigation.setSelectedItemId(navigation.getMenu().getItem(2).getItemId());
        home.setImageResource(R.drawable.home_blue);
        View tab = menuView.getChildAt(1);
        BottomNavigationItemView itemView = (BottomNavigationItemView) tab;
        badge = LayoutInflater.from(this).inflate(R.layout.menu_badge, null, false);
        itemView.addView(badge);
        tab = menuView.getChildAt(1);
        itemView = (BottomNavigationItemView) tab;
        msgBadge = LayoutInflater.from(this).inflate(R.layout.menu_badge, null, false);
        itemView.addView(msgBadge);

        if (App.sharedPreferences.getBoolean("isRead", false)) {
            setNotifyReadView(true);
        } else {
            setNotifyReadView(false);
        }
        if (App.sharedPreferences.getBoolean("isMsgRead", false)) {
            setMsgReadView(true);
        } else {
            setMsgReadView(false);
        }
    }

    Long exitTime = 0l;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //弹出提示，可以有多种方式
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==3){
//                //处理扫描结果（在界面上显示）
//                if (null != data) {
//                    Bundle bundle = data.getExtras();
//                    if (bundle == null) {
//                        return;
//                    }
//                    if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
//                        final String result = bundle.getString(CodeUtils.RESULT_STRING);
////                        Requests.scan(new Observer<JSONObject>() {
////                            @Override
////                            public void onSubscribe(Disposable d) {
////
////                            }
////
////                            @Override
////                            public void onNext(JSONObject jsonObject) {
////
////                            }
////
////                            @Override
////                            public void onError(Throwable e) {
////                                Toast.makeText(MainActivity.this, "解析上传失败结果:" + result, Toast.LENGTH_LONG).show();
////                            }
////
////                            @Override
////                            public void onComplete() {
////                                Toast.makeText(MainActivity.this, "解析结果:" + result, Toast.LENGTH_LONG).show();
////                            }
////                        },App.userId,result);
//                    } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
//                        Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//    }

}