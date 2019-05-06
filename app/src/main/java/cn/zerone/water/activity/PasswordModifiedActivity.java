package cn.zerone.water.activity;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import cn.zerone.water.R;
import cn.zerone.water.fragment.MyselfFragment;

public class PasswordModifiedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_modified);
        //返回上一级页面
        ImageView password_modify_back = (ImageView) findViewById(R.id.password_modify_back);
        password_modify_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
