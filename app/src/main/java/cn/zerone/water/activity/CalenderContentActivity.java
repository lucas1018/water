package cn.zerone.water.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import cn.zerone.water.model.CalenderContent;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class CalenderContentActivity extends AppCompatActivity {

    private TextView tv_save;
    private EditText edit_job_content;
    private EditText edit_overtime_content;
    private TextView tv_overtime;
    private EditText edit_tomorrow;
    private EditText edit_remark;

    private String job_content;
    private String overtime_content;
    private String overtime;
    private String tomorrow;
    private String remark;
    private String date, data2;
    private String calenderjson;
    private TextView tv_title;
    private ImageView calender_content_back;
    private ImageView img_minus, img_add;
    private int overtime_content_int;
    private String userid;
    private ProgressDialog dialog;
    private int mLogID = 00000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender_content);
        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        data2 = intent.getStringExtra("date2");
        calenderjson = intent.getStringExtra("calenderContent");
        Log.i("myTag", "date ---->" + date);
        initView();
        initClick();
        Intent intent1 = getIntent();
//


    }

    /**
     * 初始化控件
     */
    private void initView() {
        calender_content_back = (ImageView) findViewById(R.id.calender_content_back);
        //保存按钮
        tv_save = (TextView) findViewById(R.id.tv_save);
        tv_title = (TextView) findViewById(R.id.tv_title);
        //获取今日工作
        edit_job_content = (EditText) findViewById(R.id.edit_job_content);
        //获取加班内容
        edit_overtime_content = (EditText) findViewById(R.id.edit_overtime_content);
        //加班时间
        tv_overtime = (TextView) findViewById(R.id.tv_overtime);
        //明日安排
        edit_tomorrow = (EditText) findViewById(R.id.edit_tomorrow);
        //备注
        edit_remark = (EditText) findViewById(R.id.edit_remark);
        img_minus = findViewById(R.id.img_minus);
        img_add = findViewById(R.id.img_add);

        //解析json
        if (calenderjson != null) {
            CalenderContent calenderContent = JSON.parseObject(calenderjson, CalenderContent.class);
            //获取今日工作
            edit_job_content.setText(calenderContent.getJobContent());
            //获取加班内容
            edit_overtime_content.setText(calenderContent.getOvertimeContent());
            //加班时间
            tv_overtime.setText(calenderContent.getOvertime()+ "");
            //明日安排
            edit_tomorrow.setText(calenderContent.getTomorrow());
            //备注
            edit_remark.setText(calenderContent.getRemark());
            //ID
            mLogID = calenderContent.getID();
        }
        //给控件赋值
        tv_title.setText(date);

    }

    /**
     * 设置控件的点击事件
     */
    private void initClick() {
        //返回按钮
        calender_content_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalenderContentActivity.this.finish();
            }
        });

        //对选择的加班时间进行判断
        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overtime_content = tv_overtime.getText().toString().trim();
                overtime_content_int = Integer.valueOf(overtime_content).intValue();
                tv_overtime.setText((overtime_content_int + 1) + "");
            }
        });

        img_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overtime_content = tv_overtime.getText().toString().trim();
                overtime_content_int = Integer.valueOf(overtime_content).intValue();
                if (overtime_content_int == 0) {
                    tv_overtime.setText("0");
                }
                if (overtime_content_int > 0) {
                    tv_overtime.setText((overtime_content_int - 1) + "");
                }

                if (overtime_content_int < 0) {
                    Toast.makeText(CalenderContentActivity.this, "请输入正确的时间", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //保存按钮的点击事件
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取相关的输入内容
                job_content = edit_job_content.getText().toString().trim();
                overtime_content = edit_overtime_content.getText().toString().trim();
                overtime = tv_overtime.getText().toString().trim();
                tomorrow = edit_tomorrow.getText().toString().trim();
                remark = edit_remark.getText().toString().trim();
                //将数据传递到后台之后关闭界面
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
//                String curDate = df.format(new Date());// new Date()为获取当前系统时间
                //添加时间，类型，工作内容，加班内容，加班时长，明天安排，备注

                SharedPreferences sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
                userid = sharedPreferences.getString("userId", "");
                Date date = new Date();
                SaveLog(data2, 0, job_content, overtime_content, overtime, tomorrow, remark);
            }
        });
    }

    private void SaveLog(String date2, int type, final String job_content, String overtime_content, String overtime, String tomorrow, String remark) {

        if(mLogID != 00000){
            Requests.JobLog_SaveBLL(new Observer<String>() {
                @Override
                public void onSubscribe(Disposable d) {

                    dialog = new ProgressDialog(CalenderContentActivity.this);
                    dialog.setTitle("正在保存请稍后......");
                    dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
                    dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                    dialog.show();
                }

                @Override
                public void onNext(String str) {
                    Log.i("myTag", "保存结束" + str);

                }

                @Override
                public void onError(Throwable e) {

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    e.printStackTrace();
                    Toast.makeText(CalenderContentActivity.this, "日志保存失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onComplete() {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(CalenderContentActivity.this, "日志保存成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CalenderContentActivity.this, CalenderActivity.class);
                    intent.putExtra("job_content", job_content);
                    startActivity(intent);
                    CalenderContentActivity.this.finish();
                }
            }, userid, date, type, job_content, overtime_content, overtime, tomorrow, remark, mLogID);

        }
        else{
            Requests.JobLog_SaveBLL(new Observer<String>() {
                @Override
                public void onSubscribe(Disposable d) {

                    dialog = new ProgressDialog(CalenderContentActivity.this);
                    dialog.setTitle("正在保存请稍后......");
                    dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
                    dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                    dialog.show();
                }

                @Override
                public void onNext(String str) {
                    Log.i("myTag", "保存结束" + str);

                }

                @Override
                public void onError(Throwable e) {

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    e.printStackTrace();
                    Toast.makeText(CalenderContentActivity.this, "日志保存失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onComplete() {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(CalenderContentActivity.this, "日志保存成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CalenderContentActivity.this, CalenderActivity.class);
                    intent.putExtra("job_content", job_content);
                    startActivity(intent);
                    CalenderContentActivity.this.finish();
                }
            }, userid, date, type, job_content, overtime_content, overtime, tomorrow, remark, 0);
        }





    }
}
