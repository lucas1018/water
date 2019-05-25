package cn.zerone.water.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import cn.zerone.water.R;

public class ListViewAdapter extends BaseAdapter {

    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;
    public ListViewAdapter(Context context,List<Map<String, Object>> data){
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    /**
     * 组件集合，对应list.xml中的控件
     * @author Administrator
     */
    public final class NoticeItem{

        private TextView title_type;
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
        NoticeItem notice = null;
        if(view ==null){
            notice = new NoticeItem();
            //获得组件，实例化组件
            view = layoutInflater.inflate(R.layout.notice_item,null);
            notice.title_type = (TextView)view.findViewById(R.id.item_type);
            notice.Createtime =(TextView)view.findViewById(R.id.recv_time);
            notice.title =(TextView)view.findViewById(R.id.title);

        }
        else {
            notice= (NoticeItem)view.getTag();
        }
        //绑定数据
        switch ((String)data.get(position).get("DataType")){
            case "0":
                notice.title_type.setText("普通通知");
                break;
            case "1":
                notice.title_type.setText("巡检任务");
                break;
            case "2":
                notice.title_type.setText("运维任务");
                break;
            case "3":
                notice.title_type.setText("审核审批");
                break;
            case "4":
                notice.title_type.setText("安全检查");
                break;
            case "5":
                notice.title_type.setText("在线直播");
                break;
            case "6":
                notice.title_type.setText("工单任务");
                break;
            case "7":
                notice.title_type.setText("站点建设");
                break;
        }


        //notice.title_type.setText();
        notice.Createtime.setText((String)data.get(position).get("AddTime"));
        notice.title.setText((String)data.get(position).get("title"));
        view.setTag(notice);

        return view;
    }
}
