package cn.zerone.water.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class CalenderContentActivity extends AppCompatActivity {

    private TextView tv_save;
    private EditText edit_job_content;
    private EditText edit_overtime_content;
    private EditText edit_overtime;
    private EditText edit_tomorrow;
    private EditText edit_remark;

    private String job_content;
    private String overtime_content;
    private String overtime;
    private String tomorrow;
    private String remark;
    private String date;
    private TextView tv_title;
    private ImageView calender_content_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender_content);
        Intent intent = getIntent();
        date = intent.getStringExtra("date");

        Log.i("myTag", "date ---->" + date);
        initView();
        initClick();
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
        edit_overtime = (EditText) findViewById(R.id.edit_overtime);
        //明日安排
        edit_tomorrow = (EditText) findViewById(R.id.edit_tomorrow);
        //备注
        edit_remark = (EditText) findViewById(R.id.edit_remark);

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
        //保存按钮的点击事件
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取相关的输入内容
                job_content = edit_job_content.getText().toString().trim();
                overtime_content = edit_overtime_content.getText().toString().trim();
                overtime = edit_overtime.getText().toString().trim();
                tomorrow = edit_tomorrow.getText().toString().trim();
                remark = edit_remark.getText().toString().trim();
                //将数据传递到后台之后关闭界面
//                addFeeForMeal(job_content,overtime_content,overtime,tomorrow,remark);
            }
        });

    }


    //    修改登录成功后保存在SharedPreferences中的密码
    /*
    private void addFeeForMeal(String meal_date, String meal_type, String meal_mount, String meal_remark,"") {
        String uid = App.userId;
        Requests.feesForMeals_SaveBLL(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String str) {
                System.out.println("addFeeForMeal" + str);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CalenderContentActivity.this, "工作日志保存成功", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }, uid, meal_date, meal_type, meal_mount, meal_remark);
    }
    */
}
