package cn.zerone.water.http;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zero on 2018/11/29.
 */

public class Https {
    public static final String CMD_LOGIN="login";
    public static Response post(String cmd, String json){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = null;
        try {
            FormBody.Builder  builder = new FormBody.Builder();
            builder.add("Cmd",cmd);
            String md5 = cmd+json+"d8sd19k09d1dc6";
            builder.add("Md5","");
            builder.add("Json",json);
            body= builder.build();
            return okHttpClient.newCall(new Request.Builder().url("http://124.237.77.232:50180/cweb/req.aspx").post(body).build()).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
