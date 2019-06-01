package cn.zerone.water.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.zerone.water.App;
import cn.zerone.water.R;

public class CheckDetailsActivity extends AppCompatActivity {

    private WebView mWebView;
    private Button mAgree, mReject;
    private String  mWebUrl, mCheckName;
    private TextView mTitle;
    ImageView mBack;
    private int mAgreeFlag = 1;
    private int mRejectFlag = 2;
    private int mCheckID;
    private boolean IsComplete;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_info);

        initView();
        initData();
        IsComplete = false;

    }

    public void initView(){
        mWebView = (WebView)findViewById(R.id.web_info);
        mAgree = findViewById(R.id.agree);
        mReject = findViewById(R.id.reject);
        mTitle = findViewById(R.id.check_title);
        mBack = findViewById(R.id.back);

        mWebView.getSettings().setUseWideViewPort(true);//将图片调整到适合webView的大小
        mWebView.getSettings().setLoadWithOverviewMode(true);//缩放至屏幕大小
        mWebView.getSettings().setJavaScriptEnabled(true);//支持JavaScript脚本
        mWebView.setWebViewClient(new WebViewClient());
        //返回上一级页面
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(720, intent);
                finish();
            }
        });

        mAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!IsComplete){

                    Bundle bundle = new Bundle();
                    bundle.putString("Url", mWebUrl);
                    bundle.putInt("checkID", mCheckID);
                    bundle.putString("CheckName", mCheckName);
                    bundle.putInt("State", mAgreeFlag);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(CheckDetailsActivity.this, CheckSubmitActivity.class);
                    //startActivity(intent);
                    startActivityForResult(intent, 1);
                }

                else
                    Toast.makeText(CheckDetailsActivity.this,"已审批",Toast.LENGTH_SHORT).show();


            }
        });
        mReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!IsComplete){
                    Bundle bundle = new Bundle();
                    bundle.putString("Url", mWebUrl);
                    bundle.putInt("checkID", mCheckID);
                    bundle.putString("CheckName", mCheckName);
                    bundle.putInt("State", mRejectFlag);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(CheckDetailsActivity.this, CheckSubmitActivity.class);
                    //startActivity(intent);
                    startActivityForResult(intent, 1);

                }
                else
                    Toast.makeText(CheckDetailsActivity.this,"已审批",Toast.LENGTH_SHORT).show();


            }
        });
    }

    public void initData(){
        Bundle bundle = getIntent().getExtras();
        mWebUrl = bundle.getString("Url");
        mCheckID = bundle.getInt("checkID");
        mCheckName = bundle.getString("CheckName");

        mTitle.setText(mCheckName);

        //加载网页
        mWebView.loadUrl(mWebUrl);
        //激活WebView为活跃状态，能正常执行网页的响应
        mWebView.onResume();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == 720) {

            IsComplete = true;
        }
    }
}
