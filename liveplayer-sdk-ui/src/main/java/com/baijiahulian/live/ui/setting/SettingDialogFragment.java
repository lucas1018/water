package com.baijiahulian.live.ui.setting;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import com.baijiahulian.live.ui.R;
import com.baijiahulian.live.ui.base.BaseDialogFragment;
import com.baijiahulian.live.ui.utils.QueryPlus;
import com.baijiahulian.livecore.utils.LPErrorPrintSubscriber;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;

/**
 * Created by Shubo on 2017/3/2.
 */

public class SettingDialogFragment extends BaseDialogFragment implements SettingContract.View {

    private QueryPlus $;
    private SettingContract.Presenter presenter;

    public static SettingDialogFragment newInstance() {
        return new SettingDialogFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_setting;
    }

    @Override
    protected void init(Bundle savedInstanceState, Bundle arguments) {
        super.title(getString(R.string.live_setting)).editable(false);
        $ = QueryPlus.with(contentView);
        $.id(R.id.dialog_setting_mic).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.changeMic();
            }
        });
        $.id(R.id.dialog_setting_camera).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.changeCamera();
            }
        });
        $.id(R.id.dialog_setting_beauty_filter).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    showToast(getString(R.string.live_not_support_beauty_filter));
                } else {
                    presenter.changeBeautyFilter();
                }
            }
        });
        $.id(R.id.dialog_setting_radio_ppt_fs).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.setPPTFullScreen();
            }
        });
        $.id(R.id.dialog_setting_radio_ppt_os).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.setPPTOverspread();
            }
        });
        $.id(R.id.dialog_setting_radio_definition_low).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkClickable(getString(R.string.live_frequent_error_resolution)))
                    presenter.setDefinitionLow();
            }
        });
        $.id(R.id.dialog_setting_radio_definition_high).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkClickable(getString(R.string.live_frequent_error_resolution)))
                    presenter.setDefinitionHigh();
            }
        });
        $.id(R.id.dialog_setting_radio_link_up_1).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkClickable(getString(R.string.live_frequent_error_line)))
                    presenter.setUpLinkTCP();
            }
        });
        $.id(R.id.dialog_setting_radio_link_up_2).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkClickable(getString(R.string.live_frequent_error_line)))
                    presenter.setUpLinkUDP();
            }
        });
        $.id(R.id.dialog_setting_radio_link_down_1).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkClickable(getString(R.string.live_frequent_error_line)))
                    presenter.setDownLinkTCP();
            }
        });
        $.id(R.id.dialog_setting_radio_link_down_2).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkClickable(getString(R.string.live_frequent_error_line)))
                    presenter.setDownLinkUDP();
            }
        });

        $.id(R.id.dialog_setting_radio_camera_front).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkClickable(getString(R.string.live_frequent_error_camera)))
                    presenter.switchCamera();
            }
        });

        $.id(R.id.dialog_setting_radio_camera_back).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkClickable(getString(R.string.live_frequent_error_camera)))
                    presenter.switchCamera();
            }
        });

        $.id(R.id.dialog_setting_forbid_all_speak).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.switchForbidStatus();
            }
        });

        $.id(R.id.dialog_setting_forbid_raise_hand).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.switchForbidRaiseHand();
            }
        });


        if (presenter.isTeacherOrAssistant()) {
            $.id(R.id.dialog_setting_forbid_all_speak_container).visible();
            $.id(R.id.dialog_setting_forbid_raise_hand_container).visible();
        } else {
            $.id(R.id.dialog_setting_forbid_all_speak_container).gone();
            $.id(R.id.dialog_setting_forbid_raise_hand_container).gone();
        }

    }

    @Override
    public void showMicOpen() {
        $.id(R.id.dialog_setting_mic).image(R.drawable.ic_on_switch);
    }

    @Override
    public void showMicClosed() {
        $.id(R.id.dialog_setting_mic).image(R.drawable.ic_off_switch);
    }

    @Override
    public void showCameraOpen() {
        $.id(R.id.dialog_setting_camera).image(R.drawable.ic_on_switch);
    }

    @Override
    public void showCameraClosed() {
        $.id(R.id.dialog_setting_camera).image(R.drawable.ic_off_switch);
    }

    @Override
    public void showBeautyFilterEnable() {
        $.id(R.id.dialog_setting_beauty_filter).image(R.drawable.ic_on_switch);
    }

    @Override
    public void showBeautyFilterDisable() {
        $.id(R.id.dialog_setting_beauty_filter).image(R.drawable.ic_off_switch);
    }

    @Override
    public void showPPTFullScreen() {
        $.id(R.id.dialog_setting_radio_ppt_fs).enable(false);
        $.id(R.id.dialog_setting_radio_ppt_os).enable(true);
        ((Button) $.id(R.id.dialog_setting_radio_ppt_fs).view()).setTextColor(ContextCompat.getColor(getContext(), R.color.live_white));
        ((Button) $.id(R.id.dialog_setting_radio_ppt_os).view()).setTextColor(ContextCompat.getColor(getContext(), R.color.live_text_color));
    }

    @Override
    public void showPPTOverspread() {
        $.id(R.id.dialog_setting_radio_ppt_fs).enable(true);
        $.id(R.id.dialog_setting_radio_ppt_os).enable(false);
        ((Button) $.id(R.id.dialog_setting_radio_ppt_fs).view()).setTextColor(ContextCompat.getColor(getContext(), R.color.live_text_color));
        ((Button) $.id(R.id.dialog_setting_radio_ppt_os).view()).setTextColor(ContextCompat.getColor(getContext(), R.color.live_white));
    }

    @Override
    public void showDefinitionLow() {
        $.id(R.id.dialog_setting_radio_definition_low).enable(false);
        $.id(R.id.dialog_setting_radio_definition_high).enable(true);
        ((Button) $.id(R.id.dialog_setting_radio_definition_low).view()).setTextColor(ContextCompat.getColor(getContext(), R.color.live_white));
        ((Button) $.id(R.id.dialog_setting_radio_definition_high).view()).setTextColor(ContextCompat.getColor(getContext(), R.color.live_text_color));
    }

    @Override
    public void showDefinitionHigh() {
        $.id(R.id.dialog_setting_radio_definition_low).enable(true);
        $.id(R.id.dialog_setting_radio_definition_high).enable(false);
        ((Button) $.id(R.id.dialog_setting_radio_definition_low).view()).setTextColor(ContextCompat.getColor(getContext(), R.color.live_text_color));
        ((Button) $.id(R.id.dialog_setting_radio_definition_high).view()).setTextColor(ContextCompat.getColor(getContext(), R.color.live_white));
    }

    @Override
    public void showUpLinkTCP() {
        $.id(R.id.dialog_setting_radio_link_up_1).enable(false);
        $.id(R.id.dialog_setting_radio_link_up_2).enable(true);
        ((Button) $.id(R.id.dialog_setting_radio_link_up_1).view()).setTextColor(ContextCompat.getColor(getContext(), R.color.live_white));
        ((Button) $.id(R.id.dialog_setting_radio_link_up_2).view()).setTextColor(ContextCompat.getColor(getContext(), R.color.live_text_color));
    }

    @Override
    public void showUpLinkUDP() {
        $.id(R.id.dialog_setting_radio_link_up_1).enable(true);
        $.id(R.id.dialog_setting_radio_link_up_2).enable(false);
        ((Button) $.id(R.id.dialog_setting_radio_link_up_1).view()).setTextColor(ContextCompat.getColor(getContext(), R.color.live_text_color));
        ((Button) $.id(R.id.dialog_setting_radio_link_up_2).view()).setTextColor(ContextCompat.getColor(getContext(), R.color.live_white));
    }

    @Override
    public void showDownLinkTCP() {
        $.id(R.id.dialog_setting_radio_link_down_1).enable(false);
        $.id(R.id.dialog_setting_radio_link_down_2).enable(true);
        ((Button) $.id(R.id.dialog_setting_radio_link_down_1).view()).setTextColor(ContextCompat.getColor(getContext(), R.color.live_white));
        ((Button) $.id(R.id.dialog_setting_radio_link_down_2).view()).setTextColor(ContextCompat.getColor(getContext(), R.color.live_text_color));
    }

    @Override
    public void showDownLinkUDP() {
        $.id(R.id.dialog_setting_radio_link_down_1).enable(true);
        $.id(R.id.dialog_setting_radio_link_down_2).enable(false);
        ((Button) $.id(R.id.dialog_setting_radio_link_down_1).view()).setTextColor(ContextCompat.getColor(getContext(), R.color.live_text_color));
        ((Button) $.id(R.id.dialog_setting_radio_link_down_2).view()).setTextColor(ContextCompat.getColor(getContext(), R.color.live_white));
    }

    @Override
    public void showVisitorFail() {
        showToast(getString(R.string.live_media_visitor_fail));
    }

    @Override
    public void showStudentFail() {
        showToast(getString(R.string.live_media_student_fail));
    }

    @Override
    public void showCameraFront() {
        $.id(R.id.dialog_setting_radio_camera_front).enable(false);
        $.id(R.id.dialog_setting_radio_camera_back).enable(true);
        ((Button) $.id(R.id.dialog_setting_radio_camera_front).view()).setTextColor(ContextCompat.getColor(getContext(), R.color.live_white));
        ((Button) $.id(R.id.dialog_setting_radio_camera_back).view()).setTextColor(ContextCompat.getColor(getContext(), R.color.live_text_color));
    }

    @Override
    public void showCameraBack() {
        $.id(R.id.dialog_setting_radio_camera_front).enable(true);
        $.id(R.id.dialog_setting_radio_camera_back).enable(false);
        ((Button) $.id(R.id.dialog_setting_radio_camera_front).view()).setTextColor(ContextCompat.getColor(getContext(), R.color.live_text_color));
        ((Button) $.id(R.id.dialog_setting_radio_camera_back).view()).setTextColor(ContextCompat.getColor(getContext(), R.color.live_white));
    }

    @Override
    public void showCameraSwitchStatus(boolean whetherShow) {
        if (whetherShow) {
            $.id(R.id.dialog_setting_camera_switch_wrapper).visible();
        } else {
            $.id(R.id.dialog_setting_camera_switch_wrapper).gone();
        }
    }

    @Override
    public void showForbidden() {
        $.id(R.id.dialog_setting_forbid_all_speak).image(R.drawable.ic_on_switch);
    }

    @Override
    public void showNotForbidden() {
        $.id(R.id.dialog_setting_forbid_all_speak).image(R.drawable.ic_off_switch);
    }

    @Override
    public void showAudioRoomError() {
        showToast(getString(R.string.live_audio_room_error));
    }

    @Override
    public void showForbidRaiseHandOn() {
        $.id(R.id.dialog_setting_forbid_raise_hand).image(R.drawable.ic_on_switch);
    }

    @Override
    public void showForbidRaiseHandOff() {
        $.id(R.id.dialog_setting_forbid_raise_hand).image(R.drawable.ic_off_switch);
    }

    @Override
    public void showSwitchLinkTypeError() {
        showToast(getString(R.string.live_switch_link_type_error));
    }

    @Override
    public void hidePPTShownType() {
        $.id(R.id.dialog_setting_radio_ppt_container).gone();
    }

    @Override
    public void setPresenter(SettingContract.Presenter presenter) {
        super.setBasePresenter(presenter);
        this.presenter = presenter;
    }

    private Map<String, AtomicBoolean> clickableWithKey = new ConcurrentHashMap<>();

    private boolean checkClickable(String error) {
        if (!clickableWithKey.containsKey(error)) {
            clickableWithKey.put(error, new AtomicBoolean(true));
        }
        final AtomicBoolean clickable = clickableWithKey.get(error);
        if (clickable.get()) {
            clickable.set(false);
            Observable.timer(2, TimeUnit.SECONDS).subscribe(new LPErrorPrintSubscriber<Long>() {
                @Override
                public void call(Long aLong) {
                    clickable.set(true);
                }
            });
            return true;
        } else {
            showToast(error);
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter = null;
        $ = null;
    }
}
