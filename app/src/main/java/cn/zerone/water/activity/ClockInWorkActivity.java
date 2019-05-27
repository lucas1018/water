package cn.zerone.water.activity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.zxing.cameraapplication.CameraActivity;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import cn.zerone.water.utils.LocationUtil;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ClockInWorkActivity extends AppCompatActivity {

    private String morningClockPermissionTime = "09:30:00";
    private String afterClockEndTime = "17:00:00";
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

    private Boolean morningPictureCaptured = false;
    private Boolean afternoonPictureCaptured = false;

    // 判断是否已打卡
    private Boolean morningClockIn = false;
    private Boolean afternoonClockIn = false;

    private String morningPictureCapturedPath;
    private String afternoonPictureCapturedPath;
    private String basicPicturePath = "/storage/emulated/0/JCamera/picture_";

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

                    TextView morningrelocation = findViewById(R.id.morningrelocation);
                    morningrelocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            morningloc = new LocationUtil();
                            morningloc.initLocationOption(getApplicationContext());

                            TextView location = findViewById(R.id.morninglocation);
                            location.setText(morningloc.GetAddrStr());
                        }
                    });

                    final ImageButton morningimageButton = findViewById(R.id.morningimageButton);
                    morningimageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 上午打卡拍照
                            morningPictureCaptured = true;
                            startActivityForResult(new Intent(ClockInWorkActivity.this, CameraActivity.class), 1);

                        }
                    });

                    Button clockIn = findViewById(R.id.clockIn);
                    clockIn.setText("上班打卡");
                    clockIn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 上午打卡
                            if(date.getTime() > morningpermission.getTime())
                                Toast.makeText(ClockInWorkActivity.this,"您已错过今天的打卡时间，打卡无效", Toast.LENGTH_SHORT).show();
                            else if(!morningPictureCaptured)
                                Toast.makeText(ClockInWorkActivity.this,"请先完成拍照后在打卡", Toast.LENGTH_SHORT).show();
                            else{
                                if(!morningClockIn){
                                    morningClockIn = true;
                                    Pattern pattern = Pattern.compile("[0-9][0-9].+");
                                    Matcher matcher = pattern.matcher(morningPictureCapturedPath);
                                    if(matcher.find())
                                        morningPictureCapturedPath = matcher.group(0);
                                    addClockIn(datetime, String.valueOf(morningloc.GetLat()), String.valueOf(morningloc.GetLng()), "0",morningPictureCapturedPath, "");
                                    Toast.makeText(ClockInWorkActivity.this,"上班打卡成功", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(ClockInWorkActivity.this,"您上班已打卡，请勿重复操作", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                // 下班打卡
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

                    ImageView morningImage = findViewById(R.id.morningImage);
                    morningImage.setVisibility(View.VISIBLE);
                    morningImage.setImageURI(Uri.fromFile(new File(basicPicturePath + clocklist.get(0).getString("Path"))));

                    TextView morningrelocation = findViewById(R.id.morningrelocation);
                    morningrelocation.setVisibility(View.INVISIBLE);

                    // 照片
                    LinearLayout afterLayout = findViewById(R.id.afterLayout);
                    afterLayout.setVisibility(View.VISIBLE);

                    // 下午未打
                    TextView afternoonNow = findViewById(R.id.afterNow);
                    afternoonNow.setText("打卡时间 "+ datetime.substring(11,16));

                    TextView afternoonlocation = findViewById(R.id.afterlocation);
                    afternoonlocation.setText(morningloc.GetAddrStr());

                    TextView afterrelocation = findViewById(R.id.afterrelocation);
                    afterrelocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            afternoonloc = new LocationUtil();
                            afternoonloc.initLocationOption(getApplicationContext());

                            TextView location = findViewById(R.id.afterlocation);
                            location.setText(afternoonloc.GetAddrStr());
                        }
                    });

                    ImageButton afterimageButton = findViewById(R.id.afterimageButton);
                    afterimageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 下午打卡拍照
                            afternoonPictureCaptured = true;
                            startActivityForResult(new Intent(ClockInWorkActivity.this, CameraActivity.class), 2);
                        }
                    });

                    Button clockIn = findViewById(R.id.clockIn);
                    clockIn.setText("下班打卡");
                    clockIn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 下午打卡
                            if(date.getTime() < afterpermission.getTime())
                                Toast.makeText(ClockInWorkActivity.this,"还未到下午打卡时间", Toast.LENGTH_SHORT).show();
                            else if(!afternoonPictureCaptured)
                                Toast.makeText(ClockInWorkActivity.this,"请先完成拍照后在打卡", Toast.LENGTH_SHORT).show();
                            else{
                                if(!afternoonClockIn){
                                    afternoonClockIn = true;
                                    Pattern pattern = Pattern.compile("[0-9][0-9].+");
                                    Matcher matcher = pattern.matcher(afternoonPictureCapturedPath);
                                    if(matcher.find())
                                        afternoonPictureCapturedPath = matcher.group(0);
                                    addClockIn(datetime, String.valueOf(afternoonloc.GetLat()),
                                            String.valueOf(afternoonloc.GetLng()), "0",afternoonPictureCapturedPath, "");
                                    Toast.makeText(ClockInWorkActivity.this,"下班打卡成功", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(ClockInWorkActivity.this,"您下班已打卡，请勿重复操作", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                // 查看
                else{

                    TextView dateString = findViewById(R.id.dateString);
                    dateString.setText(datetime.substring(0,10));

                    // 上午已打
                    TextView morningNow = findViewById(R.id.morningNow);
                    morningNow.setText("打卡时间 "+ morningtime.substring(11,16));

                    TextView morninglocation = findViewById(R.id.morninglocation);
                    morninglocation.setText(clocklist.get(0).getString("Address"));

                    TextView morningrelocation = findViewById(R.id.morningrelocation);
                    morningrelocation.setVisibility(View.INVISIBLE);

                    ImageButton morningimageButton = findViewById(R.id.morningimageButton);
                    morningimageButton.setVisibility(View.GONE);

                    ImageView morningImage = findViewById(R.id.morningImage);
                    morningImage.setVisibility(View.VISIBLE);
                    morningImage.setImageURI(Uri.fromFile(new File(basicPicturePath + clocklist.get(0).getString("Path"))));

                    // 下午已打
                    LinearLayout afterLayout = findViewById(R.id.afterLayout);
                    afterLayout.setVisibility(View.VISIBLE);

                    TextView afternoonNow = findViewById(R.id.afterNow);
                    afternoonNow.setText("打卡时间 "+ afternoontime.substring(11,16));

                    TextView afterrelocation = findViewById(R.id.afterrelocation);
                    afterrelocation.setVisibility(View.INVISIBLE);

                    TextView afternoonlocation = findViewById(R.id.afterlocation);
                    afternoonlocation.setText(clocklist.get(1).getString("Address"));

                    ImageButton afterimageButton = findViewById(R.id.afterimageButton);
                    afterimageButton.setVisibility(View.GONE);

                    ImageView afterImage = findViewById(R.id.afterImage);
                    afterImage.setVisibility(View.VISIBLE);
                    afterImage.setImageURI(Uri.fromFile(new File(basicPicturePath + clocklist.get(1).getString("Path"))));


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String path = data.getStringExtra("path");
        if (requestCode == 1 && resultCode == 101) {
            morningPictureCapturedPath = path;

            ImageButton morningimageButton = findViewById(R.id.morningimageButton);
            morningimageButton.setVisibility(View.GONE);

            ImageView morningImage = findViewById(R.id.morningImage);
            morningImage.setVisibility(View.VISIBLE);
            morningImage.setImageURI(Uri.fromFile(new File(path)));
        }
        if (requestCode == 2 && resultCode == 101) {
            afternoonPictureCapturedPath = path;

            ImageButton afterimageButton = findViewById(R.id.afterimageButton);
            afterimageButton.setVisibility(View.GONE);

            ImageView afterImage = findViewById(R.id.afterImage);
            afterImage.setVisibility(View.VISIBLE);
            afterImage.setImageURI(Uri.fromFile(new File(path)));
        }
    }
}
