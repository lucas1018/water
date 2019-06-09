package cn.zerone.water.presenter;

import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.baijiahulian.live.ui.base.BaseView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.zerone.water.http.Requests;
import cn.zerone.water.model.UserMode;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.operators.observable.ObservableCache;

/**
 * Created by Administrator on 2019/6/7.
 */

public class SelectionMembersPresenter extends AppPresenter<SelectionMembersPresenter.SelectionMemberView>{
    public SelectionMembersPresenter(SelectionMemberView view) {
        super(view);
    }
//选择成员
    public void Selection(){
        Requests.selectMember(new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONArray objects) {
             ArrayList<UserMode> userModes= new Gson().fromJson(objects.toJSONString(), new TypeToken<ArrayList<UserMode>>(){}.getType());
               getView(). onSelectionSuccess(userModes);


            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
    //创建房间
    public void CeateRoom(){
        Requests.selectMember(new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONArray objects) {
                ArrayList<UserMode> userModes= new Gson().fromJson(objects.toJSONString(), new TypeToken<ArrayList<UserMode>>(){}.getType());
                getView(). onSelectionSuccess(userModes);


            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    protected void init() {

    }

    public interface  SelectionMemberView extends AppView {
        void onSelectionSuccess(ArrayList<UserMode> userModes);
        void onFailure();
    }
}
