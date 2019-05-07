package cn.zerone.water.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
        verificationCode = (Button) findViewById(R.id.login_verificate_code);

//        MobSDK.init(this, APPKEY, APPSERECT);

    }
}
