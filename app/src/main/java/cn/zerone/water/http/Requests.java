package cn.zerone.water.http;

import android.util.Base64;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.io.FileUtils;
import org.ksoap2.serialization.SoapObject;

import java.io.File;
import java.io.IOException;
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
 * Created by zero on 2018/11/29.
 */

public class Requests {

    private static final String NAMESPACE = "http://tempuri.org/";

    public static <T>  void login(Observer<JSONObject> observer,String username,String password){
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

    public static void signIn(final Observer<String> observer,String userId, double  longitude, double latitude) {
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", userId)
                .add("x", String.valueOf(latitude))
                .add("y", String.valueOf(longitude))
                .build();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("userId", userId);
//        jsonObject.put("x",latitude);
//        jsonObject.put("y",longitude);
        baseString(observer,"sign_in", requestBody);
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
    
    public static void signOut(final Observer<String> observer,String userId, double  longitude, double latitude) {
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", userId)
                .add("x", String.valueOf(latitude))
                .add("y", String.valueOf(longitude))
                .build();
        baseString(observer,"sign_out", requestBody);
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

    public static void uploadGps(String userId,double y,double x) {
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", userId)
                .add("X", String.valueOf(x))
                .add("Y",String.valueOf(y))
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

    public static void getUserInfo(Observer<JSONObject> observer, String userId) {
            SoapObject request = new SoapObject(NAMESPACE, "getUserInfo");
            request.addProperty("userId", userId); //传入参数
            baseJSONObject(observer, request);
    }

    public static void updatePWD(Observer<JSONObject> observer, String userID,String pwd1, String pwd2, String pwd) {
        RequestBody requestBody = new FormBody.Builder()
                .add("ID", userID)
                .add("PASSWORD1", pwd1)
                .add("PASSWORD2", pwd2)
                .add("PASSWORD", pwd)
                .build();
        baseJSONObject(observer,"UpdataPwd", requestBody);
    }

    public  static void getCheckedList(Observer<JSONArray> observer) {
        RequestBody requestBody = new FormBody.Builder().build();
        baseJSONArray(observer, "EngineeringFileCheck_GetList", requestBody);
    }

    public static void feesForMeals_SaveBLL(Observer<String> observer, String id, String meal_date, String meal_type, String meal_mount, String meal_remark) {
        SoapObject request = new SoapObject(NAMESPACE, "FeesForMeals_SaveBLL");
        request.addProperty("CreateUserId", id);
        request.addProperty("Date", meal_date);
        request.addProperty("Name", meal_type);
        request.addProperty("Cost", meal_mount);
        request.addProperty("Remark", meal_remark);
        baseString(observer, request);
    }
    public static void  Notice_GetList(final Observer<JSONObject>observer){
        SoapObject request = new SoapObject(NAMESPACE, "Notice_GetList");
        baseJSONObject(observer, request);
    }
}
