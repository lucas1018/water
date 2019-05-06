package cn.zerone.water.fragment;

public class MyItem {
    private String itemLab;
    private int itemIcon;

    public MyItem(String itemLab, int itemIcon) {
        this.itemLab = itemLab;
        this.itemIcon = itemIcon;
    }

    public void setItemLab(String itemLab) {
        this.itemLab = itemLab;
    }

    public void setItemIcon(int itemIcon) {
        this.itemIcon = itemIcon;
    }

    public String getItemLab() {
        return itemLab;
    }

    public int getItemIcon() {
        return itemIcon;
    }
}
