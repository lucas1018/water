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

public class ClockInGetTypeActivity extends AppCompatActivity {

    private ListView typeList;
    private List<Map<String,String>> typeItem;
    private String[] types = {"出发","到达","收工"};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_in_car_list);

        ImageView listBack = findViewById(R.id.listBack);
        listBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title = findViewById(R.id.title);
        title.setText("请选择打卡类型");

        typeItem = new ArrayList<Map<String,String>>();
        for(int i=0;i<types.length;i++){
            Map<String, String> item = new HashMap<String, String>();
            item.put("Item",types[i]);
            typeItem.add(item);
        }
        typeList = findViewById(R.id.carClockInList);
        typeList.setAdapter(new SimpleAdapter(getApplicationContext(), typeItem,
                R.layout.clock_in_item, new String[]{"Item"}, new int[]{R.id.clockInItem}));

        typeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //position 点击的Item位置，从0开始算
                //Toast.makeText(ClockInGetCatNumberActivity.this,cars.getJSONObject(position).getString("CarNember") , Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("type", String.valueOf(position));
                setResult(920, intent);
                finish();
            }
        });
    }
}
