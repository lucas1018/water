package cn.zerone.water.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
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
import cn.zerone.water.utils.image2Base64Util;
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
    private String basicPicturePath = "http://47.105.187.185:8011/Content/img/WebImg/";
    private String fakeMorningPic;
    private String fakeAfternoonPic;
    private String morningBase64;
    private String afternoonBase64;

    private Boolean dateChanged = false;
    private String selectedDateByCalendar;

    private image2Base64Util img2base;

    // 各个控件
    TextView dateString;
    ImageView morningIcon;
    ImageView afternoonIcon;
    LinearLayout clockInLayout;
    TextView morningNow;
    TextView morningrelocation;
    ImageButton morningimageButton;
    Button clockIn;
    TextView morninglocation;
    ImageView morningImage;
    LinearLayout afterLayout;
    TextView afternoonNow;
    TextView afternoonlocation;
    TextView afterrelocation;
    ImageButton afterimageButton;
    ImageView afterImage;

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

        img2base = new image2Base64Util();

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

        // 日历选择
        // calendar
        // setClickListen()->{ calendarChange(); }

        showUI();

    }

    void calendarChange(){
        Requests.getClockInList(new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(JSONArray objects) {
                Boolean isClockInMorning = false;
                Boolean isClockInAfternoon = false;

                clocklist = new ArrayList<JSONObject>();

                // 通过选择日历获取日期
                // datetime = selectedDateByCalendar; 2019-05-02

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
                            morningPictureCapturedPath = jsonObject.getString("Path");
                        }
                        if(tmp.getTime() > afterpermission.getTime()){
                            isClockInAfternoon = true;
                            clocklist.add(jsonObject);
                            afternoontime = return_date;
                            afternoonPictureCapturedPath = jsonObject.getString("Path");
                            break;
                        }
                    }
                }
                clockInLayout = findViewById(R.id.clockInLayout);
                clockInLayout.setVisibility(View.GONE);

                morningIcon = findViewById(R.id.morningBall);
                morningIcon.setImageResource(R.mipmap.grey_ball);

                afternoonIcon = findViewById(R.id.afternoonBall);
                afternoonIcon.setImageResource(R.mipmap.grey_ball);
                if(!isClockInMorning&&!isClockInAfternoon){
                    dateString = findViewById(R.id.dateString);
                    dateString.setText(selectedDateByCalendar.substring(0,10));

                    // 上午
                    morningNow = findViewById(R.id.morningNow);
                    morningNow.setText("该天未打卡");

                    morninglocation = findViewById(R.id.morninglocation);
                    morninglocation.setText("未知地点");

                    morningrelocation = findViewById(R.id.morningrelocation);
                    morningrelocation.setVisibility(View.GONE);

                    morningimageButton = findViewById(R.id.morningimageButton);
                    morningimageButton.setVisibility(View.GONE);

                    // 下午
                    afterLayout = findViewById(R.id.afterLayout);
                    afterLayout.setVisibility(View.VISIBLE);

                    afternoonNow = findViewById(R.id.afterNow);
                    afternoonNow.setText("该天未打卡");

                    afterrelocation = findViewById(R.id.afterrelocation);
                    afterrelocation.setVisibility(View.GONE);

                    afternoonlocation = findViewById(R.id.afterlocation);
                    afternoonlocation.setText("未知地点");

                    afterimageButton = findViewById(R.id.afterimageButton);
                    afterimageButton.setVisibility(View.GONE);

                }
                else if(isClockInMorning&&!isClockInAfternoon){
                    dateString = findViewById(R.id.dateString);
                    dateString.setText(selectedDateByCalendar.substring(0,10));

                    // 上午
                    morningNow = findViewById(R.id.morningNow);
                    morningNow.setText("打卡时间 "+ morningtime.substring(11,16));

                    morningIcon = findViewById(R.id.morningBall);
                    morningIcon.setImageResource(R.mipmap.grey_ball);

                    afternoonIcon = findViewById(R.id.afternoonBall);
                    afternoonIcon.setImageResource(R.mipmap.blue_ball);

                    morninglocation = findViewById(R.id.morninglocation);
                    morninglocation.setText(clocklist.get(0).getString("Address"));


                    morningimageButton = findViewById(R.id.morningimageButton);
                    morningimageButton.setVisibility(View.GONE);

                    morningImage = findViewById(R.id.morningImage);
                    morningImage.setVisibility(View.VISIBLE);

                    morningPictureCapturedPath = basicPicturePath + morningPictureCapturedPath;
                    morningBase64 = img2base.encodeImageToBase64(morningPictureCapturedPath);
                    byte[] decodedString = Base64.decode(morningBase64, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    morningImage.setImageBitmap(decodedByte);


                    morningrelocation = findViewById(R.id.morningrelocation);
                    morningrelocation.setVisibility(View.INVISIBLE);

                    // 下午
                    afterLayout = findViewById(R.id.afterLayout);
                    afterLayout.setVisibility(View.VISIBLE);

                    afternoonNow = findViewById(R.id.afterNow);
                    afternoonNow.setText("该天未打卡");

                    afterrelocation = findViewById(R.id.afterrelocation);
                    afterrelocation.setVisibility(View.GONE);

                    afternoonlocation = findViewById(R.id.afterlocation);
                    afternoonlocation.setText("未知地点");

                    afterimageButton = findViewById(R.id.afterimageButton);
                    afterimageButton.setVisibility(View.GONE);
                }
                else{
                    dateString = findViewById(R.id.dateString);
                    dateString.setText(datetime.substring(0,10));

                    // 上午已打
                    morningNow = findViewById(R.id.morningNow);
                    morningNow.setText("打卡时间 "+ morningtime.substring(11,16));

                    morninglocation = findViewById(R.id.morninglocation);
                    morninglocation.setText(clocklist.get(0).getString("Address"));

                    morningIcon = findViewById(R.id.morningBall);
                    morningIcon.setImageResource(R.mipmap.grey_ball);

                    afternoonIcon = findViewById(R.id.afternoonBall);
                    afternoonIcon.setImageResource(R.mipmap.grey_ball);

                    morningrelocation = findViewById(R.id.morningrelocation);
                    morningrelocation.setVisibility(View.INVISIBLE);

                    morningimageButton = findViewById(R.id.morningimageButton);
                    morningimageButton.setVisibility(View.GONE);

                    morningImage = findViewById(R.id.morningImage);
                    morningImage.setVisibility(View.VISIBLE);
                    morningPictureCapturedPath = basicPicturePath + morningPictureCapturedPath;
                    morningBase64 = img2base.encodeImageToBase64(morningPictureCapturedPath);
                    byte[] decodedString = Base64.decode(morningBase64, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    morningImage.setImageBitmap(decodedByte);

                    // 下午已打
                    afterLayout = findViewById(R.id.afterLayout);
                    afterLayout.setVisibility(View.VISIBLE);

                    afternoonNow = findViewById(R.id.afterNow);
                    afternoonNow.setText("打卡时间 "+ afternoontime.substring(11,16));

                    afterrelocation = findViewById(R.id.afterrelocation);
                    afterrelocation.setVisibility(View.INVISIBLE);

                    afternoonlocation = findViewById(R.id.afterlocation);
                    afternoonlocation.setText(clocklist.get(1).getString("Address"));

                    afterimageButton = findViewById(R.id.afterimageButton);
                    afterimageButton.setVisibility(View.GONE);

                    afterImage = findViewById(R.id.afterImage);
                    afterImage.setVisibility(View.VISIBLE);

                    afternoonPictureCapturedPath = basicPicturePath + afternoonPictureCapturedPath;
                    afternoonBase64 = img2base.encodeImageToBase64(afternoonPictureCapturedPath);
                    byte[] decodedStringAfter = Base64.decode(afternoonBase64, Base64.DEFAULT);
                    Bitmap decodedByteAfter = BitmapFactory.decodeByteArray(decodedStringAfter, 0, decodedStringAfter.length);
                    afterImage.setImageBitmap(decodedByteAfter);
                }
                dateChanged = false;
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        },App.userId, "0");
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

                clocklist = new ArrayList<JSONObject>();

                // 通过选择日历获取历史
                if(dateChanged)
                    datetime = selectedDateByCalendar;

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
                            morningPictureCapturedPath = jsonObject.getString("Path");
                        }
                        if(tmp.getTime() > afterpermission.getTime()){
                            isClockInAfternoon = true;
                            clocklist.add(jsonObject);
                            afternoontime = return_date;
                            afternoonPictureCapturedPath = jsonObject.getString("Path");
                            break;
                        }
                    }
                }
                // 上班打卡
                if(!isClockInAfternoon && !isClockInMorning ){

                    dateString = findViewById(R.id.dateString);
                    dateString.setText(datetime.substring(0,10));

                    morningIcon = findViewById(R.id.morningBall);
                    morningIcon.setImageResource(R.mipmap.blue_ball);

                    afternoonIcon = findViewById(R.id.afternoonBall);
                    afternoonIcon.setImageResource(R.mipmap.grey_ball);

                    morningNow = findViewById(R.id.morningNow);
                    morningNow.setText("打卡时间 "+ datetime.substring(11,16));

                    morninglocation = findViewById(R.id.morninglocation);
                    morninglocation.setText(morningloc.GetAddrStr());

                    morningrelocation = findViewById(R.id.morningrelocation);
                    morningrelocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            morningloc = new LocationUtil();
                            morningloc.initLocationOption(getApplicationContext());

                            morninglocation = findViewById(R.id.morninglocation);
                            morninglocation.setText(morningloc.GetAddrStr());
                        }
                    });

                    morningimageButton = findViewById(R.id.morningimageButton);
                    morningimageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 上午打卡拍照
                            morningPictureCaptured = true;
                            startActivityForResult(new Intent(ClockInWorkActivity.this, CameraActivity.class), 1);

                        }
                    });

                    clockIn = findViewById(R.id.clockIn);
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
                                    morningBase64 = img2base.getBase64(morningPictureCapturedPath);
                                    Requests.Picture_SaveBLL(new Observer<JSONObject>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onNext(JSONObject object) {
                                            morningPictureCapturedPath = object.getString("Temp");
                                            fakeMorningPic = img2base.getPicName(morningPictureCapturedPath);
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                        }

                                        @Override
                                        public void onComplete() {
                                            addClockIn(datetime, String.valueOf(morningloc.GetLat()), String.valueOf(morningloc.GetLng()), "0",fakeMorningPic, "");
                                        }
                                    },morningBase64,"jpg");
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


                    dateString = findViewById(R.id.dateString);
                    dateString.setText(datetime.substring(0,10));

                    // 上午已打
                    morningNow = findViewById(R.id.morningNow);
                    morningNow.setText("打卡时间 "+ morningtime.substring(11,16));

                    morningIcon = findViewById(R.id.morningBall);
                    morningIcon.setImageResource(R.mipmap.grey_ball);

                    afternoonIcon = findViewById(R.id.afternoonBall);
                    afternoonIcon.setImageResource(R.mipmap.blue_ball);

                    morninglocation = findViewById(R.id.morninglocation);
                    morninglocation.setText(clocklist.get(0).getString("Address"));


                    morningimageButton = findViewById(R.id.morningimageButton);
                    morningimageButton.setVisibility(View.GONE);

                    morningImage = findViewById(R.id.morningImage);
                    morningImage.setVisibility(View.VISIBLE);

                    morningPictureCapturedPath = basicPicturePath + morningPictureCapturedPath;
                    morningBase64 = img2base.encodeImageToBase64(morningPictureCapturedPath);
                    byte[] decodedString = Base64.decode(morningBase64, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    morningImage.setImageBitmap(decodedByte);


                    morningrelocation = findViewById(R.id.morningrelocation);
                    morningrelocation.setVisibility(View.INVISIBLE);

                    // 照片
                    afterLayout = findViewById(R.id.afterLayout);
                    afterLayout.setVisibility(View.VISIBLE);

                    // 下午未打
                    afternoonNow = findViewById(R.id.afterNow);
                    afternoonNow.setText("打卡时间 "+ datetime.substring(11,16));

                    afternoonlocation = findViewById(R.id.afterlocation);
                    afternoonlocation.setText(morningloc.GetAddrStr());

                    afterrelocation = findViewById(R.id.afterrelocation);
                    afterrelocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            afternoonloc = new LocationUtil();
                            afternoonloc.initLocationOption(getApplicationContext());

                            afternoonlocation = findViewById(R.id.afterlocation);
                            afternoonlocation.setText(afternoonloc.GetAddrStr());
                        }
                    });

                    afterimageButton = findViewById(R.id.afterimageButton);
                    afterimageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 下午打卡拍照
                            afternoonPictureCaptured = true;
                            startActivityForResult(new Intent(ClockInWorkActivity.this, CameraActivity.class), 2);
                        }
                    });

                    clockIn = findViewById(R.id.clockIn);
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
                                    afternoonBase64 = img2base.getBase64(afternoonPictureCapturedPath);
                                    Requests.Picture_SaveBLL(new Observer<JSONObject>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onNext(JSONObject object) {
                                            afternoonPictureCapturedPath = object.getString("Temp");
                                            fakeAfternoonPic = img2base.getPicName(afternoonPictureCapturedPath);
                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onComplete() {
                                            addClockIn(datetime, String.valueOf(afternoonloc.GetLat()),
                                                    String.valueOf(afternoonloc.GetLng()), "0",fakeAfternoonPic, "");
                                        }
                                    },afternoonBase64,"jpg");
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

                    dateString = findViewById(R.id.dateString);
                    dateString.setText(datetime.substring(0,10));

                    // 上午已打
                    morningNow = findViewById(R.id.morningNow);
                    morningNow.setText("打卡时间 "+ morningtime.substring(11,16));

                    morninglocation = findViewById(R.id.morninglocation);
                    morninglocation.setText(clocklist.get(0).getString("Address"));

                    morningIcon = findViewById(R.id.morningBall);
                    morningIcon.setImageResource(R.mipmap.grey_ball);

                    afternoonIcon = findViewById(R.id.afternoonBall);
                    afternoonIcon.setImageResource(R.mipmap.grey_ball);

                    morningrelocation = findViewById(R.id.morningrelocation);
                    morningrelocation.setVisibility(View.INVISIBLE);

                    morningimageButton = findViewById(R.id.morningimageButton);
                    morningimageButton.setVisibility(View.GONE);

                    morningImage = findViewById(R.id.morningImage);
                    morningImage.setVisibility(View.VISIBLE);
                    morningPictureCapturedPath = basicPicturePath + morningPictureCapturedPath;
                    morningBase64 = img2base.encodeImageToBase64(morningPictureCapturedPath);
                    byte[] decodedString = Base64.decode(morningBase64, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    morningImage.setImageBitmap(decodedByte);

                    // 下午已打
                    afterLayout = findViewById(R.id.afterLayout);
                    afterLayout.setVisibility(View.VISIBLE);

                    afternoonNow = findViewById(R.id.afterNow);
                    afternoonNow.setText("打卡时间 "+ afternoontime.substring(11,16));

                    afterrelocation = findViewById(R.id.afterrelocation);
                    afterrelocation.setVisibility(View.INVISIBLE);

                    afternoonlocation = findViewById(R.id.afterlocation);
                    afternoonlocation.setText(clocklist.get(1).getString("Address"));

                    afterimageButton = findViewById(R.id.afterimageButton);
                    afterimageButton.setVisibility(View.GONE);

                    afterImage = findViewById(R.id.afterImage);
                    afterImage.setVisibility(View.VISIBLE);

                    afternoonPictureCapturedPath = basicPicturePath + afternoonPictureCapturedPath;
                    afternoonBase64 = img2base.encodeImageToBase64(afternoonPictureCapturedPath);
                    byte[] decodedStringAfter = Base64.decode(afternoonBase64, Base64.DEFAULT);
                    Bitmap decodedByteAfter = BitmapFactory.decodeByteArray(decodedStringAfter, 0, decodedStringAfter.length);
                    afterImage.setImageBitmap(decodedByteAfter);


                    // 查看
                    clockInLayout = findViewById(R.id.clockInLayout);
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

            morningimageButton = findViewById(R.id.morningimageButton);
            morningimageButton.setVisibility(View.GONE);

            morningImage = findViewById(R.id.morningImage);
            morningImage.setVisibility(View.VISIBLE);
            morningImage.setImageURI(Uri.fromFile(new File(path)));
        }
        if (requestCode == 2 && resultCode == 101) {
            afternoonPictureCapturedPath = path;

            afterimageButton = findViewById(R.id.afterimageButton);
            afterimageButton.setVisibility(View.GONE);

            afterImage = findViewById(R.id.afterImage);
            afterImage.setVisibility(View.VISIBLE);
            afterImage.setImageURI(Uri.fromFile(new File(path)));
        }
    }
}
