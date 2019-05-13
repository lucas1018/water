package cn.zerone.water.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.activity.CheckActivity;
import cn.zerone.water.activity.MealActivity;
import cn.zerone.water.activity.PasswordModifiedActivity;
import cn.zerone.water.activity.PhoneNumberModifiedActivity;
import cn.zerone.water.activity.SystemUpdateActivity;
import cn.zerone.water.http.Requests;
import cn.zerone.water.utils.ImageUtil;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by zero on 2018/12/3.
 */

public class MyselfFragment extends Fragment {
    private ListView listView;
    private List<MyItem> myItemList;

    private TextView userName;
    private ImageView headImg;
    private TextView nickName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myself,null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        initData();
        listView=view.findViewById(R.id.list_view);
        userName = view.findViewById(R.id.user_name);
        headImg = view.findViewById(R.id.myself_image);
        nickName = view.findViewById(R.id.nick_name);


        Requests.getUserInfo(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(JSONObject jsonObject) {
                String str = jsonObject.getString("Data");
                JSONObject json = JSONArray.parseArray(str).getJSONObject(0);
                String username = json.getString("LOGIN_NAME");
                App.username = username;
                userName.setText(username);
                String imgUrl = json.getString("Photo");
                ImageUtil imageUtil = ImageUtil.getIntance();
                Bitmap temp_bitmap = imageUtil.getBitMBitmap(imgUrl);
                Bitmap bitmap = imageUtil.comp(temp_bitmap);
                headImg.setImageBitmap(bitmap);
                String nickname = json.getString("NAME");
                nickName.setText(nickname);
            }
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
            @Override
            public void onComplete() {
            }
        },App.userId);
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
                    case 0:
                        i = new Intent(getContext(), CheckActivity.class);
                        getContext().startActivity(i);
                        break;
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
