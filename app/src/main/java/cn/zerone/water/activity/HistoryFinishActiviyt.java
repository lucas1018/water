package cn.zerone.water.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.zerone.water.App;
import cn.zerone.water.R;
import cn.zerone.water.http.Requests;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class HistoryFinishActiviyt extends AppCompatActivity implements DatePicker.OnDateChangedListener{

    private List<Map<String,String>> datalist;
    private List<Map<String,String>> title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_finish);

        setTitle();
        getList();

        DatePicker datePicker = (DatePicker)findViewById(R.id.dpPicker);
        Calendar calendar = Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int monthOfYear=calendar.get(Calendar.MONTH);
        int dayOfMonth=calendar.get(Calendar.DAY_OF_MONTH);
        datePicker.init(year,monthOfYear,dayOfMonth,HistoryFinishActiviyt.this);
        datePicker.bringToFront();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String datetime = simpleDateFormat.format(date);

        TextView seleted_date = findViewById(R.id.seleted_date);
        seleted_date.setText(datetime.substring(0, 7));


        Button select_date = findViewById(R.id.select_date);
        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker datePicker = (DatePicker)findViewById(R.id.dpPicker);
                Calendar calendar = Calendar.getInstance();
                int year=calendar.get(Calendar.YEAR);
                int monthOfYear=calendar.get(Calendar.MONTH);
                int dayOfMonth=calendar.get(Calendar.DAY_OF_MONTH);
                datePicker.init(year,monthOfYear,dayOfMonth,HistoryFinishActiviyt.this);
                datePicker.bringToFront();
            }
        });

        ImageView home_history_back = (ImageView) findViewById(R.id.finish_history_back);
        home_history_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onDateChanged(DatePicker view, int year,int month,int day){
        Toast.makeText(HistoryFinishActiviyt.this,"您选择的日期是："+year+"年"+(month+1)+"月"+day+"日!",Toast
                .LENGTH_SHORT).show();
    }

    private void setTitle(){
        GridView history_title=(GridView) findViewById(R.id.history_title);
        title = new ArrayList<Map<String,String>>();
        Map<String,String> item=new HashMap<String,String>();
        item.put("Item", "日期");
        title.add(item);
        Map<String,String> item2=new HashMap<String,String>();
        item2.put("Item", "到家时间");
        title.add(item2);
        history_title.setAdapter(new SimpleAdapter(getApplicationContext(), title,
                R.layout.clock_in_grid, new String[]{"Item"}, new int[]{R.id.ItemText}));
    }

    private void getList(){
        Requests.getClockInList(new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(JSONArray objects) {

                GridView history_list=(GridView) findViewById(R.id.history_list);
                datalist = new ArrayList<Map<String,String>>();
                for(int i = 0; i<objects.size();i++){
                    Map<String, String> item = new HashMap<String, String>();
                    Map<String,String> item2=new HashMap<String,String>();
                    JSONObject jsonObject = objects.getJSONObject(i);
                    String time = jsonObject.getString("AddTime");
                    String t = time.substring(6, 19);
                    Long timeLong = Long.parseLong(t);
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(timeLong);
                    String tt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime());
                    item.put("Item", tt.substring(0, 10));
                    datalist.add(item);
                    item2.put("Item", tt.substring(11, 19));
                    datalist.add(item2);
                }
                history_list.setAdapter(new SimpleAdapter(getApplicationContext(), datalist,
                        R.layout.clock_in_grid, new String[]{"Item"}, new int[]{R.id.ItemText}));
            }
            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        },App.userId, "2");
    }
}
