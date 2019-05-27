package cn.zerone.water.http;

import android.util.Base64;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.io.FileUtils;
import org.ksoap2.serialization.SoapObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.sql.SQLOutput;
import java.util.List;

import cn.zerone.water.model.EngineeringStation;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

import static cn.zerone.water.utils.HttpUtil.baseJSONArray;
import static cn.zerone.water.utils.HttpUtil.baseJSONObject;
import static cn.zerone.water.utils.HttpUtil.baseString;

/**
 * Created by qhk on 2019/5/12.
 *
 * 调用接口可按下列形式书写：
 * public static <T> void function(Observer<JSONObject> observer, Type param, ...){
 *     RequestBody requestBody = new FormBody.Builder()
 *             .add("param", param) //传入参数，无参可省略这一步
 *             .build();
 *     baseJSONObject(observer, "func", requestBody);
 * }
 *
 * 备注：
 * 1.参数的选择参考相应接口文档
 * 2.此处的 func 才是真正调用的远程接口名
 * 3.根据返回类型可选择baseJSONObject、baseJSONArray、baseString其中之一
 */

public class Requests {

    public static <T> void login(Observer<JSONObject> observer, String username, String password) {
        RequestBody requestBody = new FormBody.Builder()
                .add("LOGIN_NAME", username)
                .add("PASSWORD", password)
                .build();
        baseJSONObject(observer, "Loging", requestBody);
    }

//    public static <T>  void scan(Observer<JSONObject> observer,String userId,String barcode){
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("userId", userId);
//        jsonObject.put("State","3");
//        jsonObject.put("BarCode",barcode);
//        baseJSONObject(observer,"scancode",jsonObject);
//    }
//    public  static<T>   void getUserList(Observer<JSONArray> observer){
//        baseJSONArray(observer,"getuserlist",new JSONObject());
//    }

//    public static void getSystemMessages(final Observer<JSONObject> observer,String userId,String createtime,String title,String details,String datatype) {
//        RequestBody requestBody = new FormBody.Builder()
//                .add("userId", userId)
//                .add("CrateTime",createtime)
//                .add("Title",title)
//                .add("Content",details)
//                .add("DataType",datatype)
//                .build();
//        baseJSONObject(observer,"getmsg", requestBody);
//    }

    public static void signIn(final Observer<String> observer, String userId, double longitude, double latitude) {
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", userId)
                .add("x", String.valueOf(latitude))
                .add("y", String.valueOf(longitude))
                .build();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("userId", userId);
//        jsonObject.put("x",latitude);
//        jsonObject.put("y",longitude);
        baseString(observer, "sign_in", requestBody);
    }
//    public static void upload(Observer<JSONObject> observer, String userId, File file){
//       final JSONObject json = new JSONObject();
//        json.put("userId", userId);
//        try {
//            json.put("MediaType",file.getName().substring(file.getName().indexOf(".")+1,file.getName().length()));
//            byte[] bytes = FileUtils.readFileToByteArray(file);
//            json.put("Base64Str",Base64.encodeToString(bytes,Base64.NO_WRAP));
//            baseJSONObject(observer,"uploadmedia", json);
//        } catch (Exception e) {
//            e.printStackTrace();
//            observer.onError(new IOException());
//        }
//        System.out.println("uploadmedia:"+json);
//    }

    public static void signOut(final Observer<String> observer, String userId, double longitude, double latitude) {
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", userId)
                .add("x", String.valueOf(latitude))
                .add("y", String.valueOf(longitude))
                .build();
        baseString(observer, "sign_out", requestBody);
    }

//    public static void sendChatinfo(final Observer<String> observer, String userId, String toUsername, String text, EngineeringStation engineeringStation) {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("userId", userId);
//        jsonObject.put("ToUserId",toUsername);
//        jsonObject.put("Msg",text);
//        try{
//            jsonObject.put("TaskInfoId",engineeringStation.getTaskId());
//            jsonObject.put("StepId",engineeringStation.getStepId());
//            jsonObject.put("EngineeringStationId",engineeringStation.getEngineeringStationId());
//        }catch (Exception e){
//            e.printStackTrace();
//            jsonObject.put("TaskInfoId","0");
//            jsonObject.put("StepId","0");
//            jsonObject.put("EngineeringStationId","0");
//        }
//        baseString(observer,"sendchatinfo",jsonObject);
//    }

//    public static void getchatinfo(final Observer<JSONArray> observer,String userId) {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("userId", userId);
//        baseJSONArray(observer,"getchatinfo",jsonObject);
//    }

    public static void uploadGps(String userId, double y, double x) {
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", userId)
                .add("X", String.valueOf(x))
                .add("Y", String.valueOf(y))
                .build();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("userId", userId);
//        jsonObject.put("X", x);
//        jsonObject.put("Y", y);
        baseString(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String jsonObject) {
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("uploadgps:onError");
            }

            @Override
            public void onComplete() {
                System.out.println("uploadgps:onComplete");

            }
        }, "uploadgps", requestBody);
    }

//    public static void updateuserinfo(Observer<String> observer, String userId, String headImg) {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("userId", userId);
//        jsonObject.put("NickName", "");
//        jsonObject.put("Password", "");
//        jsonObject.put("HeadImg", headImg);
//        baseString(observer, "updateuserinfo", jsonObject);
//    }
//
//    public static void uploadjobstepinfo(Observer<String> observer, String userId,String jobId,String stepId,List<String> imageList,List<String> videoList,String text) {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("userId", userId);
//        jsonObject.put("JobId", jobId);
//        jsonObject.put("StepId",stepId);
//        jsonObject.put("Text",text);
//        jsonObject.put("ImageList", imageList);
//        jsonObject.put("videoList", videoList);
//        baseString(observer, "uploadjobstepinfo", jsonObject);
//    }

    //调用用户信息
    public static void USER_INFO_GetModel(Observer<JSONObject> observer, String userId) {
        RequestBody requestBody = new FormBody.Builder()
                .add("ID", userId)
                .build();
        baseJSONObject(observer, "USER_INFO_GetModelBLL", requestBody);
    }

    //修改密码
    public static void updatePWD(Observer<JSONObject> observer, String userID, String pwd1, String pwd2, String pwd) {
        RequestBody requestBody = new FormBody.Builder()
                .add("ID", userID)
                .add("PASSWORD1", pwd1)
                .add("PASSWORD2", pwd2)
                .add("PASSWORD", pwd)
                .build();
        baseJSONObject(observer, "UpdataPwd", requestBody);
    }

    public static void getCheckedList(Observer<JSONArray> observer) {
        RequestBody requestBody = new FormBody.Builder().build();
        baseJSONArray(observer, "EngineeringFileCheck_GetList", requestBody);
    }

    //添加工作餐记录
    public static void feesForMeals_SaveBLL(Observer<String> observer, String id, String username, String meal_date, String meal_type, String meal_mount, String meal_resturant, String meal_remark) {
        RequestBody requestBody = new FormBody.Builder()
                .add("CreateUserId", id)//录入人
                .add("Name", username)//姓名
                .add("Date", meal_date)//餐费日期
                .add("DataType", meal_type)//类型 工作餐=0,商务餐=1,其他=2
                .add("Cost", meal_mount)//餐费
                .add("Restaurant", meal_resturant)//餐馆
                .add("Remark", meal_remark)//备注
                .build();
        baseString(observer, "feesForMeals_SaveBLL", requestBody);
    }

    //添加工作日志
    public static void JobLog_SaveBLL(Observer<String> observer, String id, String username, String date, int  type, String job_content, String overtime_content, String overtime,String tomorrow,String remark) {
        RequestBody requestBody = new FormBody.Builder()
                .add("CreateUserId", id)//录入人
                .add("Name", username)//姓名
                .add("AddTime", date)//日期
                .add("JobType", String.valueOf(type))//类型 日报=0,周报=1,月报=2,季报=3,年报=4
                .add("JobContent", job_content)//日志内容
                .add("OvertimeContent", overtime_content)//加班内容
                .add("Overtime", overtime)//加班时间
                .add("Tomorrow", tomorrow)//明日安排
                .add("Remark", remark)//备注
                .build();
        baseString(observer, "JobLog_SaveBLL", requestBody);
    }

    //获取工作日志
    public static void JobLog_GetModelBLL(Observer<String> observer, String id, String username) {
        RequestBody requestBody = new FormBody.Builder()
                .add("CreateUserId", id)//录入人
                .add("Name", username)//姓名

                .build();
        baseString(observer, "JobLog_SaveBLL", requestBody);
    }
    //修改手机号
    public static void UpdataPHONE(Observer<JSONObject> observer, String phone, String id, String code) {
        RequestBody requestBody = new FormBody.Builder()
                .add("PHONE", phone)
                .add("ID", id)
                .add("Code", code)
                .build();
        baseJSONObject(observer, "UpdataPHONE", requestBody);
    }

    //调取用户信息
    public static void USER_INFO_GetModelBLL(Observer<JSONObject> observer, String id) {
        RequestBody requestBody = new FormBody.Builder()
                .add("ID", id).build();
        baseJSONObject(observer, "USER_INFO_GetModel", requestBody);
    }

    public static void ClockIn_SaveBLL(Observer<String> observer, String id, String add_time, String latitude, String longitude, String data_type, String pic, String address) {
        RequestBody requestBody = new FormBody.Builder()
                .add("UserId", id)
                .add("AddTime", add_time)
                .add("Lat", latitude)
                .add("Lng", longitude)
                .add("DataType", data_type)
                .add("Path", pic)
                .add("Address", address)
                .build();
        baseString(observer, "ClockIn_SaveBLL", requestBody);
    }

    //调用消息列表接口
    public static void UserMessage_GetList(Observer<JSONArray> observer) {

        RequestBody requestBody = new FormBody.Builder().build();
        baseJSONArray(observer, "UserMessage_GetList", requestBody);
    }
}
