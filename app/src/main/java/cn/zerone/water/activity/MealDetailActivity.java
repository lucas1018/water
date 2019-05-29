package cn.zerone.water.activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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

import static com.baidu.mapapi.BMapManager.getContext;

public class MealDetailActivity extends AppCompatActivity {

    private ListView mealListView;
    private List<MealDetailItem> mealDetailItemList;
    private TextView detail_title;
    private String firstDay;
    private String endDay;
    private JSONArray jsonArray;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_detail);

        //返回页面
        ImageView meal_detail_back = findViewById(R.id.meal_detail_back);
        meal_detail_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //获取上一个页面传过来的值
        Intent intent = getIntent();
        String currentTime = intent.getStringExtra("currentDate");
        detail_title = findViewById(R.id.detail_title);
        detail_title.setText(currentTime + "月餐费详情");

        firstDay = currentTime + "-01";
        String years = currentTime.substring(0,4);
        int year = Integer.parseInt(years);
        String month = currentTime.substring(5,7);
        if (month.equals("02")) {
            if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
                endDay = currentTime + "-29";
            } else {
                endDay = currentTime + "-28";
            }
        } else if(month.equals("01") || month.equals("03") || month.equals("05") || month.equals("07") ||month.equals("08") ||month.equals("10")|| month.equals("12")) {
            endDay = currentTime + "-31";
        } else {
            endDay = currentTime + "-30";
        }
        mealListView = findViewById(R.id.list_view);
        initData();
        mealListView.setAdapter(new MealDetailActivityAdapter(getContext(),R.layout.mealdetailitem, mealDetailItemList));
    }

    public void initData(){
        mealDetailItemList = new ArrayList<>();
        Requests.FeesForMeals_GetPageInfo(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }
            @Override
            public void onNext(JSONObject objects) {
                jsonArray = JSON.parseArray(objects.getString("ModelList"));
                System.out.println("99999" + jsonArray);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (int i =0 ;i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String time = jsonObject.getString("AddTime");
                    String t = time.substring(6, 19);
                    Long timeLong = Long.parseLong(t);
                    Date tmp = new Date(timeLong);
                    String return_date = simpleDateFormat.format(tmp);
                    String type = jsonObject.getString("Name");
                    String fee = jsonObject.getString("Cost");
                    System.out.println("nnnnnnnn" + return_date + "ccccc" + type + "bbbbbbbbbbb" + fee);
                    mealDetailItemList.add(new MealDetailItem(return_date,type,fee));
                    System.out.println("mmmmmmmmmm" + mealDetailItemList);
                }
            }
            @Override
            public void onError(Throwable e) {
            }
            @Override
            public void onComplete() {

            }
        }, App.userId, firstDay, endDay );

/*
        MealDetailItem mealDetailItem_1=new MealDetailItem("2019-05-01","商务餐", "100");
        mealDetailItemList.add(mealDetailItem_1);
        MealDetailItem mealDetailItem_2=new MealDetailItem("2019-05-01","商务餐", "100");
        mealDetailItemList.add(mealDetailItem_2);
        MealDetailItem mealDetailItem_3=new MealDetailItem("2019-05-01","商务餐", "100");
        mealDetailItemList.add(mealDetailItem_3);
        MealDetailItem mealDetailItem_4=new MealDetailItem("2019-05-01","商务餐", "100");
        mealDetailItemList.add(mealDetailItem_4);*/
    }

}
