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

        private TextView title;
        public TextView Createtime;
        public TextView info;
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
            notice.title = (TextView)view.findViewById(R.id.item_title);
            notice.Createtime =(TextView)view.findViewById(R.id.recv_time);
            notice.info =(TextView)view.findViewById(R.id.info);

        }
        else {
            notice= (NoticeItem)view.getTag();
        }
        //绑定数据
        notice.title.setText((String)data.get(position).get("title"));
        notice.Createtime.setText((String)data.get(position).get("AddTime"));
        notice.info.setText((String)data.get(position).get("Msg"));
        view.setTag(notice);

        return view;
    }
}
