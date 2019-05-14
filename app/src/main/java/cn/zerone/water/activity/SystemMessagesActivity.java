package cn.zerone.water.activity;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import cn.zerone.water.model.DaoMaster;
import cn.zerone.water.model.Friend;
import cn.zerone.water.model.SystemMessage;
import cn.zerone.water.model.SystemMessageDao;
import cn.zerone.water.utils.TimeUtil;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by zero on 2018/11/30.
 */

public class SystemMessagesActivity extends Fragment {
    ListView listview = null;
    List<SystemMessage> systemMessages = new ArrayList<>();
    private CommonAdapter<SystemMessage> listAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return View.inflate(getActivity(),R.layout.activity_system_messages,null);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            initData();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listview = view.findViewById(R.id.system_message_listView);
        listAdapter = new CommonAdapter<SystemMessage>(getActivity(), R.layout.layout_system_message_listview_item, systemMessages) {
            @Override
            protected void convert(ViewHolder holder, SystemMessage item, int position) {
                System.out.print("内容"+ item.getContent());
                holder.setText(R.id.system_message_item_content, item.getContent());
                holder.setText(R.id.system_message_item_createTime, TimeUtil.QQFormatTime(item.getCreateTime()));
                holder.setText(R.id.system_message_item_level, item.getTitle());
            }
        };
        listview.setAdapter(listAdapter);
        initData();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initData() {
        try{
            DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(getActivity(), App.username);
            SQLiteDatabase db = devOpenHelper.getWritableDatabase();
            DaoMaster daoMaster = new DaoMaster(db);
            systemMessages.clear();
            systemMessages.addAll(daoMaster.newSession().getSystemMessageDao().queryBuilder().orderDesc(SystemMessageDao.Properties.CreateTime).build().list());
            listAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
