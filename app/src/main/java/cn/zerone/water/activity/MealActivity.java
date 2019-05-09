package cn.zerone.water.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.zerone.water.R;

public class MealActivity extends AppCompatActivity {

//    private static EditText dateText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);

        //返回上一级页面
        ImageView meal_back = (ImageView) findViewById(R.id.meal_back);
        meal_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




//        DatePicker datepicker = findViewById(R.id.current_date);
//        dateText.setInputType(InputType.TYPE_NULL);
//        dateText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                // TODO Auto-generated method stub
//                if(hasFocus){
//                    showDatePickerDialog();
//                }
//            }
//        });

//        dateText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDatePickerDialog();
//            }
//        });
    }

    /**
     * 展示日期选择对话框
     */
//    private void showDatePickerDialog() {
//        Calendar c = Calendar.getInstance();
//        new DatePickerDialog(MealActivity.this, new DatePickerDialog.OnDateSetListener() {
//
//            @Override
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                // TODO Auto-generated method stub
//                dateText.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
//            }
//        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
//
//    }
}
