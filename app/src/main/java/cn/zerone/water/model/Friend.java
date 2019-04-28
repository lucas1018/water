package cn.zerone.water.model;

/**
 * Created by zero on 2018/11/29.
 */

public class Friend {
    String userName;
    String headImg;
    String lastText;
    long lastTextTime;
    String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Friend() {
    }

    public String getLastText() {
        return lastText;
    }

    public void setLastText(String lastText) {
        this.lastText = lastText;
    }

    public long getLastTextTime() {
        return lastTextTime;
    }

    public void setLastTextTime(long lastTextTime) {
        this.lastTextTime = lastTextTime;
    }

    public Friend(String userName, String headImg,String userId) {
        this.userName = userName;
        this.headImg = headImg;
        this.userId =  userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }
}
