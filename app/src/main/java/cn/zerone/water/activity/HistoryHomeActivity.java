package cn.zerone.water.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.w3c.dom.Text;

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

public class HistoryHomeActivity extends AppCompatActivity {

    private List<Map<String,String>> datalist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_home);

        getList();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String datetime = simpleDateFormat.format(date);

        TextView seleted_date = findViewById(R.id.seleted_date);
        seleted_date.setText(datetime.substring(0, 7));


        ImageView home_history_back = (ImageView) findViewById(R.id.home_history_back);
        home_history_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                    if(i == 0){
                        Map<String,String> item=new HashMap<String,String>();
                        item.put("Item", "日期");
                        datalist.add(item);
                        Map<String,String> item2=new HashMap<String,String>();
                        item2.put("Item", "完工时间");
                        datalist.add(item2);
                    }
                    else {
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
        },App.userId);
    }
}
