package cn.zerone.water.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.activity.MealActivity;
import cn.zerone.water.activity.PasswordModifiedActivity;
import cn.zerone.water.activity.PhoneNumberModifiedActivity;
import cn.zerone.water.activity.SystemUpdateActivity;
import cn.zerone.water.http.Requests;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by zero on 2018/12/3.
 */

public class MyselfFragment extends Fragment {
    private ListView listView;
    private List<MyItem> myItemList;
    private TextView userName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_myself,null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        listView=view.findViewById(R.id.list_view);
        userName = view.findViewById(R.id.user_name);
        userName.setText(App.username);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setAdapter(new MySelfFragmentAdapter(getContext(),R.layout.myitem,myItemList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i;
                switch (position) {
                    case 3:
                        i = new Intent(getContext(), MealActivity.class);
                        getContext().startActivity(i);
                        break;
                    case 4:
                        i = new Intent(getContext(), PhoneNumberModifiedActivity.class);
                        getContext().startActivity(i);
                        break;
                    case 5:
                        i = new Intent(getContext(), PasswordModifiedActivity.class);
                        getContext().startActivity(i);
                        break;
                    case 6:
                        i = new Intent(getContext(), SystemUpdateActivity.class);
                        getContext().startActivity(i);
                        break;
                    default:
                        break;
                }
//                 else {
//                     Toast.makeText(getContext(),"还没有实现",Toast.LENGTH_SHORT).show();
//                 }


//                Toast.makeText(getContext(),"name="+myItemList.get(position).getItemLab(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initData(){
        myItemList=new ArrayList<>();
        MyItem myItem_1=new MyItem("我的审批", R.drawable.trues);
        myItemList.add(myItem_1);
        MyItem myItem_2=new MyItem("我的工作日志", R.drawable.pen);
        myItemList.add(myItem_2);
        MyItem myItem=new MyItem("我的安全检查", R.drawable.date);
        myItemList.add(myItem);
        MyItem myItem_3=new MyItem("我的工作餐", R.drawable.coffee);
        myItemList.add(myItem_3);
        MyItem myItem_4=new MyItem("修改手机号", R.drawable.phone);
        myItemList.add(myItem_4);
        MyItem myItem_5=new MyItem("修改密码", R.drawable.password);
        myItemList.add(myItem_5);
        MyItem myItem_6=new MyItem("系统更新", R.drawable.setting);
        myItemList.add(myItem_6);
    }
}
