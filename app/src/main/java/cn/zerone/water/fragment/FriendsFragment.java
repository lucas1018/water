package cn.zerone.water.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.activity.ChatActivity;
import cn.zerone.water.activity.MainActivity;
import cn.zerone.water.http.Requests;
import cn.zerone.water.manager.ChatManager;
import cn.zerone.water.model.ChatMessage;
import cn.zerone.water.model.Friend;
import cn.zerone.water.utils.SortList;
import cn.zerone.water.utils.TimeUtil;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class FriendsFragment extends Fragment {
    ListView listView = null;
    List<Friend> friends = new ArrayList();
    SwipeRefreshLayout swipeLoading = null;
    private BaseAdapter listAdapter;
    private JSONArray tempFriends = null;
    private boolean isQuick = false;
    private TextView tvConnect = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.activity_friends, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            App application = (App) getActivity().getApplication();
            if (application.engineeringStation != null) {
                String connectText = MessageFormat.format("已连接\n[{0}] - [{1}] - [{2}]",application.engineeringStation.getWorkName(),application.engineeringStation.getTaskName(),application.engineeringStation.getStepName());
                tvConnect.setText(connectText);
                tvConnect.setVisibility(View.VISIBLE);
            }else{
                tvConnect.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            tvConnect.setVisibility(View.GONE);
        }

    }
    boolean isHidden = false;
    @Override
    public void onHiddenChanged(boolean hidden) {
        isHidden = hidden;
        if(!hidden){
            try {
                App application = (App) getActivity().getApplication();
                if (application.engineeringStation != null) {
                    String connectText = MessageFormat.format("已连接\n{1} - [{2}]",application.engineeringStation.getWorkName(),application.engineeringStation.getTaskName(),application.engineeringStation.getStepName());
                    tvConnect.setText(connectText);
                    tvConnect.setVisibility(View.VISIBLE);
                }else{
                    tvConnect.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                tvConnect.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.lv_friends);
        swipeLoading = view.findViewById(R.id.swipe_loading);
        tvConnect =  view.findViewById(R.id.tv_connect);
        tvConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App application = (App) getActivity().getApplication();
                application.clearEngineeringStation();
                tvConnect.setVisibility(View.GONE);
            }
        });
        listAdapter = new CommonAdapter<Friend>(getActivity(), R.layout.layout_friend_listview_item, friends) {
            @Override
            protected void convert(ViewHolder holder, Friend item, int position) {
                holder.setText(R.id.item_tv_friend_name, item.getUserName());
                holder.setText(R.id.item_tv_friend_last_text, item.getLastText());
                if (item.getLastTextTime() != 0) {
                    holder.setText(R.id.item_tv_friend_last_time, TimeUtil.QQFormatTime(item.getLastTextTime()) + "");
                } else {
                    holder.setText(R.id.item_tv_friend_last_time, "");
                }
                holder.getView(R.id.item_iv_friend_head);
                Uri uri = Uri.parse(item.getHeadImg());
                SimpleDraweeView draweeView = holder.getView(R.id.item_iv_friend_head);
                draweeView.setImageURI(uri);
            }
        };
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long itemId) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("userName", friends.get(position).getUserName());
                intent.putExtra("userId", friends.get(position).getUserId());
                intent.putExtra("toHeadImg", friends.get(position).getHeadImg());
                intent.putExtra("headImg", getMyHeadImg());
                isQuick = true;
                startActivityForResult(intent, 0);
                getMsg();
            }
        });
        swipeLoading.setRefreshing(true);
        swipeLoading.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateLastMessage(FriendsFragment.this.friends);
                swipeLoading.setRefreshing(false);
            }
        });
//        Requests.getUserList(new Observer<JSONArray>() {
//
//
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(JSONArray friends) {
//                tempFriends = friends;
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                tempFriends = null;
//                swipeLoading.setRefreshing(false);
//                System.out.println("onError");
//            }
//
//            @Override
//            public void onComplete() {
//                try {
//                    FriendsFragment.this.friends.clear();
//                    for (int i = 0; i < tempFriends.size(); i++) {
//                        String userName = tempFriends.getJSONObject(i).getString("UserName");
//                        if (App.username != null && App.username.equals(userName)) {
//                            continue;
//                        }
//                        FriendsFragment.this.friends.add(new Friend(userName, tempFriends.getJSONObject(i).getString("HeadImg"), tempFriends.getJSONObject(i).getString("UserId")));
//                    }
//                    updateLastMessage(FriendsFragment.this.friends);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                swipeLoading.setRefreshing(false);
//                System.out.println("onComplete:" + tempFriends);
//            }
//        });
//        Requests.getchatinfo(new GetMsgObserver(), App.userId);
    }

    private String getMyHeadImg() {
        for (int i = 0; i < friends.size(); i++) {
            Friend friend = friends.get(i);
            if (App.username.equals(friend.getUserName())) {
                return friend.getHeadImg();
            }
        }
        return "";
    }

    boolean isGetMsg = false;

    class GetMsgObserver implements Observer<com.alibaba.fastjson.JSONArray> {
        List<ChatMessage> msgs = new ArrayList<>();

        public GetMsgObserver() {
        }

        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onNext(JSONArray array) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject s = array.getJSONObject(i);
                long addTime = s.getLong("AddTime") * 1000;
                String sendUserId = s.getString("FromUserId");
                String msg = s.getString("Msg");
                int engineeringStationId = s.getInteger("EngineeringStationId");
                int taskId= s.getInteger("TaskInfoId");;
                int stepId= s.getInteger("StepId");;
                int type = ChatMessage.TYPE_TEXT;
                if (msg.contains("[视频]")) {
                    type = ChatMessage.TYPE_VIDEO;
                } else if (msg.contains("[图片]")) {
                    type = ChatMessage.TYPE_IMAGE;
                }
                try {
                    ChatMessage chatMessage = new ChatMessage("", null, sendUserId, msg, addTime + 1000, true, type);
                    chatMessage.setEngineeringStationId(engineeringStationId);
                    chatMessage.setTaskId(taskId);
                    chatMessage.setStepId(stepId);
                    msgs.add(chatMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onError(Throwable e) {
            isGetMsg = false;
            getMsg();
        }

        @Override
        public void onComplete() {
            isGetMsg = false;
            System.out.println(msgs);
            boolean isUpdate= false;
            try {
                for (int i = 0; i < msgs.size(); i++) {
                  boolean temp =   ChatManager.getInstance(getActivity(), App.username).insert(msgs.get(i));
                    if(!isUpdate){
                        isUpdate  =temp;
                    }
                }
                updateLastMessage(FriendsFragment.this.friends);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                getMsg();
                try{
                    if(isHidden&&isUpdate){
                        MainActivity activity = (MainActivity) getActivity();
                        activity.setMsgReadView(true);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    private void getMsg() {
        if (isQuick) {
            if (getMsg != null) {
                handler.removeCallbacks(getMsg);
                getMsg = null;
            }
            handler.postDelayed(getMsg = new GetMsgRunnable(), 2 * 1000);
        } else {
            if (getMsg != null) {
                handler.removeCallbacks(getMsg);
                getMsg = null;
            }
            handler.postDelayed(getMsg = new GetMsgRunnable(), 3 * 60 * 1000);
        }
    }

    Runnable getMsg = null;

    class GetMsgRunnable implements Runnable {

        @Override
        public void run() {
            getMsg = null;
            if (isGetMsg) {
                return;
            }
//            Requests.getchatinfo(new GetMsgObserver(), App.userId);
            isGetMsg = true;
            return;
        }
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:

            }
        }
    };

    public void updateLastMessage(final List<Friend> friends) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < friends.size(); i++) {
                    ChatManager.getInstance(getActivity(), App.username).setFriendLastMessage(friends.get(i));
                }
                new SortList<Friend>().Sort(friends, "getLastTextTime", "desc");
                listAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            isQuick = false;
            updateLastMessage(this.friends);
        }
    }
}
