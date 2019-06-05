package cn.zerone.water.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;

import cn.zerone.water.R;
import cn.zerone.water.http.Requests;

/**
 * Created by litinghui on 2019/5/31.
 */

public class TaskDetailActivity extends AppCompatActivity {

    public  void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_detail);

        ImageView iv = (ImageView)findViewById(R.id.task_back);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView task_stationName = (TextView)findViewById(R.id.stationName) ;
        TextView task_state = (TextView)findViewById(R.id.state) ;
        TextView task_address = (TextView)findViewById(R.id.address) ;
        TextView task_begin_time = (TextView)findViewById(R.id.begin_time) ;


        Bundle bundle = getIntent().getExtras();
        String stationName = bundle.getString("stationName");
        task_stationName.setText(stationName);


        String state = bundle.getString("state");
        task_state.setText(state);

        String address =bundle.getString("address");
        task_address.setText(address);

        String begin_time =bundle.getString("beginDate");
        task_begin_time.setText(begin_time);



    }
}
