package com.baijiahulian.live.ui.chat;

import com.baijiahulian.live.ui.base.BasePresenter;
import com.baijiahulian.live.ui.base.BaseView;

/**
 * Created by Shubo on 2017/3/4.
 */

interface MessageSendContract {

    interface View extends BaseView<Presenter> {
        void showMessageSuccess();

        void showEmojiPanel();

//        void showPicture(int position);
        void onPictureSend();
    }

    interface Presenter extends BasePresenter {
        void sendMessage(String message);

        void sendEmoji(String emoji);

        void sendPicture(String path);

        void chooseEmoji();

        boolean isTeacherOrAssistant();
    }
}
