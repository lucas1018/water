package com.baijiahulian.live.ui.rightbotmenu;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;

import com.baijiahulian.live.ui.R;
import com.baijiahulian.live.ui.base.BaseFragment;
import com.baijiahulian.live.ui.utils.RotationObserver;
import com.baijiahulian.live.ui.utils.RxUtils;
import com.baijiahulian.livecore.utils.LPErrorPrintSubscriber;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Shubo on 2017/2/16.
 */

public class RightBottomMenuFragment extends BaseFragment implements RightBottomMenuContract.View,
        RotationObserver.OnRotationSettingChangedListener {

    private RightBottomMenuContract.Presenter presenter;
    private Subscription subscriptionOfVideoClick, subscriptionOfAudioClick, subscriptionOfZoomClick, subscriptionOfMoreClick;
    private RotationObserver rotationObserver;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_right_bottom_menu;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        subscriptionOfVideoClick = $.id(R.id.fragment_right_bottom_video).clicked()
                .throttleFirst(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (!clickableCheck()) {
                            showToast(getString(R.string.live_frequent_error));
                            return;
                        }
                        presenter.changeVideo();
                    }
                });

        subscriptionOfAudioClick = $.id(R.id.fragment_right_bottom_audio).clicked()
                .throttleFirst(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (!clickableCheck()) {
                            showToast(getString(R.string.live_frequent_error));
                            return;
                        }
                        presenter.changeAudio();
                    }
                });

        subscriptionOfMoreClick = $.id(R.id.fragment_right_bottom_more).clicked()
                .throttleFirst(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        int location[] = new int[2];
                        $.id(R.id.fragment_right_bottom_more).view().getLocationInWindow(location);
                        presenter.more(location[0], location[1]);
                    }
                });

        subscriptionOfZoomClick = $.id(R.id.fragment_right_bottom_zoom)
                .clicked()
                .throttleFirst(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        presenter.changeZoom();
                    }
                });

        rotationObserver = new RotationObserver(new Handler(), getActivity().getContentResolver());
        rotationObserver.startObserver();
        rotationObserver.setOnRotationSettingChangedListener(this);
    }

    @Override
    public void showVideoStatus(boolean isOn) {
        if (isOn) {
            $.id(R.id.fragment_right_bottom_video).image(R.drawable.live_ic_stopvideo_on);
            showToast(getString(R.string.live_camera_on));
        } else {
            $.id(R.id.fragment_right_bottom_video).image(R.drawable.live_ic_stopvideo);
            showToast(getString(R.string.live_camera_off));
        }
    }

    @Override
    public void showAudioStatus(boolean isOn) {
        if (isOn) {
            $.id(R.id.fragment_right_bottom_audio).image(R.drawable.live_ic_stopaudio_1);
            showToast(getString(R.string.live_mic_on));
        } else {
            $.id(R.id.fragment_right_bottom_audio).image(R.drawable.live_ic_stopaudio);
            showToast(getString(R.string.live_mic_off));
        }
    }

    @Override
    public void enableSpeakerMode() {
        $.id(R.id.fragment_right_bottom_video).visible();
        $.id(R.id.fragment_right_bottom_audio).visible();
    }

    @Override
    public void disableSpeakerMode() {
        $.id(R.id.fragment_right_bottom_video).gone();
        $.id(R.id.fragment_right_bottom_audio).gone();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void clearScreen() {
        if ($.id(R.id.fragment_right_bottom_video).view().getVisibility() == View.VISIBLE)
            $.id(R.id.fragment_right_bottom_video).invisible();
        if ($.id(R.id.fragment_right_bottom_audio).view().getVisibility() == View.VISIBLE)
            $.id(R.id.fragment_right_bottom_audio).invisible();
        if ($.id(R.id.fragment_right_bottom_zoom).view().getVisibility() == View.VISIBLE)
            $.id(R.id.fragment_right_bottom_zoom).invisible();
    }

    @Override
    public void unClearScreen() {
        if ($.id(R.id.fragment_right_bottom_video).view().getVisibility() == View.INVISIBLE)
            $.id(R.id.fragment_right_bottom_video).visible();
        if ($.id(R.id.fragment_right_bottom_audio).view().getVisibility() == View.INVISIBLE)
            $.id(R.id.fragment_right_bottom_audio).visible();
        if ($.id(R.id.fragment_right_bottom_zoom).view().getVisibility() == View.INVISIBLE)
            $.id(R.id.fragment_right_bottom_zoom).visible();
        presenter.getSysRotationSetting();
    }

    @Override
    public void showVolume(int level) {
        // level between [0,9]
        switch (level) {
            case 0:
                $.id(R.id.fragment_right_bottom_audio).image(R.drawable.live_ic_stopaudio_1);
                break;
            case 1:
            case 2:
                $.id(R.id.fragment_right_bottom_audio).image(R.drawable.live_ic_stopaudio_2);
                break;
            case 3:
            case 4:
                $.id(R.id.fragment_right_bottom_audio).image(R.drawable.live_ic_stopaudio_3);
                break;
            case 5:
            case 6:
                $.id(R.id.fragment_right_bottom_audio).image(R.drawable.live_ic_stopaudio_4);
                break;
            case 7:
            case 8:
                $.id(R.id.fragment_right_bottom_audio).image(R.drawable.live_ic_stopaudio_5);
                break;
            case 9:
                $.id(R.id.fragment_right_bottom_audio).image(R.drawable.live_ic_stopaudio_6);
                break;
            default:
                break;
        }
    }

    @Override
    public void showZoomIn() {
        $.id(R.id.fragment_right_bottom_zoom).image(R.drawable.live_ic_zoomin);
    }

    @Override
    public void showZoomOut() {
        $.id(R.id.fragment_right_bottom_zoom).image(R.drawable.live_ic_zoomout);
    }

    @Override
    public void showZoom() {
        $.id(R.id.fragment_right_bottom_zoom).visible();
    }

    @Override
    public void hideZoom() {
        $.id(R.id.fragment_right_bottom_zoom).gone();
    }

    @Override
    public void showAudioRoomError() {
        showToast(getString(R.string.live_audio_room_error));
    }

    @Override
    public void setPresenter(RightBottomMenuContract.Presenter presenter) {
        super.setBasePresenter(presenter);
        this.presenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unSubscribe(subscriptionOfAudioClick);
        RxUtils.unSubscribe(subscriptionOfVideoClick);
        RxUtils.unSubscribe(subscriptionOfMoreClick);
        RxUtils.unSubscribe(subscriptionOfZoomClick);
        rotationObserver.stopObserver();
        presenter = null;
    }

    @Override
    public void onRotationSettingChanged() {
        //TODO:主动获取自动旋转设置
        presenter.setSysRotationSetting();
    }

    private Subscription subscriptionOfClickable;

    private boolean clickableCheck() {
        if (subscriptionOfClickable != null && !subscriptionOfClickable.isUnsubscribed()) {
            return false;
        }
        subscriptionOfClickable = Observable.timer(1, TimeUnit.SECONDS).subscribe(new LPErrorPrintSubscriber<Long>() {
            @Override
            public void call(Long aLong) {
                RxUtils.unSubscribe(subscriptionOfClickable);
            }
        });
        return true;
    }
}
