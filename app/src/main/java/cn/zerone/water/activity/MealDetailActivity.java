package cn.zerone.water.activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.zerone.water.R;

import static com.baidu.mapapi.BMapManager.getContext;

public class MealDetailActivity extends AppCompatActivity {

    private ListView mealListView;
    private List<MealDetailItem> mealDetailItemList;
    private TextView detail_title;


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

        mealListView = findViewById(R.id.list_view);
        initData();
        mealListView.setAdapter(new MealDetailActivityAdapter(getContext(),R.layout.mealdetailitem, mealDetailItemList));
    }

    public void initData(){
        mealDetailItemList = new ArrayList<>();
        MealDetailItem mealDetailItem_1=new MealDetailItem("2019-05-01","商务餐", "100");
        mealDetailItemList.add(mealDetailItem_1);
        MealDetailItem mealDetailItem_2=new MealDetailItem("2019-05-01","商务餐", "100");
        mealDetailItemList.add(mealDetailItem_2);
        MealDetailItem mealDetailItem_3=new MealDetailItem("2019-05-01","商务餐", "100");
        mealDetailItemList.add(mealDetailItem_3);
        MealDetailItem mealDetailItem_4=new MealDetailItem("2019-05-01","商务餐", "100");
        mealDetailItemList.add(mealDetailItem_4);
    }

}
