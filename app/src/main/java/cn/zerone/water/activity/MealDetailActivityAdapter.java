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

    public MealDetailActivityAdapter(@NonNull Context context, int resource, @NonNull List<MealDetailItem> objects) {
        super(context, resource, objects);
        this.resourceId=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MealDetailItem mealDetailItem = getItem(position);
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
        viewHolder.itemDate.setText(mealDetailItem.getItemDate());
        viewHolder.itemType.setText(mealDetailItem.getItemType());
        viewHolder.itemFee.setText(mealDetailItem.getItemFee());
        return view;
    }
    class ViewHolder{

        public TextView itemDate;
        public TextView itemType;
        public TextView itemFee;
    }
}
