package cn.zerone.water.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.baidumaps.common.Constant;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Observer;

import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import cn.zerone.water.service.UpdateService;
import cn.zerone.water.views.UpdataDialog;

public class SystemUpdateActivity extends AppCompatActivity {

    private String updateUrl = "http://47.105.187.185:8011";
    private UpdataDialog updataDialog;
    private int  isAutoUpdate=0;
    private TextView tvmsg;
    //todo 这个参数要提取全局变量 要不然当activity杀死之后 其他地方存在他的引用，容易造成内存泄漏
    public static String APK_DOWNLOAD_URL="";
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
//            String result = Requests.UpdateProgram();
//            System.out.println("mmmmmmmmm" + result);
//            if (result != null) {
//                com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(result);
//                String new_version = jsonObject.getString("Version");
//                System.out.println("new_version->>>>" + new_version);
//                final String path = jsonObject.getString("Path");
//                final String remark = jsonObject.getString("Remark");
//                if (version.equals(new_version)) {
//                    btn_refresh.setVisibility(View.INVISIBLE);
//                } else {
//                    btn_refresh.setVisibility(View.VISIBLE);
//                    //todo isAutoUpdate   updateUrl  都是从接口获取的 这要发起一个数据请求
//                    btn_refresh.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            updataDialog = new UpdataDialog(SystemUpdateActivity.this, R.layout.dialog_updataversion,
//                                    new int[]{R.id.dialog_sure, R.id.relative_imagv_dialog_cancel}, isAutoUpdate);
//                            updataDialog.show();
//                            tvmsg = (TextView) updataDialog.findViewById(R.id.updataversion_msg);
//                            tvmsg.setText(remark);
//                            updataDialog.setOnCenterItemClickListener(new UpdataDialog.OnCenterItemClickListener() {
//                                @Override
//                                public void OnCenterItemClick(UpdataDialog dialog, View view) {
//                                    switch (view.getId()) {
//                                        case R.id.dialog_sure:
//
////                            /**调用系统自带的浏览器去下载最新apk*/
////                            Intent intent = new Intent();
////                            intent.setAction("android.intent.action.VIEW");
////                            if (updateUrl != null && updateUrl.startsWith("http")) {
////                                Uri content_url = Uri.parse(updateUrl);
////                                intent.setData(content_url);
////                                startActivity(intent);
////                            }
//                                            goToDownload(SystemUpdateActivity.this, updateUrl+path);
//                                            break;
//                                    }
//
//                                }
//                            });
//                        }
//                    });
//
//                }
//            }

        }catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void goToDownload(Context context, String updateUrl) {
        System.out.println("vvvvvvvvv" + updateUrl);
        Intent intent = new Intent();
        intent.setClass(context, UpdateService.class);
        intent.putExtra(APK_DOWNLOAD_URL, updateUrl);
        context.startService(intent);
    }
}
