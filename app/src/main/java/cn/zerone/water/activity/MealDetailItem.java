package cn.zerone.water.activity;

public class MealDetailItem {
    private String itemDate;
    private String itemType;
    private String itemFee;

    public MealDetailItem(String itemDate, String itemType, String itemFee) {
        this.itemDate = itemDate;
        this.itemType = itemType;
        this.itemFee = itemFee;
    }

    public void setItemDate(String itemDate) {
        this.itemDate = itemDate;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public void setItemFee(String itemFee) {
        this.itemFee = itemFee;
    }

    public String getItemDate() {
        return itemDate;
    }

    public String getItemType() {
        return itemType;
    }

    public String getItemFee() {
        return itemFee;
    }
}
