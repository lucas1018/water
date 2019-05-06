package cn.zerone.water.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cjt2325.cameralibrary.JCameraView;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.zxing.cameraapplication.CameraActivity;

import java.io.File;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.activity.MainActivity;
import cn.zerone.water.activity.StepActivity;
import cn.zerone.water.activity.WebActivity;
import cn.zerone.water.http.Requests;
import cn.zerone.water.model.EngineeringStation;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by zero on 2018/12/3.
 */

public  class WebFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.fragment_web,null);
    }
    public void changeUrl(final String url){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(url);
            }
        });
    }
    WebView webView = null;
    TextView title = null;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         webView = view.findViewById(R.id.webview);
         title = view.findViewById(R.id.title);
        title.setText(title());

        /*webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                WebFragment.this.title.setText(title);
            }
        });
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) { // 表示按返回键
                        // 时的操作
                        webView.goBack(); // 后退
                        return true; // 已处理
                    }
                }
                return false;
            }
        });

        System.out.println("loadURL:"+getArguments().getString("url"));
        webView.loadUrl(getArguments().getString("url"));
        webView.addJavascriptInterface(new Callback(),"JSEx");*/

    }
    public String title(){
        return null;
    }
    public class Callback{
        @JavascriptInterface
        public void execute(String cmd,String params){
            JSONArray jsonArray  = null;
            if(params!=null&&!params.equals("undefined")){
                 jsonArray  = JSON.parseArray(params);
            }
            System.out.println("cmd:"+cmd+"params:"+jsonArray);
            switch (cmd){
                case "Sign_In":
                    if(App.lastDBLocation==null){
                        Toast.makeText(getActivity(), "签到失败 坐标获取失败", Toast.LENGTH_SHORT).show();
                    }else{
                        Requests.signIn(new Observer<String>(){

                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(String string) {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(getActivity(), "签到失败 上传数据失败", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onComplete() {
                                App.isUploadGps=true;
                                Toast.makeText(getActivity(), "签到成功", Toast.LENGTH_SHORT).show();
                                //flashWebView();

                            }
                        },App.token,App.lastDBLocation.getLatitude(),App.lastDBLocation.getLongitude());
                    }
                    break;
                case "Sign_Out":
                    if(App.lastDBLocation==null){
                        Toast.makeText(getActivity(), "签退失败 坐标获取失败", Toast.LENGTH_SHORT).show();
                    }else{
                        Requests.signOut(new Observer<String>(){

                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(String string) {
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(getActivity(), "签退失败 上传数据失败", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onComplete() {
                                App.isUploadGps=false;
                                Toast.makeText(getActivity(), "签退成功", Toast.LENGTH_SHORT).show();
                                //flashWebView();

                            }
                        },App.token,App.lastDBLocation.getLatitude(),App.lastDBLocation.getLongitude());
                    }                    break;
                case "ScanBarCode":
                    Intent intent = new Intent(getActivity(), CaptureActivity.class);
                    startActivityForResult(intent, 3);
                    break;
                case "GoList":
                     intent = new Intent(getActivity(), WebActivity.class);
                    intent.putExtra("url",jsonArray.getString(0));
                    startActivity(intent);
                    break;
                case "ChangeHeadImg":
                     intent = new Intent(getActivity(), CameraActivity.class);
                     intent.putExtra("mode", JCameraView.BUTTON_STATE_ONLY_CAPTURE);
                    startActivityForResult(intent, 400);
                    break;
                case "UploadJobStepInfo":
                    intent = new Intent(getActivity(),StepActivity.class);
                    intent.putExtra("jobjId",jsonArray.getString(0));
                    intent.putExtra("stepId",jsonArray.getString(1));
                    startActivityForResult(intent,777);
                    break;
                case "ShowMsg":
                    Toast.makeText(getActivity(), jsonArray.getString(0), Toast.LENGTH_SHORT).show();
                    break;
                case "GoJob":
                    if(getActivity() instanceof  MainActivity){
                        String url = null;
                        if(jsonArray!=null){
                            url = jsonArray.getString(0);
                        }
                        ((MainActivity)getActivity()).changeTab(3,App.baseUrl+url+"&token="+App.token);
                    }
                    break;
                case "GoChat":
                    if(getActivity() instanceof  MainActivity){
                        int engineeringStationId = 0;
                        int taskInfoId = 0;
                        int stepId = 0;
                        String workName =null;
                        String taskName =null;
                        String stepName = null;
                        try{
                            if(jsonArray!=null){
                                engineeringStationId = jsonArray.getIntValue(0);
                                taskInfoId = jsonArray.getIntValue(1);
                                stepId = jsonArray.getIntValue(2);
                                workName = jsonArray.getString(3);
                                taskName = jsonArray.getString(4);
                                stepName = jsonArray.getString(5);
                                EngineeringStation engineeringStation = new EngineeringStation();
                                engineeringStation.setEngineeringStationId(engineeringStationId);
                                engineeringStation.setTaskId(taskInfoId);
                                engineeringStation.setStepId(stepId);
                                engineeringStation.setStepName(stepName);
                                engineeringStation.setTaskName(taskName);
                                engineeringStation.setWorkName(workName);
                                ((App)(getActivity().getApplication())).saveEngineeringStation(engineeringStation);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        ((MainActivity)getActivity()).changeTab(2,"");
                    }
                    break;
                case "GoMy":
                    if(getActivity() instanceof  MainActivity){
                        String url = null;
                        if(jsonArray!=null){
                            url = jsonArray.getString(0);
                        }
                        ((MainActivity)getActivity()).changeTab(4,App.baseUrl+url+"&token="+App.token);
                    }
                    break;
                case "LogOut":
                    ((App) (getActivity().getApplication())).clearLoginToken();
                    System.exit(0);
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==3){
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    final String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Intent intent = new Intent(getActivity(), WebActivity.class);
                    intent.putExtra("url",App.baseUrl+"CWeb/GoodsInfo.aspx?token="+App.token+"&BarCode="+result);
                    startActivityForResult(intent,999);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(getActivity(), "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }else if(requestCode==400&&resultCode==101){
            String imagePath = data.getStringExtra("path");
            File file = new File(imagePath);
            Requests.upload(new Observer<JSONObject>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(JSONObject jsonObject) {
                    String url = jsonObject.getString("Url");
                    Requests.updateuserinfo(new Observer<String>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(String s) {
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getActivity(), "上传头像失败", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete() {
                            //flashWebView();
                            Toast.makeText(getActivity(), "上传头像成功", Toast.LENGTH_LONG).show();
                        }
                    },App.token,url);
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(getActivity(), "上传头像失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onComplete() {

                }
            },App.token,file);
        }else if(requestCode==777){
            //flashWebView();
        }else if(requestCode==999){
            //flashWebView();
        }
    }

    /*private void flashWebView() {
        handler.postDelayed(new Runnable() {
             @Override
             public void run() {
                 webView.reload();
             }
         },2000);
    }

    Handler handler  =new Handler(Looper.getMainLooper());*/
}
