package com.baijiahulian.live.ui.loading;

import com.baijiahulian.live.ui.base.BasePresenter;
import com.baijiahulian.live.ui.base.BaseView;
import com.baijiahulian.livecore.context.LPError;
import com.baijiahulian.livecore.context.LiveRoom;
import com.baijiahulian.livecore.launch.LPLaunchListener;
import com.baijiahulian.livecore.models.imodels.IUserModel;

/**
 * Created by Shubo on 2017/2/14.
 */

interface LoadingContract {
    interface View extends BaseView<Presenter> {
        void showLoadingSteps(int currentStep, int totalSteps);

        void showLaunchError(LPError lpError);

        void setTechSupportVisibility(boolean shouldShow);
    }

    interface Presenter extends BasePresenter {
        LPLaunchListener getLaunchListener();

        String getCode();

        String getName();

        String getSign();

        IUserModel getUser();

        long getRoomId();

        void setLiveRoom(LiveRoom liveRoom);

        boolean isJoinCode();
    }
}
