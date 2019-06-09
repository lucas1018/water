package cn.zerone.water.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;

;import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baijiahulian.live.ui.LiveSDKWithUI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.activity.CreateLiveActivity;
import cn.zerone.water.adapter.ApproveItem;
import cn.zerone.water.http.Requests;
import cn.zerone.water.utils.ImageUtil;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * created by qhk
 * on 2019/5/20
 */
public class LiveFragment extends Fragment implements View.OnClickListener {

    private Button startLive;
    private String code;
    private SharedPreferences sp;
    private TabLayout mTablayout;
    private ViewPager mViewPager;
    MyPagerAdapter myPagerAdapter;
    ArrayList<Fragment> fragments=new ArrayList<>();
    InvolvedLiveFragment involvedLiveFragment;
    InitiateLiveFragment initiateLiveFragment;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live,null);
        initView(view);
        return view;
    }


    private void initView(View view) {
        startLive = view.findViewById(R.id.start_live_live_fragment);
        mTablayout=view.findViewById(R.id.tablayout_live_fragment);
        mViewPager=view.findViewById(R.id.viewpager_live_fragment);
        involvedLiveFragment=new InvolvedLiveFragment();
        initiateLiveFragment=new InitiateLiveFragment();
        fragments.add(initiateLiveFragment);
        fragments.add(involvedLiveFragment);

        myPagerAdapter = new MyPagerAdapter(getActivity().getSupportFragmentManager());
        myPagerAdapter.setFragments(fragments);
        mViewPager.setAdapter(myPagerAdapter);
        mTablayout.addTab(mTablayout.newTab());
        mTablayout.addTab(mTablayout.newTab());
        mTablayout.setupWithViewPager(mViewPager);
        mTablayout.getTabAt(0).setText("我发起的");
        mTablayout.getTabAt(1).setText("我参与的");
        startLive.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_live_live_fragment:
                Intent intent=new Intent();
                intent.setClass(getActivity(),CreateLiveActivity.class);
                startActivity(intent);
                break;
        }
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        protected List<Fragment> mFragmentList;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setFragments(ArrayList<Fragment> fragments) {
            mFragmentList = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = mFragmentList.get(position);
            return fragment;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

    }

    // @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//
//        startLive.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//       // initData();
//    }
//    private void initData() {
//        sp = getActivity().getSharedPreferences("live_temp", Context.MODE_PRIVATE);
//        code = sp.getString("code", "tecq8z");
//        Button button=getActivity().findViewById(R.id.enter);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String code = ((EditText)getActivity(). findViewById(R.id.activity_entry_join_code)).getText().toString();
//                String name = ((EditText) getActivity().findViewById(R.id.activity_entry_name)).getText().toString();
//                SharedPreferences.Editor editor = sp.edit();
//                editor.putString("code", code);
//                editor.apply();
//                LiveSDKWithUI.enterRoom(getActivity(), code, name, new LiveSDKWithUI.LiveSDKEnterRoomListener() {
//                    @Override
//                    public void onError(String s) {
//
//                    }
//                });
//
//            }
//        });
//
//
//    }
//
//    private void createLiveRoom(String identity_id,
//                                String identity_pwd,String title,String end_time,int type,
//                                int max_users,int pre_ener_time,int is_long_them) {
//        Requests.CreateLiveRoom(new Observer<JSONArray>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//            }
//
//            @Override
//            public void onNext(JSONArray objects) {
//                     }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        },identity_id,identity_pwd,title,end_time,type,max_users,pre_ener_time,is_long_them);
//    }
//
}
