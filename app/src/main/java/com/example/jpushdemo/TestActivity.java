package com.example.jpushdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import cn.jpush.android.api.JPushInterface;
import cn.zerone.water.R;

public class TestActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push_notification);
        TextView tv1 = (TextView)findViewById(R.id.push_notification_title);
        TextView tv2 = (TextView)findViewById(R.id.push_notification_content);

        Intent intent = getIntent();
        if (null != intent) {
	        Bundle bundle = getIntent().getExtras();
            String title = null;
            String content = null;
            if(bundle!=null){
                title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
                content = bundle.getString(JPushInterface.EXTRA_ALERT);
            }
            //tv.setText("标题 : " + title +" "+"内容: " + content );
            tv1.setText(title);
            tv2.setText(content);

        }
    }

}
