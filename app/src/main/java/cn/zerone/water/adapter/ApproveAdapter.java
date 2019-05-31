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
import cn.zerone.water.activity.CheckSubmitActivity;

public class ApproveAdapter extends RecyclerView.Adapter<ApproveAdapter.ViewHolder> implements View.OnClickListener{

    private int resourceId;
    private List<ApproveItem> mObjects;//数据源
    private Context context;//上下文



    class ViewHolder extends RecyclerView.ViewHolder{
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

            view.setOnClickListener(ApproveAdapter.this);
            itemGo.setOnClickListener(ApproveAdapter.this);

        }
    }

    public ApproveAdapter(List<ApproveItem> objects) {
        this.mObjects = objects;
        //this.context = context;
    }

    public void setData(List<ApproveItem> objects) {
        this.mObjects = objects;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.approve_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
      /*  holder.myView.setOnClickListener(new View.OnClickListener(){
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
                    //startActivityForResult(intent, 1);
                }

            }

        });*/

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ApproveItem item = mObjects.get(position);
        holder.itemIcon.setImageBitmap(item.getItemIcon());
        holder.itemAbstruct.setText(item.getItemAbstruct());
        holder.itemApplyUser.setText(item.getItemApplyUser());
        holder.itemTime.setText(item.getItemTime());

        holder.itemView.setTag(position);
        holder.itemGo.setTag(position);

    }

    @Override
    public int getItemCount() {
        return mObjects.size();
    }

    //item里面有多个控件可以点击（item+item内部控件）
    public enum ViewName {
        ITEM,
        PRACTISE
    }

    //自定义一个回调接口来实现Click和LongClick事件
    public interface OnItemClickListener  {
        void onItemClick(View v, ViewName viewName, int position);
        void onItemLongClick(View v);
    }

    private OnItemClickListener mOnItemClickListener;//声明自定义的接口

    //定义方法并传给外面的使用者
    public void setOnItemClickListener(OnItemClickListener  listener) {
        this.mOnItemClickListener  = listener;
    }

    @Override
    public void onClick(View v) {

        int position = (int) v.getTag();      //getTag()获取数据
        if (mOnItemClickListener != null) {
            switch (v.getId()){
                case R.id.item_go:
                    mOnItemClickListener.onItemClick(v, ViewName.PRACTISE, position);
                    break;
                default:
                    mOnItemClickListener.onItemClick(v, ViewName.ITEM, position);
                    break;
            }
        }


    }


}
