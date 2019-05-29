package cn.zerone.water.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.baidu.vi.VIContext.getContext;

public class HistoryCarClockInActivity extends AppCompatActivity {

    List<Map<String,String>> carItem;
    Map<String,String> carTypes;
    Map<String,String> stationIds;
    Map<String,String> projectIds;

    private List<HistoryCarItem> carItems;

    private ListView carList;

    Map<String, String> nowitem;
    private String[] types = {"出发打卡","到达打卡","收工打卡"};

    private String basicPicturePath = "/storage/emulated/0/JCamera/picture_";

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_in_car_list);

        ImageView listBack = findViewById(R.id.listBack);
        listBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        carTypes= new HashMap<String,String>();
        projectIds = new HashMap<String,String>();
        stationIds= new HashMap<String,String>();
        carTypes.put("0","未知车牌号");
        projectIds.put("0", "未知项目");
        stationIds.put("0", "未知站点");

        TextView title = findViewById(R.id.title);
        title.setText("车辆打卡记录");

        carList = findViewById(R.id.carClockInList);
        carItems = new ArrayList<>();

        Requests.getCarList(new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(JSONArray objects) {
                for(int i = 0; i<objects.size();i++){
                    JSONObject jsonObject = objects.getJSONObject(i);
                    String id = jsonObject.getString("ID");
                    String car = jsonObject.getString("CarNember");
                    carTypes.put(id, car);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Requests.getProjectLogList(new Observer<JSONArray>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(JSONArray objects) {
                        for(int i = 0; i<objects.size();i++){
                            JSONObject jsonObject = objects.getJSONObject(i);
                            String id = jsonObject.getString("ID");
                            String project = jsonObject.getString("PROJECT_NAME");
                            projectIds.put(id, project);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Requests.getAllStationList(new Observer<JSONArray>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(JSONArray objects) {
                                for(int i = 0; i<objects.size();i++){
                                    JSONObject jsonObject = objects.getJSONObject(i);
                                    String station = jsonObject.getString("STNM");
                                    String id = jsonObject.getString("ID");
                                    stationIds.put(id, station);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                Requests.getCarClockInList(new Observer<JSONArray>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                    }

                                    @Override
                                    public void onNext(JSONArray objects) {
                                        carItem= new ArrayList<Map<String,String>>();
                                        for(int i = 0; i<objects.size();i++){
                                            Map<String, String> item = new HashMap<String, String>();
                                            JSONObject jsonObject = objects.getJSONObject(i);
                                            String carType = jsonObject.getString("CarInfoId");
                                            String projectid = jsonObject.getString("EngineeringId");
                                            String stationid = jsonObject.getString("EngineeringStationId");
                                            String address = jsonObject.getString("Address");
                                            String picpath = jsonObject.getString("Path");
                                            String time = jsonObject.getString("AddTime");
                                            String type = jsonObject.getString("DataType");
                                            String t = time.substring(6, 19);
                                            Long timeLong = Long.parseLong(t);
                                            Calendar c = Calendar.getInstance();
                                            c.setTimeInMillis(timeLong);
                                            String tt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
                                            if(address==null)
                                                address="未能成功获取定位";

                                            item.put("Time",tt.substring(0,10));
                                            item.put("Type", types[Integer.parseInt(type)]);
                                            item.put("Address", address);
                                            item.put("Path", basicPicturePath + picpath);
                                            item.put("StationName", stationIds.get(stationid));
                                            item.put("EngineeringId", projectIds.get(projectid));
                                            item.put("CarInfoId", carTypes.get(carType));
                                            carItem.add(item);
                                        }
                                        for(int i= 0; i<objects.size();i++){
                                            HistoryCarItem item = new HistoryCarItem(carItem.get(i).get("Address"),carItem.get(i).get("Path"),carItem.get(i).get("Time"),
                                                    carItem.get(i).get("Type"),carItem.get(i).get("EngineeringId"),carItem.get(i).get("StationName"),carItem.get(i).get("CarInfoId"));
                                            carItems.add(item);
                                            System.out.println("AAAAAAAAAAAAAAAAAAAAAAA"+ carItem.get(i).get("Address"));
                                            System.out.println("AAAAAAAAAAAAAAAAAAAAAAA"+ carItem.get(i).get("Path"));
                                            System.out.println("AAAAAAAAAAAAAAAAAAAAAAA"+ carItem.get(i).get("Type"));
                                            System.out.println("AAAAAAAAAAAAAAAAAAAAAAA"+ carItem.get(i).get("Time"));
                                            System.out.println("AAAAAAAAAAAAAAAAAAAAAAA"+ carItem.get(i).get("EngineeringId"));
                                            System.out.println("AAAAAAAAAAAAAAAAAAAAAAA"+ carItem.get(i).get("StationName"));
                                            System.out.println("AAAAAAAAAAAAAAAAAAAAAAA"+ carItem.get(i).get("CarInfoId"));

                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {
                                        carList.setAdapter(new HistoryCarClockInAdapter(getApplicationContext(),R.layout.car_history_item, carItems));
//                                        carList.setAdapter(new SimpleAdapter(getApplicationContext(), carItem,
//                                                R.layout.car_history_item, new String[]{"Time","Address","Path","StationName","EngineeringId","CarInfoId","Type"},
//                                                new int[]{R.id.datetime,R.id.location,R.id.Pic,R.id.relatedStation,R.id.relatedProject,R.id.carNumber,R.id.clockInType}));
                                    }
                                },App.userId);
                            }
                        },App.userId);
                    }
                },App.userId);
            }
        },App.userId);





//        Requests.getCarClockInList(new Observer<JSONArray>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//            }
//
//            @Override
//            public void onNext(JSONArray objects) {
//                carItem= new ArrayList<Map<String,String>>();
//                carTypes= new ArrayList<String>();
//                stationIds=new ArrayList<String>();
//                projectIds=new ArrayList<String>();
//                size=objects.size();
//                for(int i = 0; i<objects.size();i++){
//                    Map<String, String> item = new HashMap<String, String>();
//                    JSONObject jsonObject = objects.getJSONObject(i);
//                    String carType = jsonObject.getString("CarInfoId");
//                    String projectid = jsonObject.getString("EngineeringId");
//                    String stationid = jsonObject.getString("EngineeringStationId");
//                    String address = jsonObject.getString("Address");
//                    String picpath = jsonObject.getString("Path");
//                    String time = jsonObject.getString("AddTime");
//                    String t = time.substring(6, 19);
//                    Long timeLong = Long.parseLong(t);
//                    Calendar c = Calendar.getInstance();
//                    c.setTimeInMillis(timeLong);
//                    String tt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
//                    item.put("Time",tt);
//                    item.put("Address", address);
//                    item.put("path", picpath);
//                    carTypes.add(carType);
//                    stationIds.add(stationid);
//                    projectIds.add(projectid);
//                    carItem.add(item);
//                }
//                for(int i = 0; i<objects.size();i++){
//                    nowitem = carItem.get(i);
//                    Requests.getProjectLogByField(new Observer<JSONObject>() {
//                        @Override
//                        public void onSubscribe(Disposable d) {
//                        }
//
//                        @Override
//                        public void onNext(JSONObject object) {
//                            String projectname = object.getString("PROJECT_NAME");
//                            nowitem.put("PROJECT_NAME", projectname);
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//
//                        }
//
//                        @Override
//                        public void onComplete() {
//
//                        }
//                    },projectIds.get(i));
//                }
//                for(int i = 0; i<objects.size();i++) {
//                    nowitem = carItem.get(i);
//                    Requests.getStationByField(new Observer<JSONObject>() {
//                        @Override
//                        public void onSubscribe(Disposable d) {
//                        }
//
//                        @Override
//                        public void onNext(JSONObject object) {
//                            String stationname = object.getString("STNM");
//                            nowitem.put("STNM", stationname);
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//
//                        }
//
//                        @Override
//                        public void onComplete() {
//
//                        }
//                    }, projectIds.get(i));
//                }
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        },App.userId);
    }
}
