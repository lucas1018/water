package com.baijiahulian.live.ui.ppt.quickswitchppt;

import com.baijiahulian.live.ui.activity.LiveRoomRouterListener;
import com.baijiahulian.live.ui.utils.RxUtils;
import com.baijiahulian.livecore.viewmodels.impl.LPDocListViewModel;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by szw on 17/7/5.
 */

public class SwitchPPTFragmentPresenter implements SwitchPPTContract.Presenter {
    private SwitchPPTContract.View view;
    private LiveRoomRouterListener listener;
    private Subscription subscriptionOfDocListChange;

    public SwitchPPTFragmentPresenter(SwitchPPTContract.View view) {
        this.view = view;
    }

    @Override
    public void setRouter(LiveRoomRouterListener liveRoomRouterListener) {
        this.listener = liveRoomRouterListener;
    }

    @Override
    public void subscribe() {
        subscriptionOfDocListChange = listener.getLiveRoom().getDocListVM().getObservableOfDocListChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<LPDocListViewModel.DocModel>>() {
                    @Override
                    public void call(List<LPDocListViewModel.DocModel> docModels) {
                        view.docListChanged(docModels);
                    }
                });
        view.setType(!listener.isTeacherOrAssistant());
        view.docListChanged(listener.getLiveRoom().getDocListVM().getDocList());
        view.setIndex();
    }

    @Override
    public void unSubscribe() {
        RxUtils.unSubscribe(subscriptionOfDocListChange);
    }

    @Override
    public void destroy() {
        listener = null;
        view = null;
    }

    @Override
    public void setSwitchPosition(int position) {
        listener.notifyPageCurrent(position);
    }

    public void notifyMaxIndexChange(int maxIndex) {
        if (view != null)
            view.setMaxIndex(maxIndex);
    }
}
