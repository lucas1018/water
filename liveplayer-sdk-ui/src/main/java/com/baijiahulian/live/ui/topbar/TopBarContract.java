package com.baijiahulian.live.ui.topbar;

import com.baijiahulian.live.ui.base.BasePresenter;
import com.baijiahulian.live.ui.base.BaseView;

/**
 * Created by Shubo on 2017/2/13.
 */

interface TopBarContract {

    interface View extends BaseView<Presenter> {

        void showHideShare(boolean show);
    }

    interface Presenter extends BasePresenter {

        void navigateToShare();
    }

}
