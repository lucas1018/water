package cn.zerone.water.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zhy.adapter.abslistview.MultiItemTypeAdapter;
import com.zhy.adapter.abslistview.ViewHolder;
import com.zhy.adapter.abslistview.base.ItemViewDelegate;
import com.zxing.cameraapplication.CameraActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import cn.zerone.water.manager.ChatManager;
import cn.zerone.water.model.ChatMessage;
import cn.zerone.water.model.EngineeringStation;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ChatActivity extends AppCompatActivity {
    TextView tvSend = null;
    EditText edText = null;
    View tvVideoOrPic = null;
    ListView listView = null;
    List<ChatMessage> chatMessages = new ArrayList<>();
    String toUsername = null;
    String toUserId = null;
    String toHeadImg;
    ChatAdapter chatAdapter = null;
    private String headImg;
    boolean isReply =false;
    private EngineeringStation tempEngineeringStation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        listView = findViewById(R.id.lv_chat);
        listView.setDivider(null);
        chatAdapter = new ChatAdapter(this, chatMessages);
        listView.setAdapter(chatAdapter);
        toUsername = getIntent().getExtras().getString("userName");
        toUserId = getIntent().getExtras().getString("userId");
        toHeadImg = getIntent().getStringExtra("toHeadImg");
        headImg = getIntent().getStringExtra("headImg");

        ((TextView) findViewById(R.id.title)).setText(toUsername);
        tvSend = findViewById(R.id.chat_tv_send);
        edText = findViewById(R.id.chat_et_text);
        tvVideoOrPic = findViewById(R.id.chat_tv_videoOrPic);
        tvVideoOrPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, CameraActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send(edText.getText().toString());
            }
        });
        notifyDataSetChanged();
        ChatManager.getInstance(this,App.username).listen(new Runnable(){

            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }
    class SendObserver implements Observer<String> {
        private final String sendMsg;

        public SendObserver(String sendMsg) {
            this.sendMsg = sendMsg;
        }

        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(String s) {
            System.out.println("SendObserver："+s);
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            Toast.makeText(ChatActivity.this,"发送失败",0).show();
        }

        @Override
        public void onComplete() {
            int type = ChatMessage.TYPE_TEXT;
            if(sendMsg.contains("[视频]")){
                type = ChatMessage.TYPE_VIDEO;
            }else if(sendMsg.contains("[图片]")){
                type = ChatMessage.TYPE_IMAGE;
            }
            ChatMessage chatMessage = new ChatMessage("", toUsername,toUserId,sendMsg , System.currentTimeMillis(), false, type);
            ChatManager.getInstance(ChatActivity.this, App.username).insert(chatMessage);
            notifyDataSetChanged();
            listView.setSelection(chatMessages.size());
            setResult(2,new Intent());
        }
    }

    private void send(String text) {
        if(text!=null&&"".equals(text.replace(" ",""))){
            return ;
        }
        if(tempEngineeringStation!=null){
            Requests.sendChatinfo(new SendObserver(text), App.token, toUserId, text,tempEngineeringStation);
        }else{
            Requests.sendChatinfo(new SendObserver(text), App.token, toUserId, text,((App) getApplication()).engineeringStation);
        }
        listView.setSelection(chatMessages.size());
        edText.setText("");
        setResult(2,new Intent());
    }
    private void sendImage(final String imgPath) {
        Requests.upload(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }
            @Override
            public void onNext(JSONObject s) {
                System.out.println(s);
                String url = s.getString("Url");
                String nativeUrl = "[图片]" + imgPath;
                String msg = "[图片]" + url;
                if(tempEngineeringStation!=null){
                    Requests.sendChatinfo(new SendObserver(nativeUrl), App.token, toUserId, msg, tempEngineeringStation);
                }else{
                    Requests.sendChatinfo(new SendObserver(nativeUrl), App.token, toUserId, msg, ((App) getApplication()).engineeringStation);
                }
            }

            @Override
            public void onError(Throwable e) {
            e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        },App.token,new File(imgPath));
    }

    private void sendVideo(final String videoUrl) {
        Requests.upload(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject s) {
                String url = s.getString("Url");
                System.out.println(s);
                String nativeUrl = "[视频]" + videoUrl;
                String msg = "[视频]" + url;
                if(tempEngineeringStation!=null){
                    Requests.sendChatinfo(new SendObserver(nativeUrl), App.token, toUserId, msg, tempEngineeringStation);
                }else{
                    Requests.sendChatinfo(new SendObserver(nativeUrl), App.token, toUserId, msg, ((App)getApplication()).engineeringStation);
                }
            }
            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        },App.token,new File(videoUrl));

    }
    public void notifyDataSetChanged() {
        List<ChatMessage> messages = ChatManager.getInstance(ChatActivity.this, App.username).getMessages(toUserId);
        if (messages != null) {
            chatMessages.clear();
            chatMessages.addAll(messages);
            chatAdapter.notifyDataSetChanged();
            listView.setSelection(chatMessages.size());
        }

    }
    public static void showOrHide(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
          imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);//SHOW_FORCED表示强制显示
//        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }
    public class ChatAdapter extends MultiItemTypeAdapter<ChatMessage> {
        public ChatAdapter(Context context, List<ChatMessage> datas) {
            super(context, datas);
            addItemViewDelegate(new MsgSendItemDelegate());
            addItemViewDelegate(new MsgComingItemDelegate());
        }

    }

    public class MsgComingItemDelegate implements ItemViewDelegate<ChatMessage> {

        @Override
        public int getItemViewLayoutId() {
            return R.layout.main_chat_from_msg;
        }

        @Override
        public boolean isForViewType(ChatMessage item, int position) {
            return item.isComMeg();
        }

        @Override
        public void convert(ViewHolder holder, final ChatMessage chatMessage, int position) {
            holder.setText(R.id.chat_from_content, chatMessage.getContent());
            View view = holder.getView(R.id.org);
            if(chatMessage.getEngineeringStationId()!=0){
                view.setVisibility(View.VISIBLE);
            }else{
                view.setVisibility(View.INVISIBLE);
            }
            Uri uri = Uri.parse(toHeadImg);
            SimpleDraweeView draweeView = holder.getView(R.id.chat_from_icon);
            draweeView.setImageURI(uri);
            holder.setOnLongClickListener(R.id.chat_from_content, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(chatMessage.getEngineeringStationId()!=0){
                        tempEngineeringStation = new EngineeringStation();
                        tempEngineeringStation.setStepId(chatMessage.getStepId());
                        tempEngineeringStation.setTaskId(chatMessage.getTaskId());
                        tempEngineeringStation.setEngineeringStationId(chatMessage.getEngineeringStationId());
                        changeReplay(true);
                    }

                    return true;
                }
            });
            holder.setOnClickListener(R.id.chat_from_content,new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if(chatMessage.getType() == ChatMessage.TYPE_IMAGE){
                        Intent intent = new Intent(ChatActivity.this, FullImageActivity.class);
                        intent.putExtra("imagePath",chatMessage.getContent().replace("[图片]",""));
                        startActivity(intent);
                    }else if(chatMessage.getType() ==ChatMessage.TYPE_VIDEO){
                        Intent intent = new Intent(ChatActivity.this, FullVideoActivity.class);
                        intent.putExtra("videoPath",chatMessage.getContent().replace("[视频]",""));
                        startActivity(intent);
                    }
                }
            });
        }
    }

    public void changeReplay(boolean replay) {
        if(replay){
            isReply =true;
            Toast.makeText(this, "当前为回复模式", Toast.LENGTH_SHORT).show();
            tvSend.setText("回复");
        }else{
            isReply =false;
            tvSend.setText("发送");
            Toast.makeText(this, "当前为正常模式", Toast.LENGTH_SHORT).show();
            tempEngineeringStation = null;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if(isReply){
                    changeReplay(false);
                    return true;
                }
                break;
        }
        return super.onKeyDown(keyCode,event);
    }

    public class MsgSendItemDelegate implements ItemViewDelegate<ChatMessage> {

        @Override
        public int getItemViewLayoutId() {
            return R.layout.main_chat_send_msg;
        }

        @Override
        public boolean isForViewType(ChatMessage item, int position) {
            return !item.isComMeg();
        }

        @Override
        public void convert(ViewHolder holder, final ChatMessage chatMessage, int position) {
            holder.setText(R.id.chat_send_content, chatMessage.getContent());
            Uri uri = Uri.parse(headImg);
            SimpleDraweeView draweeView = holder.getView(R.id.chat_send_icon);
            draweeView.setImageURI(uri);
            holder.setOnClickListener(R.id.chat_send_content,new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if(chatMessage.getType() == ChatMessage.TYPE_IMAGE){
                        Intent intent = new Intent(ChatActivity.this, FullImageActivity.class);
                        intent.putExtra("imagePath",chatMessage.getContent().replace("[图片]",""));
                        startActivity(intent);
                    }else if(chatMessage.getType() ==ChatMessage.TYPE_VIDEO){
                        Intent intent = new Intent(ChatActivity.this, FullVideoActivity.class);
                        intent.putExtra("videoPath",chatMessage.getContent().replace("[视频]",""));
                        startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            String imagePath =  null;
            String videoPath = null;
            if(resultCode==101){
                imagePath = data.getStringExtra("path");
            }else if(resultCode==102){
                videoPath = data.getStringExtra("path");
            }
            System.out.println("videoPath:" + videoPath);
            System.out.println("imagePath:" + imagePath);

            if (imagePath!=null&&new File(imagePath).exists()) {
                sendImage(imagePath);
            } else if (videoPath!=null&&new File(videoPath).exists()) {
                sendVideo(videoPath);
            }
    }
}
