package cn.zerone.water.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.greendao.annotation.Index;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
/**
 * created by qhk
 * on 2019/5/11
 */
public class HttpUtil {
    // 接口调用的url
    private static final String ADVANCED_URL = "http://47.105.187.185:8011/api1/";

    public static void baseJSONArray(final Observer<JSONArray> observer, final String cmd, final RequestBody requestBody) {
        Observable oble = Observable.create(new ObservableOnSubscribe<JSONArray>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<JSONArray> e) throws Exception {
                Response response = post(cmd, requestBody);
                if(response.code() == 200) {
                    String json = response.body().string();
                    e.onNext(JSON.parseArray(json));
                    e.onComplete();
                } else {
                    throw new IOException();
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        Observer oser = observer;
        oble.subscribe(oser);
    }

    public static void baseString(Observer<String> observer, final String cmd, final RequestBody requestBody) {
        Observable oble = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                Response response = post(cmd, requestBody);
                int code = response.code();
                if(code == 200) {
                    String json = response.body().string();
                    e.onNext(json);
                    e.onComplete();
                } else {
                    throw new IOException();
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        Observer oser = observer;
        oble.subscribe(oser);
    }

    public static <T>  void baseJSONObject(final Observer<JSONObject> observer, final String cmd, final RequestBody requestBody){
        Observable<T> oble = Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<T> e) throws Exception {
                Response response = post(cmd, requestBody);

                if(response != null && response.code() == 200){
                    String json = response.body().string();
                    e.onNext((T) JSON.parseObject(json));
                    e.onComplete();
                }else{
                    e.onError(new IOException());
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        Observer oser = observer;
        oble.subscribe(oser);
    }

    //post请求服务端接口
    public static Response post(String cmd, RequestBody requestBody){
        String url = ADVANCED_URL + cmd;//实际url
        System.out.println("sssssssssss" + url);
        OkHttpClient okHttpClient = new OkHttpClient();
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            return okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
