package cn.zerone.water.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import cn.zerone.water.R;

public class LogAdapter extends BaseAdapter {

    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;
    public LogAdapter(Context context, List<Map<String, Object>> data){
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    /**
     * 组件集合，对应list.xml中的控件
     * @author Administrator
     */
    public final class NoticeItem{

        private TextView name;
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
        NoticeItem notice = null;
        if(view ==null){
            notice = new NoticeItem();
            //获得组件，实例化组件
            view = layoutInflater.inflate(R.layout.log_item,null);
            notice.name = (TextView)view.findViewById(R.id.log_name);


        }
        else {
            notice= (NoticeItem)view.getTag();
        }
        //绑定数据
        notice.name.setText((String)data.get(position).get("PROJECT_NAME"));
        view.setTag(notice);
        return view;
    }
}
