package com.baijiahulian.live.ui.pptleftmenu;

import com.baijiahulian.live.ui.base.BasePresenter;
import com.baijiahulian.live.ui.base.BaseView;

/**
 * Created by wangkangfei on 17/5/4.
 */

public interface PPTLeftContract {
    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter {
        void clearPPTAllShapes();
    }
}
