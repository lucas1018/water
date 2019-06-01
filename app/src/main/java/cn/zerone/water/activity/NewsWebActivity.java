package cn.zerone.water.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import cn.zerone.water.R;

public class NewsWebActivity extends Activity {

    private WebView mWebView;
    private ImageView iv;

    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        setContentView(R.layout.news_web);
        super.onCreate(savedInstanceState);
        iv = (ImageView)findViewById(R.id.homeback);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBundle = getIntent().getExtras();
        initView();
    }

    public void initView() {
        // TODO Auto-generated method stub
        mWebView = (WebView) findViewById(R.id.news_web);
        mWebView.loadUrl(mBundle.getString("Href"));
        mWebView.requestFocusFromTouch();
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        /**覆盖调用系统或自带浏览器行为打开网页*/
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }
        });
        /**判断加载过程*/
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                if (newProgress == 100) {
                    // 网页加载完成

                } else {
                    // 加载中

                }
            }
        });
        initListener();
    }

    public void initListener() {
        /**打开页面时， 自适应屏幕*/
        WebSettings webSettings =   mWebView .getSettings();
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);

        /**便页面支持缩放*/
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);

    }


}
