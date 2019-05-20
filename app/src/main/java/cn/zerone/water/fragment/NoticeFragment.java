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
        View view= inflater.inflate(R.layout.activity_system_messages , container, false);
        listView = (ListView)view.findViewById(R.id.system_message_listView);
        list = new ArrayList<Map<String, Object>>();
        list = getData();
        listView.setAdapter(new ListViewAdapter(getActivity(), list));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();

                JSONObject list1 =  JSONObject.parseObject(adapterView.getItemAtPosition(i).toString());

                bundle.putString("title", list1.get("title").toString());
                bundle.putString("Createtime",list1.get("Createtime").toString());
                bundle.putString("info",list1.get("info").toString());
                bundle.putString("DataType",list1.get("DataType").toString());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(getActivity(), NoticeActivity.class);
                startActivity(intent);

            }
        });
        return view;
    }

    public List<Map<String,Object>> getData() {

        Requests.Notice_GetList(new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(JSONArray objects) {
                for(int i = 0; i<objects.size();i++){

                    JSONObject json1 = new JSONObject();
                    //Map<String, Object> map=new HashMap<String, Object>();
                    JSONObject   jsonObject  =  objects.getJSONObject(i) ;
                    String Title = jsonObject.getString("Title");
                    json1.put("title",Title);
                    String time = jsonObject.getString("CreateTime1");
                    json1.put("Createtime", time);
                    String content = jsonObject.getString("Details");
                    json1.put("info", content);
                    String Type = jsonObject.getString("DataType");
                    json1.put("DataType",Type);
                    list.add(json1);

                }

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        return list;
    }

}
