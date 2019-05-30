package cn.zerone.water.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class image2Base64Util {


    public String getBase64(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        InputStream is = null;
        byte[] data;
        String result = null;
        try {
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    public String encodeImageToBase64(String imgUrl){
        HttpURLConnection conn = null;
        String base64 = "";
        try {
            URL url = new URL(imgUrl);

            conn = (HttpURLConnection) url.openConnection();
//设置请求方式为"GET"
            conn.setRequestMethod("GET");
//超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);
//通过输入流获取图片数据
            InputStream inStream = conn.getInputStream();
//得到图片的二进制数据，以二进制封装得到数据，具有通用性
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//创建一个Buffer字符串
            byte[] buffer = new byte[1024];
//每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
//使用一个输入流从buffer里把数据读取出来
            while ((len = inStream.read(buffer)) != -1) {
//用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
//关闭输入流
            inStream.close();
            byte[] data = outStream.toByteArray();
//对字节数组Base64编码
            BASE64Encoder encoder = new BASE64Encoder();
            base64 = encoder.encode(data);
            //返回Base64编码过的字节数组字符串
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64;
    }

    public String image2Base64(String imgUrl) {
        URL url;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        try {
            url = new URL(imgUrl);
            urlConnection = ( HttpURLConnection ) url.openConnection();
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];

            int len = 0;

            //使用一个输入流从buffer里把数据读取出来

            while ((len = inputStream.read(buffer)) != -1) {
                //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                baos.write(buffer, 0, len);

            }

            // 对字节数组Base64编码
            return encode(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return imgUrl;
    }



    public static String encode(byte[] image){
        BASE64Encoder decoder = new BASE64Encoder();
        return replaceEnter(decoder.encode(image));

    }

    public static String replaceEnter(String str){
        String reg ="[\n-\r]";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }



    /**

     * 字符串转图片

     * @param base64Str

     * @return

     */
    private static byte[] decode(String base64Str){
        byte[] b = null;
        BASE64Decoder decoder = new BASE64Decoder();

        try {

            b = decoder.decodeBuffer(replaceEnter(base64Str));

        } catch (IOException e) {

            e.printStackTrace();
        }
        return b;
    }

    private static ByteBuffer decodeBuffer(String base64Str) {
        ByteBuffer buffer = null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            buffer = decoder.decodeBufferToByteBuffer(base64Str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }
}
