package com.baijiahulian.live.ui.more;

import com.baijiahulian.live.ui.base.BasePresenter;
import com.baijiahulian.live.ui.base.BaseView;

/**
 * Created by Shubo on 2017/4/17.
 */

interface MoreMenuContract {

    interface View extends BaseView<Presenter> {
        void showCloudRecordOn();

        void showCloudRecordOff();

        void showCloudRecordNotAllowed(String reason);
    }

    interface Presenter extends BasePresenter {
        void navigateToAnnouncement();

        void switchCloudRecord();

        void navigateToHelp();

        void navigateToSetting();

        boolean isTeacher();
    }
}
