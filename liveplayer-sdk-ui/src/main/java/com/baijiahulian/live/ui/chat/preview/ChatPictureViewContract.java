package com.baijiahulian.live.ui.chat.preview;

import com.baijiahulian.live.ui.base.BasePresenter;
import com.baijiahulian.live.ui.base.BaseView;

/**
 * Created by wangkangfei on 17/5/13.
 */

public interface ChatPictureViewContract {

    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter {
        void showSaveDialog(byte[] bmpArray);
    }
}
