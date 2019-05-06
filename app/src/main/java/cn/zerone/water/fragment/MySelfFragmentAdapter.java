package cn.zerone.water.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.zerone.water.R;

public class MySelfFragmentAdapter extends ArrayAdapter<MyItem>{

    private int resourceId;

    public MySelfFragmentAdapter(@NonNull Context context, int resource, @NonNull List<MyItem> objects) {
        super(context, resource, objects);
        this.resourceId=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MyItem myItem=getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView==null){
            view=LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.itemIcon=view.findViewById(R.id.myitem_icon);
            viewHolder.itemLab=view.findViewById(R.id.myitem_lab);
            viewHolder.itemGo=view.findViewById(R.id.myitem_go);
            view.setTag(viewHolder);

        }else {
            view=convertView;
            viewHolder= (ViewHolder) view.getTag();
        }
        viewHolder.itemIcon.setImageResource(myItem.getItemIcon());
        viewHolder.itemLab.setText(myItem.getItemLab());
        return view;
    }
    class ViewHolder{

        ImageView itemIcon;
        TextView itemLab;
        ImageView itemGo;

    }
}
