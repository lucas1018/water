package cn.zerone.water.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;

import cn.zerone.water.R;
import cn.zerone.water.fragment.CheckedFragment;
import cn.zerone.water.fragment.UncheckedFragment;
import cn.zerone.water.http.Requests;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by litinghui on 2019/5/9.
 */

public class CheckActivity extends AppCompatActivity implements View.OnClickListener{
    private String[] data = {"已审核1","已审核2","已审核3","已审核4","已审核5","已审核6","已审核7","已审核8","已审核9","已审核10","已审核11","已审核12","已审核13","已审核14"};

    private ViewPager checkViewPager;

    private FragmentPagerAdapter mAdapter;

    private List<Fragment> checkFragments;

    private LinearLayout checkLayout;
    private LinearLayout uncheckLayout;


    private ImageButton checkButton;
    private ImageButton uncheckButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        //返回上一级页面
        ImageView meal_back = (ImageView) findViewById(R.id.meal_back);
        meal_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ArrayAdapter<String> datas = new ArrayAdapter<String>(CheckActivity.this, android.R.layout.simple_list_item_1,data);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(datas);




    }

    @Override
    public void onClick(View view) {

    }

    public void initDatas(){
        checkFragments = new ArrayList<>();
        checkFragments.add(new CheckedFragment());
        checkFragments.add(new UncheckedFragment());



    }

    public void initViews(){
        checkViewPager = (ViewPager)findViewById(R.id.viewfinder_view);
    }
}
