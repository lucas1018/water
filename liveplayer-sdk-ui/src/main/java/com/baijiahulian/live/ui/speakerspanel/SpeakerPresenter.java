package com.baijiahulian.live.ui.speakerspanel;

import android.text.TextUtils;
import android.view.View;

import com.baijiahulian.live.ui.activity.LiveRoomRouterListener;
import com.baijiahulian.live.ui.ppt.MyPPTFragment;
import com.baijiahulian.live.ui.utils.RxUtils;
import com.baijiahulian.livecore.context.LPConstants;
import com.baijiahulian.livecore.launch.LPEnterRoomNative;
import com.baijiahulian.livecore.models.LPMediaModel;
import com.baijiahulian.livecore.models.LPUserModel;
import com.baijiahulian.livecore.models.LPVideoSizeModel;
import com.baijiahulian.livecore.models.imodels.IMediaControlModel;
import com.baijiahulian.livecore.models.imodels.IMediaModel;
import com.baijiahulian.livecore.models.imodels.IUserInModel;
import com.baijiahulian.livecore.models.imodels.IUserModel;
import com.baijiahulian.livecore.utils.LPBackPressureBufferedSubscriber;
import com.baijiahulian.livecore.utils.LPErrorPrintSubscriber;
import com.baijiahulian.livecore.utils.LPLogger;
import com.baijiahulian.livecore.utils.LPSubscribeObjectWithLastValue;
import com.baijiahulian.livecore.wrapper.LPPlayer;
import com.baijiahulian.livecore.wrapper.LPRecorder;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

import static com.baijiahulian.live.ui.speakerspanel.SpeakersContract.PPT_TAG;
import static com.baijiahulian.live.ui.speakerspanel.SpeakersContract.RECORD_TAG;
import static com.baijiahulian.live.ui.speakerspanel.SpeakersContract.VIEW_TYPE_APPLY;
import static com.baijiahulian.live.ui.speakerspanel.SpeakersContract.VIEW_TYPE_PPT;
import static com.baijiahulian.live.ui.speakerspanel.SpeakersContract.VIEW_TYPE_PRESENTER;
import static com.baijiahulian.live.ui.speakerspanel.SpeakersContract.VIEW_TYPE_RECORD;
import static com.baijiahulian.live.ui.speakerspanel.SpeakersContract.VIEW_TYPE_SPEAKER;
import static com.baijiahulian.live.ui.speakerspanel.SpeakersContract.VIEW_TYPE_VIDEO_PLAY;
import static com.baijiahulian.live.ui.utils.Precondition.checkNotNull;

/**
 * 发言者列表遵从{[PPT]---[主讲人视频|头像]---[自己视频]---[其他人视频]---[其他发言用户音频]---[请求发言用户]}的顺序
 * 如果全屏或没有对应的项目则不在此列表中
 * 如果老师不是主讲人并切没有被全屏则在其他人视频里
 * currentFullScreenTag 为当前全屏的tag VideoView为UserId
 * Created by Shubo on 2017/6/5.
 */

public class SpeakerPresenter implements SpeakersContract.Presenter {

    private final boolean disableSpeakQueuePlaceholder;
    private LiveRoomRouterListener routerListener;
    private SpeakersContract.View view;
    private LPSubscribeObjectWithLastValue<String> fullScreenKVO;
    private static final int MAX_VIDEO_COUNT = 6;
    private boolean autoPlayPresenterVideo = true;
    private boolean isEmptyPPT = false;

    // 显示视频或发言用户的分段列表 item为相应的tag或者userId;
    private List<String> displayList;

    // 分段列表各个段位的下标 维护需谨慎
    private int _displayPresenterSection = -1; //主讲人分段
    private int _displayRecordSection = -1;    //自己视频
    private int _displayVideoSection = -1;     //视频发言分段
    private int _displaySpeakerSection = -1;   //未开视频发言分段
    private int _displayApplySection = -1;     //请求发言用户分段
    private IUserModel tempUserIn;

    private Subscription subscriptionOfMediaNew, subscriptionOfMediaChange, subscriptionOfMediaClose,
            subscriptionSpeakApply, subscriptionSpeakResponse, subscriptionOfActiveUser, subscriptionOfFullScreen,
            subscriptionOfUserOut, subscriptionOfPresenterChange, subscriptionOfShareDesktopAndPlayMedia,
            subscriptionOfVideoSizeChange, subscriptionOfUserIn;

    public SpeakerPresenter(SpeakersContract.View view, boolean disableSpeakQueuePlaceholder) {
        this.view = view;
        this.disableSpeakQueuePlaceholder = disableSpeakQueuePlaceholder;
        fullScreenKVO = new LPSubscribeObjectWithLastValue<>(PPT_TAG);
    }

    @Override
    public void setRouter(LiveRoomRouterListener liveRoomRouterListener) {
        routerListener = liveRoomRouterListener;
    }

    private void initView() {
        if (disableSpeakQueuePlaceholder)
            view.disableSpeakQueuePlaceholder();
        displayList = new ArrayList<>();
        if (!fullScreenKVO.getParameter().equals(PPT_TAG)) {
            displayList.add(PPT_TAG);
        }
        _displayPresenterSection = displayList.size();
        if (routerListener.getLiveRoom().getPresenterUser() != null) {
            displayList.add(routerListener.getLiveRoom().getPresenterUser().getUserId());
        }
        _displayRecordSection = displayList.size();
        if (routerListener.getLiveRoom().getRecorder().isVideoAttached()) {
            displayList.add(RECORD_TAG);
        }
        _displayVideoSection = displayList.size();
        _displaySpeakerSection = displayList.size();
        for (IMediaModel model : routerListener.getLiveRoom().getSpeakQueueVM().getSpeakQueueList()) {
            if (routerListener.getLiveRoom().getPresenterUser() == null || !model.getUser().getUserId().equals(routerListener.getLiveRoom().getPresenterUser().getUserId())) {
                // exclude presenter
                displayList.add(model.getUser().getUserId());
            }
        }
        _displayApplySection = displayList.size();
        for (IUserModel model : routerListener.getLiveRoom().getSpeakQueueVM().getApplyList()) {
            displayList.add(model.getUserId());
        }
        // init view
        for (int i = 0; i < displayList.size(); i++) {
            view.notifyItemInserted(i);
            if (getItemViewType(i) != VIEW_TYPE_SPEAKER)
                continue;
            if (getSpeakModel(i).isVideoOn())
                playVideo(getSpeakModel(i).getUser().getUserId());
        }
    }

    @Override
    public void subscribe() {

        LPErrorPrintSubscriber<List<IMediaModel>> activeUserSubscriber = new LPErrorPrintSubscriber<List<IMediaModel>>() {
            @Override
            public void call(List<IMediaModel> iMediaModels) {
                initView();
            }
        };
        subscriptionOfActiveUser = routerListener.getLiveRoom().getSpeakQueueVM().getObservableOfActiveUsers().observeOn(AndroidSchedulers.mainThread()).subscribe(activeUserSubscriber);
        routerListener.getLiveRoom().getSpeakQueueVM().requestActiveUsers();

        subscriptionOfMediaNew = routerListener.getLiveRoom().getSpeakQueueVM().getObservableOfMediaNew()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<IMediaModel>() {
                    @Override
                    public void call(IMediaModel iMediaModel) {
                        if (!iMediaModel.isVideoOn() && !iMediaModel.isAudioOn())
                            return; // 兼容iOS下课发media_publish
                        if (routerListener.getLiveRoom().getPresenterUser() != null
                                && iMediaModel.getUser().getUserId().equals(routerListener.getLiveRoom().getPresenterUser().getUserId())) {
                            if (_displayPresenterSection < displayList.size() &&
                                    displayList.get(_displayPresenterSection).equals(iMediaModel.getUser().getUserId())) {
                                view.notifyItemChanged(_displayPresenterSection);
                            } else {
                                displayList.add(_displayPresenterSection, iMediaModel.getUser().getUserId());
                                _displayRecordSection++;
                                _displayVideoSection++;
                                _displaySpeakerSection++;
                                _displayApplySection++;
                                view.notifyItemInserted(_displayPresenterSection);
                            }
                            printSections();
                            return;
                        }
                        displayList.add(_displayApplySection, iMediaModel.getUser().getUserId());
                        _displayApplySection++;
                        view.notifyItemInserted(_displayApplySection - 1);
                        if (iMediaModel.isVideoOn())
                            playVideo(iMediaModel.getUser().getUserId());
                        printSections();
                    }
                });

        subscriptionOfMediaChange = routerListener.getLiveRoom().getSpeakQueueVM().getObservableOfMediaChange()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<IMediaModel>() {
                    @Override
                    public void call(IMediaModel iMediaModel) {
                        if (fullScreenKVO.getParameter().equals(iMediaModel.getUser().getUserId())) {
                            // full screen user
                            if (!iMediaModel.isVideoOn()) {
                                fullScreenKVO.setParameter(null);
                                if (routerListener.getLiveRoom().getPresenterUser() != null
                                        && iMediaModel.getUser().getUserId().equals(routerListener.getLiveRoom().getPresenterUser().getUserId())) {
                                    _displayRecordSection++;
                                    _displayVideoSection++;
                                    _displaySpeakerSection++;
                                    _displayApplySection++;
                                    displayList.add(_displayPresenterSection, iMediaModel.getUser().getUserId());
                                    view.notifyItemInserted(_displayPresenterSection);
                                } else {
                                    routerListener.getLiveRoom().getPlayer().replay(iMediaModel.getUser().getUserId());
                                    _displayApplySection++;
                                    displayList.add(_displayApplySection - 1, iMediaModel.getUser().getUserId());
                                    view.notifyItemInserted(_displayApplySection - 1);
                                }
                            }else{
                                routerListener.getLiveRoom().getPlayer().replay(iMediaModel.getUser().getUserId());
                            }
                            printSections();
                            return;
                        }
                        int position = displayList.indexOf(iMediaModel.getUser().getUserId());
                        if (position == -1) { // 未在speaker列表
                            return;
                        }
                        if (position < _displayRecordSection) {
                            view.notifyItemChanged(position);
                            printSections();
                            return;
                        }
                        if (position < _displayVideoSection) {
                            throw new RuntimeException("position < _displayVideoSection");
                        } else if (position < _displaySpeakerSection) { // 视频打开用户
                            if (!iMediaModel.isVideoOn()) { // 通知视频关闭
                                view.notifyItemDeleted(position);
                                // core 处理了
//                                routerListener.getLiveRoom().getPlayer().playAVClose(getItem(position));
//                                routerListener.getLiveRoom().getPlayer().playAudio(getItem(position));
                                String item = displayList.remove(position);
                                displayList.add(_displayApplySection - 1, item);
                                _displaySpeakerSection--;
                                view.notifyItemInserted(_displayApplySection - 1);
                            }else{ // 重播
                                view.notifyItemChanged(position);
                            }
                        } else if (position < _displayApplySection) { // 视频未打开用户
                            view.notifyItemChanged(position);
                            if (iMediaModel.isVideoOn())
                                playVideo(iMediaModel.getUser().getUserId());
                        } else {
                            throw new RuntimeException("position > _displayApplySection");
                        }
                        printSections();
                    }
                });

        subscriptionOfMediaClose = routerListener.getLiveRoom().getSpeakQueueVM().getObservableOfMediaClose()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<IMediaModel>() {
                    @Override
                    public void call(IMediaModel iMediaModel) {
                        if (iMediaModel.getUser().getUserId().equals(fullScreenKVO.getParameter())) {
                            // full screen user
                            fullScreenKVO.setParameter(null);
                            if (routerListener.getLiveRoom().getPresenterUser() != null
                                    && iMediaModel.getUser().getUserId().equals(routerListener.getLiveRoom().getPresenterUser().getUserId())) {
                                _displayRecordSection++;
                                _displayVideoSection++;
                                _displaySpeakerSection++;
                                _displayApplySection++;
                                displayList.add(_displayPresenterSection, iMediaModel.getUser().getUserId());
                                view.notifyItemInserted(_displayPresenterSection);
                            }
                            printSections();
                            return;
                        }
                        int position = displayList.indexOf(iMediaModel.getUser().getUserId());
                        if (position == -1)
                            return;
                        if (position < _displayPresenterSection) {
                            throw new RuntimeException("position < _displayPresenterSection");
                        } else if (position < _displayVideoSection) {
                            if (routerListener.getLiveRoom().getPresenterUser() == null) {
                                view.notifyItemDeleted(position);
                                displayList.remove(position);
                                _displayVideoSection--;
                                _displayRecordSection--;
                                _displaySpeakerSection--;
                                _displayApplySection--;
                            } else {
                                view.notifyItemChanged(position);
                            }
                        } else if (position < _displaySpeakerSection) { // 视频打开用户
                            view.notifyItemDeleted(position);
                            displayList.remove(position);
                            _displaySpeakerSection--;
                            _displayApplySection--;
                        } else if (position < _displayApplySection) { // 视频未打开用户
                            view.notifyItemDeleted(position);
                            displayList.remove(position);
                            _displayApplySection--;
                        } else {
//                            throw new RuntimeException("position > _displayApplySection");
                        }
                        printSections();
                    }
                });

        subscriptionOfUserIn = routerListener.getLiveRoom().getObservableOfUserIn().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<IUserInModel>() {
                    @Override
                    public void call(IUserInModel iUserInModel) {
                        tempUserIn = iUserInModel.getUser();
                    }
                });

        subscriptionOfPresenterChange = routerListener.getLiveRoom().getSpeakQueueVM().getObservableOfPresenterChange()
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return !routerListener.isTeacherOrAssistant() && _displayPresenterSection != -1;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<String>() {
                    @Override
                    public void call(String newPresenter) {
                        if (TextUtils.isEmpty(newPresenter)) {
                            // presenter is null
                            return;
                        }

                        if (!PPT_TAG.equals(fullScreenKVO.getParameter())) {
                            View ppt = view.removeViewAt(displayList.indexOf(PPT_TAG));
                            View fullScreenView = routerListener.removeFullScreenView();
                            String fullId = fullScreenKVO.getParameter();
                            displayList.remove(PPT_TAG);
                            _displaySpeakerSection--;
                            _displayPresenterSection--;
                            _displayVideoSection--;
                            _displayRecordSection--;
                            _displayApplySection--;
//                            getPlayer().playAVClose(fullId);
                            if (_displayPresenterSection < _displayRecordSection) {
                                // 旧主讲在列表，全屏换下来到video section
                                displayList.add(_displaySpeakerSection, fullId);
                                _displaySpeakerSection++;
                                _displayApplySection++;
                                view.notifyViewAdded(fullScreenView, _displaySpeakerSection - 1);
                            } else {
                                // 旧主讲在全屏，换下来到presenter section
                                displayList.add(_displayPresenterSection, fullId);
                                _displayRecordSection++;
                                _displayVideoSection++;
                                _displaySpeakerSection++;
                                _displayApplySection++;
                                view.notifyViewAdded(fullScreenView, _displayPresenterSection);
                            }
                            routerListener.setFullScreenView(ppt);
                            fullScreenKVO.setParameterWithoutNotify(PPT_TAG);
                        }

                        if (_displayPresenterSection < _displayRecordSection) {
                            String lastPresenter = displayList.get(_displayPresenterSection);
                            if (TextUtils.isEmpty(lastPresenter)) {
                                return;
                            }
                            if (displayList.indexOf(newPresenter) < 0) {
                                displayList.set(_displayPresenterSection, newPresenter);
                                view.notifyItemChanged(_displayPresenterSection);
                                IMediaModel model = getSpeakModel(lastPresenter);
                                if (model != null && (model.isAudioOn() || model.isVideoOn())) {
                                    displayList.add(_displayApplySection, lastPresenter);
                                    _displayApplySection++;
                                    view.notifyItemInserted(_displayApplySection - 1);
                                }
                            } else {
                                // new presenter origin index
                                int switchIndex = displayList.indexOf(newPresenter);
                                getPlayer().playAVClose(newPresenter);
                                getPlayer().playAVClose(lastPresenter);
                                displayList.set(_displayPresenterSection, newPresenter);
                                view.notifyItemChanged(_displayPresenterSection);
                                view.notifyItemDeleted(switchIndex);
                                IMediaModel lastSpeakModel = getSpeakModel(lastPresenter);

                                boolean needToAdd;
                                if (lastSpeakModel == null) {
                                    needToAdd = false;
                                } else {
                                    needToAdd = lastSpeakModel.isVideoOn() || lastSpeakModel.isAudioOn();
                                }
                                displayList.remove(switchIndex);
                                if (needToAdd) {
                                    if (switchIndex < _displaySpeakerSection) {
                                        _displaySpeakerSection--;
                                        _displayApplySection--;
                                    } else if (switchIndex < _displayApplySection) {
                                        _displayApplySection--;
                                    }
                                    displayList.add(_displayApplySection, lastPresenter);
                                    if (lastSpeakModel.isAudioOn())
                                        getPlayer().playAudio(lastPresenter);
                                    _displayApplySection++;
                                    view.notifyItemInserted(_displayApplySection - 1);
                                } else {
                                    if (switchIndex < _displaySpeakerSection) {
                                        _displaySpeakerSection--;
                                        _displayApplySection--;
                                    } else if (switchIndex < _displayApplySection) {
                                        _displayApplySection--;
                                    }
                                }
                            }
                        } else {
                            displayList.add(_displayPresenterSection, newPresenter);
                            _displayRecordSection++;
                            _displaySpeakerSection++;
                            _displayVideoSection++;
                            _displayApplySection++;
                            view.notifyItemInserted(_displayPresenterSection);
                        }
                        printSections();
                    }
                });

        subscriptionOfVideoSizeChange = routerListener.getLiveRoom().getPlayer().getObservableOfVideoSizeChange()
                .onBackpressureBuffer()
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<LPVideoSizeModel, Boolean>() {
                    @Override
                    public Boolean call(LPVideoSizeModel lpVideoSizeModel) {
                        return routerListener.getLiveRoom().getPresenterUser() != null &&
                                lpVideoSizeModel.userId.equals(routerListener.getLiveRoom().getPresenterUser().getUserId());
                    }
                })
                .subscribe(new LPBackPressureBufferedSubscriber<LPVideoSizeModel>() {
                    @Override
                    public void call(LPVideoSizeModel lpVideoSizeModel) {
                        if (fullScreenKVO.getParameter().equals(lpVideoSizeModel.userId)) {
                            routerListener.resizeFullScreenWaterMark(lpVideoSizeModel.height, lpVideoSizeModel.width);
                        } else if (displayList.indexOf(lpVideoSizeModel.userId) != -1) {
                            view.notifyPresenterVideoSizeChange(displayList.indexOf(lpVideoSizeModel.userId),
                                    lpVideoSizeModel.height, lpVideoSizeModel.width);
                        }
                    }
                });

        subscriptionOfUserOut = routerListener.getLiveRoom()
                .getObservableOfUserOut()
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return routerListener.getLiveRoom().getPresenterUser() != null &&
                                routerListener.getLiveRoom().getPresenterUser().getUserId().equals(s); // 主讲人退出教室
                    }
                })
                .subscribe(new LPErrorPrintSubscriber<String>() {
                    @Override
                    public void call(String s) {
                        if (s.equals(fullScreenKVO.getParameter())) {
//                            fullScreenKVO.setParameter(null);
                            printSections();
                            return;
                        }
                        if (displayList.indexOf(s) < 0) return;
                        view.notifyItemDeleted(_displayPresenterSection);
                        displayList.remove(_displayPresenterSection);
                        _displayRecordSection--;
                        _displayVideoSection--;
                        _displaySpeakerSection--;
                        _displayApplySection--;
                        printSections();
                    }
                });

        subscriptionOfShareDesktopAndPlayMedia = routerListener.getLiveRoom().
                getObservableOfPlayMedia().mergeWith(routerListener.getLiveRoom().
                getObservableOfShareDesktop())
                .filter(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        // 不是老师都自动全屏
                        return routerListener.getLiveRoom().getCurrentUser() != null
                                && routerListener.getLiveRoom().getCurrentUser().getType() != LPConstants.LPUserType.Teacher;
                    }
                })
                .filter(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        return aBoolean &&
                                routerListener.getLiveRoom().getPresenterUser() != null &&
                                routerListener.getLiveRoom().getTeacherUser() != null &&
                                routerListener.getLiveRoom().getPresenterUser().getUserId().equals(
                                        routerListener.getLiveRoom().getTeacherUser().getUserId());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (routerListener.getLiveRoom().getTeacherUser() == null)
                            return;
                        String teacherId = routerListener.getLiveRoom().getTeacherUser().getUserId();
                        if (displayList.indexOf(teacherId) >= _displayPresenterSection
                                && displayList.indexOf(teacherId) < _displaySpeakerSection
                                && !fullScreenKVO.getParameter().equals(teacherId)
                                && getSpeakModel(teacherId) != null
                                && getSpeakModel(teacherId).isVideoOn()) {
                            fullScreenKVO.setParameter(teacherId);
                        }
                    }
                });

        subscriptionOfFullScreen = fullScreenKVO.newObservableOfParameterChanged()
                .onBackpressureBuffer()
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return s == null || !s.equals(fullScreenKVO.getLastParameter());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPBackPressureBufferedSubscriber<String>() {
                    @Override
                    public void call(String s) {
                        if (TextUtils.isEmpty(s)) {
                            // full screen ppt
                            fullScreenKVO.setParameter(PPT_TAG);
                        } else {
                            String lastTag = fullScreenKVO.getLastParameter();
                            String tag = fullScreenKVO.getParameter();

                            View view1 = routerListener.removeFullScreenView();
                            View view2 = view.removeViewAt(displayList.indexOf(tag));

                            displayList.remove(tag);
                            if (PPT_TAG.equals(tag)) {
                                _displayPresenterSection--;
                                _displayRecordSection--;
                                _displayVideoSection--;
                                _displaySpeakerSection--;
                                _displayApplySection--;
                            } else if (routerListener.getLiveRoom().getPresenterUser() != null
                                    && tag.equals(routerListener.getLiveRoom().getPresenterUser().getUserId())) {
                                _displayRecordSection--;
                                _displayVideoSection--;
                                _displaySpeakerSection--;
                                _displayApplySection--;
                            } else if (RECORD_TAG.equals(tag)) {
                                _displayVideoSection--;
                                _displaySpeakerSection--;
                                _displayApplySection--;
                            } else { // video
                                _displaySpeakerSection--;
                                _displayApplySection--;
                            }

                            int position = -1;
                            if (!TextUtils.isEmpty(lastTag)) {
                                if (PPT_TAG.equals(lastTag)) {
                                    position = 0;
                                    displayList.add(0, PPT_TAG);
                                    _displayPresenterSection++;
                                    _displayRecordSection++;
                                    _displayVideoSection++;
                                    _displaySpeakerSection++;
                                    _displayApplySection++;
                                } else if (routerListener.getLiveRoom().getPresenterUser() != null
                                        && lastTag.equals(routerListener.getLiveRoom().getPresenterUser().getUserId())) {
                                    position = _displayPresenterSection;
                                    displayList.add(position, lastTag);
                                    _displayRecordSection++;
                                    _displayVideoSection++;
                                    _displaySpeakerSection++;
                                    _displayApplySection++;
                                } else if (RECORD_TAG.equals(lastTag)) {
                                    position = _displayRecordSection;
                                    displayList.add(position, RECORD_TAG);
                                    _displayVideoSection++;
                                    _displaySpeakerSection++;
                                    _displayApplySection++;
                                } else { // video
                                    position = _displaySpeakerSection;
                                    displayList.add(position, lastTag);
                                    _displaySpeakerSection++;
                                    _displayApplySection++;
                                }
                            }

                            if (!TextUtils.isEmpty(lastTag)) {
                                view.notifyViewAdded(view1, position);
                            }
                            routerListener.setFullScreenView(view2);
                        }
                        printSections();
                    }
                });

        if (routerListener.isTeacherOrAssistant()) {

            subscriptionSpeakApply = routerListener.getLiveRoom().getSpeakQueueVM().getObservableOfSpeakApply()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new LPErrorPrintSubscriber<IMediaModel>() {
                        @Override
                        public void call(IMediaModel iMediaModel) {
                            displayList.add(iMediaModel.getUser().getUserId());
                            view.notifyItemInserted(displayList.size() - 1);
                        }
                    });

            subscriptionSpeakResponse = routerListener.getLiveRoom().getSpeakQueueVM().getObservableOfSpeakResponse()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new LPErrorPrintSubscriber<IMediaControlModel>() {
                        @Override
                        public void call(IMediaControlModel iMediaControlModel) {
                            int position = displayList.indexOf(iMediaControlModel.getUser().getUserId());
                            if (position < _displayApplySection) {
                                throw new RuntimeException("position < _displayApplySection");
                            } else if (position < displayList.size()) {
                                view.notifyItemDeleted(position);
                                displayList.remove(position);
                            } else {
                                throw new RuntimeException("position > displayList.size()");
                            }
                        }
                    });
        }
    }

    @Override
    public void unSubscribe() {
        RxUtils.unSubscribe(subscriptionOfMediaNew);
        RxUtils.unSubscribe(subscriptionOfMediaChange);
        RxUtils.unSubscribe(subscriptionOfMediaClose);
        RxUtils.unSubscribe(subscriptionSpeakApply);
        RxUtils.unSubscribe(subscriptionSpeakResponse);
        RxUtils.unSubscribe(subscriptionOfActiveUser);
        RxUtils.unSubscribe(subscriptionOfFullScreen);
        RxUtils.unSubscribe(subscriptionOfUserIn);
        RxUtils.unSubscribe(subscriptionOfUserOut);
        RxUtils.unSubscribe(subscriptionOfPresenterChange);
        RxUtils.unSubscribe(subscriptionOfVideoSizeChange);
        RxUtils.unSubscribe(subscriptionOfShareDesktopAndPlayMedia);
    }

    @Override
    public void destroy() {
        view = null;
        routerListener = null;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < 0)
            throw new RuntimeException("position < 0 in getItemViewType");
        else if (position < _displayPresenterSection)
            return VIEW_TYPE_PPT;
        else if (position < _displayRecordSection)
            return VIEW_TYPE_PRESENTER;
        else if (position < _displayVideoSection)
            return VIEW_TYPE_RECORD;
        else if (position < _displaySpeakerSection)
            return VIEW_TYPE_VIDEO_PLAY;
        else if (position < _displayApplySection)
            return VIEW_TYPE_SPEAKER;
        else if (position < displayList.size())
            return VIEW_TYPE_APPLY;
        else
            throw new RuntimeException("position > displayList.size() in getItemViewType");
    }

    @Override
    public int getItemViewType(String userId) {
        return getItemViewType(displayList.indexOf(userId));
    }

    @Override
    public LPRecorder getRecorder() {
        return routerListener.getLiveRoom().getRecorder();
    }

    @Override
    public LPPlayer getPlayer() {
        return routerListener.getLiveRoom().getPlayer();
    }

    @Override
    public IUserModel getApplyModel(int position) {
        String userId = displayList.get(position);
        for (IUserModel model : routerListener.getLiveRoom().getSpeakQueueVM().getApplyList()) {
            if (model.getUserId().equals(userId)) {
                return model;
            }
        }
        return null;
    }

    @Override
    public IMediaModel getSpeakModel(String userId) {
        for (IMediaModel model : routerListener.getLiveRoom().getSpeakQueueVM().getSpeakQueueList()) {
            if (model.getUser().getUserId().equals(userId)) {
                return model;
            }
        }
        // presenter mismatching
        if (routerListener.getLiveRoom().getPresenterUser() != null &&
                userId.equals(routerListener.getLiveRoom().getPresenterUser().getUserId())) {
            LPMediaModel model = new LPMediaModel();
            model.user = (LPUserModel) routerListener.getLiveRoom().getPresenterUser();
            return model;
        }

        if (tempUserIn != null && tempUserIn.getUserId().equals(userId)) {
            LPMediaModel model = new LPMediaModel();
            model.user = (LPUserModel) tempUserIn;
            return model;
        }
        // mismatching
        LPMediaModel model = new LPMediaModel();
        model.user = (LPUserModel) routerListener.getLiveRoom().getOnlineUserVM().getUserById(userId);
        return model;
    }

    @Override
    public IMediaModel getSpeakModel(int position) {
        String userId = displayList.get(position);
        return getSpeakModel(userId);
    }

    @Override
    public void playVideo(String userId) {
        int position = displayList.indexOf(userId);

        if (position == -1) return;
        if (getSpeakModel(position) == null || !getSpeakModel(position).isVideoOn()) return;

        if (position < _displayRecordSection) {
            autoPlayPresenterVideo = true;
            view.notifyItemChanged(position);
            return;
        }

        IMediaModel model = getSpeakModel(fullScreenKVO.getParameter());
        boolean isFullScreenStudentVideo = model.getUser() != null && model.getUser().getType() == LPConstants.LPUserType.Student;

        if (_displaySpeakerSection - _displayVideoSection + (isFullScreenStudentVideo ? 1 : 0) >= MAX_VIDEO_COUNT) {
            view.showMaxVideoExceed();
            return;
        }
        view.notifyItemDeleted(position);
        displayList.remove(position);

        displayList.add(_displaySpeakerSection, userId);
        _displaySpeakerSection++;
        view.notifyItemInserted(_displaySpeakerSection - 1);
    }

    @Override
    public void closeVideo(String tag) {
        if (tag.equals(RECORD_TAG)) {
            if (routerListener.getLiveRoom().getRecorder().isVideoAttached()) {
                routerListener.detachLocalVideo();
                if (!routerListener.getLiveRoom().getRecorder().isAudioAttached()) {
                    routerListener.getLiveRoom().getRecorder().stopPublishing();
                }
            }
            return;
        } else if (tag.equals(PPT_TAG)) {
            throw new RuntimeException("close PPT? wtf");
        }
        int position = displayList.indexOf(tag);

        // 在dialog操作过程中 数据发生了变化
        if (position == -1) return;
        IMediaModel model = getSpeakModel(position);
        if (model == null) return;

        if (position < _displayRecordSection) { // presenter
            autoPlayPresenterVideo = false;
            view.notifyItemChanged(position);
            return;
        }

        routerListener.getLiveRoom().getPlayer().playAVClose(tag);
        routerListener.getLiveRoom().getPlayer().playAudio(tag);
        view.notifyItemDeleted(position);
        displayList.remove(position);
        displayList.add(_displayApplySection - 1, tag);
        _displaySpeakerSection--;
        view.notifyItemInserted(_displayApplySection - 1);
    }

    @Override
    public void closeSpeaking(String userId) {
        int position = displayList.indexOf(userId);
        if (position == -1) return;
        routerListener.getLiveRoom().getSpeakQueueVM().closeOtherSpeak(userId);
    }

    @Override
    public boolean isTeacherOrAssistant() {
        return routerListener.isTeacherOrAssistant();
    }

    @Override
    public void changeBackgroundContainerSize(boolean isShrink) {
        routerListener.changeBackgroundContainerSize(isShrink);
    }

    @Override
    public void setFullScreenTag(String tag) {
        fullScreenKVO.setParameter(tag);
    }

    @Override
    public MyPPTFragment getPPTFragment() {
        return routerListener.getPPTFragment();
    }

    @Override
    public boolean isFullScreen(String tag) {
        checkNotNull(tag);
        return tag.equals(fullScreenKVO.getParameter());
    }

    @Override
    public void switchCamera() {
        routerListener.getLiveRoom().getRecorder().switchCamera();
    }

    @Override
    public void switchPrettyFilter() {
        if (getRecorder().isBeautyFilterOn()) {
            getRecorder().closeBeautyFilter();
        } else {
            getRecorder().openBeautyFilter();
        }
    }

    private boolean isScreenCleared = false;

    @Override
    public void clearScreen() {
        isScreenCleared = !isScreenCleared;
        if (isScreenCleared) routerListener.clearScreen();
        else routerListener.unClearScreen();
    }

    @Override
    public boolean isAutoPlay() {
        return autoPlayPresenterVideo;
    }

    @Override
    public LPEnterRoomNative.LPWaterMark getWaterMark() {
        return routerListener.getLiveRoom().getPartnerConfig().waterMark;
    }

    @Override
    public String getItem(int position) {
        if (position < displayList.size())
            return displayList.get(position);
        else
            throw new RuntimeException("position > displayList.size() in getItem");
    }

    @Override
    public int getCount() {
        return displayList.size();
    }

    @Override
    public void agreeSpeakApply(String userId) {
        int position = displayList.indexOf(userId);
        if (position == -1) {
            throw new RuntimeException("invalid userId:" + userId + " in agreeSpeakApply");
        } else {
            routerListener.getLiveRoom().getSpeakQueueVM().agreeSpeakApply(displayList.get(position));
        }
    }

    @Override
    public void disagreeSpeakApply(String userId) {
        int position = displayList.indexOf(userId);
        if (position == -1) {
            throw new RuntimeException("invalid userId:" + userId + " in disagreeSpeakApply");
        } else {
            routerListener.getLiveRoom().getSpeakQueueVM().disagreeSpeakApply(displayList.get(position));
        }
    }

    public void attachVideo() {
        if (routerListener.checkCameraPermission()) {
            if (_displayRecordSection == _displayVideoSection) {
                displayList.add(_displayRecordSection, RECORD_TAG);
                _displayVideoSection++;
                _displaySpeakerSection++;
                _displayApplySection++;
                view.notifyItemInserted(_displayRecordSection);
            }
        }
        printSections();
    }

    @Override
    public boolean isStopPublish() {
        return isStopPublish;
    }

    @Override
    public void setIsStopPublish(boolean b) {
        isStopPublish = b;
    }

    @Override
    public boolean isMultiClass() {
        return routerListener.getRoomType() == LPConstants.LPRoomType.Multi;
    }

    private boolean isStopPublish = false;

    public void detachVideo() {
        if (RECORD_TAG.equals(fullScreenKVO.getParameter())) {
            if (getRecorder().isVideoAttached())
                getRecorder().detachVideo();
            fullScreenKVO.setParameter(null);
            return;
        }
        if (_displayRecordSection == _displayVideoSection - 1) {
            view.notifyItemDeleted(_displayRecordSection);
            displayList.remove(_displayRecordSection);
            _displayVideoSection--;
            _displaySpeakerSection--;
            _displayApplySection--;
        }
        printSections();
    }

    public void notifyEmptyPPTStatus(Boolean isEmpty) {
//        if (isEmptyPPT == isEmpty) return;
//        isEmptyPPT = isEmpty;
//        if (isEmptyPPT) {
//            if (displayList.indexOf(PPT_TAG) >= 0) {
//                _displayPresenterSection--;
//                _displayRecordSection--;
//                _displayVideoSection--;
//                _displaySpeakerSection--;
//                _displayApplySection--;
//                view.removeViewAt(displayList.indexOf(PPT_TAG));
//            }
//        } else {
//            if (!PPT_TAG.equals(fullScreenKVO.getParameter())) {
//                _displayPresenterSection++;
//                _displayRecordSection++;
//                _displayVideoSection++;
//                _displaySpeakerSection++;
//                _displayApplySection++;
//                view.notifyViewAdded(getPPTFragment().getView(), 0);
//            }
//        }
    }

    private void printSections() {
        LPLogger.i("section: " + _displayPresenterSection + " " + _displayRecordSection + " " + _displayVideoSection + " " +
                _displaySpeakerSection + " " + _displayApplySection);
    }
}
