package cn.zerone.water.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import cn.zerone.water.R;

/**
 * Created by litinghui on 2019/5/30.
 */

public class TaskAdapter extends BaseAdapter {

    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public TaskAdapter(Context context,List<Map<String, Object>> data){
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public final class TaskItem{

        private TextView stationName;
        public TextView address;
        public TextView state;
        public TextView beginDate;
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        TaskItem taskItem = null;
        if(view ==null){
            taskItem = new TaskItem();
            //获得组件，实例化组件
            view = layoutInflater.inflate(R.layout.task_item,null);
            taskItem.stationName = (TextView)view.findViewById(R.id.station_name);
            taskItem.beginDate =(TextView)view.findViewById(R.id.begin_time);
            taskItem.state =(TextView)view.findViewById(R.id.state);

        }
        else {
            taskItem= (TaskItem) view.getTag();
        }
        //绑定数据

        String stationName = (String) data.get(position).get("stationName");
        if (!stationName.isEmpty()){
            taskItem.stationName.setText(stationName);
        }

        taskItem.beginDate.setText((String)data.get(position).get("beginDate"));
        taskItem.state.setText((String)data.get(position).get("state"));

        view.setTag(taskItem);

        return view;
    }
}
