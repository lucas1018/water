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

public class LogInGetStationActivity extends AppCompatActivity {

    private List<Map<String,String>> stationItem;
    private ListView stationList;
    private JSONArray stations;

    private String projectID;

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
        title.setText("请选择所属站点");

        stationList = findViewById(R.id.carClockInList);
        projectID = getIntent().getStringExtra("projectID");
        Requests.EngineeringStationList(new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(JSONArray objects) {
                stationItem= new ArrayList<Map<String,String>>();
                stations = objects;
                for(int i = 0; i<objects.size();i++){
                    Map<String, String> item = new HashMap<String, String>();
                    JSONObject jsonObject = objects.getJSONObject(i);
                    String car = jsonObject.getString("StationName");
                    String stationid = jsonObject.getString("EngineeringId");
                    item.put("Item",car);
                    item.put("EngineeringId",stationid);
                    stationItem.add(item);
                }
                stationList.setAdapter(new SimpleAdapter(getApplicationContext(), stationItem,
                        R.layout.clock_in_item, new String[]{"Item"}, new int[]{R.id.clockInItem}));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        },App.userId, projectID);

        stationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //position 点击的Item位置，从0开始算
                //Toast.makeText(ClockInGetCatNumberActivity.this,cars.getJSONObject(position).getString("CarNember") , Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("station", stations.getJSONObject(position).getString("StationName"));
                intent.putExtra("stationID", stations.getJSONObject(position).getString("EngineeringId"));
                setResult(820, intent);
                finish();
            }
        });
    }
}

