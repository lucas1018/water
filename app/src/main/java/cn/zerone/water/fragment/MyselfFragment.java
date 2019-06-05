package cn.zerone.water.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.activity.ApproveActivity;
import cn.zerone.water.activity.CalenderActivity;
import cn.zerone.water.activity.LoginActivity;
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
    private TextView phoneNum;

    private ImageView photo1;

    private Button action_sign_out;

    private String imgUrl = null;


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
        listView = view.findViewById(R.id.list_view);
        userName = view.findViewById(R.id.user_name);
        phoneNum = view.findViewById(R.id.phone_number);
        photo1=view.findViewById(R.id.image_1);

        action_sign_out = view.findViewById(R.id.action_sign_out);

        Requests.USER_INFO_GetModelBLL(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(JSONObject json) {
                //设置用户名
                String username = json.getString("NAME");
                App.username = username;
                userName.setText(username);
                //设置头像
                imgUrl = json.getString("Photo");
                if (imgUrl == null || imgUrl.equals("")) {
                    photo1.setImageResource(R.mipmap.logo);
                } else {
                    String url = "http://47.105.187.185:8011" + imgUrl;
                    ImageUtil imageUtil = ImageUtil.getInstance();
                    Bitmap temp_bitmap = ImageUtil.getBitMBitmap(url);
                    Bitmap bitmap = imageUtil.comp(temp_bitmap);
                    photo1.setImageBitmap(bitmap);
                }
                //设置登录名
                String login_name = json.getString("LOGIN_NAME");
                phoneNum.setText(login_name);
                //获取密码
                String pwd = json.getString("PASSWORD");
                App.pwd = pwd;
            }
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
            @Override
            public void onComplete() {

            }
        }, App.userId);



        action_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                System.exit(0);
                Intent i = new Intent(getContext(), LoginActivity.class);
                getContext().startActivity(i);
            }
        });

    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setAdapter(new MySelfFragmentAdapter(getContext(),R.layout.myitem, myItemList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i;
                switch (position) {
                    case 0:
                        i = new Intent(getContext(), ApproveActivity.class);
                        getContext().startActivity(i);
                        break;
                    case 1:
                        i = new Intent(getContext(), CalenderActivity.class);
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
        MyItem myItem_1=new MyItem("我的审批", R.mipmap.my_myself);
        myItemList.add(myItem_1);
        MyItem myItem_2=new MyItem("我的工作日志", R.mipmap.my_log);
        myItemList.add(myItem_2);
        MyItem myItem=new MyItem("我的安全检查", R.mipmap.my_safe);
        myItemList.add(myItem);
        MyItem myItem_3=new MyItem("我的工作餐", R.mipmap.my_meal);
        myItemList.add(myItem_3);
        MyItem myItem_4=new MyItem("修改手机号", R.mipmap.my_phone);
        myItemList.add(myItem_4);
        MyItem myItem_5=new MyItem("修改密码", R.mipmap.my_pwd);
        myItemList.add(myItem_5);
        MyItem myItem_6=new MyItem("系统更新", R.mipmap.my_up);
        myItemList.add(myItem_6);
    }

}
