package cn.zerone.water.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import cn.zerone.water.R;

public class NoticeActivity extends Activity {
    public  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_message);

        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString("title");
        TextView notice_title = (TextView)findViewById(R.id.notice_title) ;
        notice_title.setText(title);
        //String time = bundle.getString("Createtime");
        //String info = bundle.getString("info");

        ImageView iv = (ImageView)findViewById(R.id.back);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




    }
}
