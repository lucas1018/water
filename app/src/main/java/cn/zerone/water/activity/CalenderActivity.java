package cn.zerone.water.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.zerone.water.R;

public class CalenderActivity extends AppCompatActivity {

    private ImageView calender_back, iv_edit_log;
    private EditText edit;
    private String editContent;
    private MaterialCalendarView calendarView;
    private String currentDate;
    private ProgressBar pro;

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
        edit = (EditText) findViewById(R.id.edit);

        iv_edit_log = (ImageView) findViewById(R.id.iv_edit_log);


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
               //点击选择时间的时候出现加载框，并请求网络数据
                pro = new ProgressBar(CalenderActivity.this);

            }
        });


        //点击编辑图标，跳转页面,并将选择的日期传递过去
        iv_edit_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = getSelectedDatesString();
                if (date.equals("No Selection")) {
                    Toast.makeText(CalenderActivity.this, "请选择一个日期", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(CalenderActivity.this, CalenderContentActivity.class);
                    intent.putExtra("date", date);
                    startActivity(intent);
                }
            }
        });
    }

    private String getSelectedDatesString() {
        CalendarDay date = calendarView.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        Date date1 = date.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
        String dateStr = sdf.format(date1);
        Log.i("myTag","dateStr " + dateStr);
        return dateStr;

    }

}
