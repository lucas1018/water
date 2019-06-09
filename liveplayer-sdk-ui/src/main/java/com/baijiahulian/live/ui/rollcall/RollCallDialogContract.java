package com.baijiahulian.live.ui.rollcall;

import com.baijiahulian.live.ui.base.BasePresenter;
import com.baijiahulian.live.ui.base.BaseView;

/**
 * Created by wangkangfei on 17/5/31.
 */

public interface RollCallDialogContract {

    interface View extends BaseView<Presenter> {
        void timerDown(int time);

        void timeOutSoDismiss();
    }

    interface Presenter extends BasePresenter {
        void rollCallConfirm();

        void timeOut();
    }
}
