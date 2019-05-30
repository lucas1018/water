package cn.zerone.water.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by litinghui on 2019/5/28.
 */

public class ProblemTypeActivity extends AppCompatActivity {

    private List<Map<String,String>> problemType;
    private ListView problemTypeList;
    private JSONArray problemTypes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_type);

        ImageView listBack = findViewById(R.id.listBack);
        listBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title = findViewById(R.id.title);
        title.setText("请选择问题类型");

        problemTypeList = findViewById(R.id.problem_typeList);
        Requests.getProblemTypeList(new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(JSONArray objects) {
                problemType= new ArrayList<Map<String,String>>();
                problemTypes = objects;
                for(int i = 0; i<objects.size();i++){
                    Map<String, String> item = new HashMap<String, String>();
                    JSONObject jsonObject = objects.getJSONObject(i);
                    String typeName = jsonObject.getString("Name");
                    item.put("Item",typeName);
                    problemType.add(item);
                }
                problemTypeList.setAdapter(new SimpleAdapter(getApplicationContext(), problemType,
                        R.layout.problem_type_item, new String[]{"Item"}, new int[]{R.id.project_Item}));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        }, App.userId);

        problemTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //position 点击的Item位置，从0开始算
                //Toast.makeText(ClockInGetCatNumberActivity.this,cars.getJSONObject(position).getString("CarNember") , Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("Name", problemTypes.getJSONObject(position).getString("Name"));
                intent.putExtra("problem_typeID", problemTypes.getJSONObject(position).getString("ID"));
                setResult(720, intent);
                finish();
            }
        });
    }
}
