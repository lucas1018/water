package cn.zerone.water.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import cn.zerone.water.R;

public class SystemUpdateActivity extends AppCompatActivity {

    private String updateUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_update);
        //返回上一级页面
        ImageView system_update_back = (ImageView) findViewById(R.id.system_update_back);
        system_update_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView old_version = findViewById(R.id.old_version);
        Button btn_refresh = findViewById(R.id.btn_refresh);
        //获取当前版本号
        try {
            String version = getPackageManager().getPackageInfo(getPackageName(),0).versionName + "";
            old_version.setText("当前版本：" + version);
            if (version.equals("1.1")) {
                btn_refresh.setVisibility(View.INVISIBLE);
            } else {
                btn_refresh.setVisibility(View.VISIBLE);
//                btn_refresh.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        System.out.println("pppppppppppp");
//                        /**调用系统自带的浏览器去下载最新apk*/
//                        updateUrl = "http://47.33333333";
//                        Intent intent = new Intent();
//                        intent.setAction("android.intent.action.VIEW");
//                        if (updateUrl != null && updateUrl.startsWith("http")) {
//                            Uri content_url = Uri.parse(updateUrl);
//                            intent.setData(content_url);
//                            startActivity(intent);
//                        }
//                    }
//                });
            }
        }catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
