package cn.zerone.water.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import cn.zerone.water.model.CalenderContent;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class CalenderActivity extends AppCompatActivity {

    private ImageView calender_back, iv_edit_log;
    private TextView edit;
    private String editContent;
    private MaterialCalendarView calendarView;
    private String currentDate;
    private ProgressBar pro;
    private LinearLayout lin_edit;
    private String date, date2;
    ProgressDialog dialog;
    String userid;
    private String calenderContent;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        initView();
        initOnClick();
        //获取当前时间，并在日历上展示当前时间
        Calendar now = Calendar.getInstance();

        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);
        CalendarDay today = CalendarDay.from(year, month, day);
        calendarView.setCurrentDate(today);
        calendarView.setSelectedDate(today);

        calendarView.state().edit()
//                .setFirstDayOfWeek(Calendar.WEDNESDAY)
//                .setMinimumDate(CalendarDay.from(2016, 4, 3))
//                .setMaximumDate(CalendarDay.from(2016, 5, 12))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

    }

    /*
    初始化控件
     */
    private void initView() {
        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        calender_back = (ImageView) findViewById(R.id.calender_back);
        edit = (TextView) findViewById(R.id.edit);
        lin_edit = (LinearLayout) findViewById(R.id.lin_edit);
//        iv_edit_log = (ImageView) findViewById(R.id.iv_edit_log);


    }

    /*
    设置控件的点击事件
     */
    private void initOnClick() {
        //返回上一级页面
        calender_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                String selectedDates = getSelectedDatesString();
                sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
                userid = sharedPreferences.getString("userId", "");
                editClaender(userid,selectedDates );

            }
        });


        //点击编辑图标，跳转页面,并将选择的日期传递过去
        lin_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date = getSelectedDatesString();
                date2 = getSelectedDates();
                if (date.equals("No Selection")) {
                    Toast.makeText(CalenderActivity.this, "请选择一个日期", Toast.LENGTH_SHORT).show();
                } else {
                    sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
                    userid = sharedPreferences.getString("userId", "");
//                    Log.i("myTag","userid" + userid);
                    editLog(userid, date);


                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        ;
        Intent intent = getIntent();
        String job_content = intent.getStringExtra("job_content");
        if (job_content != null)
            edit.setText(job_content);
    }

    /**
     * 点击日历上面的日期获取日志的内容
     *
     * @param userid
     * @param date
     */
    private void editClaender(String userid, final String date) {
        Requests.JobLog_GetModelByDate(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                dialog = new ProgressDialog(CalenderActivity.this);
                dialog.setTitle("正在请求数据，请稍后......");
                dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
                dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                dialog.show();
            }

            @Override
            public void onNext(String str) {
                //对获取的日志进行解析
                Log.i("myTag", "点击编辑按钮 获取的日志内容" + str);
                if (str != null) {
                    CalenderContent calenderContent = JSON.parseObject(str, CalenderContent.class);
                    //获取今日工作
                    edit.setText(calenderContent.getJobContent());
                } else {
                    Toast.makeText(CalenderActivity.this, "没有工作安排", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                e.printStackTrace();
                Toast.makeText(CalenderActivity.this, "获取日志失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }, userid, date);
    }


    /**
     * 点击编辑按钮获取日志内容
     *
     * @param userid
     * @param date
     */
    private void editLog(String userid, final String date) {
        Requests.JobLog_GetModelByDate(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                dialog = new ProgressDialog(CalenderActivity.this);
                dialog.setTitle("正在请求数据，请稍后......");
                dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
                dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                dialog.show();
            }

            @Override
            public void onNext(String str) {
                //对获取的日志进行解析
                Log.i("myTag", "点击编辑按钮 获取的日志内容" + str);
                if (str != null) {
                    calenderContent = str;
                } else {
                    calenderContent = "";
                }
            }
            @Override
            public void onError(Throwable e) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                e.printStackTrace();
                Toast.makeText(CalenderActivity.this, "获取日志失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Intent intent = new Intent(CalenderActivity.this, CalenderContentActivity.class);
                intent.putExtra("date", date);
                intent.putExtra("date2", date2);
                intent.putExtra("calenderContent", calenderContent);
                startActivity(intent);
            }
        }, userid, date);
    }


    private String getSelectedDatesString() {
        CalendarDay date = calendarView.getSelectedDate();
        calendarView.getCurrentDate();
        if (date == null) {
            return "No Selection";
        }
        Date date1 = date.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date1);
        Log.i("myTag", "dateStr " + dateStr);
        return dateStr;

    }

    private String getSelectedDates() {
        CalendarDay date = calendarView.getCurrentDate();
        if (date == null) {
            return "No Selection";
        }
        Date date1 = date.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String dateStr = sdf.format(date1);
        Log.i("myTag", "dateStr " + dateStr);
        return dateStr;
    }
}
