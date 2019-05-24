package cn.zerone.water.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.zerone.water.R;
import cn.zerone.water.utils.LocationUtil;

public class ClockInWorkActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_in_work);

        ImageView home_back = (ImageView) findViewById(R.id.work_back);
        home_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // get time
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String datetime = simpleDateFormat.format(date);

        //get location
        LocationUtil loc = new LocationUtil();
        loc.initLocationOption(getApplicationContext());

        TextView dateString = findViewById(R.id.dateString);
        dateString.setText(datetime.substring(0,10));

        TextView morningNow = findViewById(R.id.morningNow);
        morningNow.setText("打卡时间"+ datetime.substring(11,16));

        TextView location = findViewById(R.id.location);
        location.setText(loc.GetAddrStr());
    }
}
