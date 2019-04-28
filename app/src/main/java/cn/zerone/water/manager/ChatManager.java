package cn.zerone.water.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

import cn.zerone.water.App;
import cn.zerone.water.model.ChatMessage;
import cn.zerone.water.model.ChatMessageDao;
import cn.zerone.water.model.DaoMaster;
import cn.zerone.water.model.DaoSession;
import cn.zerone.water.model.Friend;
import io.reactivex.Observer;

/**
 * Created by zero on 2018/11/29.
 */

public class ChatManager {
    String to;
    String form;
    ChatMessageDao chatMessageDao  =null;
   static ChatManager chatManager =  null;
    public ChatManager  to(String userName){
        this.to = to;
        return this;
    }
    public void init(Context context,String username){
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context.getApplicationContext(), username);
        SQLiteDatabase db = devOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
         chatMessageDao = daoSession.getChatMessageDao();
    }
    public static ChatManager getInstance(Context context,String userName){
        if(chatManager==null){
            chatManager = new ChatManager();
            chatManager.form  = userName;
            chatManager.init(context,userName);
        }
        return chatManager;
    }
    public  List<ChatMessage> getMessages(String to){
        List<ChatMessage> chatMessage = chatMessageDao.queryBuilder()
                .where(ChatMessageDao.Properties.UserId.eq(to))
                .orderAsc(ChatMessageDao.Properties.CreateDate).list();
    return chatMessage;
    }

    public void setFriendLastMessage(Friend friend) {
        try{
            ChatMessage chatMessage = chatMessageDao.queryBuilder()
                    .where(ChatMessageDao.Properties.UserId.eq(friend.getUserId()))
                    .orderDesc(ChatMessageDao.Properties.CreateDate)
                    .listLazy().get(0);
            if(chatMessage!=null){
                friend.setLastText(chatMessage.getContent());
                friend.setLastTextTime(chatMessage.getCreateDate());
            }
        }catch (Exception e){
            friend.setLastTextTime(0);
            friend.setLastText("暂无消息");
        }

    }
    List<Runnable> runnables  =new ArrayList<>();
    public boolean insert(ChatMessage chatMessage) {
        boolean isUpdate= false;
        List<ChatMessage> list = chatMessageDao.queryBuilder().where(ChatMessageDao.Properties.CreateDate.eq(chatMessage.getCreateDate()), ChatMessageDao.Properties.UserId.eq(chatMessage.getUserId())).list();
        if(list==null||list.size()==0){
            isUpdate=true;
            chatMessageDao.save(chatMessage);
        }
        for (int i = 0; i < runnables.size(); i++) {
            Runnable runnable = runnables.get(i);
            runnable.run();
        }
        return isUpdate;
    }

    public void listen(Runnable runnable) {
        runnables.add(runnable);
    }
}
