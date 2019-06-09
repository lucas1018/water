package com.baijiahulian.live.ui.ppt;

import android.widget.FrameLayout;

import com.baijiahulian.live.ui.base.BasePresenter;
import com.baijiahulian.live.ui.base.BaseView;

/**
 * Created by Shubo on 2017/2/18.
 */

interface PPTContract {

    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter {
        void clearScreen();

        void showQuickSwitchPPTView(int currentIndex, int maxIndex);

        void updateQuickSwitchPPTView(int maxIndex);

        FrameLayout getBackgroundContainer();

        void notifyPPTResumeInSpeakers();
    }
}
