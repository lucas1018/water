package cn.zerone.water.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class CheckSubmitActivity extends AppCompatActivity {

    private ImageView mBack;
    private EditText mOpinion;
    private Button mSubmit;
    private String  mWebUrl, mWebPostUrl, mCheckName;
    private int mCheckID;
    private int mState;
    private TextView mTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opinion);

        initView();
        initData();


    }

    public void initView(){

        mOpinion = findViewById(R.id.edit_opinion_content);
        mTitle = findViewById(R.id.title);
        mBack = findViewById(R.id.back);
        //返回上一级页面
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSubmit = findViewById(R.id.opinion_submit);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Submit();

            }
        });

    }

    public void initData() {
        Bundle bundle = getIntent().getExtras();
        mWebPostUrl = bundle.getString("Url");
        mCheckID = bundle.getInt("checkID");
        mCheckName = bundle.getString("CheckName");
        mState = bundle.getInt("State");

        mTitle.setText(mCheckName);
    }

    public void Submit(){

        String Remark = mOpinion.getText().toString();

        Requests.Submit_GeneralCheck(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject jsonObject) {
                String info = jsonObject.getString("Text");
                if (info.equals("审核内容已提交！")){
                    Toast.makeText(CheckSubmitActivity.this,"提交成功",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent();
                    setResult(720, intent);
                    finish();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        },mCheckID,Remark,mState);
    }
}
