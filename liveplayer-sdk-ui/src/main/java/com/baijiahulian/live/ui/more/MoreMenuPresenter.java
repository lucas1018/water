package com.baijiahulian.live.ui.more;

import com.baijiahulian.live.ui.activity.LiveRoomRouterListener;
import com.baijiahulian.live.ui.utils.RxUtils;
import com.baijiahulian.livecore.models.LPCheckRecordStatusModel;
import com.baijiahulian.livecore.utils.LPErrorPrintSubscriber;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Shubo on 2017/4/17.
 */

public class MoreMenuPresenter implements MoreMenuContract.Presenter {

    private MoreMenuContract.View view;
    private LiveRoomRouterListener routerListener;
    private Subscription subscriptionOfCloudRecord, subscriptionOfIsCloudRecordAllowed;
    private boolean recordStatus;

    public MoreMenuPresenter(MoreMenuContract.View view) {
        this.view = view;
    }

    @Override
    public void setRouter(LiveRoomRouterListener liveRoomRouterListener) {
        routerListener = liveRoomRouterListener;
    }

    @Override
    public void subscribe() {
        subscriptionOfCloudRecord = routerListener.getLiveRoom().getObservableOfCloudRecordStatus()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        recordStatus = aBoolean;
                        if (aBoolean)
                            view.showCloudRecordOn();
                        else
                            view.showCloudRecordOff();
                    }
                });
        recordStatus = routerListener.getLiveRoom().getCloudRecordStatus();
        if (recordStatus)
            view.showCloudRecordOn();
        else
            view.showCloudRecordOff();
    }

    @Override
    public void unSubscribe() {
        RxUtils.unSubscribe(subscriptionOfCloudRecord);
    }

    @Override
    public void destroy() {
        routerListener = null;
        view = null;
    }

    @Override
    public void navigateToAnnouncement() {
        routerListener.navigateToAnnouncement();
    }

    @Override
    public void switchCloudRecord() {
        if (!recordStatus) {
            subscriptionOfIsCloudRecordAllowed = routerListener.getLiveRoom().requestIsCloudRecordAllowed()
                    .subscribe(new LPErrorPrintSubscriber<LPCheckRecordStatusModel>() {
                        @Override
                        public void call(LPCheckRecordStatusModel lpCheckRecordStatusModel) {
                            if (lpCheckRecordStatusModel.recordStatus == 1) {
                                routerListener.navigateToCloudRecord(true);
                                routerListener.getLiveRoom().requestCloudRecord(true);
                            } else {
                                if (view != null)
                                    view.showCloudRecordNotAllowed(lpCheckRecordStatusModel.reason);
                            }
                        }
                    });
        } else {
            //ui
            routerListener.navigateToCloudRecord(false);
            //logic
            routerListener.getLiveRoom().requestCloudRecord(false);

        }
    }

    @Override
    public void navigateToHelp() {
        routerListener.navigateToHelp();
    }

    @Override
    public void navigateToSetting() {
        routerListener.navigateToSetting();
    }

    @Override
    public boolean isTeacher() {
        return routerListener.isCurrentUserTeacher();
    }
}
