package com.baijiahulian.live.ui.activity;

import android.widget.FrameLayout;

import com.baijiahulian.live.ui.ppt.MyPPTFragment;
import com.baijiahulian.livecore.context.LPConstants;
import com.baijiahulian.livecore.context.LPError;
import com.baijiahulian.livecore.context.LiveRoom;
import com.baijiahulian.livecore.listener.OnPhoneRollCallListener;
import com.baijiahulian.livecore.models.LPJsonModel;
import com.baijiahulian.livecore.models.imodels.IMediaControlModel;
import com.baijiahulian.livecore.models.imodels.IMediaModel;

/**
 * Created by Shubo on 2017/2/15.
 */

public interface LiveRoomRouterListener {

    LiveRoom getLiveRoom();

    void setLiveRoom(LiveRoom liveRoom);

    void navigateToMain();

    void clearScreen();

    void unClearScreen();

    void navigateToMessageInput();

    void navigateToQuickSwitchPPT(int index, int maxIndex);

    void updateQuickSwitchPPTMaxIndex(int index);

    void notifyPageCurrent(int position);

    void navigateToPPTDrawing();

    LPConstants.LPPPTShowWay getPPTShowType();

    void setPPTShowType(LPConstants.LPPPTShowWay type);

    void navigateToUserList();

    void navigateToPPTWareHouse();

    void disableSpeakerMode();

    void enableSpeakerMode();

    void showMorePanel(int anchorX, int anchorY);

    void navigateToShare();

    void navigateToAnnouncement();

    void navigateToCloudRecord(boolean recordStatus);

    void navigateToHelp();

    void navigateToSetting();

    boolean isTeacherOrAssistant();

    void attachLocalVideo();

    void detachLocalVideo();

    boolean isPPTMax();

    void clearPPTAllShapes();

    void changeScreenOrientation();

    int getCurrentScreenOrientation();

    int getSysRotationSetting();

    //允许自由转屏
    void letScreenRotateItself();

    //不允许自由转屏
    void forbidScreenRotateItself();

    void showBigChatPic(String url);

    void sendImageMessage(String path);

    void showMessage(String message);

    void saveTeacherMediaStatus(IMediaModel model);

    void showSavePicDialog(byte[] bmpArray);

    void realSaveBmpToFile(byte[] bmpArray);

    void doReEnterRoom();

    void doHandleErrorNothing();

    void showError(LPError error);

    boolean canStudentDraw();

    boolean isCurrentUserTeacher();

    // 学生是否操作过老师视频
    boolean isVideoManipulated();

    void setVideoManipulated(boolean b);

    int getSpeakApplyStatus();

    void showMessageClassEnd();

    void showMessageClassStart();

    void showMessageForbidAllChat(boolean isOn);

    void showMessageTeacherOpenAudio();

    void showMessageTeacherOpenVideo();

    void showMessageTeacherOpenAV();

    void showMessageTeacherCloseAV();

    void showMessageTeacherCloseAudio();

    void showMessageTeacherCloseVideo();

    void showMessageTeacherEnterRoom();

    void showMessageTeacherExitRoom();

    boolean getVisibilityOfShareBtn();

    void changeBackgroundContainerSize(boolean isShrink);

    android.view.View removeFullScreenView();

    FrameLayout getBackgroundContainer();

    void setFullScreenView(android.view.View view);

    MyPPTFragment getPPTFragment();

    void showRollCallDlg(int time, OnPhoneRollCallListener.RollCall rollCallListener);

    void dismissRollCallDlg();

    /*小测v2*/
    void onQuizStartArrived(LPJsonModel jsonModel);

    void onQuizEndArrived(LPJsonModel jsonModel);

    void onQuizSolutionArrived(LPJsonModel jsonModel);

    void onQuizRes(LPJsonModel jsonModel);

    void dismissQuizDlg();

    boolean checkCameraPermission();

    void attachLocalAudio();

    void resizeFullScreenWaterMark(int height, int width);

    void notifyPPTResumeInSpeakers();

    void setIsStopPublish(boolean b);

    void notifyFullScreenPresenterStatusChange(String id, boolean isPresenter);

    void showForceSpeakDlg(IMediaControlModel iMediaControlModel);

    void showSpeakInviteDlg(int invite); //0 取消 1邀请

    LPConstants.LPRoomType getRoomType();

    void showHuiyinDebugPanel(); //弹出debug面板

    void showStreamDebugPanel();

    void showDebugBtn();

    void enableStudentSpeakMode();
}
