package cn.zerone.water.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zxing.cameraapplication.CameraActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import cn.zerone.water.utils.LocationUtil;
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

    private String number;
    private String cartype;
    private String project;
    private String projectID="";
    private String station;
    private String type;
    private String basicPicturePath = "/storage/emulated/0/JCamera/picture_";
    private String carPictureCapturedPath;

    private Boolean isNumber = false;
    private Boolean isProject = false;
    private Boolean isStation = false;
    private Boolean isType = false;
    private Boolean isPictureCaptured = false;


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

        LocationUtil loc = new LocationUtil();
        loc.initLocationOption(getApplicationContext());
        location.setText(loc.GetAddrStr());

        car_back = (ImageView) findViewById(R.id.car_back);

        setListener();

    }

    void setListener(){
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
                    // 打卡接口
                    Toast.makeText(ClockInCarActivity.this,"“打卡成功", Toast.LENGTH_SHORT).show();
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

            carImage = findViewById(R.id.carImage);
            carImage.setVisibility(View.VISIBLE);
            carImage.setImageURI(Uri.fromFile(new File(path)));
            isPictureCaptured=true;
        }
    }
}
