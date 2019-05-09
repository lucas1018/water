package cn.zerone.water.http;

import android.util.Base64;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.zerone.water.model.EngineeringStation;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static cn.zerone.water.http.Https.baseJSONArray;
import static cn.zerone.water.http.Https.baseJSONObject;
import static cn.zerone.water.http.Https.baseString;

/**
 * Created by zero on 2018/11/29.
 */

public class Requests {
    public static <T>  void login(Observer<JSONObject> observer,String username,String password){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("UserName",username);
        jsonObject.put("Password",password);
        jsonObject.put("Token","");
        baseJSONObject(observer,"login",jsonObject);
    }
    public static <T>  void login(Observer<JSONObject> observer,String token){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("UserName","");
        jsonObject.put("Password","");
        jsonObject.put("Token",token);
        baseJSONObject(observer,"login",jsonObject);
    }
    public static <T>  void scan(Observer<JSONObject> observer,String token,String barcode){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Token", token);
        jsonObject.put("State","3");
        jsonObject.put("BarCode",barcode);
        baseJSONObject(observer,"scancode",jsonObject);
    }
    public  static<T>   void getUserList(Observer<JSONArray> observer){
        baseJSONArray(observer,"getuserlist",new JSONObject());
    }


    public static void getSystemMessages(final Observer<JSONObject> observer,String token) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Token", token);
        baseJSONObject(observer,"getmsg",jsonObject);
    }

    public static void signIn(final Observer<String> observer,String token, double  longitude, double latitude) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Token", token);
        jsonObject.put("x",latitude);
        jsonObject.put("y",longitude);
        baseString(observer,"sign_in",jsonObject);
    }
    public static void upload(Observer<JSONObject> observer, String token, File file){
       final JSONObject json = new JSONObject();
        json.put("Token", token);
        try {
            json.put("MediaType",file.getName().substring(file.getName().indexOf(".")+1,file.getName().length()));
            byte[] bytes = FileUtils.readFileToByteArray(file);
            json.put("Base64Str",Base64.encodeToString(bytes,Base64.NO_WRAP));
            baseJSONObject(observer,"uploadmedia", json);
        } catch (Exception e) {
            e.printStackTrace();
            observer.onError(new IOException());
        }
        System.out.println("uploadmedia:"+json);
    }
    
    public static void signOut(final Observer<String> observer,String token, double  longitude, double latitude) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Token", token);
        jsonObject.put("x",latitude);
        jsonObject.put("y",longitude);
        baseString(observer,"sign_out",jsonObject);
    }

    public static void sendChatinfo(final Observer<String> observer, String token, String toUsername, String text, EngineeringStation engineeringStation) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Token", token);
        jsonObject.put("ToUserId",toUsername);
        jsonObject.put("Msg",text);
        try{
            jsonObject.put("TaskInfoId",engineeringStation.getTaskId());
            jsonObject.put("StepId",engineeringStation.getStepId());
            jsonObject.put("EngineeringStationId",engineeringStation.getEngineeringStationId());
        }catch (Exception e){
            e.printStackTrace();
            jsonObject.put("TaskInfoId","0");
            jsonObject.put("StepId","0");
            jsonObject.put("EngineeringStationId","0");
        }
        baseString(observer,"sendchatinfo",jsonObject);
    }
    public static void getchatinfo(final Observer<JSONArray> observer,String token) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Token", token);
        baseJSONArray(observer,"getchatinfo",jsonObject);
    }

    public static void uploadGps(String token,double y,double x) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Token", token);
        jsonObject.put("X", x);
        jsonObject.put("Y", y);
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
        }, "uploadgps", jsonObject);
    }

    public static void updateuserinfo(Observer<String> observer, String token, String headImg) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Token", token);
        jsonObject.put("NickName", "");
        jsonObject.put("Password", "");
        jsonObject.put("HeadImg", headImg);
        baseString(observer, "updateuserinfo", jsonObject);
    }

    public static void uploadjobstepinfo(Observer<String> observer, String token,String jobId,String stepId,List<String> imageList,List<String> videoList,String text) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Token", token);
        jsonObject.put("JobId", jobId);
        jsonObject.put("StepId",stepId);
        jsonObject.put("Text",text);
        jsonObject.put("ImageList", imageList);
        jsonObject.put("videoList", videoList);
        baseString(observer, "uploadjobstepinfo", jsonObject);
    }

    public static void getUserInfo(Observer<JSONObject> observer, Integer userId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", userId);
        baseJSONObject(observer,"getUserInfo",jsonObject);
    }

    public static void updatePWD(Observer<String> observer, Integer userID,String pwd1, String pwd2, String pwd) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ID",userID);
        jsonObject.put("PASSWORD1",pwd1);//新密码
        jsonObject.put("PASSWORD2",pwd2);//确认新密码
        jsonObject.put("PASSWORD",pwd);//旧密码
        baseString(observer,"UpdataPwd",jsonObject);
    }

    public  static void getCheckedList(Observer<JSONArray> observer){
        baseJSONArray(observer,"EngineeringFileCheck_GetList",new JSONObject());
    }

}
