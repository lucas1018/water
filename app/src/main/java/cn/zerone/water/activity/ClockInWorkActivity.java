package cn.zerone.water.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import cn.zerone.water.utils.LocationUtil;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ClockInWorkActivity extends AppCompatActivity {

    private String morningClockPermissionTime = "09:30:00";
    private String afterClockEndTime = "17:30:00";
    private List<JSONObject> clocklist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_in_work);

        ImageView home_back = findViewById(R.id.work_back);
        home_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        showUI();

    }

    void isClockInMorning(){
        Requests.getClockInList(new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(JSONArray objects) {
                Boolean isClockInMorning = false;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                String datetime = simpleDateFormat.format(date);
                datetime = datetime.replaceAll(".{2}:.{2}:.{2}", morningClockPermissionTime);
                Date morningpermission = null;
                try {
                    morningpermission = simpleDateFormat.parse(datetime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                for(int i = 0; i<objects.size();i++){
                    JSONObject jsonObject = objects.getJSONObject(i);
                    String time = jsonObject.getString("AddTime");
                    String t = time.substring(6, 19);
                    Long timeLong = Long.parseLong(t);
                    Date tmp = new Date(timeLong);
                    String return_date = simpleDateFormat.format(tmp);
                    if(return_date.substring(0, 10).equals(datetime.substring(0,10)) && tmp.getTime() < morningpermission.getTime()){
                        isClockInMorning = true;
                        datetime = return_date;
                        break;
                    }
                }
                if(!isClockInMorning){

                    //get location
                    LocationUtil loc = new LocationUtil();
                    loc.initLocationOption(getApplicationContext());

                    date = new Date(System.currentTimeMillis());
                    String now = simpleDateFormat.format(date);

                    TextView dateString = findViewById(R.id.dateString);
                    dateString.setText(now.substring(0,10));

                    TextView morningNow = findViewById(R.id.morningNow);
                    morningNow.setText("打卡时间 "+ now.substring(11,16));

                    TextView location = findViewById(R.id.morninglocation);
                    location.setText(loc.GetAddrStr());

                    Button clockIn = findViewById(R.id.clockIn);
                    clockIn.setText("上班打卡");
                    clockIn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LinearLayout after = findViewById(R.id.afterLayout);
                            after.setVisibility(View.VISIBLE);
                        }
                    });
                }
                else{
                    LocationUtil loc = new LocationUtil();
                    loc.initLocationOption(getApplicationContext());

                    TextView dateString = findViewById(R.id.dateString);
                    dateString.setText(datetime.substring(0,10));

                    TextView morningNow = findViewById(R.id.morningNow);
                    morningNow.setText("打卡时间 "+ datetime.substring(11,16));

                    TextView location = findViewById(R.id.morninglocation);
                    location.setText(loc.GetAddrStr());

                    Button clockIn = findViewById(R.id.clockIn);
                    clockIn.setText("下班打卡");
                    clockIn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LinearLayout after = findViewById(R.id.afterLayout);
                            after.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        },App.userId);
    }

    void showUI(){
        Requests.getClockInList(new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(JSONArray objects) {
                Boolean isClockInMorning = false;
                Boolean isClockInAfternoon = false;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                String datetime = simpleDateFormat.format(date);

                String morningtime = datetime.replaceAll(".{2}:.{2}:.{2}", morningClockPermissionTime);
                String afternoontime = datetime.replaceAll(".{2}:.{2}:.{2}", afterClockEndTime);

                Date afterpermission = null;
                Date morningpermission = null;
                try {
                    morningpermission = simpleDateFormat.parse(morningtime);
                    afterpermission = simpleDateFormat.parse(afternoontime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                for(int i = 0; i<objects.size();i++){
                    JSONObject jsonObject = objects.getJSONObject(i);
                    String time = jsonObject.getString("AddTime");
                    String t = time.substring(6, 19);
                    Long timeLong = Long.parseLong(t);
                    Date tmp = new Date(timeLong);
                    String return_date = simpleDateFormat.format(tmp);

                    if(return_date.substring(0, 10).equals(datetime.substring(0,10))){
                        if(tmp.getTime() > morningpermission.getTime()){
                            isClockInMorning = true;
                            clocklist.add(jsonObject);
                            morningtime = return_date;
                        }
                        if(tmp.getTime() > afterpermission.getTime()){
                            isClockInAfternoon = true;
                            clocklist.add(jsonObject);
                            afternoontime = return_date;
                            break;
                        }
                    }
                }
                if(!isClockInAfternoon && !isClockInMorning ){

                    //get location
                    LocationUtil loc = new LocationUtil();
                    loc.initLocationOption(getApplicationContext());

                    date = new Date(System.currentTimeMillis());
                    String now = simpleDateFormat.format(date);

                    TextView dateString = findViewById(R.id.dateString);
                    dateString.setText(now.substring(0,10));

                    TextView morningNow = findViewById(R.id.morningNow);
                    morningNow.setText("打卡时间 "+ now.substring(11,16));

                    TextView location = findViewById(R.id.morninglocation);
                    location.setText(loc.GetAddrStr());

                    Button clockIn = findViewById(R.id.clockIn);
                    clockIn.setText("上班打卡");
                    clockIn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 上午打卡

                            LinearLayout after = findViewById(R.id.afterLayout);
                            after.setVisibility(View.VISIBLE);
                        }
                    });
                }
                else if(!isClockInAfternoon && isClockInMorning){
                    LocationUtil loc = new LocationUtil();
                    loc.initLocationOption(getApplicationContext());

                    TextView dateString = findViewById(R.id.dateString);
                    dateString.setText(datetime.substring(0,10));

                    // 上午已打
                    TextView morningNow = findViewById(R.id.morningNow);
                    morningNow.setText("打卡时间 "+ morningtime.substring(11,16));

                    TextView morninglocation = findViewById(R.id.morninglocation);
                    morninglocation.setText(clocklist.get(0).getString("Address"));

                    // 下午未打
                    TextView afternoonNow = findViewById(R.id.afterNow);
                    morningNow.setText("打卡时间 "+ datetime.substring(11,16));

                    TextView afternoonlocation = findViewById(R.id.afterlocation);
                    afternoonlocation.setText(loc.GetAddrStr());

                    Button clockIn = findViewById(R.id.clockIn);
                    clockIn.setText("下班打卡");
                    clockIn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 下午打卡

                            LinearLayout after = findViewById(R.id.afterLayout);
                            after.setVisibility(View.VISIBLE);
                        }
                    });
                }
                else{
                    LocationUtil loc = new LocationUtil();
                    loc.initLocationOption(getApplicationContext());

                    TextView dateString = findViewById(R.id.dateString);
                    dateString.setText(datetime.substring(0,10));

                    // 上午已打
                    TextView morningNow = findViewById(R.id.morningNow);
                    morningNow.setText("打卡时间 "+ morningtime.substring(11,16));

                    TextView morninglocation = findViewById(R.id.morninglocation);
                    morninglocation.setText(clocklist.get(0).getString("Address"));

                    // 下午已打
                    TextView afternoonNow = findViewById(R.id.afterNow);
                    afternoonNow.setText("打卡时间 "+ afternoontime.substring(11,16));

                    TextView afternoonlocation = findViewById(R.id.afterlocation);
                    afternoonlocation.setText(clocklist.get(1).getString("Address"));


                    // 查看
                    LinearLayout clockInLayout = findViewById(R.id.clockInLayout);
                    clockInLayout.setVisibility(View.GONE);

                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        },App.userId);
    }
}
