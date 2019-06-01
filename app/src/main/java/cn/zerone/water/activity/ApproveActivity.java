package cn.zerone.water.activity;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.adapter.ApproveAdapter;
import cn.zerone.water.adapter.ApproveItem;
import cn.zerone.water.adapter.FmPagerAdapter;
import cn.zerone.water.fragment.CheckAgreedFragment;
import cn.zerone.water.fragment.CheckRejectedFragment;
import cn.zerone.water.fragment.WaitCheckFragment;
import cn.zerone.water.http.Requests;
import cn.zerone.water.utils.ImageUtil;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class ApproveActivity extends AppCompatActivity {

    private ListView mListView;
    private List<ApproveItem> mMyItemList_wait, mMyItemList_approved, mMyItemList_dismissed;
    private List<Map<String, Object>> mChecklist;
    private final String mPreUrl = "http://47.105.187.185:8011";
    private int mCurrState = 0;
    private int mWait_PageIndex = 1;
    private int mApproved_PageIndex = 1;
    private int mDismissed_PageIndex = 1;
    private final int mPageSize = 10;
    private Button mbut_Wait, mbut_Approved, mbut_Dismissed;
    private SwipeRefreshLayout mSwipe;
    private Handler mHandler;
    private ApproveAdapter mAdapter;
    private boolean approved_loaded = false;
    private boolean dismissed_loaded = false;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private String[] titles = new String[]{"待审批","已通过","已拒绝"};
    private FmPagerAdapter pagerAdapter;
    private ImageView mBack;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    public void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        fragments.add(new WaitCheckFragment());//待审批
        fragments.add(new CheckAgreedFragment());//已同意
        fragments.add(new CheckRejectedFragment());//已拒绝
        for (int i = 0; i < titles.length; i++) {
            mTabLayout.addTab(mTabLayout.newTab());
        }
        mTabLayout.setupWithViewPager(mViewPager, false);
        pagerAdapter = new FmPagerAdapter(fragments, getSupportFragmentManager());
        mViewPager.setAdapter(pagerAdapter);

        for (int i = 0; i < titles.length; i++) {
            mTabLayout.getTabAt(i).setText(titles[i]);
        }

        mBack = (ImageView) findViewById(R.id.back);
        //返回上一级页面
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == 720) {

            //IsComplete = true;
        }
    }

}
