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
 * Created by litinghui on 2019/5/29.
 */

public class ProblemAdapter extends BaseAdapter {

    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public ProblemAdapter(Context context,List<Map<String, Object>> data){
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }


    public final class ProblemItem{

        private TextView pro_type;
        public TextView Createtime;
        public TextView title;
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
        ProblemItem problemItem = null;
        if(view ==null){
            problemItem = new ProblemItem();
            //获得组件，实例化组件
            view = layoutInflater.inflate(R.layout.problem_item,null);
            problemItem.pro_type = (TextView)view.findViewById(R.id.item_type);
            problemItem.Createtime =(TextView)view.findViewById(R.id.recv_time);
            problemItem.title =(TextView)view.findViewById(R.id.title);

        }
        else {
            problemItem= (ProblemItem) view.getTag();
        }
        //绑定数据

        String typeName = (String) data.get(position).get("typeName");
        if (typeName != null){
            problemItem.pro_type.setText(typeName);
        }

        problemItem.Createtime.setText((String)data.get(position).get("AddTime"));
        problemItem.title.setText((String)data.get(position).get("Title"));

        view.setTag(problemItem);

        return view;
    }
}
