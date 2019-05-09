package cn.zerone.water.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;

import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by litinghui on 2019/5/9.
 */

public class CheckActivity extends AppCompatActivity {
    private String[] data = {"166","255","333","444","555","6","77","88","19999","789789","177","182","441","4641"};

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

        Requests.getCheckedList(new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONArray objects) {
                System.out.println("-----------------------------------");

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                System.out.println("~~~~!@#$%^&*(&^%$#!@~@#$%^&*()*&^%$#@!~");
            }
        });

    }
}
