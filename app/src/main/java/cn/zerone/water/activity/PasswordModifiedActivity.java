package cn.zerone.water.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.zerone.water.R;
import cn.zerone.water.fragment.MyselfFragment;
import cn.zerone.water.utils.MD5Utils;


public class PasswordModifiedActivity extends AppCompatActivity {

    private TextView tv_main_title;
    private TextView tv_back;
    private EditText et_original_psw;
    private EditText et_new_psw;
    private EditText et_new_psw_again;
    private Button btn_save;
    private String originalPsw;
    private String newPsw;
    private String newPswAgain;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_modified);
        init();
//        userName = AnalysisUtils.readLoginUserName(this);
    }
    //  获取界面控件并处理相关控件的处理事件
    private void init() {
        //返回我的页面
        ImageView pwd_modify_back = (ImageView) findViewById(R.id.pwd_modify_back);
        pwd_modify_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et_original_psw = (EditText) findViewById(R.id.et_original_psw);
        et_new_psw = (EditText) findViewById(R.id.et_new_psw);
        et_new_psw_again = (EditText) findViewById(R.id.et_new_psw_again);
        btn_save = (Button)  findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEditString();
                if (TextUtils.isEmpty(originalPsw)){
                    Toast.makeText(PasswordModifiedActivity.this,"请输入原始密码",Toast.LENGTH_SHORT).show();
                }else if (!originalPsw.equals(readPsw())){
                    Toast.makeText(PasswordModifiedActivity.this,"输入的密码与原始密码不一致",Toast.LENGTH_SHORT).show();
                }else if (newPsw.equals(readPsw())){
                    Toast.makeText(PasswordModifiedActivity.this,"输入的新密码与原始密码不能一致",Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(newPsw)){
                    Toast.makeText(PasswordModifiedActivity.this,"请输入新密码",Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(newPswAgain)){
                    Toast.makeText(PasswordModifiedActivity.this,"请再次输入新密码",Toast.LENGTH_SHORT).show();
                }else if (!newPsw.equals(newPswAgain)){
                    Toast.makeText(PasswordModifiedActivity.this,"两次输入的新密码不一致",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(PasswordModifiedActivity.this,"新密码设置成功",Toast.LENGTH_SHORT).show();
                    //修改登录成功后保存在SharedPreferences中的密码
                    modifyPsw(newPsw);
                    Intent intent = new Intent(PasswordModifiedActivity.this,LoginActivity.class);
                    startActivity(intent);
                    PasswordModifiedActivity.this.finish();//关闭当前界面
                }
            }
        });

    }
//    修改登录成功后保存在SharedPreferences中的密码
    private void modifyPsw(String newPsw) {
        SharedPreferences sp = getSharedPreferences("loginInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();//获取编辑器
        editor.putString(userName,newPsw);
        editor.commit();

    }
//    从SharedPreferences中读取原始密码
    private String readPsw() {
        SharedPreferences sp = getSharedPreferences("loginInfo",MODE_PRIVATE);
        String spPsw = sp.getString(userName,"");
        System.out.println(spPsw);
        return spPsw;

    }

    //h获取控件上的字符串
    private void getEditString() {
        originalPsw = et_original_psw.getText().toString().trim();
        newPsw = et_new_psw.getText().toString().trim();
        newPswAgain = et_new_psw_again.getText().toString().trim();
    }
}

