package cn.zerone.water.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Intent;

import cn.zerone.water.R;
import cn.zerone.water.activity.ClockInCarActivity;
import cn.zerone.water.activity.ClockInFinishActivity;
import cn.zerone.water.activity.ClockInHomeActivity;
import cn.zerone.water.activity.ClockInWorkActivity;

/**
 * Created by zero on 2018/12/3.
 */

<<<<<<< Updated upstream
public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home,null);
        Button car_btn = (Button) view.findViewById(R.id.car_button);
        car_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ClockInCarActivity.class));
            }
        });
        Button finish_btn = (Button) view.findViewById(R.id.finish_button);
        finish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ClockInFinishActivity.class));
            }
        });
        Button home_btn = (Button) view.findViewById(R.id.home_button);
        home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ClockInHomeActivity.class));
            }
        });
        Button clock_btn = (Button) view.findViewById(R.id.clock_button);
        clock_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ClockInWorkActivity.class));
            }
        });
        return view;
=======
public class HomeFragment extends MasterArticleFragment {

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    }

    public String title() {
        return  "首页";
>>>>>>> Stashed changes
    }
}
