package cn.zerone.water.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Calendar;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MealActivity extends AppCompatActivity {

    private final String[] items = new String[]{"工作餐","商务餐","其他"};
    private EditText dateText;
    private EditText mealType;
    private EditText mealMoney;
    private EditText mealResturant;
    private EditText mealRemark;
    private ImageView meal_back;
    private Button btn_add;
    private String meal_date;
    private String meal_type;
    private String meal_mount;
    private String meal_resturant;
    private String meal_remark;
    private String meal_username;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);
        meal_back = findViewById(R.id.meal_back);//返回上一级按钮
        dateText = findViewById(R.id.current_date);// 工作餐日期
        mealType = findViewById(R.id.meal_type);// 工作餐类型
        mealMoney = findViewById(R.id.consume_money);//餐费金额
        mealResturant = findViewById(R.id.resturant);
        mealRemark = findViewById(R.id.remark);//备注
        btn_add = findViewById(R.id.add_meal);//添加按钮
        //初始化参数值
        init();
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取参数值
                getEditString();
                //调用接口请求
                addFeeForMeal( meal_date, meal_type, meal_mount, meal_resturant, meal_remark);
            }
        });
    }

    private void init() {
        //返回上一级页面
        meal_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

<<<<<<< HEAD



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
=======
        //获取工作餐类型
        mealType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MealActivity.this);
                    //final View view = View.inflate(MealActivity.this, R.layout.meal_list, null);
                    //builder.setView(view);
                    builder.setTitle("选择工作餐类型：");
                    builder.setItems(items,null);
                    if(v.getId()==R.id.meal_type){
                        final int inType = mealType.getInputType();
                        mealType.setInputType(InputType.TYPE_NULL);
                        meal_back.onTouchEvent(motionEvent);
                        mealType.setInputType(inType);
                        mealType.setSelection(mealType.getText().length());
                    }
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            StringBuffer sb = new StringBuffer();
                            sb.append(items[i]);
                            mealType.setText(sb);
                        }
                    });

                    builder.create().show();
                }
                return true;
            }

        });

        //获取日期
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
                    if(cal.get(Calendar.DAY_OF_MONTH) > 10){
                        cal.set(Calendar.DAY_OF_MONTH, 1);
                        datePicker.setMinDate(cal.getTimeInMillis());
                    } else {
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
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
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

    private void getEditString() {
        meal_date    = dateText.getText().toString().trim();
        String temp   = mealType.getText().toString().trim();
        if (temp.equals(items[0])) {        //工作餐
            meal_type = "0";
        } else if (temp.equals(items[1])) {//商务餐
            meal_type = "1";
        } else {                            //其他
            meal_type = "2";
        }
        meal_mount      = mealMoney.getText().toString().trim();
        meal_resturant = mealResturant.getText().toString().trim();
        meal_remark     = mealRemark.getText().toString().trim();
    }

    //    修改登录成功后保存在SharedPreferences中的密码
    private void addFeeForMeal(String meal_date, String meal_type, String meal_mount, String meal_resturant, String meal_remark) {
        Requests.feesForMeals_SaveBLL(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String str) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Toast.makeText(MealActivity.this,"工作餐添加成功", Toast.LENGTH_SHORT).show();
            }
        },App.userId, App.username, meal_date, meal_type, meal_mount, meal_resturant, meal_remark);

>>>>>>> ec8df93cdd5dce8f9248aed7d1bd9283ac4cc013
    }

}
