package cn.zerone.water.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSONObject;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.fragment.FriendsFragment;
import cn.zerone.water.fragment.HomeFragment;
import cn.zerone.water.fragment.JobListFragment;
import cn.zerone.water.fragment.MyselfFragment;
import cn.zerone.water.http.Requests;
import cn.zerone.water.utils.BottomNavigationViewHelper;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    public static MainActivity activity;
    Fragment[] fragments = new Fragment[]{new HomeFragment(),new SystemMessagesActivity(),new FriendsFragment(),new JobListFragment(),new MyselfFragment()};
    /*{
        Bundle args = new Bundle();
        fragments[0].setArguments(args);

         args = new Bundle();
        fragments[3].setArguments(args);

        args = new Bundle();
        fragments[4].setArguments(args);

        fragments[0].getArguments().putString("url","http://124.237.77.232:50180/CWeb/Home.aspx?token="+App.token);
        fragments[3].getArguments().putString("url","http://124.237.77.232:50180/CWeb/JobList.aspx?token="+App.token);
        fragments[4].getArguments().putString("url","http://124.237.77.232:50180/CWeb/My.aspx?token="+App.token);
    }*/
    public synchronized void changeTab(int index,String url){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i <5; i++) {
            if(i==index){
                transaction.show(fragments[i]);
            }else{
                transaction.hide(fragments[i]);
            }
        }
        transaction.commit();
       /* if(url!=null&&fragments[index] instanceof  WebFragment){
            ((WebFragment) fragments[index]).changeUrl(url);
            if(index==3){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        navigation.setSelectedItemId( R.id.navigation_task);
                    }
                });
            }else if(index==5){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        navigation.setSelectedItemId( R.id.navigation_myself);
                    }
                });
            }
        }else if(url!=null&&index==2){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    navigation.setSelectedItemId(R.id.navigation_friends);
                }

                ;
            });
        }*/

    }
    public void setNotifyReadView(final boolean isShow){
        if(activity!=null)
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isShow){
                    badge.setVisibility(View.VISIBLE);
                }else{
                    App.sharedPreferences.edit().putBoolean("isRead",false).commit();
                    badge.setVisibility(View.GONE);
                }
            }
        });
    }
    public void setMsgReadView(final boolean isShow){
        if(activity!=null)
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(isShow){
                        msgBadge.setVisibility(View.VISIBLE);
                        App.sharedPreferences.edit().putBoolean("isMsgRead",true).commit();
                    }else{
                        App.sharedPreferences.edit().putBoolean("isMsgRead",false).commit();
                        msgBadge.setVisibility(View.GONE);
                    }
                }
            });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity= null;
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    changeTab(0,null);
                    return true;
                case R.id.navigation_friends:
                    setMsgReadView(false);
                    changeTab(2,null);
                    return true;
                case R.id.navigation_myself:
                    changeTab(4,null);
                    return true;
                case R.id.navigation_notify:
                    setNotifyReadView(false);
                    changeTab(1,null);
                    return true;
                case R.id.navigation_task:
                    changeTab(3,null);
                    return true;
            }
            return false;
        }
    };
    BottomNavigationView navigation =null;
    View badge = null;
    View msgBadge = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((App)getApplication()).mapInit();
        ((App)getApplication()).tokenInit();
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container,fragments[0] );
        transaction.add(R.id.fragment_container,fragments[1] );
        transaction.add(R.id.fragment_container,fragments[2] );
        transaction.add(R.id.fragment_container,fragments[3] );
        transaction.add(R.id.fragment_container,fragments[4] );
        transaction.commit();
        changeTab(0,null);

        MainActivity.activity = this;
        BottomNavigationMenuView menuView  = (BottomNavigationMenuView) navigation.getChildAt(0);
        View tab = menuView.getChildAt(1);
        BottomNavigationItemView itemView = (BottomNavigationItemView) tab;
         badge = LayoutInflater.from(this).inflate(R.layout.menu_badge, null, false);
        itemView.addView(badge);
         tab = menuView.getChildAt(3);
         itemView = (BottomNavigationItemView) tab;
        msgBadge= LayoutInflater.from(this).inflate(R.layout.menu_badge, null, false);
        itemView.addView(msgBadge);

        if( App.sharedPreferences.getBoolean("isRead",false)){
            setNotifyReadView(true);
        }else{
            setNotifyReadView(false);
        }
        if( App.sharedPreferences.getBoolean("isMsgRead",false)){
            setMsgReadView(true);
        }else{
            setMsgReadView(false);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(dialog==null){
                showBack("确定要退出吗！");
                return true;
            }else{
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);

    }

    Dialog dialog = null;
    Handler handler = new Handler(Looper.getMainLooper());
    public void showBack(String message){
        if(dialog!=null){
            dialog.dismiss();
            dialog = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_back, null);
//        builder.setView(view,-1,-1,-1,-1);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        ((TextView)view.findViewById(R.id.message)).setText(message);
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog!=null){
                    dialog.dismiss();
                    dialog =null;
                }
            }
        });
        view.findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog!=null){
                    dialog.dismiss();
                    dialog =null;
                    finish();
                    System.exit(0);
                }
            }
        });
        dialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==3){
                //处理扫描结果（在界面上显示）
                if (null != data) {
                    Bundle bundle = data.getExtras();
                    if (bundle == null) {
                        return;
                    }
                    if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                        final String result = bundle.getString(CodeUtils.RESULT_STRING);
                        Requests.scan(new Observer<JSONObject>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(JSONObject jsonObject) {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(MainActivity.this, "解析上传失败结果:" + result, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onComplete() {
                                Toast.makeText(MainActivity.this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                            }
                        },App.token,result);
                    } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                        Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                    }
                }
            }
    }
}
