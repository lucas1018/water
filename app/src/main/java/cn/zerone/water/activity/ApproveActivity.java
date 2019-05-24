package cn.zerone.water.activity;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.adapter.ApproveAdapter;
import cn.zerone.water.adapter.ApproveItem;
import cn.zerone.water.fragment.CheckedFragment;
import cn.zerone.water.fragment.MyItem;
import cn.zerone.water.fragment.UncheckedFragment;
import cn.zerone.water.http.Requests;
import cn.zerone.water.utils.ImageUtil;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class ApproveActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView mListView;
    private List<ApproveItem> mMyItemList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);

        mListView = findViewById(R.id.appr_list_view);

        //返回上一级页面
        ImageView mBack = (ImageView) findViewById(R.id.back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //initDatas();

    }

    @Override
    public void onClick(View view) {

    }

    public void initDatas(){


        Requests.getApproveInfo(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(JSONObject jsonObject) {

                String str = jsonObject.getString("Data");
                JSONObject json = JSONArray.parseArray(str).getJSONObject(0);

                String username = json.getString("ApplyUserId");
                System.out.println("============");
                System.out.println(username);

                /*String imgUrl = json.getString("Photo");
                System.out.println("============");
                ImageUtil imageUtil = ImageUtil.getIntance();
                Bitmap temp_bitmap = imageUtil.getBitMBitmap(imgUrl);
                Bitmap bitmap = imageUtil.comp(temp_bitmap);
                photo1.setImageBitmap(bitmap);*/


            }
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
            @Override
            public void onComplete() {
            }
        },App.userId, 0, 1, 20);




        /*mListView.setAdapter(new ApproveAdapter(getApplicationContext(), R.layout.approve_item, mMyItemList));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(ApproveActivity.this, "审批条目", Toast.LENGTH_LONG).show();


            }
        });*/



    }


}
