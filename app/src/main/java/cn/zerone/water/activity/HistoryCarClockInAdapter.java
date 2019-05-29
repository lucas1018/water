package cn.zerone.water.activity;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;
import java.util.Map;

import cn.zerone.water.R;
import cn.zerone.water.fragment.MyItem;
import cn.zerone.water.fragment.MySelfFragmentAdapter;


public class HistoryCarClockInAdapter extends ArrayAdapter<HistoryCarItem> {
    private int resourceId;

    public HistoryCarClockInAdapter(@NonNull Context context, int resource, @NonNull List<HistoryCarItem> objects) {
        super(context, resource, objects);
        this.resourceId=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        HistoryCarItem carItem = getItem(position);
        View view;
        HistoryCarClockInAdapter.ViewHolder viewHolder;
        if (convertView == null){
            view=LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new HistoryCarClockInAdapter.ViewHolder();
            viewHolder.Address = view.findViewById(R.id.location);
            viewHolder.Pic = view.findViewById(R.id.Pic);
            viewHolder.CarInfoId = view.findViewById(R.id.carNumber);
            viewHolder.DataType = view.findViewById(R.id.clockInType);
            viewHolder.StationName = view.findViewById(R.id.relatedStation);
            viewHolder.EngineeringId = view.findViewById(R.id.relatedProject);
            viewHolder.AddTime = view.findViewById(R.id.datetime);
            view.setTag(viewHolder);
        } else {
            view=convertView;
            viewHolder= (HistoryCarClockInAdapter.ViewHolder) view.getTag();
        }
        viewHolder.Pic.setImageURI(Uri.fromFile(new File(carItem.getpath())));
        viewHolder.Address.setText(carItem.getAddress());
        viewHolder.AddTime.setText(carItem.getAddTime());
        viewHolder.EngineeringId.setText(carItem.getEngineeringId());
        viewHolder.StationName.setText(carItem.getStationName());
        viewHolder.DataType.setText(carItem.getDataType());
        viewHolder.CarInfoId.setText(carItem.getCarInfoId());
        return view;
    }
    class ViewHolder{

        public ImageView Pic;
        public TextView Address;
        public TextView AddTime;
        public TextView DataType;
        public TextView EngineeringId;
        public TextView StationName;
        public TextView CarInfoId;
    }
}
