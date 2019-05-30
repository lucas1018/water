package cn.zerone.water.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zxing.cameraapplication.CameraActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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

public class ClockInCarActivity extends AppCompatActivity {

    private ImageButton carNumberButton;
    private ImageButton relatedProjectButton;
    private ImageButton relatedStationButton;
    private ImageButton clockInTypeButton;
    private ImageButton carPictureButton;
    private Button clockInButton;
    private TextView carNumber;
    private TextView carType;
    private TextView relatedProject;
    private TextView relatedStation;
    private TextView clockInType;
    private TextView location;
    ImageView carImage;

    private TextView relocation;

    private ImageView car_back;

    private Date date;
    private String datetime;

    private String[] types = {"出发","到达","收工"};
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String number;
    private String cartype;
    private String project;
    private String projectID="";
    private String stationID="";
    private String carID = "";
    private String station;
    private String type;
    private String basicPicturePath = "/storage/emulated/0/JCamera/picture_";
    private String carPictureCapturedPath;

    private Boolean isNumber = false;
    private Boolean isProject = false;
    private Boolean isStation = false;
    private Boolean isType = false;
    private Boolean isPictureCaptured = false;
    private Boolean isRepeatArrival = false;
    private Boolean isRepeatFinish = false;
    private Boolean isRepeatDepart = false;

    private LocationUtil loc;
    private String picBase64;
    private String pic2base64;
    private String base64;

    private image2Base64Util img2base;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_in_car);

        carNumberButton = findViewById(R.id.carNumberButton);
        relatedProjectButton = findViewById(R.id.relatedProjectButton);
        relatedStationButton = findViewById(R.id.relatedStationButton);
        clockInTypeButton = findViewById(R.id.clockInTypeButton);
        relocation = findViewById(R.id.relocation);
        carPictureButton = findViewById(R.id.carPictureButton);

        carNumber = findViewById(R.id.carNumber);
        carType = findViewById(R.id.carType);
        relatedProject = findViewById(R.id.relatedProject);
        relatedStation = findViewById(R.id.relatedStation);
        clockInType = findViewById(R.id.clockInType);
        location = findViewById(R.id.location);
        clockInButton = findViewById(R.id.clockInButton);

        loc = new LocationUtil();
        loc.initLocationOption(getApplicationContext());
        location.setText(loc.GetAddrStr());

        car_back = (ImageView) findViewById(R.id.car_back);
        img2base = new image2Base64Util();

        date = new Date(System.currentTimeMillis());
        datetime = simpleDateFormat.format(date);

        setListener();

    }

    void setListener(){
        Button car_history = findViewById(R.id.car_history);
        car_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClockInCarActivity.this, HistoryCarClockInActivity.class));
            }
        });


        carNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ClockInCarActivity.this, ClockInGetCatNumberActivity.class), 1);
            }
        });


        relatedProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ClockInCarActivity.this, ClockInGetProjectActivity.class), 2);
            }
        });

        relatedStationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (projectID == "")
                    Toast.makeText(ClockInCarActivity.this, "请先选择“所属项目”！", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent();
                    intent.putExtra("projectID", projectID);
                    startActivityForResult(intent.setClass(ClockInCarActivity.this, ClockInGetStationActivity.class), 3);
                }
            }
        });

        clockInTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ClockInCarActivity.this, ClockInGetTypeActivity.class), 4);
            }
        });

        relocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationUtil loc = new LocationUtil();
                loc.initLocationOption(getApplicationContext());
                location.setText(loc.GetAddrStr());
            }
        });

        carPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ClockInCarActivity.this, CameraActivity.class), 5);

            }
        });

        car_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        clockInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isNumber)
                    Toast.makeText(ClockInCarActivity.this,"“车牌号码”不能为空！", Toast.LENGTH_SHORT).show();
                else if(!isProject)
                    Toast.makeText(ClockInCarActivity.this,"“所属项目”不能为空！", Toast.LENGTH_SHORT).show();
                else if(!isStation)
                    Toast.makeText(ClockInCarActivity.this,"“所属站点”不能为空！", Toast.LENGTH_SHORT).show();
                else if(!isType)
                    Toast.makeText(ClockInCarActivity.this,"“打卡类型”不能为空！", Toast.LENGTH_SHORT).show();
                else if(!isPictureCaptured)
                    Toast.makeText(ClockInCarActivity.this,"“现场照片”不能为空！", Toast.LENGTH_SHORT).show();
                else{
                    // 判断今天是否已打卡
                    whetherRepeat();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 620) {
            number = data.getStringExtra("number");
            cartype = data.getStringExtra("type");
            carID = data.getStringExtra("carID");
            carNumber.setText(number);
            carType.setText(cartype);
            isNumber=true;
        }
        if (requestCode == 2 && resultCode == 720) {
            project = data.getStringExtra("project");
            projectID = data.getStringExtra("projectID");
            relatedProject.setText(project);
            isProject=true;
        }
        if (requestCode == 3 && resultCode == 820) {
            station = data.getStringExtra("station");
            stationID = data.getStringExtra("stationID");
            relatedStation.setText(station);
            isStation=true;
        }
        if (requestCode == 4 && resultCode == 920) {
            type = data.getStringExtra("type") ;
            clockInType.setText(types[Integer.parseInt(type)]);
            isType=true;
        }
        if (requestCode == 5 && resultCode == 101) {
            String path = data.getStringExtra("path");
            carPictureCapturedPath = path;

            carPictureButton.setVisibility(View.GONE);

            pic2base64 = img2base.getBase64(carPictureCapturedPath);
            Requests.Picture_SaveBLL(new Observer<JSONObject>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(JSONObject object) {
                    carPictureCapturedPath = object.getString("Temp");
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {
                    carPictureCapturedPath = "http://47.105.187.185:8011" + carPictureCapturedPath;
                    base64 = img2base.encodeImageToBase64(carPictureCapturedPath);
                    carImage = findViewById(R.id.carImage);
                    carImage.setVisibility(View.VISIBLE);
                    byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    carImage.setImageBitmap(decodedByte);
                    isPictureCaptured=true;
                }
            },pic2base64,"jpg");

        }
    }

    private void addClockIn(String add_time, String latitude, String longitude, String data_type,
                            String pic, String address, String enID, String stID, String carID) {
        Requests.CarClockIn_SaveBLL(new Observer<String>() {
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
        },App.userId, add_time, latitude, longitude, data_type, pic, address, enID, stID, carID);

    }

    private void whetherRepeat(){
        Requests.getCarClockInList(new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(JSONArray objects) {
                isRepeatArrival=false;
                isRepeatDepart=false;
                isRepeatFinish=false;
                String str="";
                for(int i = 0; i<objects.size();i++){
                    JSONObject jsonObject = objects.getJSONObject(i);
                    String time = jsonObject.getString("AddTime");
                    String thisType = jsonObject.getString("DataType");
                    String t = time.substring(6, 19);
                    Long timeLong = Long.parseLong(t);
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(timeLong);
                    String tt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
                    str = str + tt.substring(0,10);
                    if(tt.substring(0,10).equals(datetime.substring(0, 10))){
                        if(thisType.equals("0"))
                            isRepeatDepart=true;
                        if(thisType.equals("1"))
                            isRepeatArrival=true;
                        if(thisType.equals("2"))
                            isRepeatFinish=true;
                    }
                }

                // Toast.makeText(ClockInCarActivity.this,Boolean.toString(isRepeatArrival) + Boolean.toString(isRepeatFinish) +Boolean.toString(isRepeatDepart), Toast.LENGTH_SHORT).show();
                if(isRepeatDepart&&type.equals("0"))
                    Toast.makeText(ClockInCarActivity.this,"您今天已出发打卡，请勿重复操作", Toast.LENGTH_SHORT).show();
                else if(isRepeatArrival&&type.equals("1"))
                    Toast.makeText(ClockInCarActivity.this,"您今天已到达打卡，请勿重复操作", Toast.LENGTH_SHORT).show();
                else if(isRepeatFinish&&type.equals("2"))
                    Toast.makeText(ClockInCarActivity.this,"您今天已完工打卡，请勿重复操作", Toast.LENGTH_SHORT).show();
                else {
                    carPictureCapturedPath = img2base.getPicName(carPictureCapturedPath);
                    addClockIn(datetime, String.valueOf(loc.GetLat()),
                            String.valueOf(loc.GetLng()), type, carPictureCapturedPath, "", projectID, stationID, carID);
                    Toast.makeText(ClockInCarActivity.this, "打卡成功", Toast.LENGTH_SHORT).show();
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
