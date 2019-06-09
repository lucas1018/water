package cn.zerone.water.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.chad.library.adapter.base.BaseViewHolder;

import cn.zerone.water.R;
import cn.zerone.water.utils.RoundImageUtil;

/**
 * Created by Administrator on 2019/6/7.
 */

public class SelectionMemberAdapter extends BaseAdapter<SelectionMemberAdapter.Item> {


    public SelectionMemberAdapter() {
        super(R.layout.item_selection_member_live);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final Item item) {
       helper.setText(R.id.textview_usename_live,item.name);
        if (item.photo==null) {
            helper.setImageResource(R.id.imagv_user_head, R.mipmap.logo);
        } else {
            if (!TextUtils.isEmpty(item.photo)) {
                if (item.photo instanceof String) {
             ImageView mImageViewHead=helper.getView(R.id.imagv_user_head);
                    Glide.with(mContext).load(item.photo).asBitmap().centerCrop().into(new BitmapImageViewTarget(mImageViewHead) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            helper.setImageDrawable(R.id.imagv_user_head,circularBitmapDrawable);
                        }
                    });

                }
            } else {
                helper.setImageResource(R.id.imagv_user_head, R.mipmap.logo);
            }
        }
        if (item.isChecked){
            helper.setImageResource(R.id.imagv_checkbox_selection_memeber,R.drawable.zihaohs);

        }else {
            helper.setImageResource(R.id.imagv_checkbox_selection_memeber,R.drawable.zihao);

        }
    }

    public static class Item {
        public String id;
        public String name;
        public String photo;
        public  boolean isChecked;

    }
}


