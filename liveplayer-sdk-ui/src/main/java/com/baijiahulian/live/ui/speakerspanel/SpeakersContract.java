package com.baijiahulian.live.ui.speakerspanel;

import com.baijiahulian.live.ui.base.BasePresenter;
import com.baijiahulian.live.ui.base.BaseView;
import com.baijiahulian.live.ui.ppt.MyPPTFragment;
import com.baijiahulian.livecore.launch.LPEnterRoomNative;
import com.baijiahulian.livecore.models.imodels.IMediaModel;
import com.baijiahulian.livecore.models.imodels.IUserModel;
import com.baijiahulian.livecore.wrapper.LPPlayer;
import com.baijiahulian.livecore.wrapper.LPRecorder;

/**
 * Created by Shubo on 2017/6/5.
 */

interface SpeakersContract {

    int VIEW_TYPE_APPLY = 0;
    int VIEW_TYPE_RECORD = 1;
    int VIEW_TYPE_SPEAKER = 2;
    int VIEW_TYPE_VIDEO_PLAY = 3;
    int VIEW_TYPE_PPT = 4;
    int VIEW_TYPE_PRESENTER = 5;

    String PPT_TAG = "PPT";
    String RECORD_TAG = "RECORD";

    interface View extends BaseView<Presenter> {
        void notifyItemChanged(int position);

        void notifyItemInserted(int position);

        void notifyItemDeleted(int position);

        android.view.View removeViewAt(int position);

        void notifyViewAdded(android.view.View view, int position);

        void showMaxVideoExceed();

        void notifyPresenterVideoSizeChange(int position, int height, int width);

        void notifyFullScreenPresenterStatusChange(int position, boolean b);

        void disableSpeakQueuePlaceholder();
    }

    interface Presenter extends BasePresenter {
        String getItem(int position);

        int getCount();

        void agreeSpeakApply(String userId);

        void disagreeSpeakApply(String userId);

        int getItemViewType(int position);

        int getItemViewType(String userId);

        IMediaModel getSpeakModel(String userId);

        LPRecorder getRecorder();

        LPPlayer getPlayer();

        IUserModel getApplyModel(int position);

        IMediaModel getSpeakModel(int position);

        void playVideo(String userId);

        void closeVideo(String tag);

        void closeSpeaking(String userId);

        boolean isTeacherOrAssistant();

        void changeBackgroundContainerSize(boolean isShrink);

        void setFullScreenTag(String tag);

        MyPPTFragment getPPTFragment();

        boolean isFullScreen(String tag);

        void switchCamera();

        void switchPrettyFilter();

        void clearScreen();

        boolean isAutoPlay();

        LPEnterRoomNative.LPWaterMark getWaterMark();

        boolean isStopPublish();

        void setIsStopPublish(boolean b);

        boolean isMultiClass();
    }
}
