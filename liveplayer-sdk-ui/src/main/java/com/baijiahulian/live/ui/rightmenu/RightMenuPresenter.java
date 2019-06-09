package com.baijiahulian.live.ui.rightmenu;

import android.util.Log;

import com.baijiahulian.live.ui.activity.LiveRoomRouterListener;
import com.baijiahulian.live.ui.utils.RxUtils;
import com.baijiahulian.livecore.context.LPConstants;
import com.baijiahulian.livecore.listener.OnSpeakApplyCountDownListener;
import com.baijiahulian.livecore.models.LPSpeakInviteModel;
import com.baijiahulian.livecore.models.imodels.IMediaControlModel;
import com.baijiahulian.livecore.utils.LPErrorPrintSubscriber;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.baijiahulian.live.ui.utils.Precondition.checkNotNull;

/**
 * Created by Shubo on 2017/2/15.
 */

public class RightMenuPresenter implements RightMenuContract.Presenter {

    private LiveRoomRouterListener liveRoomRouterListener;
    private RightMenuContract.View view;
    private LPConstants.LPUserType currentUserType;
    private Subscription subscriptionOfMediaControl, subscriptionOfSpeakApplyCounter,
            subscriptionOfClassEnd, subscriptionOfSpeakApplyResponse, subscriptionOfSpeakInvite,
            subscriptionOfClassStart;
    private int speakApplyStatus = RightMenuContract.STUDENT_SPEAK_APPLY_NONE;
    private boolean isDrawing = false;

    public RightMenuPresenter(RightMenuContract.View view) {
        this.view = view;
    }

    @Override
    public void visitOnlineUser() {
        liveRoomRouterListener.navigateToUserList();
    }

    @Override
    public void changeDrawing() {
        if (!isDrawing && !liveRoomRouterListener.canStudentDraw()) {
            view.showCantDraw();
            return;
        }
        if (!liveRoomRouterListener.isTeacherOrAssistant() && !liveRoomRouterListener.getLiveRoom().isClassStarted()) {
            view.showCantDrawCauseClassNotStart();
            return;
        }
        liveRoomRouterListener.navigateToPPTDrawing();
        isDrawing = !isDrawing;
        view.showDrawingStatus(isDrawing);
    }

    @Override
    public void managePPT() {
        if (currentUserType == LPConstants.LPUserType.Teacher
                || currentUserType == LPConstants.LPUserType.Assistant) {
            liveRoomRouterListener.navigateToPPTWareHouse();
        }
    }

    public int getSpeakApplyStatus() {
        return speakApplyStatus;
    }

    @Override
    public void speakApply() {
        checkNotNull(liveRoomRouterListener);

        if (!liveRoomRouterListener.getLiveRoom().isClassStarted()) {
            view.showHandUpError();
            return;
        }

        if (speakApplyStatus == RightMenuContract.STUDENT_SPEAK_APPLY_NONE) {
            if (liveRoomRouterListener.getLiveRoom().getForbidRaiseHandStatus()) {
                view.showHandUpForbid();
                return;
            }
//            liveRoomRouterListener.getLiveRoom().getSpeakQueueVM().requestSpeakApply(null);OnSpeakApplyCountDownListener
            liveRoomRouterListener.getLiveRoom().getSpeakQueueVM().requestSpeakApply(new OnSpeakApplyCountDownListener() {
                @Override
                public void onTimeOut() {
                }

                @Override
                public void onTimeCountDown(int i, int i1) {
                }
            });
            speakApplyStatus = RightMenuContract.STUDENT_SPEAK_APPLY_APPLYING;
            view.showWaitingTeacherAgree();
            subscriptionOfSpeakApplyCounter = Observable.interval(0, 100, TimeUnit.MILLISECONDS)
                    .take(300)
                    .map(new Func1<Long, Long>() {
                        @Override
                        public Long call(Long aLong) {
                            return 30000L - aLong.intValue() * 100;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            view.showSpeakApplyCountDown(aLong.intValue());
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            // 倒计时结束，取消发言请求
                            speakApplyStatus = RightMenuContract.STUDENT_SPEAK_APPLY_NONE;
                            RxUtils.unSubscribe(subscriptionOfSpeakApplyCounter);
                            liveRoomRouterListener.getLiveRoom().getSpeakQueueVM().cancelSpeakApply();
                            view.showSpeakApplyCanceled();
                        }
                    });
        } else if (speakApplyStatus == RightMenuContract.STUDENT_SPEAK_APPLY_APPLYING) {
            // 取消发言请求
            speakApplyStatus = RightMenuContract.STUDENT_SPEAK_APPLY_NONE;
            RxUtils.unSubscribe(subscriptionOfSpeakApplyCounter);
            liveRoomRouterListener.getLiveRoom().getSpeakQueueVM().cancelSpeakApply();
            view.showSpeakApplyCanceled();
        } else if (speakApplyStatus == RightMenuContract.STUDENT_SPEAK_APPLY_SPEAKING) {
            // 取消发言
            speakApplyStatus = RightMenuContract.STUDENT_SPEAK_APPLY_NONE;
            liveRoomRouterListener.disableSpeakerMode();
            liveRoomRouterListener.getLiveRoom().getSpeakQueueVM()
                    .closeOtherSpeak(liveRoomRouterListener.getLiveRoom().getCurrentUser().getUserId());
            view.showSpeakApplyCanceled();
            if (isDrawing) {
                // 如果画笔打开 关闭画笔模式
                changeDrawing();
            }
        }
    }

    @Override
    public void changePPTDrawBtnStatus(boolean shouldShow) {
        if (shouldShow) {
            //老师或者助教或者已同意发言的学生可以使用ppt
            if (currentUserType == LPConstants.LPUserType.Teacher
                    || currentUserType == LPConstants.LPUserType.Assistant
                    || speakApplyStatus == RightMenuContract.STUDENT_SPEAK_APPLY_SPEAKING) {
                view.showPPTDrawBtn();
            }
        } else {
            view.hidePPTDrawBtn();
        }
    }

    @Override
    public void onSpeakInvite(int confirm) {
        liveRoomRouterListener.getLiveRoom().sendSpeakInvite(confirm);
        if (confirm == 1) {
            //接受
            speakApplyStatus = RightMenuContract.STUDENT_SPEAK_APPLY_SPEAKING;
            liveRoomRouterListener.getLiveRoom().getRecorder().publish();
            liveRoomRouterListener.attachLocalAudio();
            if (liveRoomRouterListener.getLiveRoom().getAutoOpenCameraStatus()) {
                Observable.timer(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new LPErrorPrintSubscriber<Long>() {
                            @Override
                            public void call(Long aLong) {
                                liveRoomRouterListener.attachLocalVideo();
                            }
                        });
            }
            view.showForceSpeak();
            liveRoomRouterListener.enableSpeakerMode();
        }
    }

    @Override
    public void setRouter(LiveRoomRouterListener liveRoomRouterListener) {
        this.liveRoomRouterListener = liveRoomRouterListener;
    }

    @Override
    public void subscribe() {
        checkNotNull(liveRoomRouterListener);
        currentUserType = liveRoomRouterListener.getLiveRoom().getCurrentUser().getType();

        if (liveRoomRouterListener.isTeacherOrAssistant()) {
            view.showTeacherRightMenu();
        } else {
            view.showStudentRightMenu();
            if (liveRoomRouterListener.getLiveRoom().getPartnerConfig().liveHideUserList == 1) {
                view.hideUserList();
            }
        }

        if (!liveRoomRouterListener.isTeacherOrAssistant()) {
            // 学生
            subscriptionOfMediaControl = liveRoomRouterListener.getLiveRoom().getSpeakQueueVM()
                    .getObservableOfMediaControl()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new LPErrorPrintSubscriber<IMediaControlModel>() {
                        @Override
                        public void call(IMediaControlModel iMediaControlModel) {
                            if (iMediaControlModel.isApplyAgreed()) {
                                // 强制发言
                                if (speakApplyStatus == RightMenuContract.STUDENT_SPEAK_APPLY_SPEAKING) {
                                    //已经在发言了
                                    if (iMediaControlModel.isAudioOn() && !liveRoomRouterListener.getLiveRoom().getRecorder().isAudioAttached()) {
                                        liveRoomRouterListener.getLiveRoom().getRecorder().attachAudio();
                                    } else if (!iMediaControlModel.isAudioOn() && liveRoomRouterListener.getLiveRoom().getRecorder().isAudioAttached()) {
                                        liveRoomRouterListener.getLiveRoom().getRecorder().detachAudio();
                                    }
                                } else {
                                    speakApplyStatus = RightMenuContract.STUDENT_SPEAK_APPLY_SPEAKING;
                                    liveRoomRouterListener.enableSpeakerMode();
                                    view.showForceSpeak();
                                    liveRoomRouterListener.showForceSpeakDlg(iMediaControlModel);
                                }
                            } else {
                                // 结束发言模式
                                RxUtils.unSubscribe(subscriptionOfSpeakApplyCounter);
                                speakApplyStatus = RightMenuContract.STUDENT_SPEAK_APPLY_NONE;
                                liveRoomRouterListener.disableSpeakerMode();
                                if (isDrawing) {
                                    // 如果画笔打开 关闭画笔模式
                                    changeDrawing();
                                }
                                if (!iMediaControlModel.getSenderUserId().equals(liveRoomRouterListener.getLiveRoom().getCurrentUser().getUserId())) {
                                    // 不是自己结束发言的
                                    view.showSpeakClosedByTeacher();
                                }
                            }
                        }
                    });

            subscriptionOfSpeakApplyResponse = liveRoomRouterListener.getLiveRoom().getSpeakQueueVM().getObservableOfSpeakResponse()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new LPErrorPrintSubscriber<IMediaControlModel>() {
                        @Override
                        public void call(IMediaControlModel iMediaControlModel) {
                            if (!iMediaControlModel.getUser().getUserId()
                                    .equals(liveRoomRouterListener.getLiveRoom().getCurrentUser().getUserId()))
                                return;
                            // 请求发言的用户自己
                            if (iMediaControlModel.isApplyAgreed()) {
                                // 进入发言模式
                                RxUtils.unSubscribe(subscriptionOfSpeakApplyCounter);
                                speakApplyStatus = RightMenuContract.STUDENT_SPEAK_APPLY_SPEAKING;
                                liveRoomRouterListener.enableSpeakerMode();
                                view.showSpeakApplyAgreed();
                                liveRoomRouterListener.getLiveRoom().getRecorder().publish();
                                liveRoomRouterListener.attachLocalAudio();
                                if (liveRoomRouterListener.getLiveRoom().getAutoOpenCameraStatus()) {
                                    Observable.timer(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new LPErrorPrintSubscriber<Long>() {
                                                @Override
                                                public void call(Long aLong) {
                                                    liveRoomRouterListener.attachLocalVideo();
                                                }
                                            });
                                }
                            } else {
                                RxUtils.unSubscribe(subscriptionOfSpeakApplyCounter);
                                speakApplyStatus = RightMenuContract.STUDENT_SPEAK_APPLY_NONE;
                                if (!iMediaControlModel.getSenderUserId().equals(liveRoomRouterListener.getLiveRoom().getCurrentUser().getUserId())) {
                                    // 不是自己结束发言的
                                    view.showSpeakApplyDisagreed();
                                }
                            }
                        }
                    });
        } else if (liveRoomRouterListener.getLiveRoom().getCurrentUser().getType() == LPConstants.LPUserType.Assistant) {
            // 助教
            subscriptionOfMediaControl = liveRoomRouterListener.getLiveRoom().getSpeakQueueVM()
                    .getObservableOfMediaControl()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new LPErrorPrintSubscriber<IMediaControlModel>() {
                        @Override
                        public void call(IMediaControlModel iMediaControlModel) {
                            if (!iMediaControlModel.isApplyAgreed()) {
                                // 结束发言模式
                                liveRoomRouterListener.disableSpeakerMode();
                                if (isDrawing) changeDrawing();

                            }
                        }
                    });
        }

        subscriptionOfClassEnd = liveRoomRouterListener.getLiveRoom().getObservableOfClassEnd()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (speakApplyStatus == RightMenuContract.STUDENT_SPEAK_APPLY_APPLYING) {
                            // 取消发言请求
                            speakApplyStatus = RightMenuContract.STUDENT_SPEAK_APPLY_NONE;
                            RxUtils.unSubscribe(subscriptionOfSpeakApplyCounter);
                            liveRoomRouterListener.getLiveRoom().getSpeakQueueVM().cancelSpeakApply();
                            view.showSpeakApplyCanceled();
                        } else if (speakApplyStatus == RightMenuContract.STUDENT_SPEAK_APPLY_SPEAKING) {
                            // 取消发言
                            speakApplyStatus = RightMenuContract.STUDENT_SPEAK_APPLY_NONE;
                            liveRoomRouterListener.disableSpeakerMode();
                            liveRoomRouterListener.getLiveRoom().getSpeakQueueVM()
                                    .closeOtherSpeak(liveRoomRouterListener.getLiveRoom().getCurrentUser().getUserId());
                            view.showSpeakApplyCanceled();
                            if (isDrawing) {
                                // 如果画笔打开 关闭画笔模式
                                changeDrawing();
                            }
                        }
                    }
                });

        subscriptionOfClassStart = liveRoomRouterListener.getLiveRoom().getObservableOfClassStart()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (liveRoomRouterListener.getLiveRoom().getCurrentUser().getType() == LPConstants.LPUserType.Student &&
                                liveRoomRouterListener.getLiveRoom().getRoomType() != LPConstants.LPRoomType.Multi) {
                            speakApplyStatus = RightMenuContract.STUDENT_SPEAK_APPLY_SPEAKING;
                        }
                    }
                });

        if (liveRoomRouterListener.getLiveRoom().getCurrentUser().getType() == LPConstants.LPUserType.Student &&
                liveRoomRouterListener.getLiveRoom().getRoomType() != LPConstants.LPRoomType.Multi) {
            view.showAutoSpeak();
            speakApplyStatus = RightMenuContract.STUDENT_SPEAK_APPLY_SPEAKING;
        }

        //邀请发言
        subscriptionOfSpeakInvite = liveRoomRouterListener.getLiveRoom().getObservableOfSpeakInvite()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<LPSpeakInviteModel>() {
                    @Override
                    public void call(LPSpeakInviteModel lpSpeakInviteModel) {
                        if (liveRoomRouterListener.getLiveRoom().getCurrentUser().getUserId().equals(lpSpeakInviteModel.to)) {
                            liveRoomRouterListener.showSpeakInviteDlg(lpSpeakInviteModel.invite);
                        }
                    }
                });
    }

    @Override
    public void unSubscribe() {
        RxUtils.unSubscribe(subscriptionOfMediaControl);
        RxUtils.unSubscribe(subscriptionOfSpeakApplyCounter);
        RxUtils.unSubscribe(subscriptionOfSpeakApplyResponse);
        RxUtils.unSubscribe(subscriptionOfClassEnd);
        RxUtils.unSubscribe(subscriptionOfSpeakInvite);
        RxUtils.unSubscribe(subscriptionOfClassStart);
    }

    @Override
    public void destroy() {
        liveRoomRouterListener = null;
        view = null;
    }
}
