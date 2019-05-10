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
    private EditText dateText;
    private EditText mealText;
    private EditText mealMoney;
    private EditText mealRemark;
    private ImageView meal_back;
    private Button btn_add;
    private String meal_date;
    private String meal_type;
    private String meal_mount;
    private String meal_remark;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);
        meal_back = findViewById(R.id.meal_back);//返回上一级按钮
        mealText = findViewById(R.id.meal_type);// 工作餐类型
        dateText = findViewById(R.id.current_date);// 工作餐日期
        mealMoney = findViewById(R.id.consume_money);//餐费金额
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
                addFeeForMeal( meal_date, meal_type, meal_mount, meal_remark);
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

        //获取工作餐类型
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

    private void getEditString() {
        meal_date = dateText.getText().toString().trim();
        meal_type = mealText.getText().toString().trim();
        meal_mount = mealMoney.getText().toString().trim();
        meal_remark = mealRemark.getText().toString().trim();
    }

    //    修改登录成功后保存在SharedPreferences中的密码
    private void addFeeForMeal(String meal_date, String meal_type, String meal_mount, String meal_remark) {
        Integer uid = (Integer) App.userId;
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
                Toast.makeText(MealActivity.this,"工作餐添加成功", Toast.LENGTH_SHORT).show();
            }
        },uid, meal_date, meal_type, meal_mount, meal_remark);
    }

}
