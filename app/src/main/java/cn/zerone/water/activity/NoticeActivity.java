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
        TextView notice_title = (TextView)findViewById(R.id.notice_title) ;
        TextView Create_time = (TextView)findViewById(R.id.create_time) ;
        TextView notice_content = (TextView)findViewById(R.id.notice_content) ;
        TextView notice_type = (TextView)findViewById(R.id.notice_type) ;


        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString("title");

        notice_title.setText(title);
        String createtime = bundle.getString("Createtime");
        Create_time.setText(createtime);
        String info = bundle.getString("info");
        notice_content.setText(info);
        String dataType =bundle.getString("DataType");
        if(dataType.equals("0")){
            notice_type.setText("运维系统公告");
        }
        else if (dataType.equals("1")){
            notice_type.setText("安全生产公告");
        }
        else if (dataType.equals("2")){
            notice_type.setText("安全生产活动");
        }
        ImageView iv = (ImageView)findViewById(R.id.back);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




    }
}
