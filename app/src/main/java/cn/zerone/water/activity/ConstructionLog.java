package cn.zerone.water.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import cn.zerone.water.model.ListViewAdapter;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ConstructionLog extends Activity {
    private ImageView log_back = null;
    private ImageView log_go = null;
    private TextView project_name = null;
    private EditText date = null;
    private EditText weather = null;


    public  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);
        project_name = (TextView)findViewById(R.id.project_name);
        log_back = (ImageView)findViewById(R.id.log_back);
        log_go = (ImageView)findViewById(R.id.log_go);
        date = (EditText)findViewById(R.id.log_date) ;
        weather = (EditText)findViewById(R.id.log_date);

        log_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        log_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ConstructionLog.this,LogContentActivity.class);
                startActivityForResult(intent,1);
            }
        });

        //获取日期
        date.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ConstructionLog.this);
                    final View view = View.inflate(ConstructionLog.this, R.layout.meal_date, null);
                    final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
                    builder.setView(view);

                    final Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(System.currentTimeMillis());
                    datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),null);
                    if (v.getId() == R.id.log_date) {
                        final int inType = date.getInputType();
                        date.setInputType(InputType.TYPE_NULL);
                        date.onTouchEvent(event);
                        date.setInputType(inType);
                        date.setSelection(date.getText().length());

                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StringBuffer sb = new StringBuffer();
                                sb.append(String.format("%d-%02d-%02d",
                                        datePicker.getYear(),
                                        datePicker.getMonth() + 1,
                                        datePicker.getDayOfMonth()));
                                date.setText(sb);
                                dialog.cancel();
                            }
                        })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                    }
                    builder.create().show();
                }
                return true;
            }

        });



    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == 1 && resultCode == 3) {
            String result = data.getStringExtra("PROJECT_NAME");
            project_name.setText(result);
        }
    }


}
