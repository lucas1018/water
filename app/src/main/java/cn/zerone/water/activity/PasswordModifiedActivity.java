package cn.zerone.water.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.adapter.UserMode;
import cn.zerone.water.http.Requests;
import cn.zerone.water.utils.SPUtils;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class PasswordModifiedActivity extends AppCompatActivity {

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
    }
    //  获取界面控件并处理相关控件的处理事件
    private void init() {
        //返回我的页面
        ImageView pwd_modify_back = findViewById(R.id.pwd_modify_back);
        pwd_modify_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et_original_psw = findViewById(R.id.et_original_psw);
        et_new_psw = findViewById(R.id.et_new_psw);
        et_new_psw_again = findViewById(R.id.et_new_psw_again);
        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEditString();
                if (TextUtils.isEmpty(originalPsw)){
                    Toast.makeText(PasswordModifiedActivity.this,"请输入原始密码",Toast.LENGTH_SHORT).show();
                }else if(!originalPsw.equals(App.pwd)) {
                    Toast.makeText(PasswordModifiedActivity.this,"输入的旧密码和原密码不一致",Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(newPsw)){
                    Toast.makeText(PasswordModifiedActivity.this,"请输入新密码",Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(newPswAgain)){
                    Toast.makeText(PasswordModifiedActivity.this,"请再次输入新密码",Toast.LENGTH_SHORT).show();
                }else if (!newPsw.equals(newPswAgain)){
                    Toast.makeText(PasswordModifiedActivity.this,"两次输入的新密码不一致",Toast.LENGTH_SHORT).show();
                }else if (newPsw.equals(newPswAgain) && newPsw.length() < 6){
                    Toast.makeText(PasswordModifiedActivity.this,"密码必须至少6个字符",Toast.LENGTH_SHORT).show();
                }else{
                    modifyPsw(newPsw, newPswAgain, originalPsw);
                }
            }
        });

    }
//    修改登录成功后保存在SharedPreferences中的密码
    private void modifyPsw(String p1, String p2, final String p3) {
        String id =  App.userId;
        Requests.updatePWD(new Observer<com.alibaba.fastjson.JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(com.alibaba.fastjson.JSONObject jsonObject) {
                String info = jsonObject.getString("Text");
                if (info.equals("密码修改成功！")){
                    UserMode userMode = (UserMode)SPUtils.getBean(PasswordModifiedActivity.this,SPUtils.KEY_USER);
                    userMode.setPwd(p3);
                    Toast.makeText(PasswordModifiedActivity.this,"新密码设置成功",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        },id,p1,p2,p3);
    }

    //获取控件上的字符串
    private void getEditString() {
        originalPsw = et_original_psw.getText().toString().trim();
        newPsw = et_new_psw.getText().toString().trim();
        newPswAgain = et_new_psw_again.getText().toString().trim();
    }
}

