package cn.zerone.water.model;

public class ItemArticle {
    // 新闻的 id
    private int NewsId;
    // 新闻里的图片 url
    private String imageUrl;
    private String imageHref;

    public ItemArticle(int index, String imageUrl,String imageHref) {
        this.NewsId = index;
        this.imageUrl = imageUrl;
        this.imageHref = imageHref;
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
    public String getImageHref() {
        return imageHref;
    }

    public void setImageHref(String imageHref) {
        this.imageHref = imageHref;
    }
}
