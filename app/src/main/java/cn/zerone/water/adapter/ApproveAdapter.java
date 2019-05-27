package cn.zerone.water.adapter;

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

import cn.zerone.water.fragment.MyItem;
import cn.zerone.water.R;

public class ApproveAdapter extends ArrayAdapter<ApproveItem> {

    private int resourceId;

    public ApproveAdapter(@NonNull Context context, int resource, @NonNull List<ApproveItem> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ApproveItem myItem = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null){ view = LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
        viewHolder = new ViewHolder();
        viewHolder.itemIcon = view.findViewById(R.id.item_user_icon);
        viewHolder.itemAbstruct = view.findViewById(R.id.item_abstruct);
        viewHolder.itemApplyUser = view.findViewById(R.id.item_apply_user);
        viewHolder.itemTime = view.findViewById(R.id.item_time);
        viewHolder.itemGo=view.findViewById(R.id.item_go);
        view.setTag(viewHolder);

        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.itemIcon.setImageResource(myItem.getItemIcon());
        viewHolder.itemAbstruct.setText(myItem.getItemAbstruct());
        viewHolder.itemApplyUser.setText(myItem.getItemApplyUser());
        viewHolder.itemTime.setText(myItem.getItemTime());
        return view;
    }
    class ViewHolder{

        ImageView itemIcon;
        TextView itemAbstruct;
        TextView itemApplyUser;
        TextView itemTime;
        ImageView itemGo;

    }
}
