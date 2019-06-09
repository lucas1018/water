package com.baijiahulian.live.ui.activity;

import android.text.TextUtils;

import com.baijiahulian.live.ui.base.BasePresenter;
import com.baijiahulian.live.ui.utils.JsonObjectUtil;
import com.baijiahulian.live.ui.utils.RxUtils;
import com.baijiahulian.livecore.context.LPConstants;
import com.baijiahulian.livecore.context.LPError;
import com.baijiahulian.livecore.listener.OnPhoneRollCallListener;
import com.baijiahulian.livecore.models.LPJsonModel;
import com.baijiahulian.livecore.models.imodels.IAnnouncementModel;
import com.baijiahulian.livecore.models.imodels.IMediaModel;
import com.baijiahulian.livecore.models.imodels.IUserInModel;
import com.baijiahulian.livecore.models.responsedebug.LPResRoomDebugModel;
import com.baijiahulian.livecore.utils.LPErrorPrintSubscriber;
import com.baijiahulian.livecore.utils.LPRxUtils;

import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by Shubo on 2017/5/11.
 */

public class GlobalPresenter implements BasePresenter {

    private LiveRoomRouterListener routerListener;

    private boolean teacherVideoOn, teacherAudioOn;

    private Subscription subscriptionOfClassStart, subscriptionOfClassEnd, subscriptionOfForbidAllStatus,
            subscriptionOfTeacherMedia, subscriptionOfUserIn, subscriptionOfUserOut, subscriptionOfQuizStart,
            subscriptionOfQuizRes, subscriptionOfQuizEnd, subscriptionOfQuizSolution, subscriptionOfDebug,
            subscriptionOfAnnouncement;

    private boolean isVideoManipulated = false;

    private int counter = 0;

    private boolean isForbidChatChanged = false;

    @Override
    public void setRouter(LiveRoomRouterListener liveRoomRouterListener) {
        routerListener = liveRoomRouterListener;
    }

    @Override
    public void subscribe() {
        subscriptionOfClassStart = routerListener.getLiveRoom().getObservableOfClassStart()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        routerListener.showMessageClassStart();
                        routerListener.enableStudentSpeakMode();
                    }
                });
        subscriptionOfClassEnd = routerListener.getLiveRoom().getObservableOfClassEnd()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        routerListener.showMessageClassEnd();
                        teacherVideoOn = false;
                        teacherAudioOn = false;
                    }
                });
        subscriptionOfForbidAllStatus = routerListener.getLiveRoom().getObservableOfForbidAllChatStatus()
                .observeOn(AndroidSchedulers.mainThread())
//                .skip(1) // 排除进教室第一次回调
                .subscribe(new LPErrorPrintSubscriber<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (counter == 0) {
                            isForbidChatChanged = aBoolean;
                            counter++;
                            return;
                        }
                        if (isForbidChatChanged == aBoolean) return;
                        isForbidChatChanged = aBoolean;
                        routerListener.showMessageForbidAllChat(aBoolean);
                    }
                });

        if (!routerListener.isCurrentUserTeacher()) {

            // 学生监听老师音视频状态
            subscriptionOfTeacherMedia = routerListener.getLiveRoom().getSpeakQueueVM().getObservableOfMediaNew()
                    .mergeWith(routerListener.getLiveRoom().getSpeakQueueVM().getObservableOfMediaChange())
                    .mergeWith(routerListener.getLiveRoom().getSpeakQueueVM().getObservableOfMediaClose())
                    .filter(new Func1<IMediaModel, Boolean>() {
                        @Override
                        public Boolean call(IMediaModel iMediaModel) {
                            return !routerListener.isTeacherOrAssistant() && iMediaModel.getUser().getType() == LPConstants.LPUserType.Teacher;
                        }
                    })
                    .throttleLast(500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new LPErrorPrintSubscriber<IMediaModel>() {
                        @Override
                        public void call(IMediaModel iMediaModel) {
                            if (!routerListener.getLiveRoom().isClassStarted()) {
                                return;
                            }
                            if (iMediaModel.isVideoOn() && iMediaModel.isAudioOn()) {
                                if (!teacherVideoOn || !teacherAudioOn) {
                                    routerListener.showMessageTeacherOpenAV();
                                }
                            } else if (iMediaModel.isVideoOn()) {
                                if (teacherAudioOn && teacherVideoOn) {
                                    routerListener.showMessageTeacherCloseAudio();
                                } else if (!teacherVideoOn) {
                                    routerListener.showMessageTeacherOpenVideo();
                                }
                            } else if (iMediaModel.isAudioOn()) {
                                if (teacherAudioOn && teacherVideoOn) {
                                    routerListener.showMessageTeacherCloseVideo();
                                } else if (!teacherAudioOn) {
                                    routerListener.showMessageTeacherOpenAudio();
                                }
                            } else {
                                routerListener.showMessageTeacherCloseAV();
                            }
                            setTeacherMedia(iMediaModel);
                        }
                    });

            subscriptionOfUserIn = routerListener.getLiveRoom().getObservableOfUserIn().observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new LPErrorPrintSubscriber<IUserInModel>() {
                        @Override
                        public void call(IUserInModel iUserInModel) {
                            if (iUserInModel.getUser().getType() == LPConstants.LPUserType.Teacher) {
                                routerListener.showMessageTeacherEnterRoom();
                            }
                        }
                    });

            subscriptionOfUserOut = routerListener.getLiveRoom().getObservableOfUserOut().observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new LPErrorPrintSubscriber<String>() {
                        @Override
                        public void call(String s) {
                            if (TextUtils.isEmpty(s)) return;
                            if (routerListener.getLiveRoom().getTeacherUser() == null) return;
                            if (s.equals(routerListener.getLiveRoom().getTeacherUser().getUserId())) {
                                routerListener.showMessageTeacherExitRoom();
                            }
                        }
                    });
            //点名
            routerListener.getLiveRoom().setOnRollCallListener(new OnPhoneRollCallListener() {
                @Override
                public void onRollCall(int time, RollCall rollCallListener) {
                    routerListener.showRollCallDlg(time, rollCallListener);
                }

                @Override
                public void onRollCallTimeOut() {
                    routerListener.dismissRollCallDlg();
                }
            });

            //开始小测
            subscriptionOfQuizStart = routerListener.getLiveRoom().getQuizVM().getObservableOfQuizStart()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new LPErrorPrintSubscriber<LPJsonModel>() {
                        @Override
                        public void call(LPJsonModel jsonModel) {
                            if (!routerListener.isTeacherOrAssistant()) {
                                routerListener.onQuizStartArrived(jsonModel);
                            }
                        }
                    });
            //中途打开
            subscriptionOfQuizRes = routerListener.getLiveRoom().getQuizVM().getObservableOfQuizRes()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new LPErrorPrintSubscriber<LPJsonModel>() {
                        @Override
                        public void call(LPJsonModel jsonModel) {
                            if (!routerListener.isTeacherOrAssistant()) {
                                if (jsonModel != null && jsonModel.data != null) {
                                    String quizId = JsonObjectUtil.getAsString(jsonModel.data, "quiz_id");
                                    boolean solutionStatus = false;
                                    if (!jsonModel.data.has("solution")) {
                                        //没有solution
                                        solutionStatus = true;
                                    } else if (jsonModel.data.getAsJsonObject("solution").entrySet().isEmpty()) {
                                        //"solution":{}
                                        solutionStatus = true;
                                    } else if (jsonModel.data.getAsJsonObject("solution").isJsonNull()) {
                                        //"solution":"null"
                                        solutionStatus = true;
                                    }
                                    boolean endFlag = jsonModel.data.get("end_flag").getAsInt() == 1;
                                    //quizid非空、solution是空、没有结束答题 才弹窗
                                    if (!TextUtils.isEmpty(quizId) && solutionStatus && !endFlag) {
                                        routerListener.onQuizRes(jsonModel);
                                    }
                                }

                            }
                        }
                    });
            //结束，只转发h5
            subscriptionOfQuizEnd = routerListener.getLiveRoom().getQuizVM().getObservableOfQuizEnd()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new LPErrorPrintSubscriber<LPJsonModel>() {
                        @Override
                        public void call(LPJsonModel jsonModel) {
                            if (!routerListener.isTeacherOrAssistant()) {
                                routerListener.onQuizEndArrived(jsonModel);
                            }
                        }
                    });

            //发答案啦
            subscriptionOfQuizSolution = routerListener.getLiveRoom().getQuizVM().getObservableOfQuizSolution()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new LPErrorPrintSubscriber<LPJsonModel>() {
                        @Override
                        public void call(LPJsonModel jsonModel) {
                            if (!routerListener.isTeacherOrAssistant()) {
                                routerListener.onQuizSolutionArrived(jsonModel);
                            }
                        }
                    });
            //debug信息
            subscriptionOfDebug = routerListener.getLiveRoom().getObservableOfDebug()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new LPErrorPrintSubscriber<LPResRoomDebugModel>() {
                        @Override
                        public void call(LPResRoomDebugModel lpResRoomDebugModel) {
                            if (lpResRoomDebugModel != null && lpResRoomDebugModel.data != null) {
                                String commandType = "";
                                if (JsonObjectUtil.isJsonNull(lpResRoomDebugModel.data, "command_type")) {
                                    return;
                                }
                                commandType = lpResRoomDebugModel.data.get("command_type").getAsString();
                                if ("logout".equals(commandType)) {
                                    routerListener.showError(LPError.getNewError(LPError.CODE_ERROR_LOGIN_KICK_OUT, "您已被踢出房间"));
                                }
                            }
                        }
                    });
        }
        if(!routerListener.isTeacherOrAssistant()){
            // 公告变了
            observeAnnouncementChange();
            routerListener.getLiveRoom().requestAnnouncement();
        }
    }

    public void observeAnnouncementChange() {
        if(routerListener.isCurrentUserTeacher()) return;
        subscriptionOfAnnouncement = routerListener.getLiveRoom().getObservableOfAnnouncementChange()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<IAnnouncementModel>() {
                    @Override
                    public void call(IAnnouncementModel iAnnouncementModel) {
                        if (!TextUtils.isEmpty(iAnnouncementModel.getLink()) ||
                                !TextUtils.isEmpty(iAnnouncementModel.getContent())) {
                            routerListener.navigateToAnnouncement();
                        }
                    }
                });
    }

    public void unObserveAnnouncementChange() {
        LPRxUtils.unSubscribe(subscriptionOfAnnouncement);
    }

    void setTeacherMedia(IMediaModel media) {
        teacherVideoOn = media.isVideoOn();
        teacherAudioOn = media.isAudioOn();
    }

    public boolean isVideoManipulated() {
        return isVideoManipulated;
    }

    public void setVideoManipulated(boolean videoManipulated) {
        isVideoManipulated = videoManipulated;
    }

    @Override
    public void unSubscribe() {
        RxUtils.unSubscribe(subscriptionOfClassStart);
        RxUtils.unSubscribe(subscriptionOfClassEnd);
        RxUtils.unSubscribe(subscriptionOfForbidAllStatus);
        RxUtils.unSubscribe(subscriptionOfTeacherMedia);
        RxUtils.unSubscribe(subscriptionOfUserIn);
        RxUtils.unSubscribe(subscriptionOfUserOut);
        RxUtils.unSubscribe(subscriptionOfQuizStart);
        RxUtils.unSubscribe(subscriptionOfQuizRes);
        RxUtils.unSubscribe(subscriptionOfQuizEnd);
        RxUtils.unSubscribe(subscriptionOfQuizSolution);
        RxUtils.unSubscribe(subscriptionOfDebug);
        RxUtils.unSubscribe(subscriptionOfAnnouncement);
    }

    @Override
    public void destroy() {
        unSubscribe();
        routerListener = null;
    }
}
