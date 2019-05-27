package cn.zerone.water.activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.zerone.water.R;

import static com.baidu.mapapi.BMapManager.getContext;

public class MealDetailActivity extends AppCompatActivity {

    private ListView mealListView;
    private List<MealDetailItem> mealDetailItemList;


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
