package cn.zerone.water.model;

public class ItemArticle {
    // 新闻的 id
    private int NewsId;
    // 新闻里的图片 url
    private String imageUrl;
    //private String imageUries;

    public ItemArticle(int index, String imageUrl) {
        this.NewsId = index;
        this.imageUrl = imageUrl;
    }

    public int getNewsId() {
        return NewsId;
    }

    public void setNewsId(int newsId) {
        this.NewsId = newsId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
//    public String getImageUries() {
//        return imageUries;
//    }
//
//    public void setImageUries(String imageUries) {
//        this.imageUrl = imageUries;
//    }


}
