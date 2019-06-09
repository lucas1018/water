package com.baijiahulian.live.ui.ppt;

import android.widget.FrameLayout;

import com.baijiahulian.live.ui.activity.LiveRoomRouterListener;

/**
 * Created by Shubo on 2017/2/18.
 */

public class PPTPresenter implements PPTContract.Presenter {

    private PPTContract.View view;
    private LiveRoomRouterListener routerListener;
    private boolean isScreenCleared = false;

    public PPTPresenter(PPTContract.View view) {
        this.view = view;
    }

    @Override
    public void clearScreen() {
        if (!routerListener.isPPTMax()) return;
        isScreenCleared = !isScreenCleared;
        if (isScreenCleared) routerListener.clearScreen();
        else routerListener.unClearScreen();
    }

    @Override
    public void showQuickSwitchPPTView(int currentIndex, int maxIndex) {
        routerListener.navigateToQuickSwitchPPT(currentIndex, maxIndex);
    }

    @Override
    public void updateQuickSwitchPPTView(int maxIndex) {
        routerListener.updateQuickSwitchPPTMaxIndex(maxIndex);
    }

    @Override
    public FrameLayout getBackgroundContainer() {
        return routerListener.getBackgroundContainer();
    }

    @Override
    public void notifyPPTResumeInSpeakers() {
        routerListener.notifyPPTResumeInSpeakers();
    }

    @Override
    public void setRouter(LiveRoomRouterListener liveRoomRouterListener) {
        routerListener = liveRoomRouterListener;
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unSubscribe() {

    }

    @Override
    public void destroy() {
        view = null;
        routerListener = null;
    }
}
