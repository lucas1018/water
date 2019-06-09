package cn.zerone.water.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/6/7.
 */

public  abstract  class BaseAdapter<T> extends BaseQuickAdapter<T,BaseViewHolder> {

    public BaseAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void clearData(){
        getData().clear();
        notifyDataSetChanged();
    }

    @Override
    public void setNewData(@Nullable List<T> data) {
        if (data==null){return;}
        ArrayList<T> list = new ArrayList<>();
        for (T t:data){
            list.add(t);
        }
        super.setNewData(list);
    }
}