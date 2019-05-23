package cn.zerone.water.activity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.internal.ListenerClass;
import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import cn.zerone.water.utils.LocationUtil;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.baidu.vi.VIContext.getContext;

public class ClockInHomeActivity extends AppCompatActivity {

    private List<Map<String,String>> datalist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_in_home);

        getList();

        ImageView home_back = (ImageView) findViewById(R.id.home_back);
        home_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageButton button_home = findViewById(R.id.button_home);
        button_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wetherClockIn();
            }
        });

        Button home_history = findViewById(R.id.home_history);
        home_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClockInHomeActivity.this, HistoryHomeActivity.class));
            }
        });

    }

    private void wetherClockIn(){
        Requests.getClockInList(new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(JSONArray objects) {
                Boolean isRepeatClockIn = false;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                String datetime = simpleDateFormat.format(date);
                for(int i = 0; i<objects.size();i++){
                    JSONObject jsonObject = objects.getJSONObject(i);
                    String time = jsonObject.getString("AddTime");
                    String t = time.substring(6, 19);
                    Long timeLong = Long.parseLong(t);
                    Date tmp = new Date(timeLong);
                    String return_date = simpleDateFormat.format(tmp);
                    if(return_date.substring(0, 10).equals(datetime.substring(0,10))){
                        isRepeatClockIn = true;
                        break;
                    }
                }
                if(!isRepeatClockIn){
                    LocationUtil loc = new LocationUtil();
                    loc.initLocationOption(getApplicationContext());

                    String lat = String.valueOf(loc.GetLat());
                    String lng = String.valueOf(loc.GetLng());

                    addClockIn(datetime, lat, lng, "3", "", "");
                }
                else
                    Toast.makeText(ClockInHomeActivity.this,"您今天已打卡，请勿重复操作", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        },App.userId);
    }

    private void getList(){
        Requests.getClockInList(new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(JSONArray objects) {

                GridView home_list=(GridView) findViewById(R.id.home_list);
                datalist = new ArrayList<Map<String,String>>();
                for(int i = 0; i<objects.size();i++){
                    if(i == 0){
                        Map<String,String> item=new HashMap<String,String>();
                        item.put("Item", "日期");
                        datalist.add(item);
                        Map<String,String> item2=new HashMap<String,String>();
                        item2.put("Item", "到家时间");
                        datalist.add(item2);
                    }
                    else {
                        Map<String, String> item = new HashMap<String, String>();
                        Map<String,String> item2=new HashMap<String,String>();
                        JSONObject jsonObject = objects.getJSONObject(i);
                        String time = jsonObject.getString("AddTime");
                        String t = time.substring(6, 19);
                        Long timeLong = Long.parseLong(t);
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(timeLong);
                        String tt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
                        item.put("Item", tt.substring(0, 10));
                        datalist.add(item);
                        item2.put("Item", tt.substring(11, 19));
                        datalist.add(item2);
                    }
                }
                home_list.setAdapter(new SimpleAdapter(getApplicationContext(), datalist,
                        R.layout.clock_in_grid, new String[]{"Item"}, new int[]{R.id.ItemText}));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        },App.userId);
    }

    private void addClockIn(String add_time, String latitude, String longitude, String data_type,String pic, String address) {
        Requests.ClockIn_SaveBLL(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String str) {
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Toast.makeText(ClockInHomeActivity.this,"到家打卡成功", Toast.LENGTH_SHORT).show();
            }
        },App.userId, add_time, latitude, longitude, data_type, pic, address);

    }
}
