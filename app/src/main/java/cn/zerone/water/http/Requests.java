package cn.zerone.water.http;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.FormBody;
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

    public static <T> void login(Observer<JSONObject> observer, String username, String password,String device_tokens) {
        RequestBody requestBody = new FormBody.Builder()
                .add("LOGIN_NAME", username)
                .add("PASSWORD", password)
                .add("device_tokens",device_tokens)
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

//    调用用户信息
//    public static void USER_INFO_GetModel(Observer<JSONObject> observer, String userId) {
//        RequestBody requestBody = new FormBody.Builder()
//                .add("ID", userId)
//                .build();
//        baseJSONObject(observer, "USER_INFO_GetModelBLL", requestBody);
//    }

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
        baseJSONObject(observer, "USER_INFO_GetModelBLL", requestBody);
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

    public static void getClockInList(Observer<JSONArray> observer, String id, String DataType) {
        RequestBody requestBody = new FormBody.Builder()
                .add("ID", id)
                .add("field", "DataType")
                .add("value", DataType).build();
        baseJSONArray(observer, "ClockIn_GetListByField", requestBody);
    }

    // 获取车辆信息
    public static void getCarList(Observer<JSONArray> observer, String id) {
        RequestBody requestBody = new FormBody.Builder()
                .add("ID", id).build();
        baseJSONArray(observer, "CarInfo_GetList", requestBody);
    }

    // 获取项目
    public static void getProjectLogList(Observer<JSONArray> observer, String id) {
        RequestBody requestBody = new FormBody.Builder()
                .add("ID", id).build();
        baseJSONArray(observer, "PROJECT_INFO_GetList", requestBody);
    }

    public static void getProjectLogByField(Observer<JSONObject> observer, String projectId) {
        RequestBody requestBody = new FormBody.Builder()
                .add("field", "ID")
                .add("value", projectId).build();
        baseJSONObject(observer, "PROJECT_INFO_GetListByField", requestBody);
    }

    public static void getStationByField(Observer<JSONObject> observer, String stationId) {
        RequestBody requestBody = new FormBody.Builder()
                .add("field", "ID")
                .add("value", stationId).build();
        baseJSONObject(observer, "STATION_INFO_GetListByField", requestBody);
    }

    // 获取相关项目的站点信息
    public static void getStationList(Observer<JSONArray> observer, String id, String val) {
        RequestBody requestBody = new FormBody.Builder()
                .add("ID", id)
                .add("field", "PROJECT_ID")
                .add("value", val).build();
        baseJSONArray(observer, "STATION_INFO_GetListByField", requestBody);
    }

    public static void getAllStationList(Observer<JSONArray> observer, String id) {
        RequestBody requestBody = new FormBody.Builder()
                .add("ID", id).build();
        baseJSONArray(observer, "STATION_INFO_GetList", requestBody);
    }

    //调用消息列表接口
    public static void UserMessage_GetListByField(Observer<JSONArray> observer,String userid) {
        RequestBody requestBody = new FormBody.Builder()
                .add("field","UserId")
                .add("value",userid).build();
        baseJSONArray(observer, "UserMessage_GetListByField", requestBody);
    }

    //调用轮播图片接口
    public static void AdInfo_GetList(Observer<JSONArray> observer) {

        RequestBody requestBody = new FormBody.Builder().build();
        baseJSONArray(observer, "AdInfo_GetList", requestBody);
    }

    public static void GetCheckInfo(Observer<JSONObject> observer, String CheckUserId, int  state,int PageIndex, int PageSize) {
        RequestBody requestBody = new FormBody.Builder()
                .add("CheckUserId", CheckUserId)
                .add("State", String.valueOf(state))
                .add("PageIndex", String.valueOf(PageIndex))
                .add("PageSize", String.valueOf(PageSize))
                .build();
        baseJSONObject(observer, "GeneralCheck_GetPageInfo", requestBody);
    }
    // 施工日志
    public static void ConstructionLog_SaveBLL(Observer<String> observer, String User_id, String project_id, String station_id, String time, String weather, String content, String safety) {
        RequestBody requestBody = new FormBody.Builder()
                .add("UserId", User_id)
                .add("EngineeringId", project_id)
                .add("EngineeringStationId", station_id)
                .add("Date", time)
                .add("Weather", weather)
                .add("EngineeringContent", content)
                .add("SafetySituation", safety)
                .build();
        baseString(observer, "ConstructionLog_SaveBLL", requestBody);
    }

    //获取所有问题类型
    public static void getProblemTypeList(Observer<JSONArray> observer, String id) {
        RequestBody requestBody = new FormBody.Builder()
                .add("ID", id)
                .add("field","Type")
                .add("value","6").build();
        baseJSONArray(observer, "TreeInfo_GetListByField", requestBody);
    }

    //上传保存问题
    public static void NewQuestion_SaveBLL(Observer<JSONObject> observer, String User_id, String Title, String Remark, String TreeInfoId) {
        RequestBody requestBody = new FormBody.Builder()
                .add("UserId", User_id)
                .add("Title", Title)
                .add("Remark", Remark)
                .add("TreeInfoId", TreeInfoId)
                .build();
        baseJSONObject(observer, "NewQuestion_SaveBLL", requestBody);
    }

    //上传问题的图片视频或者文档
    public static void NewFile_SaveBLL(Observer<JSONObject> observer, String User_id, String FileType, String Path, String FileName,String NewQuestionId) {
        RequestBody requestBody = new FormBody.Builder()
                .add("UserId", User_id)
                .add("FileType", FileType)
                .add("Path", Path)
                .add("FileName", FileName)
                .add("NewQuestionId", NewQuestionId)
                .build();
        baseJSONObject(observer, "NewFile_SaveBLL", requestBody);
    }

    //根据问题类型ID获取问题类型名称
    public static JSONObject getProTypeNameById(String ID){
        RequestBody requestBody = new FormBody.Builder()
                .add("ID",ID)
                .build();
        JSONObject jsonObject = baseJSONObject("TreeInfo_GetModel", requestBody);
        return jsonObject;
    }

    //获取所有问题信息的接口
    public static JSONArray getProblems(String User_id){
        RequestBody requestBody = new FormBody.Builder()
                .add("UserId",User_id)
                .build();
        JSONArray objects = baseJSONArray("NewQuestion_GetList", requestBody);
        return objects;

    }

    //根据问题获取附件信息
    public static JSONArray getAttachmentsByProId(String ProId){
        RequestBody requestBody = new FormBody.Builder()
                .add("field","NewQuestionId")
                .add("value",ProId)
                .build();
        JSONArray objects = baseJSONArray("NewFile_GetListByField", requestBody);
        return objects;
    }


    //调用任务列表接口
    public static void TaskInfo_GetList(Observer<JSONArray> observer) {

        RequestBody requestBody = new FormBody.Builder().build();
        baseJSONArray(observer, "TaskInfo_GetList", requestBody);
    }


    public static void CarClockIn_SaveBLL(Observer<String> observer, String id, String add_time, String latitude, String longitude,
                                          String data_type, String pic, String address,String EnID, String StID, String CarID) {
        RequestBody requestBody = new FormBody.Builder()
                .add("UserId", id)
                .add("AddTime", add_time)
                .add("Lat", latitude)
                .add("Lng", longitude)
                .add("DataType", data_type)
                .add("Path", pic)
                .add("Address", address)
                .add("EngineeringId", EnID)
                .add("EngineeringStationId", StID)
                .add("CarInfoId", CarID)
                .build();
        baseString(observer, "CarGpsPhoto_SaveBLL", requestBody);
    }

    public static void Picture_SaveBLL(Observer<JSONObject> observer, String strbase64, String suffix) {
        RequestBody requestBody = new FormBody.Builder()
                .add("strbase64", strbase64)
                .add("suffix", suffix)
                .build();
        baseJSONObject(observer, "SavePic", requestBody);
    }

    public static void getCarClockInList(Observer<JSONArray> observer, String id) {
        RequestBody requestBody = new FormBody.Builder()
                .add("field", "UserId")
                .add("value", id).build();
        baseJSONArray(observer, "CarGpsPhoto_GetListByField", requestBody);
    }


    //审批提交
    public static void Submit_GeneralCheck(Observer<JSONObject> observer, int ID, String Remark, int State){
        RequestBody requestBody = new FormBody.Builder()
                .add("ID", String.valueOf(ID))
                .add("Remark", Remark)
                .add("State", String.valueOf(State))
                .build();
        baseJSONObject(observer, "GeneralCheck", requestBody);


    }
    
    //工作餐详情
    public static void FeesForMeals_GetPageInfo(Observer<JSONObject> observer, String id, String start, String end) {
        RequestBody requestBody = new  FormBody.Builder()
                .add("UserId", id)
                .add("BeginTime", start)
                .add("EndTime", end)
                .add("PageSize", "1000")
                .build();
        baseJSONObject(observer,"FeesForMeals_GetPageInfo",requestBody);
    }

//    //获取任务列表
//    public static JSONArray EngineeringStation_GetList(String User_id){
//        RequestBody requestBody = new FormBody.Builder()
//                .add("field","UserId")
//                .add("value",User_id)
//                .build();
//        JSONArray jsons = baseJSONArray("EngineeringStation_GetListByField", requestBody);
//        return jsons;
//    }
}
