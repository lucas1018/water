package cn.zerone.water.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.view.View.OnClickListener;
import android.widget.Switch;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.activity.LiteActivity;
import cn.zerone.water.activity.NoticeActivity;
import cn.zerone.water.activity.TaskDetailActivity;
import cn.zerone.water.adapter.ListViewAdapter;
import cn.zerone.water.adapter.TaskAdapter;
import cn.zerone.water.http.Requests;
import cn.zerone.water.map.LocationActivity;
import cn.zerone.water.map.MarkerClusterActivity;
import cn.zerone.water.map.PoiSearchActivity;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by zero on 2018/12/3.
 */

public class TaskListFragment extends Fragment {

    private ListView task_list_view;
    private List<Map<String, Object>> list;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job, container, false);
        task_list_view =  view.findViewById(R.id.task_list_view);
        list = new ArrayList<Map<String, Object>>();
        getData();
        return view;
    }

    public void getData() {
//            Requests.EngineeringStation_GetList(App.userId)

        Requests.EngineeringStation_GetList(new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(JSONArray objects) {
                for (int i = 0; i < objects.size(); i++) {

                    JSONObject json1 = new JSONObject();
                    JSONObject jsonObject = objects.getJSONObject(i);
                    String StationName = jsonObject.getString("StationName");
                    json1.put("stationName",StationName);

                    String Address = jsonObject.getString("Address");
                    json1.put("address",Address);

                    String State = jsonObject.getString("State");
                    String stateName = "";
                    switch(State){
                        case "0":
                            stateName = "未开始";
                        case "1":
                            stateName = "建设中";
                        case "2":
                            stateName = "已建成";
                        case "3":
                            stateName = "已付款";
                    }

                    json1.put("state",stateName);

                    String BeginDate = jsonObject.getString("BeginDate");
                    String realTime = BeginDate.substring(6, 19);
                    Long longtime = Long.parseLong(realTime);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
                    String showTime = format.format(longtime);
                    json1.put("beginDate",showTime);

                    list.add(json1);

                }
                UpdateAdapter2(list);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        }, App.userId);

    }
    private void UpdateAdapter2(List<Map<String, Object>> list) {
        TaskAdapter taskAdapter=new TaskAdapter(getActivity(),list);

        task_list_view.setAdapter(taskAdapter);
        taskAdapter.setOnClickListener(new TaskAdapter.onClickListener1() {
            @Override
            public void onClick(int position) {
                Bundle bundle = new Bundle();
                JSONObject list1 = JSONObject.parseObject(task_list_view.getItemAtPosition(position).toString());

                bundle.putString("stationName", list1.get("stationName").toString());
                bundle.putString("address", list1.get("address").toString());
                bundle.putString("state", list1.get("state").toString());
                bundle.putString("beginDate", list1.get("beginDate").toString());

                System.out.println(list1.get("stationName").toString());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(getActivity(), TaskDetailActivity.class);
                startActivity(intent);
            }
        });

//        task_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Bundle bundle = new Bundle();
//
//                System.out.println("----------------》》》开始进行绑定");
//                JSONObject list1 = JSONObject.parseObject(adapterView.getItemAtPosition(i).toString());
//
//                bundle.putString("stationName", list1.get("stationName").toString());
//                bundle.putString("address", list1.get("address").toString());
//                bundle.putString("state", list1.get("state").toString());
//                bundle.putString("beginDate", list1.get("beginDate").toString());
//
//                System.out.println(list1.get("stationName").toString());
//                Intent intent = new Intent();
//                intent.putExtras(bundle);
//                intent.setClass(getActivity(), TaskDetailActivity.class);
//                startActivity(intent);
//
//            }
//        });
    }

}
