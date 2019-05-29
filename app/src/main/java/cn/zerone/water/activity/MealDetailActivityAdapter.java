package cn.zerone.water.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.zerone.water.R;
import cn.zerone.water.fragment.MyItem;

public class MealDetailActivityAdapter extends ArrayAdapter<MealDetailItem>{

    private int resourceId;
    private List<MealDetailItem> mMealDetailItemList;

    public MealDetailActivityAdapter(@NonNull Context context, int resource, @NonNull List<MealDetailItem> objects) {
        super(context, resource, objects);
        this.resourceId=resource;
        this.mMealDetailItemList=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.itemDate = view.findViewById(R.id.meal_dates);
            viewHolder.itemType = view.findViewById(R.id.meal_types);
            viewHolder.itemFee = view.findViewById(R.id.meal_fees);
            view.setTag(viewHolder);
        } else {
            view=convertView;
            viewHolder= (ViewHolder) view.getTag();
        }
        viewHolder.itemDate.setText(mMealDetailItemList.get(position).getItemDate());
        viewHolder.itemType.setText(mMealDetailItemList.get(position).getItemType());
        viewHolder.itemFee.setText(mMealDetailItemList.get(position).getItemFee());
        return view;
    }
    class ViewHolder{

        public TextView itemDate;
        public TextView itemType;
        public TextView itemFee;
    }
}
