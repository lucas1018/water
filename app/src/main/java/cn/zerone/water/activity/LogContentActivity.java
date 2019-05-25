package cn.zerone.water.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import cn.zerone.water.model.ListViewAdapter;
import cn.zerone.water.model.LogAdapter;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class LogContentActivity extends Activity {
    private ListView listView;
    private List<Map<String, Object>> list;
    private ImageView back =null;
    public  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logcontent);
        back = (ImageView)findViewById(R.id.c_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listView = (ListView)findViewById(R.id.log_name);
        listView.setFooterDividersEnabled(true);
        list = new ArrayList<Map<String, Object>>();
        getData();

    }

    public void getData() {

        Requests.PROJECT_INFO_GetList(new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(JSONArray objects) {
                for (int i = 0; i < objects.size(); i++) {
                    JSONObject json1 = new JSONObject();
                    JSONObject jsonObject = objects.getJSONObject(i);
                    String name = jsonObject.getString("PROJECT_NAME");
                    json1.put("PROJECT_NAME", name);
                    list.add(json1);
                }
                UpdateAdapter(list);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }
    private void UpdateAdapter(List<Map<String, Object>> list) {
        listView.setAdapter(new LogAdapter(this, list));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                JSONObject list1 = JSONObject.parseObject(adapterView.getItemAtPosition(i).toString());
                bundle.putString("PROJECT_NAME", list1.get("PROJECT_NAME").toString());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(3,intent);
                finish();
            }
        });
    }


}
