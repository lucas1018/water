package cn.zerone.water.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
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
import cn.zerone.water.utils.image2Base64Util;


public class HistoryCarClockInAdapter extends ArrayAdapter<HistoryCarItem> {
    private int resourceId;
    private String basicPicturePath = "http://47.105.187.185:8011/Content/img/WebImg/";

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
            viewHolder.Location = view.findViewById(R.id.locationIcon);
            viewHolder.Datetime = view.findViewById(R.id.datetimeIcon);
            view.setTag(viewHolder);
        } else {
            view=convertView;
            viewHolder= (HistoryCarClockInAdapter.ViewHolder) view.getTag();
        }

        String afternoonPictureCapturedPath = basicPicturePath + carItem.getpath();
        // System.out.println("AAAAAAAAAAAAAAAAAAAAAAA"+afternoonPictureCapturedPath);
        image2Base64Util img2base = new image2Base64Util();
        String afternoonBase64 = img2base.encodeImageToBase64(afternoonPictureCapturedPath);
        byte[] decodedStringAfter = Base64.decode(afternoonBase64, Base64.DEFAULT);
        Bitmap decodedByteAfter = BitmapFactory.decodeByteArray(decodedStringAfter, 0, decodedStringAfter.length);

        viewHolder.Pic.setImageBitmap(decodedByteAfter);
        viewHolder.Address.setText(carItem.getAddress());
        viewHolder.AddTime.setText(carItem.getAddTime());
        viewHolder.EngineeringId.setText(carItem.getEngineeringId());
        viewHolder.StationName.setText(carItem.getStationName());
        viewHolder.DataType.setText(carItem.getDataType());
        viewHolder.CarInfoId.setText(carItem.getCarInfoId());
        viewHolder.Location.setImageResource(R.mipmap.location);
        viewHolder.Datetime.setImageResource(R.drawable.icon_time);
        return view;
    }
    class ViewHolder{

        private ImageView Pic;
        private TextView Address;
        private TextView AddTime;
        private TextView DataType;
        private TextView EngineeringId;
        private TextView StationName;
        private TextView CarInfoId;
        private ImageView Location;
        private ImageView Datetime;
    }
}
