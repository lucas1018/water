package cn.zerone.water.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.zerone.water.R;

public class MealActivity extends AppCompatActivity {
    private EditText dateText;
    private EditText mealText;
    private ListView listView=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);


        //返回上一级页面
        final ImageView meal_back = (ImageView) findViewById(R.id.meal_back);
        meal_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 工作餐类型
        mealText = (EditText) findViewById(R.id.meal_type);
        mealText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MealActivity.this);
                    //final View view = View.inflate(MealActivity.this, R.layout.meal_list, null);
                    //builder.setView(view);
                    final String[]items = new String[]{"餐费","招待客户","加班聚餐"};
                    builder.setTitle("选择工作餐类型：");
                    builder.setItems(items,null);
                    if(v.getId()==R.id.meal_type){
                        final int inType = mealText.getInputType();
                        mealText.setInputType(InputType.TYPE_NULL);
                        meal_back.onTouchEvent(motionEvent);
                        mealText.setInputType(inType);
                        mealText.setSelection(mealText.getText().length());
                    }
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            StringBuffer sb = new StringBuffer();
                            sb.append(items[i]);
                            mealText.setText(sb);
                        }
                    });

                    builder.create().show();
                }
                return true;
            }

        });
//        mealText.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN){
//                    Intent intent = new Intent(MealActivity.this, MealTypeActivity.class);
//                    startActivity(intent);
//                }
//
//                return true;
//            }
//        });
//        Intent intent = getIntent();
//        String result = intent.getStringExtra("meal_type");
//        mealText.setText(result);
        //dateText.requestFocus();
        // 工作餐日期
        dateText = (EditText) findViewById(R.id.current_date);
        dateText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MealActivity.this);
                    final View view = View.inflate(MealActivity.this, R.layout.meal_date, null);
                    final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
                    builder.setView(view);

                    final Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(System.currentTimeMillis());
                    datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),null);
                    datePicker.setMaxDate(cal.getTime().getTime());
                    if(cal.get(Calendar.DAY_OF_MONTH)>10){
                        cal.set(Calendar.DAY_OF_MONTH, 1);
                        datePicker.setMinDate(cal.getTimeInMillis());
                    }
                    else {
                        cal.add(Calendar.MONTH,-1);
                        cal.set(Calendar.DAY_OF_MONTH, 1);
                        datePicker.setMinDate(cal.getTimeInMillis());
                    }
                    if (v.getId() == R.id.current_date) {
                        final int inType = dateText.getInputType();
                        dateText.setInputType(InputType.TYPE_NULL);
                        dateText.onTouchEvent(event);
                        dateText.setInputType(inType);
                        dateText.setSelection(dateText.getText().length());

                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                StringBuffer sb = new StringBuffer();
                                sb.append(String.format("%d-%02d-%02d",
                                        datePicker.getYear(),
                                        datePicker.getMonth() + 1,
                                        datePicker.getDayOfMonth()));
                                dateText.setText(sb);
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

}
