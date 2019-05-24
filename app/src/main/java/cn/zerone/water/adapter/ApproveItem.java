package cn.zerone.water.adapter;

public class ApproveItem {
    private String itemAbstruct;
    private String itemApplyUser;
    private String itemTime;
    private int itemIcon;

    public ApproveItem(String itemAbstruct, String itemApplyUser, String itemTime, int itemIcon) {
        this.itemAbstruct = itemAbstruct;
        this.itemApplyUser = itemApplyUser;
        this.itemTime = itemTime;
        this.itemIcon = itemIcon;
    }

    public void setItemAbstruct(String itemAbstruct) {
        this.itemAbstruct = itemAbstruct;
    }

    public void setItemApplyUser(String itemApplyUser) {
        this.itemApplyUser = itemApplyUser;
    }

    public void setItemTime(String itemTime) {
        this.itemTime = itemTime;
    }

    public void setItemIcon(int itemIcon) {
        this.itemIcon = itemIcon;
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

    public int getItemIcon() {
        return itemIcon;
    }
}
