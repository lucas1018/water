package cn.zerone.water.activity;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.internal.ListenerClass;
import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import cn.zerone.water.utils.LocationUtil;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.baidu.vi.VIContext.getContext;

public class ClockInHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_in_home);

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

                LocationUtil loc = new LocationUtil();
                loc.initLocationOption(getApplicationContext());

                String lat = String.valueOf(loc.GetLat());
                String lng = String.valueOf(loc.GetLng());

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                String datetime = simpleDateFormat.format(date);

                addClockIn(datetime, lat, lng, "3", "", "");
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
                Toast.makeText(ClockInHomeActivity.this,"到家打卡成功", Toast.LENGTH_SHORT).show();
            }
        },App.userId, add_time, latitude, longitude, data_type, pic, address);

    }
}
