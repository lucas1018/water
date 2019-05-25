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
    private TextView carNumber;
    private TextView carType;
    private TextView relatedProject;
    private TextView relatedStation;
    private TextView clockInType;
    private TextView location;

    private TextView relocation;

    private ImageView car_back;

    private Date date;
    private String datetime;

    private String number;
    private String cartype;
    private String project;
    private String projectID="";
    private String station;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_in_car);

        carNumberButton = findViewById(R.id.carNumberButton);
        relatedProjectButton = findViewById(R.id.relatedProjectButton);
        relatedStationButton = findViewById(R.id.relatedStationButton);
        clockInTypeButton = findViewById(R.id.clockInTypeButton);
        relocation = findViewById(R.id.relocation);

        carNumber = findViewById(R.id.carNumber);
        carType = findViewById(R.id.carType);
        relatedProject = findViewById(R.id.relatedProject);
        relatedStation = findViewById(R.id.relatedStation);
        clockInType = findViewById(R.id.clockInType);
        location = findViewById(R.id.location);

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
//                Requests.getProjectLogList(new Observer<JSONArray>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                    }
//
//                    @Override
//                    // PROJECT_NAME 项目名，ID项目编号，用于查询站点
//                    public void onNext(JSONArray objects) {
//                        String str="";
//                        for(int i = 0; i<objects.size();i++){
//                            JSONObject jsonObject = objects.getJSONObject(i);
//                            String car = jsonObject.getString("PROJECT_NAME");
//                            str = str + car;
//                            Toast.makeText(ClockInCarActivity.this,str, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                },App.userId);
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

//                Requests.getStationList(new Observer<JSONArray>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                    }
//
//                    @Override
//                    public void onNext(JSONArray objects) {
//                        String str="";
//                        for(int i = 0; i<objects.size();i++){
//                            JSONObject jsonObject = objects.getJSONObject(i);
//                            String car = jsonObject.getString("STNM");
//                            str = str + car;
//                            Toast.makeText(ClockInCarActivity.this,str, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                },App.userId, "11");
            }
        });

        clockInTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

        car_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        }
        if (requestCode == 2 && resultCode == 720) {
            project = data.getStringExtra("project");
            projectID = data.getStringExtra("projectID");
            relatedProject.setText(project);
        }
        if (requestCode == 3 && resultCode == 820) {
            station = data.getStringExtra("station");
            relatedStation.setText(station);
        }
    }
}
