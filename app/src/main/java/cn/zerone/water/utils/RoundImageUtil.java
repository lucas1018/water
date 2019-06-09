package cn.zerone.water.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.Gravity;

/**
 * create by halfcup
 * on 2019/1/4
 */
public class RoundImageUtil {
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {

        //获取bmp的宽高 小的一个做为圆的直径r
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int r = Math.min(w, h);

        //创建一个paint
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        //新创建一个Bitmap对象newBitmap 宽高都是r
        Bitmap newBitmap = Bitmap.createBitmap(r, r, Bitmap.Config.ARGB_8888);

        //创建一个使用newBitmap的Canvas对象
        Canvas canvas = new Canvas(newBitmap);

        //canvas画一个圆形
      //  canvas.drawCircle(r / 2, r / 2, r /2, paint);
        canvas.drawCircle(r / 2, r / 2, (float)(0.45*r), paint);

        //然后 paint要设置Xfermode 模式为SRC_IN 显示上层图像（后绘制的一个）的相交部分
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        //canvas调用drawBitmap直接将bmp对象画在画布上 因为paint设置了Xfermode，所以最终只会显示这个bmp的一部分 也就
        //是bmp的和下层圆形相交的一部分圆形的内容
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return newBitmap;

    }
}
