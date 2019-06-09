package com.baijiahulian.live.ui.users;

import com.baijiahulian.live.ui.activity.LiveRoomRouterListener;
import com.baijiahulian.live.ui.utils.RxUtils;
import com.baijiahulian.livecore.models.imodels.IUserModel;
import com.baijiahulian.livecore.utils.LPBackPressureBufferedSubscriber;
import com.baijiahulian.livecore.utils.LPErrorPrintSubscriber;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Shubo on 2017/4/6.
 */

public class OnlineUserPresenter implements OnlineUserContract.Presenter {

    private OnlineUserContract.View view;
    private LiveRoomRouterListener routerListener;
    private Subscription subscriptionOfUserCountChange, subscriptionOfUserDataChange;
    private boolean isLoading = false;

    public OnlineUserPresenter(OnlineUserContract.View view) {
        this.view = view;
    }

    @Override
    public void setRouter(LiveRoomRouterListener liveRoomRouterListener) {
        routerListener = liveRoomRouterListener;
    }

    @Override
    public void subscribe() {
        subscriptionOfUserCountChange = routerListener.getLiveRoom()
                .getObservableOfUserNumberChange()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        view.notifyUserCountChange(integer);
                    }
                });
        subscriptionOfUserDataChange = routerListener.getLiveRoom()
                .getOnlineUserVM()
                .getObservableOfOnlineUser()
                .onBackpressureBuffer()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPBackPressureBufferedSubscriber<List<IUserModel>>() {
                    @Override
                    public void call(List<IUserModel> iUserModels) {
                        // iUserModels == null   no more data
                        if (isLoading)
                            isLoading = false;
                        view.notifyDataChanged();
                    }
                });
        view.notifyUserCountChange(routerListener.getLiveRoom().getOnlineUserVM().getUserCount());
    }

    @Override
    public void unSubscribe() {
        RxUtils.unSubscribe(subscriptionOfUserCountChange);
        RxUtils.unSubscribe(subscriptionOfUserDataChange);
    }

    @Override
    public void destroy() {
        view = null;
        routerListener = null;
    }

    @Override
    public int getCount() {
        int count = routerListener.getLiveRoom().getOnlineUserVM().getUserCount();
        return isLoading ? count + 1: count;
    }

    @Override
    public IUserModel getUser(int position) {
        if (!isLoading)
            return routerListener.getLiveRoom().getOnlineUserVM().getUser(position);
        IUserModel iUserModel;
        if (position == getCount()) {
            iUserModel = null;
        } else {
            iUserModel = routerListener.getLiveRoom().getOnlineUserVM().getUser(position);
        }
        return iUserModel;
    }

    @Override
    public void loadMore() {
        isLoading = true;
        routerListener.getLiveRoom().getOnlineUserVM().loadMoreUser();
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }
}
