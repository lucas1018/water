package cn.zerone.water.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

public class ClockInGetCatNumberActivity extends AppCompatActivity {

    private List<Map<String,String>> carNumber;
    private ListView carList;
    private JSONArray cars;

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
        title.setText("请选择车牌号");

        carList = findViewById(R.id.carClockInList);
        Requests.getCarList(new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(JSONArray objects) {
                carNumber= new ArrayList<Map<String,String>>();
                cars = objects;
                for(int i = 0; i<objects.size();i++){
                    Map<String, String> item = new HashMap<String, String>();
                    JSONObject jsonObject = objects.getJSONObject(i);
                    String car = jsonObject.getString("CarNember");
                    item.put("Item",car);
                    carNumber.add(item);
                }
                carList.setAdapter(new SimpleAdapter(getApplicationContext(), carNumber,
                        R.layout.clock_in_item, new String[]{"Item"}, new int[]{R.id.clockInItem}));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        },App.userId);

        carList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //position 点击的Item位置，从0开始算
                //Toast.makeText(ClockInGetCatNumberActivity.this,cars.getJSONObject(position).getString("CarNember") , Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("number", cars.getJSONObject(position).getString("CarNember"));
                intent.putExtra("type", cars.getJSONObject(position).getString("CarType"));
                intent.putExtra("carID", cars.getJSONObject(position).getString("ID"));
                setResult(620, intent);
                finish();
            }
        });
    }
}
