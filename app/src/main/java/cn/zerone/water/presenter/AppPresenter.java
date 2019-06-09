package cn.zerone.water.presenter;

import android.os.Handler;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2019/6/7.
 */
public abstract class AppPresenter<T extends AppView> {
    private T mView;

    public AppPresenter(T view) {
        mView = view;
        init();
    }
    private Handler mHandler = new Handler();

    protected abstract void init();
    protected void registerEvent(){
        EventBus.getDefault().register(this);
    }
    protected void unRegisterEvent(){
        EventBus.getDefault().unregister(this);
    }

    public void onDestroy(){
        unbindView();
        mHandler.removeCallbacksAndMessages(null);
    }

    protected T getView(){
        return mView;
    }

    protected Handler getHandler(){
        return mHandler;
    }


    protected boolean isMyViewNotNull() {
        return mView != null;
    }

    protected void unbindView() {
        mView = null;
    }
//
//    public boolean currentUserIsGuest(){
//        boolean isGuest= ModelFactory.getInstance(DeviceModel.class).getLastState()==DeviceModel.State.GUEST;
//        return isGuest;
//    }



}