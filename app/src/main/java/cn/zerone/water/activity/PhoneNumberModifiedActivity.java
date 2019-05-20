package cn.zerone.water.activity;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import android.widget.ImageView;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.common.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class PhoneNumberModifiedActivity extends AppCompatActivity {

    private TextView tel;
    private Button bn_modify;
    private EditText new_tel;
    private String new_phone;
    private String phone;
    private String LOGIN_NAME;
    private String NAME;
    private String PASSWORD;
    private String UNIT;
    private String EMAIL;
    private String LATEST_LOGIN_TIME;
    private String ClassType;
    private String Photo;
    private String Permissions;
    private String State;
    private String UType;
    private String DepId;
    private String ID;


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

        //获取用户信息
        Requests.USER_INFO_GetModelBLL(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject jsonObject) {
                phone = jsonObject.getString("PHONE");
                tel.setText(phone);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        },App.userId);

        tel =(TextView) findViewById(R.id.phone_number);
        new_tel = (EditText) findViewById(R.id.new_phone);
        bn_modify = (Button) findViewById(R.id.btn_save);

        //绑定点击事件
        bn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEditString();
                if (new_phone.equals(phone)){
                    Toast.makeText(PhoneNumberModifiedActivity.this,"输入的新号码和原号码一致",Toast.LENGTH_SHORT).show();
                } else if(!isMobile(new_phone)){
                    Toast.makeText(PhoneNumberModifiedActivity.this,"请输入正确的手机号",Toast.LENGTH_SHORT).show();
                } else {
                    changePhone(new_phone, App.userId, "8888");
                }
            }
        });


    }

    private void changePhone(String PHONE, String ID, String code) {
        Requests.UpdataPHONE(new Observer<JSONObject>() {
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(JSONObject jsonObject) {
            String info = jsonObject.getString("Text");
            if (info.equals("修改成功！")){
                Toast.makeText(PhoneNumberModifiedActivity.this,"手机号码修改成功",Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {

        }
    },PHONE,ID,code);

}

    //获取控件上的字符串
    private void getEditString() {
        new_phone = new_tel.getText().toString().trim();
    }

    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        String s2="^[1](([3][0-9])|([4][5,7,9])|([5][4,6,9])|([6][6])|([7][3,5,6,7,8])|([8][0-9])|([9][8,9]))[0-9]{8}$";// 验证手机号
        if(!TextUtils.isEmpty(str)){
            p = Pattern.compile(s2);
            m = p.matcher(str);
            b = m.matches();
        }
        return b;
    }

}
