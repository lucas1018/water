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
        String createtime = bundle.getString("AddTime");
        Create_time.setText(createtime);
        String info = bundle.getString("Msg");
        notice_content.setText(info);
        String dataType =bundle.getString("DataType");
        switch (dataType){
            case "0":
                notice_type.setText("普通通知");
                break;
            case "1":
                notice_type.setText("巡检任务");
                break;
            case "2":
                notice_type.setText("运维任务");
                break;
            case "3":
                notice_type.setText("审核审批");
                break;
            case "4":
                notice_type.setText("安全检查");
                break;
            case "5":
                notice_type.setText("在线直播");
                break;
            case "6":
                notice_type.setText("工单任务");
                break;
            case "7":
                notice_type.setText("站点建设");
                break;
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
