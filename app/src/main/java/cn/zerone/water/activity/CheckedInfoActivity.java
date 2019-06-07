package cn.zerone.water.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import cn.zerone.water.R;

public class CheckedInfoActivity extends AppCompatActivity {

    private WebView mWebView;
    private String  mWebUrl, mCheckName;
    private TextView mTitle;
    ImageView mBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_checked_detail);
        initView();
        initData();
    }


    private void initView(){
        mWebView = (WebView)findViewById(R.id.checked_web_info);
        mTitle = findViewById(R.id.checked_title);
        mBack = findViewById(R.id.checked_back);

        mWebView.getSettings().setUseWideViewPort(true);//将图片调整到适合webView的大小
        mWebView.getSettings().setLoadWithOverviewMode(true);//缩放至屏幕大小
        mWebView.getSettings().setJavaScriptEnabled(true);//支持JavaScript脚本
        mWebView.setWebViewClient(new WebViewClient());
        //返回上一级页面
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent();
                //setResult(720, intent);
                finish();
            }
        });


    }

    private void initData(){
        Bundle bundle = getIntent().getExtras();
        mWebUrl = bundle.getString("Url");
        //mCheckID = bundle.getInt("checkID");
        mCheckName = bundle.getString("CheckName");

        mTitle.setText(mCheckName);

        //加载网页
        mWebView.loadUrl(mWebUrl);
        //激活WebView为活跃状态，能正常执行网页的响应
        mWebView.onResume();
    }
}
