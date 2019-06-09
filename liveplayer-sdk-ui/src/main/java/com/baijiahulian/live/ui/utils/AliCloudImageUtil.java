package com.baijiahulian.live.ui.utils;

import android.content.Context;

import com.baijiahulian.livecore.utils.DisplayUtils;
import com.baijiahulian.livecore.utils.LPBJUrl;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;

/**
 * Created by Shubo on 2017/4/6.
 */

public class AliCloudImageUtil {

    /**
     * 200*200像素阿里云图片裁剪规则 (聊天图片)
     *
     * @param url
     * @return
     */
    public static String getScaledUrl(String url) {
        url = url.split("@")[0];
        if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png") || url.endsWith(".webp")
                || url.endsWith(".bmp") || url.endsWith(".gif")) {
            if (isFromAliCloud(url)) {
                return url + "@200h_200w_1e_1l_2o" + imageUrlSuffix();
            }
        }
        return url;
    }

    /**
     * 正方形阿里云图片裁剪规则
     *
     * @param url
     * @return
     */
    public static String getRectScaledUrl(Context context, String url, int dp) {
        int px = DisplayUtils.dip2px(context, dp);
        if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png") || url.endsWith(".webp")
                || url.endsWith(".bmp") || url.endsWith(".gif")) {
            if (isFromAliCloud(url)) {
                return url + "@" + px + "h_" + px + "w_1c_1e_1l_2o" + imageUrlSuffix();
            }
        }
        return url;
    }

    /**
     * 屏幕长宽阿里云图片裁剪规则
     *
     * @param context
     * @param url
     * @return
     */
    public static String getScreenScaledUrl(Context context, String url) {
        if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png") || url.endsWith(".webp")
                || url.endsWith(".bmp") || url.endsWith(".gif")) {
            if (isFromAliCloud(url)) {
                return url + "@" + DisplayUtils.getScreenHeightPixels(context) + "h_" +
                        DisplayUtils.getScreenWidthPixels(context) + "w_1e_1l_2o" + imageUrlSuffix();
            }
        }
        return url;
    }

    /**
     * 阿里云圆形图片规则
     *
     * @param url
     * @param radius
     * @return
     */
    public static String getRoundedAvatarUrl(String url, int radius) {
        String imageUrl = url;
        if (url.contains("@")) {
            imageUrl = url.substring(0, url.indexOf("@"));
        }
        if (isFromAliCloud(imageUrl))
            return imageUrl + "@" + 2 * radius + "w_" + 2 * radius + "h_1e_1c_" + radius + "-" + "1ci" + imageUrlSuffix();
        else
            return imageUrl;
    }

    /**
     * 判断是否是满足是阿里云的图片
     *
     * @param picUrl
     * @return
     */
    private static boolean isFromAliCloud(String picUrl) {
        LPBJUrl url = LPBJUrl.parse(picUrl);
        String host = url.getHost();
        return host.endsWith(".genshuixue.com") || host.endsWith(".gsxservice.com");
    }

    private static String imageUrlSuffix() {
        if (SDK_INT >= ICE_CREAM_SANDWICH) {
            return ".webp";
        } else {
            return ".png";
        }
    }
}
