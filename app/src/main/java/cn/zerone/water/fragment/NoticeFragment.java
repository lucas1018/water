package cn.zerone.water.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.activity.NoticeActivity;
import cn.zerone.water.http.Requests;
import cn.zerone.water.adapter.ListViewAdapter;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class NoticeFragment extends Fragment {

    private ListView listView;
    private List<Map<String, Object>> list;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_system_messages, container, false);
        listView = (ListView) view.findViewById(R.id.system_message_listView);
        list = new ArrayList<Map<String, Object>>();
        getData();

        return view;
    }

    public void getData() {

        Requests.UserMessage_GetListByField(new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(JSONArray objects) {
                for (int i = 0; i < objects.size(); i++) {

                    JSONObject json1 = new JSONObject();
                    JSONObject jsonObject = objects.getJSONObject(i);
                    String Title = jsonObject.getString("Title");
                    if (Title == null){
                        json1.put("title", "");

                    }
                    else{
                        json1.put("title", Title);
                    }

                    String time = jsonObject.getString("AddTime");
                    if (time ==null){
                        json1.put("AddTime", "");
                    }
                    else{
                        String realTime = time.substring(6, 19);
                        Long longtime = Long.parseLong(realTime);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        String d = format.format(longtime);
                        json1.put("AddTime", d);
                    }
                    String content = jsonObject.getString("Msg");
                    if(content == null){
                        json1.put("Msg", "");

                    }
                    else {
                        json1.put("Msg", content);

                    }
                    String Type = jsonObject.getString("DataType");
                    if (Type == null){
                        json1.put("DataType", "");

                    }
                    else {
                        json1.put("DataType", Type);

                    }
                    list.add(json1);
                }
                UpdateAdapter(list);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        }, App.userId);

    }
    private void UpdateAdapter(List<Map<String, Object>> list) {
        listView.setAdapter(new ListViewAdapter(getActivity(), list));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                JSONObject list1 = JSONObject.parseObject(adapterView.getItemAtPosition(i).toString());
                bundle.putString("title", list1.get("title").toString());
                bundle.putString("AddTime", list1.get("AddTime").toString());
                bundle.putString("Msg", list1.get("Msg").toString());
                bundle.putString("DataType", list1.get("DataType").toString());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(getActivity(), NoticeActivity.class);
                startActivity(intent);

            }
        });
    }

}
