package cn.zerone.water.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.zerone.water.R;
import cn.zerone.water.activity.NoticeActivity;
import cn.zerone.water.model.ListViewAdapter;

public class NoticeFragment extends Fragment {
    private ListView listView;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.activity_system_messages , container, false);
        listView = (ListView)view.findViewById(R.id.system_message_listView);
        List<Map<String, Object>> list = getData();
        listView.setAdapter(new ListViewAdapter(getActivity(), list));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putString("title","系统通知"+i);
                bundle.putString("Createtime","2018/3/4"+i);
                bundle.putString("info","这是一个详细信息" + i);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(getActivity(), NoticeActivity.class);
                startActivity(intent);

            }
        });
        return view;
    }

    private List<Map<String,Object>> getData() {
        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAa");

//        Requests.Notice_GetList(new Observer<JSONObject>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//            @Override
//            public void onNext(JSONObject jsonObject) {
//                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+jsonObject);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
        for (int i = 0; i < 20; i++) {
            Map<String, Object> map=new HashMap<String, Object>();
            map.put("title","系统通知" +i);
            map.put("Createtime", "2019/5/15"+i);
            map.put("info", "这是一个详细信息" + i);
            list.add(map);
        }
        return list;
    }

}
