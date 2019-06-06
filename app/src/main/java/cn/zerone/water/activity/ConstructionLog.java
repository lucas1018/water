package cn.zerone.water.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ConstructionLog extends Activity {
    private ImageView log_back = null;
    private ImageView log_go = null;
    private TextView project_name = null;
    private TextView project_station = null;
    private TextView date = null;
    private TextView save = null;
    private EditText weather = null;
    private ImageView station = null;
    private EditText isContent = null;
    private EditText isSafe = null;
    private String projectID="";
    private Boolean isProject = false;
    private Boolean isStation = false;
    String station_name ;
    String result;
    String station_id;
    String get_time;





    public  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);
        project_name = (TextView)findViewById(R.id.project_name);
        log_back = (ImageView)findViewById(R.id.log_back);
        log_go = (ImageView)findViewById(R.id.log_go);
        date = (TextView) findViewById(R.id.project_date) ;
        weather = (EditText)findViewById(R.id.log_weather);
        station = (ImageView) findViewById(R.id.station_go);
        project_station = (TextView)findViewById(R.id.project_location);
        save = (TextView)findViewById(R.id.tv_save);
        isContent =(EditText)findViewById(R.id.edit_content);
        isSafe = (EditText)findViewById(R.id.edit_safe);

        log_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // 获取项目列表
        log_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(ConstructionLog.this, LogGetProjectActivity.class), 1);

            }
        });
        //站点信息
        station.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (projectID == "")
                    Toast.makeText(ConstructionLog.this, "请先选择“所属项目”！", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent();
                    intent.putExtra("projectID", projectID);
                    startActivityForResult(intent.setClass(ConstructionLog.this, LogInGetStationActivity.class), 2);
                }

            }
        });
        // 显示日期
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String current_date = format.format(d.getTime());
        date.setText(current_date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isStation){
                    Toast.makeText(ConstructionLog.this,"“所属站点”不能为空！", Toast.LENGTH_SHORT).show();
                }
                else{
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ConstructionLog.this);
                    final View view = View.inflate(ConstructionLog.this, R.layout.meal_date, null);
                    final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
                    builder.setView(view);

                    final Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(System.currentTimeMillis());
                    datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),null);
                    if (v.getId() == R.id.project_date){

                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                StringBuffer sb = new StringBuffer();
                                sb.append(String.format("%d-%02d-%02d",
                                        datePicker.getYear(),
                                        datePicker.getMonth() + 1,
                                        datePicker.getDayOfMonth()));
                                date.setText(sb);
                                dialog.cancel();

                            }
                        })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });

                    }
                    builder.create().show();

                }

            }
        });
        get_time = date.getText().toString();


        // 确定
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isProject)
                    Toast.makeText(ConstructionLog.this,"“所属项目”不能为空！", Toast.LENGTH_SHORT).show();
                else if(!isStation)
                    Toast.makeText(ConstructionLog.this,"“所属站点”不能为空！", Toast.LENGTH_SHORT).show();
                else if(weather.getText().length()==0)
                    Toast.makeText(ConstructionLog.this,"“天气”不能为空！", Toast.LENGTH_SHORT).show();
                else if(isContent.getText().length()==0)
                    Toast.makeText(ConstructionLog.this,"“工程内容”不能为空！", Toast.LENGTH_SHORT).show();
                else if(isSafe.getText().length()==0)
                    Toast.makeText(ConstructionLog.this,"“安全情况”不能为空！", Toast.LENGTH_SHORT).show();
                else {
                    construction_save(projectID,station_id,get_time,weather.getText().toString(),isContent.getText().toString(),isSafe.getText().toString());
                }
            }
        });

    }

    private void construction_save(String projectID,String stationId,String date,String weather,String content,String safe) {
        Requests.ConstructionLog_SaveBLL(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Toast.makeText(ConstructionLog.this,"施工日志添加成功！", Toast.LENGTH_SHORT).show();
                finish();

            }
        },App.userId,projectID,stationId,date,weather,content,safe);

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == 1 && resultCode == 720) {
            result = data.getStringExtra("project");
            projectID = data.getStringExtra("projectID");
            project_name.setText(result);
            isProject = true;
        }
        if (requestCode == 2 && resultCode == 820) {
            station_name = data.getStringExtra("station");
            station_id = data.getStringExtra("stationID");
            project_station.setText(station_name);
            isStation = true;
        }

    }


}
