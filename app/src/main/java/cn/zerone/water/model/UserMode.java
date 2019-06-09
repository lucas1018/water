package cn.zerone.water.model;

/**
 * Created by Administrator on 2019/6/7.
 */

public class UserMode {
    //{"id":3,"name":"管理员","photo":"/Content/img/User/1613505035583.jpg"}
   private  int id;
private  String name;
private String photo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
