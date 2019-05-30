package cn.zerone.water.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


import cn.zerone.water.R;
import cn.zerone.water.activity.ApproveActivity;
import cn.zerone.water.activity.CheckDetailsActivity;

public class ApproveAdapter extends RecyclerView.Adapter<ApproveAdapter.ViewHolder> {

    private int resourceId;
    private List<ApproveItem> mObjects;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView itemIcon;
        TextView itemAbstruct;
        TextView itemApplyUser;
        TextView itemTime;
        ImageView itemGo;
        View myView;

        public ViewHolder(View view){
            super(view);
            myView = view;
            itemIcon = view.findViewById(R.id.item_user_icon);
            itemAbstruct = view.findViewById(R.id.item_abstruct);
            itemApplyUser = view.findViewById(R.id.item_apply_user);
            itemTime = view.findViewById(R.id.item_time);
            itemGo = view.findViewById(R.id.item_go);

        }
    }

    public ApproveAdapter(List<ApproveItem> objects) {
        mObjects = objects;
    }

    public void setData(List<ApproveItem> objects) {
        this.mObjects = objects;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.approve_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.myView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int position = holder.getAdapterPosition();
                ApproveItem item = mObjects.get(position);
                int status = item.getItemStatus();
                if(status == 0){
                    Bundle bundle = new Bundle();
                    bundle.putString("Url", item.getWebUrl());
                    bundle.putInt("checkID", item.getCheckID());
                    bundle.putString("CheckName", item.getItemAbstruct());

                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(v.getContext(), CheckDetailsActivity.class);
                    v.getContext().startActivity(intent);
                }


            }

        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ApproveItem item = mObjects.get(position);
        holder.itemIcon.setImageBitmap(item.getItemIcon());
        holder.itemAbstruct.setText(item.getItemAbstruct());
        holder.itemApplyUser.setText(item.getItemApplyUser());
        holder.itemTime.setText(item.getItemTime());

    }

    @Override
    public int getItemCount() {
        return mObjects.size();
    }


}
