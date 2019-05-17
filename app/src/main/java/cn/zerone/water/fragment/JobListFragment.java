package cn.zerone.water.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.view.View.OnClickListener;
<<<<<<< HEAD

import cn.zerone.water.activity.LiteActivity;

=======
>>>>>>> master
import cn.zerone.water.R;
import cn.zerone.water.activity.LiteActivity;
import cn.zerone.water.map.MarkerClusterDemo;
import cn.zerone.water.map.PoiSearchActivity;

/**
 * Created by zero on 2018/12/3.
 */

public class JobListFragment extends Fragment {

    private ListView task_listView;
    private  Button but_nvi, but_map, but_loc;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job,null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        task_listView = view.findViewById(R.id.task_list_view);
        but_nvi = view.findViewById(R.id.button_nvi);
        but_loc = view.findViewById(R.id.button_location);
        but_map = view.findViewById(R.id.button_map);

        but_nvi.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LiteActivity.class);
                getContext().startActivity(intent);
            }

        });
        but_map.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MarkerClusterDemo.class);
                getContext().startActivity(intent);
            }

        });
        but_loc.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PoiSearchActivity.class);
                getContext().startActivity(intent);
            }

        });
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }





    public String title() {
        return  "任务";
    }
}
