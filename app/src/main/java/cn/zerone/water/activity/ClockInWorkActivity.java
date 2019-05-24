package cn.zerone.water.activity;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    private String morningClockPermissionTime = "9:45:00";
    private String afterClockEndTime = "17:30:00";
    private List<JSONObject> clocklist;

    // 现在时间
    private Date date;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String datetime;

    // 早晨打卡时间
    private String morningtime;
    // 下午打卡时间
    private String afternoontime;

    // 上下午可打卡时间
    private Date afterpermission;
    private Date morningpermission;

    // 获取经纬度、地点
    private LocationUtil morningloc;
    private LocationUtil afternoonloc;

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
        date = new Date(System.currentTimeMillis());
        datetime = simpleDateFormat.format(date);

        morningtime = datetime.replaceAll(".{2}:.{2}:.{2}", morningClockPermissionTime);
        afternoontime = datetime.replaceAll(".{2}:.{2}:.{2}", afterClockEndTime);

        afterpermission = null;
        morningpermission = null;
        try {
            morningpermission = simpleDateFormat.parse(morningtime);
            afterpermission = simpleDateFormat.parse(afternoontime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        morningloc = new LocationUtil();
        morningloc.initLocationOption(getApplicationContext());

        afternoonloc = new LocationUtil();
        afternoonloc.initLocationOption(getApplicationContext());

        clocklist = new ArrayList<JSONObject>();

        showUI();

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

                for(int i = 0; i<objects.size();i++){
                    JSONObject jsonObject = objects.getJSONObject(i);
                    String time = jsonObject.getString("AddTime");
                    String t = time.substring(6, 19);
                    Long timeLong = Long.parseLong(t);
                    Date tmp = new Date(timeLong);
                    String return_date = simpleDateFormat.format(tmp);

                    if(return_date.substring(0, 10).equals(datetime.substring(0,10))){
                        if(tmp.getTime() < morningpermission.getTime()){
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

                // 上班打卡
                if(!isClockInAfternoon && !isClockInMorning ){

                    TextView dateString = findViewById(R.id.dateString);
                    dateString.setText(datetime.substring(0,10));

                    TextView morningNow = findViewById(R.id.morningNow);
                    morningNow.setText("打卡时间 "+ datetime.substring(11,16));

                    TextView location = findViewById(R.id.morninglocation);
                    location.setText(morningloc.GetAddrStr());

                    Button clockIn = findViewById(R.id.clockIn);
                    clockIn.setText("上班打卡");
                    clockIn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 上午打卡
                            if(date.getTime() > morningpermission.getTime())
                                Toast.makeText(ClockInWorkActivity.this,"您已错过今天的打卡时间，打卡无效", Toast.LENGTH_SHORT).show();
                            else{
                                addClockIn(datetime, String.valueOf(morningloc.GetLat()),
                                        String.valueOf(morningloc.GetLng()), "1","", "");
                                Toast.makeText(ClockInWorkActivity.this,"上班打卡成功", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else if(!isClockInAfternoon && isClockInMorning){


                    TextView dateString = findViewById(R.id.dateString);
                    dateString.setText(datetime.substring(0,10));

                    // 上午已打
                    TextView morningNow = findViewById(R.id.morningNow);
                    morningNow.setText("打卡时间 "+ morningtime.substring(11,16));

                    TextView morninglocation = findViewById(R.id.morninglocation);
                    morninglocation.setText(clocklist.get(0).getString("Address"));

                    ImageButton morningimageButton = findViewById(R.id.morningimageButton);
                    morningimageButton.setVisibility(View.GONE);

                    // 照片

                    LinearLayout afterLayout = findViewById(R.id.afterLayout);
                    afterLayout.setVisibility(View.VISIBLE);

                    // 下午未打
                    TextView afternoonNow = findViewById(R.id.afterNow);
                    afternoonNow.setText("打卡时间 "+ datetime.substring(11,16));

                    TextView afternoonlocation = findViewById(R.id.afterlocation);
                    afternoonlocation.setText(morningloc.GetAddrStr());

                    Button clockIn = findViewById(R.id.clockIn);
                    clockIn.setText("下班打卡");
                    clockIn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 下午打卡
                            if(date.getTime() < afterpermission.getTime())
                                Toast.makeText(ClockInWorkActivity.this,"还未到下午打卡时间", Toast.LENGTH_SHORT).show();
                            else{
                                addClockIn(datetime, String.valueOf(afternoonloc.GetLat()),
                                        String.valueOf(afternoonloc.GetLng()), "1","", "");
                                Toast.makeText(ClockInWorkActivity.this,"下班打卡成功", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{

                    TextView dateString = findViewById(R.id.dateString);
                    dateString.setText(datetime.substring(0,10));

                    // 上午已打
                    TextView morningNow = findViewById(R.id.morningNow);
                    morningNow.setText("打卡时间 "+ morningtime.substring(11,16));

                    TextView morninglocation = findViewById(R.id.morninglocation);
                    morninglocation.setText(clocklist.get(0).getString("Address"));

                    // 下午已打
                    LinearLayout afterLayout = findViewById(R.id.afterLayout);
                    afterLayout.setVisibility(View.VISIBLE);

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
        },App.userId, "0");
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
            }
        },App.userId, add_time, latitude, longitude, data_type, pic, address);

    }
}