package cn.zerone.water.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.view.View.OnClickListener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.zerone.water.R;
import cn.zerone.water.activity.LiteActivity;
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
        View view = inflater.inflate(R.layout.fragment_job,null);
        task_list_view = (ListView) view.findViewById(R.id.task_list_view);
        list = new ArrayList<Map<String, Object>>();
        getData();
        return view;
    }

    public void getData() {

        Requests.TaskInfo_GetList(new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(JSONArray objects) {
                for (int i = 0; i < objects.size(); i++) {

                    JSONObject json1 = new JSONObject();
                    JSONObject jsonObject = objects.getJSONObject(i);
                    //任务名称
                    String TaskName = jsonObject.getString("TaskName");
                    json1.put("TaskName", TaskName);
                    //任务时间
                    String time = jsonObject.getString("AddTime");
                    String realTime = time.substring(6, 18);
                    Long longtime = Long.parseLong(realTime);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    String d = format.format(longtime);
                    json1.put("AddTime", d);
                    //任务介绍
                    String TaskIntro = jsonObject.getString("TaskIntro");
                    json1.put("TaskIntro", TaskIntro);
                    //任务类型
                    String TaskType = jsonObject.getString("TaskType");
                    json1.put("TaskType", TaskType);
                    //站点名称
                    String stationId = jsonObject.getString("StationId");
                    json1.put("stationId",stationId);
                    list.add(json1);

                }
//                UpdateAdapter(list);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }





    public String title() {
        return  "任务";
    }
}
