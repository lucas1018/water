package cn.zerone.water.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.adapter.ListViewAdapter;
import cn.zerone.water.adapter.ProblemAdapter;
import cn.zerone.water.fragment.MasterArticleFragment;
import cn.zerone.water.http.Requests;
import okhttp3.RequestBody;

/**
 * Created by litinghui on 2019/5/29.
 * 问题记录表
 */

public class ProblemListActivity extends AppCompatActivity {

    private ListView listView;
    private List<Map<String, Object>> list;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_list);

        listView = (ListView) findViewById(R.id.problem_listView);
        list = new ArrayList<Map<String, Object>>();

        ImageView iv = (ImageView)findViewById(R.id.pro_back);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProblemListActivity.this, MainActivity.class));
            }
        });

        TextView tv = (TextView)findViewById(R.id.pro_add);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProblemListActivity.this, ProblemActivity.class));
            }
        });

        //获取所有的问题列表，填充list
        JSONArray problems = Requests.getProblems(App.userId);
        for (int i = 0 ; i < problems.size() ; i++){
            JSONObject json1 = new JSONObject();
            JSONObject jsonObject = problems.getJSONObject(i);

            String title = jsonObject.getString("Title");
            json1.put("Title", title);

            String time = jsonObject.getString("AddTime");
            String realTime = time.substring(6, 19);
            Long longtime = Long.parseLong(realTime);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String d = format.format(longtime);
            json1.put("AddTime", d);

            String content = jsonObject.getString("Remark");
            json1.put("Remark", content);

            String typeId = jsonObject.getString("TreeInfoId");
            JSONObject proTypeNameById = Requests.getProTypeNameById(typeId);
            String typeName = proTypeNameById.getString("Name");
            json1.put("typeName", typeName);

            //获取问题的主键ID
            String id = jsonObject.getString("ID");
            json1.put("ID",id);

            list.add(json1);
        }

        listView.setAdapter(new ProblemAdapter(ProblemListActivity.this, list));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();

                JSONObject list1 = JSONObject.parseObject(adapterView.getItemAtPosition(i).toString());

                bundle.putString("Title", list1.get("Title").toString());
                bundle.putString("AddTime", list1.get("AddTime").toString());
                bundle.putString("Remark", list1.get("Remark").toString());
                bundle.putString("typeName", list1.get("typeName").toString());

                bundle.putString("ID",list1.get("ID").toString());

                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(ProblemListActivity.this, ProblemDetailActivity.class);
                startActivity(intent);

            }
        });


    }





}
