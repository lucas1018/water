package cn.zerone.water.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;

public class ApproveItem {
    private String itemAbstruct;
    private String itemApplyUser;
    private String itemTime;
    private Bitmap itemIcon;
    private String webUrl;
    private int checkID;
    private int itemStatus;

    public ApproveItem(String itemAbstruct, String itemApplyUser, String itemTime, Bitmap itemIcon, String webUrl, int checkID, int itemStatus) {
        this.itemAbstruct = itemAbstruct;
        this.itemApplyUser = itemApplyUser;
        this.itemTime = itemTime;
        this.itemIcon = itemIcon;
        this.webUrl = webUrl;
        this.checkID = checkID;
        this.itemStatus = itemStatus;
    }

    public ApproveItem()
    {}

    public void setItemAbstruct(String itemAbstruct) {
        this.itemAbstruct = itemAbstruct;
    }

    public void setItemApplyUser(String itemApplyUser) {
        this.itemApplyUser = itemApplyUser;
    }

    public void setItemTime(String itemTime) {
        this.itemTime = itemTime;
    }

    public void setItemIcon(Bitmap itemIcon) {
        this.itemIcon = itemIcon;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public void setCheckID(int checkID) {
        this.checkID = checkID;
    }

    public String getItemAbstruct() {
        return itemAbstruct;
    }

    public String getItemApplyUser() {
        return itemApplyUser;
    }

    public String getItemTime() {
        return itemTime;
    }

    public Bitmap getItemIcon() {
        return itemIcon;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public int getCheckID() {
        return checkID;
    }

    public int getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(int itemStatus) {
        this.itemStatus = itemStatus;
    }
}
