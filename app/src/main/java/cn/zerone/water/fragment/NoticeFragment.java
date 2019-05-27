package cn.zerone.water.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.zerone.water.R;
import cn.zerone.water.activity.NoticeActivity;
import cn.zerone.water.http.Requests;
import cn.zerone.water.model.ListViewAdapter;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class NoticeFragment extends Fragment {

    private ListView listView;
    private List<Map<String, Object>> list;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_system_messages, container, false);
        listView = (ListView) view.findViewById(R.id.system_message_listView);
        listView.setFooterDividersEnabled(true);
        list = new ArrayList<Map<String, Object>>();
        getData();

        return view;
    }

    public void getData() {

        Requests.UserMessage_GetList(new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(JSONArray objects) {
                for (int i = 0; i < objects.size(); i++) {

                    JSONObject json1 = new JSONObject();
                    JSONObject jsonObject = objects.getJSONObject(i);
                    String Title = jsonObject.getString("Title");
                    json1.put("title", Title);
                    String time = jsonObject.getString("AddTime");
                    String realTime = time.substring(6, 18);
                    Long longtime = Long.parseLong(realTime);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    String d = format.format(longtime);
                    json1.put("AddTime", d);
                    String content = jsonObject.getString("Msg");
                    json1.put("Msg", content);
                    String Type = jsonObject.getString("DataType");
                    json1.put("DataType", Type);
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
        });

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
