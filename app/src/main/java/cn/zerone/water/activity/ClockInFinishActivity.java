package cn.zerone.water.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import cn.zerone.water.utils.LocationUtil;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ClockInFinishActivity extends AppCompatActivity {
    // 限制一天打卡一次
    private String flag = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_in_finish);

        ImageView finish_back = (ImageView) findViewById(R.id.finish_back);
        finish_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageButton button_finish = findViewById(R.id.button_finish);
        button_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocationUtil loc = new LocationUtil();
                loc.initLocationOption(getApplicationContext());

                String lat = String.valueOf(loc.GetLat());
                String lng = String.valueOf(loc.GetLng());

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                String datetime = simpleDateFormat.format(date);

                if(flag.equals(datetime.substring(0, 10)))
                    Toast.makeText(ClockInFinishActivity.this, "您今天已打卡，请勿重复操作。", Toast.LENGTH_SHORT).show();
                else{
                    addClockIn(datetime, lat, lng, "2", "", "");
                    flag = datetime.substring(0, 10);
                }

            }
        });
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
                Toast.makeText(ClockInFinishActivity.this,"到家打卡成功", Toast.LENGTH_SHORT).show();
            }
        },App.userId, add_time, latitude, longitude, data_type, pic, address);

    }
}
