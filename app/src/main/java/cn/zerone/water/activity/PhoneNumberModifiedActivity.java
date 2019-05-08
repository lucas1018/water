package cn.zerone.water.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.widget.ImageView;

import android.widget.Button;
import android.widget.EditText;


import cn.zerone.water.R;

public class PhoneNumberModifiedActivity extends AppCompatActivity {
    String APPKEY = "";
    String APPSERECT = "";

    private EditText inputPhone;

    private Button verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_modify);

        //返回我的页面
        ImageView phone_number_modify_back = (ImageView) findViewById(R.id.phone_number_modify_back);
        phone_number_modify_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
