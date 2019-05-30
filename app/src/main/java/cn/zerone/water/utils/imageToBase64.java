package cn.zerone.water.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class imageToBase64 {
    private String path;

    public imageToBase64(String path){
        this.path = path;
    }

    public String getPath(){
        return path;
    }

    public String getBase64() {
//        if (TextUtils.isEmpty(getPath())) {
//            return null;
//        }
//        InputStream is = null;
//        byte[] data;
//        String result = null;
//        try {
//            is = new FileInputStream(getPath());
//            //创建一个字符流大小的数组。
//            data = new byte[is.available()];
//            //写入数组
//            is.read(data);
//            //用默认的编码格式进行编码
//            result = Base64.encodeToString(data, Base64.DEFAULT);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (null != is) {
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return result;
        File file = new File(path);
        String imageStr="";
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            byte[] data = new byte[in.available()];
            in.read(data);
            in.close();
            imageStr = new String(Base64.encodeToString(data, Base64.DEFAULT));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return imageStr;
    }
}
