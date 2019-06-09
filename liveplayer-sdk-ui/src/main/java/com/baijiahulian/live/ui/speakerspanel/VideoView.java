package com.baijiahulian.live.ui.speakerspanel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.baijia.baijiashilian.liveplayer.ViESurfaceViewRenderer;
import com.baijiahulian.live.ui.R;
import com.baijiahulian.live.ui.utils.DisplayUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by Shubo on 2017/6/10.
 */

public class VideoView extends FrameLayout {

    private TextView tvName;
    private SurfaceView surfaceView;
    private ImageView ivWaterMark;
    private Bitmap waterMark;
    private WaterMarkTarget target;

    private String name;
    private String waterMarkUrl;
    private int waterMarkPosition = 1;

    @ColorInt
    int color = -1;

    public VideoView(Context context, String name, String waterMarkUrl, int waterMarkPosition) {
        super(context);
        this.name = name;
        this.waterMarkPosition = waterMarkPosition;
        this.waterMarkUrl = waterMarkUrl;
        init();
    }

    public VideoView(Context context, String name) {
        super(context);
        this.name = name;
        init();
    }

    private void init() {
        ViewGroup.LayoutParams flLp = new ViewGroup.LayoutParams(DisplayUtils.dip2px(getContext(), 100), DisplayUtils.dip2px(getContext(), 76));
        this.setLayoutParams(flLp);
        if (color == -1) color = ContextCompat.getColor(getContext(), R.color.live_white);
        //视频
        surfaceView = ViESurfaceViewRenderer.CreateRenderer(getContext(), true);
        surfaceView.setZOrderMediaOverlay(true);
        this.addView(surfaceView);
        //名字
        tvName = new TextView(getContext());
        FrameLayout.LayoutParams tvLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvLp.gravity = Gravity.BOTTOM;
        tvName.setGravity(Gravity.CENTER);
        tvName.setTextColor(color);
        tvName.setPadding(0, DisplayUtils.dip2px(getContext(), 2), 0, DisplayUtils.dip2px(getContext(), 2));
        tvName.setLines(1);
        tvName.setText(name);
        tvName.setTextSize(13);
        tvName.setBackgroundResource(R.drawable.shape_remote_name_bg);
        tvName.setLayoutParams(tvLp);
        this.addView(tvName);
    }

    private class WaterMarkTarget implements Target {

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            waterMark = bitmap;
            int height = Math.min(waterMark.getHeight(), VideoView.this.getMeasuredHeight() / 9);
            int width = Math.min(waterMark.getWidth(), VideoView.this.getMeasuredWidth() / 9);
            FrameLayout.LayoutParams ivLp = new FrameLayout.LayoutParams(width, height);
            switch (waterMarkPosition) {
                case 1:
                    ivLp.gravity = GravityCompat.START | Gravity.TOP;
                    break;
                case 2:
                    ivLp.gravity = GravityCompat.END | Gravity.TOP;
                    break;
                case 3:
                    ivLp.gravity = GravityCompat.END | Gravity.BOTTOM;
                    break;
                case 4:
                    ivLp.gravity = GravityCompat.START | Gravity.BOTTOM;
                    break;
            }
            ivWaterMark.setImageBitmap(waterMark);
            ivWaterMark.setScaleType(ImageView.ScaleType.FIT_START);
            VideoView.this.addView(ivWaterMark, ivLp);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }

//    public void setIsPresenter(boolean isPresenter) {
//        String hint = getContext().getString(R.string.live_presenter_hint);
//        if (!isPresenter) {
//            waterMarkUrl = null;
//            if (name.contains(hint)) {
//                name = name.substring(0, name.indexOf(hint));
//                tvName.setText(name);
//            }
//            for (int i = 0; i < this.getChildCount(); i++) {
//                if (this.getChildAt(i) == ivWaterMark) {
//                    this.removeViewAt(i);
//                    break;
//                }
//            }
//        } else {
//            if (!name.contains(hint)) {
//                name = name + hint;
//                tvName.setText(name);
//            }
//        }
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (TextUtils.isEmpty(waterMarkUrl)) {
            return;
        }
        if (target == null) {
            target = new WaterMarkTarget();
            ivWaterMark = new ImageView(getContext());
            Picasso.with(getContext()).load(waterMarkUrl).into(target);
        }
    }

//    public void resizeWaterMark(int videoHeight, int videoWidth) {
//        if (ivWaterMark == null) return;
//        if (getMeasuredWidth() * getMeasuredHeight() <= 0) return;
//        int height = Math.min(waterMark.getHeight(), VideoView.this.getMeasuredHeight() / 9);
//        int width = Math.min(waterMark.getWidth(), VideoView.this.getMeasuredWidth() / 9);
//        FrameLayout.LayoutParams ivLp = (LayoutParams) ivWaterMark.getLayoutParams();
//        ivLp.width = width;
//        ivLp.height = height;
//        switch (waterMarkPosition) {
//            case 1:
//                ivLp.leftMargin = videoWidth + 15;
//                ivLp.topMargin = videoHeight + 10;
//                ivLp.rightMargin = 0;
//                ivLp.bottomMargin = 0;
//                ivLp.gravity = GravityCompat.START | Gravity.TOP;
//                break;
//            case 2:
//                ivLp.leftMargin = 0;
//                ivLp.topMargin = videoHeight + 10;
//                ivLp.rightMargin = videoWidth + 15;
//                ivLp.bottomMargin = 0;
//                ivLp.gravity = GravityCompat.END | Gravity.TOP;
//                break;
//            case 3:
//                ivLp.leftMargin = 0;
//                ivLp.topMargin = 0;
//                ivLp.rightMargin = videoWidth + 15;
//                ivLp.bottomMargin = videoHeight + 10;
//                ivLp.gravity = GravityCompat.END | Gravity.BOTTOM;
//                break;
//            case 4:
//                ivLp.leftMargin = videoWidth + 15;
//                ivLp.topMargin = 0;
//                ivLp.rightMargin = 0;
//                ivLp.bottomMargin = videoHeight + 10;
//                ivLp.gravity = GravityCompat.START | Gravity.BOTTOM;
//                break;
//        }
//        ivWaterMark.setLayoutParams(ivLp);
//    }

    public void resizeWaterMark(final int videoHeight, final int videoWidth) {
        if (ivWaterMark == null) return;
        if (getMeasuredWidth() * getMeasuredHeight() <= 0) return;
        int height = Math.min(waterMark.getHeight(), VideoView.this.getMeasuredHeight() / 9);
        int width = Math.min(waterMark.getWidth(), VideoView.this.getMeasuredWidth() / 9);
        FrameLayout.LayoutParams ivLp = (LayoutParams) ivWaterMark.getLayoutParams();
        ivLp.width = width;
        ivLp.height = height;
        int viewHeight = VideoView.this.getMeasuredHeight();
        float viewWidth = ((float) viewHeight / videoHeight * videoWidth);
        switch (waterMarkPosition) {
            case 1:
                ivLp.leftMargin = (int) ((VideoView.this.getMeasuredWidth() - viewWidth) / 2 + 15);
                ivLp.topMargin = 10;
                ivLp.rightMargin = 0;
                ivLp.bottomMargin = 0;
                ivLp.gravity = GravityCompat.START | Gravity.TOP;
                break;
            case 2:
                ivLp.leftMargin = 0;
                ivLp.topMargin = 10;
                ivLp.rightMargin = (int) (videoWidth + (VideoView.this.getMeasuredWidth() - viewWidth) / 2 + 15);
                ivLp.bottomMargin = 0;
                ivLp.gravity = GravityCompat.END | Gravity.TOP;
                break;
            case 3:
                ivLp.leftMargin = 0;
                ivLp.topMargin = 0;
                ivLp.rightMargin = (int) (videoWidth + (VideoView.this.getMeasuredWidth() - viewWidth) / 2 + 15);
                ivLp.bottomMargin = 10;
                ivLp.gravity = GravityCompat.END | Gravity.BOTTOM;
                break;
            case 4:
                ivLp.leftMargin = (int) ((VideoView.this.getMeasuredWidth() - viewWidth) / 2 + 15);
                ivLp.topMargin = 0;
                ivLp.rightMargin = 0;
                ivLp.bottomMargin = 10;
                ivLp.gravity = GravityCompat.START | Gravity.BOTTOM;
                break;
        }
        ivWaterMark.setLayoutParams(ivLp);
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = Math.min(waterMark.getHeight(), VideoView.this.getMeasuredHeight() / 9);
                int width = Math.min(waterMark.getWidth(), VideoView.this.getMeasuredWidth() / 9);
                FrameLayout.LayoutParams ivLp = (LayoutParams) ivWaterMark.getLayoutParams();
                ivLp.width = width;
                ivLp.height = height;
                int viewVerticalHeight = VideoView.this.getMeasuredHeight();
                float viewVerticalWidth = ((float) viewVerticalHeight / videoHeight * videoWidth);
                int viewTransverseWidth = VideoView.this.getMeasuredWidth();
                float viewTransverseHeight = ((float) viewTransverseWidth / videoWidth * videoHeight);
                int transverseMargin = (int) ((VideoView.this.getMeasuredWidth() - viewVerticalWidth) / 2) <= 0 ? 0 : (int) ((VideoView.this.getMeasuredWidth() - viewVerticalWidth) / 2);
                int verticalMargin = (int) ((VideoView.this.getMeasuredHeight() - viewTransverseHeight) / 2) <= 0 ? 0 : (int) ((VideoView.this.getMeasuredHeight() - viewTransverseHeight) / 2);
                switch (waterMarkPosition) {
                    case 1:
                        ivLp.leftMargin = transverseMargin + 20;
                        ivLp.topMargin = verticalMargin + 15;
                        ivLp.rightMargin = 0;
                        ivLp.bottomMargin = 0;
                        ivLp.gravity = GravityCompat.START | Gravity.TOP;
                        break;
                    case 2:
                        ivLp.leftMargin = 0;
                        ivLp.topMargin = verticalMargin + 10;
                        ivLp.rightMargin = transverseMargin + 15;
                        ivLp.bottomMargin = 0;
                        ivLp.gravity = GravityCompat.END | Gravity.TOP;
                        break;
                    case 3:
                        ivLp.leftMargin = 0;
                        ivLp.topMargin = 0;
                        ivLp.rightMargin = transverseMargin + 15;
                        ivLp.bottomMargin = verticalMargin + 10;
                        ivLp.gravity = GravityCompat.END | Gravity.BOTTOM;
                        break;
                    case 4:
                        ivLp.leftMargin = transverseMargin + 15;
                        ivLp.topMargin = 0;
                        ivLp.rightMargin = 0;
                        ivLp.bottomMargin = verticalMargin + 10;
                        ivLp.gravity = GravityCompat.START | Gravity.BOTTOM;
                        break;
                }
                ivWaterMark.setLayoutParams(ivLp);

            }
        });
    }


    public View getSurfaceView() {
        return surfaceView;
    }

    public void setNameColor(@ColorInt int color) {
        this.color = color;
        if (tvName != null) {
            tvName.setTextColor(color);
        }
    }
}
