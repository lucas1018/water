package cn.zerone.water.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MealActivity extends AppCompatActivity {

    private final String[] items = new String[]{"工作餐","商务餐","其他"};
    private EditText dateText;
    private TextView mealType;
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
    private ImageView type_back;
    private String meal_username;

    //月度详情
    private Button btn_detail;
    private PopupWindow popupWindow;
    private RelativeLayout rlTitle;
    private ListView listView;//填充在菜单栏中列表
    private List<String> dateList;



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
        rlTitle = findViewById(R.id.rl_title);//标题栏布局
        type_back = findViewById(R.id.type_back);


        //初始化菜单列表所需数据
        initData();
        //初始化菜单列表看
        listView=new ListView(this);
        listView.setAdapter(new MyBaseAdapter());
        btn_detail = findViewById(R.id.meal_detail); //月度详情按钮
        btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow==null){
                    popupWindow=new PopupWindow();
                    popupWindow.setWidth(btn_detail.getWidth());
                    popupWindow.setHeight(btn_detail.getWidth());
                    popupWindow.setContentView(listView);
                    popupWindow.setFocusable(true);
                }
                popupWindow.showAsDropDown(btn_detail,0,0);

            }
        });
        //设置下拉菜单月份点击详情
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (popupWindow.isShowing() && popupWindow != null){

                    //把当前月份的值传过去
                    Intent intent = new Intent(MealActivity.this, MealDetailActivity.class);
                    intent.putExtra("currentDate",dateList.get(position));
                    //跳转到相应月份展示页面
                    startActivity(intent);
                    //关闭下拉列表
                    popupWindow.dismiss();
                    popupWindow=null;
                }
            }
        });

        //初始化参数值
        init();
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取参数值
                getEditString();
                if(dateText.getText().length() == 0){
                    Toast.makeText(MealActivity.this,"“餐费日期”不能为空！", Toast.LENGTH_SHORT).show();
                }
                else if(mealType.getText().length()==0){
                    Toast.makeText(MealActivity.this,"“餐费类型”不能为空！", Toast.LENGTH_SHORT).show();
                }
                else if(mealMoney.getText().length()==0){
                    Toast.makeText(MealActivity.this,"“餐费金额”不能为空！", Toast.LENGTH_SHORT).show();
                }
                else if(mealResturant.getText().length()==0){
                    Toast.makeText(MealActivity.this,"“餐馆”不能为空！", Toast.LENGTH_SHORT).show();
                }
                else{
                    //调用接口请求
                    addFeeForMeal( meal_date, meal_type, meal_mount, meal_resturant, meal_remark);

                }

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
        type_back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MealActivity.this);
                    //final View view = View.inflate(MealActivity.this, R.layout.meal_list, null);
                    //builder.setView(view);
                    builder.setTitle("选择工作餐类型：");
                    builder.setItems(items,null);
                    if(v.getId()==R.id.meal_type){
                        meal_back.onTouchEvent(motionEvent);
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
    private void addFeeForMeal(String meal_date, final String meal_type, String meal_mount, String meal_resturant, String meal_remark) {
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
                    finish();
            }
        },App.userId, App.username, meal_date, meal_type, meal_mount, meal_resturant, meal_remark);

    }

    //菜单列表栏适配器
    class MyBaseAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return dateList.size();
        }

        @Override
        public Object getItem(int position) {
            return dateList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           ViewHolder viewHolder;
            if (convertView == null){
                convertView = View.inflate(MealActivity.this,R.layout.menu_list,null);
                viewHolder=new ViewHolder();
                viewHolder.mTextView=convertView.findViewById(R.id.date_text);
                convertView.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) convertView.getTag();
            }
            viewHolder.mTextView.setText(dateList.get(position));
            return convertView;
        }
    }

    class ViewHolder{
        private TextView mTextView;
    }

    public void initData(){
        dateList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String curDate = sdf.format(new Date());//当前月份
        String preDate = getPreMonth();//上一个月份
        String lastDate = getLastMonth();//下一个月份
        dateList.add(lastDate);
        dateList.add(curDate);
        dateList.add(preDate);
    }

    /**
     * 获取上一个月
     *
     * @return
     */
    public static String getLastMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(cal.MONTH, -1);
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM");
        String lastMonth = dft.format(cal.getTime());
        return lastMonth;
    }

    /**
     *
     * 描述:获取下一个月.
     *
     * @return
     */
    public static String getPreMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(cal.MONTH, 1);
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM");
        String preMonth = dft.format(cal.getTime());
        return preMonth;
    }




}
