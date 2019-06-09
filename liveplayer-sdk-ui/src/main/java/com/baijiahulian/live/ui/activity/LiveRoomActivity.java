package com.baijiahulian.live.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.baijia.baijiashilian.liveplayer.LivePlayerInfo;
import com.baijiahulian.live.ui.LiveSDKWithUI;
import com.baijiahulian.live.ui.R;
import com.baijiahulian.live.ui.announcement.AnnouncementFragment;
import com.baijiahulian.live.ui.announcement.AnnouncementPresenter;
import com.baijiahulian.live.ui.base.BasePresenter;
import com.baijiahulian.live.ui.base.BaseView;
import com.baijiahulian.live.ui.chat.ChatFragment;
import com.baijiahulian.live.ui.chat.ChatPresenter;
import com.baijiahulian.live.ui.chat.MessageSendPresenter;
import com.baijiahulian.live.ui.chat.MessageSentFragment;
import com.baijiahulian.live.ui.chat.preview.ChatPictureViewFragment;
import com.baijiahulian.live.ui.chat.preview.ChatPictureViewPresenter;
import com.baijiahulian.live.ui.chat.preview.ChatSavePicDialogFragment;
import com.baijiahulian.live.ui.chat.preview.ChatSavePicDialogPresenter;
import com.baijiahulian.live.ui.cloudrecord.CloudRecordFragment;
import com.baijiahulian.live.ui.cloudrecord.CloudRecordPresenter;
import com.baijiahulian.live.ui.error.ErrorFragment;
import com.baijiahulian.live.ui.leftmenu.LeftMenuFragment;
import com.baijiahulian.live.ui.leftmenu.LeftMenuPresenter;
import com.baijiahulian.live.ui.loading.LoadingFragment;
import com.baijiahulian.live.ui.loading.LoadingPresenter;
import com.baijiahulian.live.ui.more.MoreMenuDialogFragment;
import com.baijiahulian.live.ui.more.MoreMenuPresenter;
import com.baijiahulian.live.ui.ppt.MyPPTFragment;
import com.baijiahulian.live.ui.ppt.PPTPresenter;
import com.baijiahulian.live.ui.ppt.quickswitchppt.QuickSwitchPPTFragment;
import com.baijiahulian.live.ui.ppt.quickswitchppt.SwitchPPTFragmentPresenter;
import com.baijiahulian.live.ui.pptleftmenu.PPTLeftFragment;
import com.baijiahulian.live.ui.pptleftmenu.PPTLeftPresenter;
import com.baijiahulian.live.ui.pptmanage.PPTManageFragment;
import com.baijiahulian.live.ui.pptmanage.PPTManagePresenter;
import com.baijiahulian.live.ui.quiz.QuizDialogFragment;
import com.baijiahulian.live.ui.quiz.QuizDialogPresenter;
import com.baijiahulian.live.ui.rightbotmenu.RightBottomMenuFragment;
import com.baijiahulian.live.ui.rightbotmenu.RightBottomMenuPresenter;
import com.baijiahulian.live.ui.rightmenu.RightMenuFragment;
import com.baijiahulian.live.ui.rightmenu.RightMenuPresenter;
import com.baijiahulian.live.ui.rollcall.RollCallDialogFragment;
import com.baijiahulian.live.ui.rollcall.RollCallDialogPresenter;
import com.baijiahulian.live.ui.setting.SettingDialogFragment;
import com.baijiahulian.live.ui.setting.SettingPresenter;
import com.baijiahulian.live.ui.share.LPShareDialog;
import com.baijiahulian.live.ui.speakerspanel.RecorderView;
import com.baijiahulian.live.ui.speakerspanel.SpeakerPresenter;
import com.baijiahulian.live.ui.speakerspanel.SpeakersFragment;
import com.baijiahulian.live.ui.speakerspanel.VideoView;
import com.baijiahulian.live.ui.topbar.TopBarFragment;
import com.baijiahulian.live.ui.topbar.TopBarPresenter;
import com.baijiahulian.live.ui.users.OnlineUserDialogFragment;
import com.baijiahulian.live.ui.users.OnlineUserPresenter;
import com.baijiahulian.live.ui.utils.JsonObjectUtil;
import com.baijiahulian.live.ui.utils.RxUtils;
import com.baijiahulian.livecore.context.LPConstants;
import com.baijiahulian.livecore.context.LPError;
import com.baijiahulian.livecore.context.LiveRoom;
import com.baijiahulian.livecore.context.OnLiveRoomListener;
import com.baijiahulian.livecore.listener.OnPhoneRollCallListener;
import com.baijiahulian.livecore.models.LPJsonModel;
import com.baijiahulian.livecore.models.imodels.ILoginConflictModel;
import com.baijiahulian.livecore.models.imodels.IMediaControlModel;
import com.baijiahulian.livecore.models.imodels.IMediaModel;
import com.baijiahulian.livecore.models.imodels.IUserModel;
import com.baijiahulian.livecore.models.roomresponse.LPResRoomMediaControlModel;
import com.baijiahulian.livecore.utils.LPErrorPrintSubscriber;
import com.baijiahulian.livecore.wrapper.exception.NotInitializedException;
import com.baijiahulian.livecore.wrapper.model.LPAVMediaModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static android.R.attr.path;
import static com.baijiahulian.live.ui.utils.Precondition.checkNotNull;
import static com.bjhl.hubble.sdk.HubbleStatisticsSDK.getContext;

public class LiveRoomActivity extends LiveRoomBaseActivity implements LiveRoomRouterListener {
    private FrameLayout flBackground;
    private FrameLayout flTop;
    private FrameLayout flLeft;
    private FrameLayout flLoading;
    private FrameLayout flError;
    private DrawerLayout dlChat;
    private FrameLayout flPPTLeft;
    private FrameLayout flRightBottom;
    private FrameLayout flRight;
    private FrameLayout flSpeakers;
    private FrameLayout flCloudRecord;
    private LiveRoom liveRoom;
    private LoadingFragment loadingFragment;
    private TopBarFragment topBarFragment;
    private CloudRecordFragment cloudRecordFragment;
    private MyPPTFragment lppptFragment;
    private ChatFragment chatFragment;
    private ChatPresenter chatPresenter;
    private RightBottomMenuFragment rightBottomMenuFragment;
    private LeftMenuFragment leftMenuFragment;
    private RightMenuFragment rightMenuFragment;
    private WindowManager windowManager;
    private SpeakersFragment speakersFragment;
    private SpeakerPresenter speakerPresenter;
    private RightMenuPresenter rightMenuPresenter;
    private PPTManagePresenter pptManagePresenter;
    private SwitchPPTFragmentPresenter switchPPTFragmentPresenter;
    private ErrorFragment errorFragment;
    private GlobalPresenter globalPresenter;
    private PPTLeftFragment pptLeftFragment;
    private RollCallDialogPresenter rollCallDialogPresenter;
    private QuizDialogFragment quizFragment;
    private QuizDialogPresenter quizPresenter;
    private OrientationEventListener orientationEventListener; //处理屏幕旋转时本地视频的方向
    private int oldRotation;

    private Subscription subscriptionOfLoginConflict, subscriptionOfStreamInfo, subscriptionOfOnlineUserDebug;

    private boolean isClearScreen;//是否已经清屏，作用于视频采集和远程视频ui的调整
    private String code, name;
    private boolean isBackgroundContainerShrink = false; //缩小背景框

    private long roomId;
    private String sign;
    private IUserModel enterUser;
    private ViewGroup.LayoutParams lpBackground;
    private Subscription subscriptionOfEmptyBoard;
    private boolean mobileNetworkDialogShown = false;
    private MaterialDialog speakInviteDlg;  //取消用
    //debug
    private RadioGroup rgAecMode, rgAecmMode, rgAudioSourceMode, rgCommunication;
    private EditText etDebugAecDelay;
    private int aecMode = 0, aecmMode = 0, audioSource = 0, delay;
    private boolean isCommunication = true;
    private TextView tvStreamInfo;
    //    private List<IUserModel> userModels;
    private List<IMediaModel> userMediaModels;

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("code", code);
        outState.putString("name", name);
        outState.putLong("roomId", roomId);
        outState.putString("sign", sign);
        outState.putSerializable("user", enterUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 6.0+ 打开硬件加速
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_room);
        initViews();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            onConfigurationChanged(getResources().getConfiguration());
        }
        if (savedInstanceState == null) {
            code = getIntent().getStringExtra("code");
            name = getIntent().getStringExtra("name");
            roomId = getIntent().getLongExtra("roomId", -1);
            sign = getIntent().getStringExtra("sign");
            enterUser = (IUserModel) getIntent().getSerializableExtra("user");
        } else {
            code = savedInstanceState.getString("code");
            name = savedInstanceState.getString("name");
            roomId = savedInstanceState.getLong("roomId", -1);
            sign = savedInstanceState.getString("sign");
            enterUser = (IUserModel) savedInstanceState.getSerializable("user");
        }

        loadingFragment = new LoadingFragment();
        Bundle args = new Bundle();
        args.putBoolean("show_tech_support", shouldShowTechSupport);
        loadingFragment.setArguments(args);
        LoadingPresenter loadingPresenter;
        if (roomId == -1) {
            loadingPresenter = new LoadingPresenter(loadingFragment, code, name);
        } else {
            loadingPresenter = new LoadingPresenter(loadingFragment, roomId, sign, enterUser);
        }
        bindVP(loadingFragment, loadingPresenter);
        addFragment(R.id.activity_live_room_loading, loadingFragment);

        windowManager = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        oldRotation = windowManager.getDefaultDisplay().getRotation();

        orientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_FASTEST) {
            @Override
            public void onOrientationChanged(int orientation) {
                int newRotation = windowManager.getDefaultDisplay().getRotation();
                if (newRotation != oldRotation) {
                    oldRotation = newRotation;
                    try {
                        if (liveRoom.getRecorder().isVideoAttached())
                            liveRoom.getRecorder().invalidVideo();
                    } catch (NotInitializedException ignored) {
                    }
                }
            }
        };
        dlChat.openDrawer(Gravity.START);
        checkScreenOrientationInit();
    }

    private void initViews() {
        lpBackground = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        flBackground = (FrameLayout) findViewById(R.id.activity_live_room_background_container);
        flTop = (FrameLayout) findViewById(R.id.activity_live_room_top);
        flLeft = (FrameLayout) findViewById(R.id.activity_live_room_bottom_left);
        flLoading = (FrameLayout) findViewById(R.id.activity_live_room_loading);
        dlChat = (DrawerLayout) findViewById(R.id.activity_live_room_chat_drawer);
        flPPTLeft = (FrameLayout) findViewById(R.id.activity_live_room_ppt_left);
        flError = (FrameLayout) findViewById(R.id.activity_live_room_error);
        flRightBottom = (FrameLayout) findViewById(R.id.activity_live_room_bottom_right);
        flRight = (FrameLayout) findViewById(R.id.activity_live_room_right);
        flSpeakers = (FrameLayout) findViewById(R.id.activity_live_room_speakers_container);
        flCloudRecord = (FrameLayout) findViewById(R.id.activity_live_room_cloud_record);
    }

    @Override
    protected void onResume() {
        super.onResume();
        orientationEventListener.enable();

        if (roomLifeCycleListener != null) {
            roomLifeCycleListener.onResume(this, new LiveSDKWithUI.LPRoomChangeRoomListener() {
                @Override
                public void changeRoom(String code, String nickName) {
                    //重新进入教室
                    removeFragment(lppptFragment);
                    removeFragment(topBarFragment);
                    removeFragment(cloudRecordFragment);
                    removeFragment(leftMenuFragment);
                    removeFragment(pptLeftFragment);
                    removeFragment(rightMenuFragment);
                    removeFragment(rightBottomMenuFragment);
                    removeFragment(chatFragment);
                    removeFragment(speakersFragment);
                    if (errorFragment != null && errorFragment.isAdded())
                        removeFragment(errorFragment);

                    loadingFragment = new LoadingFragment();
                    Bundle args = new Bundle();
                    args.putBoolean("show_tech_support", shouldShowTechSupport);
                    loadingFragment.setArguments(args);
                    LoadingPresenter loadingPresenter = new LoadingPresenter(loadingFragment, code, nickName);
                    bindVP(loadingFragment, loadingPresenter);
                    addFragment(R.id.activity_live_room_loading, loadingFragment);
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        orientationEventListener.disable();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dlChat.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            if (isClearScreen)
                unClearScreen();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dlChat.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        }
        onBackgroundContainerConfigurationChanged(newConfig);
        onSpeakersContainerConfigurationChanged(newConfig);
        onPPTLeftMenuConfigurationChanged(newConfig);
        onCloudRecordConfigurationChanged(newConfig);
    }

    private void onCloudRecordConfigurationChanged(Configuration newConfig) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) flCloudRecord.getLayoutParams();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lp.addRule(RelativeLayout.ABOVE, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            lp.addRule(RelativeLayout.ABOVE, R.id.activity_live_room_center_anchor);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        }
        flCloudRecord.setLayoutParams(lp);
    }

    private void onSpeakersContainerConfigurationChanged(Configuration newConfig) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) flSpeakers.getLayoutParams();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            flCloudRecord.setVisibility(View.GONE);
            lp.addRule(RelativeLayout.BELOW, 0);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (liveRoom.getCloudRecordStatus())
                flCloudRecord.setVisibility(View.VISIBLE);
            lp.addRule(RelativeLayout.BELOW, R.id.activity_live_room_center_anchor);
        }
        flSpeakers.setLayoutParams(lp);
    }

    private void onBackgroundContainerConfigurationChanged(Configuration newConfig) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) flBackground.getLayoutParams();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lp.addRule(RelativeLayout.ABOVE, 0); // lp.removeRule()
            if (isBackgroundContainerShrink)
                lp.addRule(RelativeLayout.BELOW, R.id.activity_live_room_speakers_container);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            lp.addRule(RelativeLayout.ABOVE, R.id.activity_live_room_center_anchor);
            if (isBackgroundContainerShrink)
                lp.addRule(RelativeLayout.BELOW, 0);
        }
        flBackground.setLayoutParams(lp);
    }

    private void onPPTLeftMenuConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (lppptFragment != null && lppptFragment.isEditable()) {
                flLeft.setVisibility(View.GONE);
                dlChat.setVisibility(View.GONE);
                flRightBottom.setVisibility(View.INVISIBLE);
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            flLeft.setVisibility(View.VISIBLE);
            dlChat.setVisibility(View.VISIBLE);
            flRightBottom.setVisibility(View.VISIBLE);
        }
    }

    //录课中ui清屏调整
    private void onRecordFullScreenConfigurationChanged(boolean isClear) {
        if (!liveRoom.getCloudRecordStatus())
            return;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            flCloudRecord.setVisibility(View.GONE);
            return;
        }
        if (isClear) {
            flCloudRecord.setVisibility(View.VISIBLE);
        } else {
            flCloudRecord.setVisibility(View.GONE);
        }
    }

    @Override
    public LiveRoom getLiveRoom() {
        checkNotNull(liveRoom);
        return liveRoom;
    }

    @Override
    public void setLiveRoom(final LiveRoom liveRoom) {
        this.liveRoom = liveRoom;
        liveRoom.setOnLiveRoomListener(new OnLiveRoomListener() {
            @Override
            public void onError(final LPError error) {
                switch ((int) error.getCode()) {
                    case LPError.CODE_ERROR_ROOMSERVER_LOSE_CONNECTION:
                        doReEnterRoom();
                        break;
                    case LPError.CODE_ERROR_NETWORK_FAILURE:
                        showMessage(error.getMessage());
                        break;
                    case LPError.CODE_ERROR_NETWORK_MOBILE:
                        if (!mobileNetworkDialogShown) {
                            mobileNetworkDialogShown = true;
                            new MaterialDialog.Builder(LiveRoomActivity.this)
                                    .content(getString(R.string.live_mobile_network_hint))
                                    .positiveText(getString(R.string.live_mobile_network_confirm))
                                    .positiveColor(ContextCompat.getColor(LiveRoomActivity.this, R.color.live_blue))
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                            materialDialog.dismiss();
                                        }
                                    })
                                    .canceledOnTouchOutside(true)
                                    .build()
                                    .show();
                        } else {
                            showMessage(getString(R.string.live_mobile_network_hint_less));
                        }
                        break;
                    case LPError.CODE_ERROR_LOGIN_CONFLICT:
                        break;
                    case LPError.CODE_ERROR_OPEN_AUDIO_RECORD_FAILED:
                        if (!TextUtils.isEmpty(error.getMessage()))
                            showMessage(error.getMessage());
                        break;
                    case LPError.CODE_ERROR_OPEN_AUDIO_CAMERA_FAILED:
                        if (!TextUtils.isEmpty(error.getMessage()))
                            showMessage(error.getMessage());
                        detachLocalVideo();
                        break;
                    default:
                        if (!TextUtils.isEmpty(error.getMessage()))
                            showMessage(error.getMessage());
                        break;
                }
            }
        });
    }

    private Toast toast;

    @Override
    public void showMessage(final String message) {
        if (TextUtils.isEmpty(message)) return;
        Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Object>() {
            @Override
            public void call(Object obj) {
                toast = Toast.makeText(LiveRoomActivity.this, message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    @Override
    public void saveTeacherMediaStatus(IMediaModel model) {
        globalPresenter.setTeacherMedia(model);
    }

    @Override
    public void showError(LPError error) {
        if (error.getCode() == LPError.CODE_ERROR_LOGIN_KICK_OUT) {
            //被踢出房间后的登录
            showKickOutDlg(error);
        } else {
            if (errorFragment != null && errorFragment.isAdded()) return;
            if (flError.getChildCount() >= 2) return;
            if (findFragment(flError.getId()) != null) return;
            if (loadingFragment != null && loadingFragment.isAdded()) {
                removeFragment(loadingFragment);
            }
            errorFragment = ErrorFragment.newInstance(getString(R.string.live_override_error), error.getMessage(), ErrorFragment.ERROR_HANDLE_REENTER);
            errorFragment.setRouterListener(this);
            flError.setVisibility(View.VISIBLE);
            addFragment(flError.getId(), errorFragment);
        }
    }

    private void showKickOutDlg(LPError error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setMessage(error.getMessage())
                .setPositiveButton(R.string.live_quiz_dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        LiveRoomActivity.this.finish();
                    }
                }).create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.live_blue));
    }

    @Override
    public boolean canStudentDraw() {
        return isTeacherOrAssistant() || lppptFragment.isCurrentMaxPage();
    }

    @Override
    public boolean isCurrentUserTeacher() {
        return liveRoom.getCurrentUser().getType() == LPConstants.LPUserType.Teacher;
    }

    @Override
    public boolean isVideoManipulated() {
        return globalPresenter.isVideoManipulated();
    }

    @Override
    public void setVideoManipulated(boolean b) {
        globalPresenter.setVideoManipulated(b);
    }

    @Override
    public int getSpeakApplyStatus() {
        return rightMenuPresenter.getSpeakApplyStatus();
    }

    @Override
    public void showMessageClassEnd() {
        showMessage(getString(R.string.live_message_le, getString(R.string.lp_override_class_end)));
    }

    @Override
    public void showMessageClassStart() {
        showMessage(getString(R.string.live_message_le, getString(R.string.lp_override_class_start)));
    }

    @Override
    public void showMessageForbidAllChat(boolean isOn) {
        if (isTeacherOrAssistant()) {
            showMessage((isOn ? "打开" : "关闭") + "全体禁言成功");
        } else {
            showMessage(getString(R.string.lp_override_role_teacher) + (isOn ? "打开了" : "关闭了") + "全体禁言");
        }
    }

    @Override
    public void showMessageTeacherOpenAudio() {
        showMessage(getString(R.string.lp_override_role_teacher) + "打开了麦克风");
    }

    @Override
    public void showMessageTeacherOpenVideo() {
        showMessage(getString(R.string.lp_override_role_teacher) + "打开了摄像头");
    }

    @Override
    public void showMessageTeacherOpenAV() {
        showMessage(getString(R.string.lp_override_role_teacher) + "打开了麦克风和摄像头");
    }

    @Override
    public void showMessageTeacherCloseAV() {
        showMessage(getString(R.string.lp_override_role_teacher) + "关闭了麦克风和摄像头");
    }

    @Override
    public void showMessageTeacherCloseAudio() {
        showMessage(getString(R.string.lp_override_role_teacher) + "关闭了麦克风");
    }

    @Override
    public void showMessageTeacherCloseVideo() {
        showMessage(getString(R.string.lp_override_role_teacher) + "关闭了摄像头");
    }

    @Override
    public void showMessageTeacherEnterRoom() {
        showMessage(getString(R.string.lp_override_role_teacher) + "进入了" + getString(R.string.lp_override_classroom));
    }

    @Override
    public void showMessageTeacherExitRoom() {
        showMessage(getString(R.string.lp_override_role_teacher) + "离开了" + getString(R.string.lp_override_classroom));
    }

    @Override
    public boolean getVisibilityOfShareBtn() {
        return shareListener != null;
    }

    @Override
    public void changeBackgroundContainerSize(boolean isShrink) {
        if (isShrink == isBackgroundContainerShrink)
            return;
        isBackgroundContainerShrink = isShrink;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            speakersFragment.setBackGroundVisible(true);
            return;
        }

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) flBackground.getLayoutParams();
        if (isBackgroundContainerShrink) {
            lp.addRule(RelativeLayout.BELOW, R.id.activity_live_room_speakers_container);
            speakersFragment.setBackGroundVisible(true);
        } else {
            lp.addRule(RelativeLayout.BELOW, 0);
            speakersFragment.setBackGroundVisible(false);
        }
        flBackground.setLayoutParams(lp);
    }

    @Override
    public View removeFullScreenView() {
        View view = flBackground.getChildAt(0);
        if (view == lppptFragment.getView()) {
            lppptFragment.onStop();
        }
        flBackground.removeView(view);
        setZOrderMediaOverlayTrue(view);
        return view;
    }

    @Override
    public FrameLayout getBackgroundContainer() {
        return flBackground;
    }

    @Override
    public void resizeFullScreenWaterMark(int height, int width) {
        View view = flBackground.getChildAt(0);
        if (view instanceof VideoView) {
            ((VideoView) view).resizeWaterMark(height, width);
        }
    }

    @Override
    public void notifyPPTResumeInSpeakers() {
        speakersFragment.pptResume();
    }

    @Override
    public void setIsStopPublish(boolean b) {
        if (speakerPresenter != null)
            speakerPresenter.setIsStopPublish(true);
    }

    @Override
    public void notifyFullScreenPresenterStatusChange(String id, boolean isPresenter) {
//        View view = flBackground.getChildAt(0);
//        if (view instanceof VideoView) {
//            ((VideoView) view).setIsPresenter(isPresenter);
//        }
    }

    @Override
    public void showForceSpeakDlg(IMediaControlModel iMediaControlModel) {
        LPResRoomMediaControlModel lpResRoomMediaControlModel = (LPResRoomMediaControlModel) iMediaControlModel;
        if (lpResRoomMediaControlModel == null) return;
        if (speakerPresenter != null && getLiveRoom().getAutoOpenCameraStatus()) {
            speakerPresenter.attachVideo();
        }
        int tipRes = R.string.live_force_speak_tip_all;
        if (lpResRoomMediaControlModel.isAudioOn() && getLiveRoom().getAutoOpenCameraStatus()) {
            tipRes = R.string.live_force_speak_tip_all;
        } else if (lpResRoomMediaControlModel.isAudioOn()) {
            tipRes = R.string.live_force_speak_tip_audio;
        } else if (getLiveRoom().getAutoOpenCameraStatus()) {
            tipRes = R.string.live_force_speak_tip_video;
        }
        new MaterialDialog.Builder(this)
                .content(tipRes)
                .positiveText(getString(R.string.live_i_got_it))
                .positiveColor(ContextCompat.getColor(LiveRoomActivity.this, R.color.live_blue))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                    }
                })
                .canceledOnTouchOutside(true)
                .build()
                .show();
    }

    @Override
    public void showSpeakInviteDlg(int invite) {
        if (invite == 0) {
            if (speakInviteDlg != null && speakInviteDlg.isShowing()) {
                speakInviteDlg.dismiss();
            }
            return;
        }
        speakInviteDlg = new MaterialDialog.Builder(this)
                .content(R.string.live_invite_speak_tip)
                .positiveText(getString(R.string.live_agree))
                .negativeText(getString(R.string.live_disagree))
                .positiveColor(ContextCompat.getColor(LiveRoomActivity.this, R.color.live_blue))
                .negativeColor(ContextCompat.getColor(LiveRoomActivity.this, R.color.live_blue))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        rightMenuPresenter.onSpeakInvite(1);
                        materialDialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        rightMenuPresenter.onSpeakInvite(0);
                        dialog.dismiss();
                    }
                })
                .canceledOnTouchOutside(true)
                .build();
        speakInviteDlg.show();
    }

    @Override
    public LPConstants.LPRoomType getRoomType() {
        return liveRoom.getRoomType();
    }


    @Override
    public void showDebugBtn() {
        if (leftMenuFragment != null) {
            leftMenuFragment.showDebugBtn();
        }
    }

    @Override
    public void showStreamDebugPanel() {
        userMediaModels = getLiveRoom().getSpeakQueueVM().getSpeakQueueList();
        subscriptionOfOnlineUserDebug = getLiveRoom().getSpeakQueueVM().getObservableOfMediaNew()
                .mergeWith(getLiveRoom().getSpeakQueueVM().getObservableOfMediaChange())
                .mergeWith(getLiveRoom().getSpeakQueueVM().getObservableOfMediaClose())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<IMediaModel>() {
                    @Override
                    public void call(IMediaModel mediaModel) {
                        userMediaModels = getLiveRoom().getSpeakQueueVM().getSpeakQueueList();
                    }
                });

        View layout = getLayoutInflater().inflate(R.layout.dlg_lp_debug_stream, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dlg = builder.create();
        dlg.setView(layout);

        Window window = dlg.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.2f;
        window.setAttributes(lp);

        tvStreamInfo = (TextView) layout.findViewById(R.id.tv_dlg_debug_stream);
        subscriptionOfStreamInfo = Observable.interval(1500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (userMediaModels != null && userMediaModels.size() > 0) {
                            StringBuilder builder = new StringBuilder();
                            for (int i = 0; i < userMediaModels.size(); i++) {
                                ConcurrentHashMap<Integer, LPAVMediaModel> map = getLiveRoom().getPlayer().getChmUserStream();
                                if (map != null && map.size() > 0) {
                                    LPAVMediaModel mediaModel = map.get(Integer.valueOf(userMediaModels.get(i).getUser().getUserId()));
                                    if (mediaModel != null) {
                                        LivePlayerInfo info = getLiveRoom().getLivePlayer().getStreamInfo(mediaModel.streamId);
                                        int videoBytesPerSecond = (int) ((HashMap) info.getPlayInfoObject().get("stream")).get("videoBytesPerSecond");
                                        int audioBytesPerSecond = (int) ((HashMap) info.getPlayInfoObject().get("stream")).get("audioBytesPerSecond");
                                        double videoBufferLength = (double) ((HashMap) info.getPlayInfoObject().get("stream")).get("videoBufferLength");
                                        double audioBufferLength = (double) ((HashMap) info.getPlayInfoObject().get("stream")).get("audioBufferLength");
                                        int videoLossRate = (int) ((HashMap) info.getPlayInfoObject().get("stream")).get("videoLossRate");
                                        int audioLossRate = (int) ((HashMap) info.getPlayInfoObject().get("stream")).get("audioLossRate");
                                        String tcpOrUdp = "";
                                        switch (mediaModel.userLinkType) {
                                            case TCP:
                                                tcpOrUdp = "TCP";
                                                break;
                                            case UDP:
                                                tcpOrUdp = "UDP";
                                                break;
                                        }
                                        builder.append("\n" + userMediaModels.get(i).getUser().getName() + "  ↓ ")
                                                .append("\nvideoBytesPerSecond:" + videoBytesPerSecond * 8 / 1024 + "kb/s  audioBytesPerSecond:" + audioBytesPerSecond * 8 / 1024 + "kb/s"
                                                        + "\nvideoBufferLength:" + videoBufferLength + "  audioBufferLength:" + audioBufferLength
                                                        + "\nvideoLossRate:" + videoLossRate + "  audioLossRate:" + audioLossRate
                                                        + "\nLinkType:" + tcpOrUdp
                                                        + "\n=====================");
                                    }
                                }
                            }
                            tvStreamInfo.setText(builder.toString());
                        } else {
                            tvStreamInfo.setText("没有发言用户");
                        }
                    }
                });
        dlg.show();
    }

    @Override
    public void showHuiyinDebugPanel() {
        MaterialDialog dlg = new MaterialDialog.Builder(this)
                .title("debug面板")
                .customView(R.layout.dlg_lp_debug_panel, true)
                .positiveText("确认")
                .negativeText("取消")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String s = etDebugAecDelay.getText().toString().trim();
                        if (TextUtils.isEmpty(s)) {
                            delay = 0;
                        } else {
                            delay = Integer.valueOf(s);
                        }
//                        getLiveRoom().getLivePlayer().setAecParameters(aecMode, aecmMode, delay);
//                        getLiveRoom().getLivePlayer().setAudioSource(audioSource);
//                        getLiveRoom().getLivePlayer().setCommunicationMode(isCommunication);
                        dialog.dismiss();
                        Toast.makeText(LiveRoomActivity.this, "aecMode: " + aecMode + "\naecmMode: " + aecmMode + "\ndelay: " + delay + "\naudio source: " + audioSource + "\n 通话模式：" + isCommunication, Toast.LENGTH_LONG).show();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .build();
        rgAecMode = (RadioGroup) dlg.getCustomView().findViewById(R.id.rg_lp_debug_mode_aec);
        rgAecmMode = (RadioGroup) dlg.getCustomView().findViewById(R.id.rg_lp_debug_mode_aecm);
        rgAudioSourceMode = (RadioGroup) dlg.getCustomView().findViewById(R.id.rg_lp_debug_mode_audio_source);
        rgCommunication = (RadioGroup) dlg.getCustomView().findViewById(R.id.rg_lp_debug_mode_is_communication);
        etDebugAecDelay = (EditText) dlg.getCustomView().findViewById(R.id.et_debug_aec_delay);
        rgAecMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.aec_unchanged) {
                    aecMode = 0;
                } else if (checkedId == R.id.aec_default) {
                    aecMode = 1;
                } else if (checkedId == R.id.aec_conference) {
                    aecMode = 2;
                } else if (checkedId == R.id.aec_aec) {
                    aecMode = 3;
                } else if (checkedId == R.id.aec_aecm) {
                    aecMode = 4;
                }
            }
        });
        rgAecmMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.aecm_quiet_or_headset) {
                    aecmMode = 0;
                } else if (checkedId == R.id.aecm_ear_piece) {
                    aecmMode = 1;
                } else if (checkedId == R.id.aecm_loud_ear_piece) {
                    aecmMode = 2;
                } else if (checkedId == R.id.aecm_speaker_phone) {
                    aecmMode = 3;
                } else if (checkedId == R.id.aecm_loud_speaker_phone) {
                    aecmMode = 4;
                }
            }
        });
        rgAudioSourceMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.audio_source_default) {
                    audioSource = 0;
                } else if (checkedId == R.id.audio_source_mic) {
                    audioSource = 1;
                } else if (checkedId == R.id.audio_source_uplink) {
                    audioSource = 2;
                } else if (checkedId == R.id.audio_source_downlink) {
                    audioSource = 3;
                } else if (checkedId == R.id.audio_source_voice_call) {
                    audioSource = 4;
                } else if (checkedId == R.id.audio_source_camcorder) {
                    audioSource = 5;
                } else if (checkedId == R.id.audio_source_recognition) {
                    audioSource = 6;
                } else if (checkedId == R.id.audio_source_communication) {
                    audioSource = 7;
                }
            }
        });
        rgCommunication.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.communication_on) {
                    isCommunication = true;
                } else if (checkedId == R.id.communication_off) {
                    isCommunication = false;
                }
            }
        });
        dlg.show();
    }

    @Override
    public void enableStudentSpeakMode() {
        // 一对一、小班课上课学生自动举手上麦
        if (liveRoom.getCurrentUser().getType() == LPConstants.LPUserType.Student) {
            if (liveRoom.getRoomType() == LPConstants.LPRoomType.Signal ||
                    liveRoom.getRoomType() == LPConstants.LPRoomType.SmallGroup) {
                if (liveRoom.isClassStarted()) {
                    liveRoom.getRecorder().publish();
                    attachLocalAudio();
                    if (liveRoom.getAutoOpenCameraStatus()) {
                        Observable.timer(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new LPErrorPrintSubscriber<Long>() {
                                    @Override
                                    public void call(Long aLong) {
                                        attachLocalVideo();
                                    }
                                });
                    }
                    rightMenuFragment.showAutoSpeak();
                }
            }
        }
    }

    @Override
    public void setFullScreenView(View view) {
        setZOrderMediaOverlayFalse(view);
        flBackground.addView(view, lpBackground);
        if (view == lppptFragment.getView()) {
            lppptFragment.onStart();
        } else if (view instanceof RecorderView) {
            liveRoom.getRecorder().invalidVideo();
        }
    }

    @Override
    public MyPPTFragment getPPTFragment() {
        checkNotNull(lppptFragment);
        return lppptFragment;
    }

    public void showRollCallDlg(int time, OnPhoneRollCallListener.RollCall rollCallListener) {
        RollCallDialogFragment rollCallDialogFragment = new RollCallDialogFragment();
        rollCallDialogFragment.setCancelable(false);
        rollCallDialogPresenter = new RollCallDialogPresenter(rollCallDialogFragment);
        rollCallDialogPresenter.setRollCallInfo(time, rollCallListener);
        bindVP(rollCallDialogFragment, rollCallDialogPresenter);
        showDialogFragment(rollCallDialogFragment);
    }

    @Override
    public void dismissRollCallDlg() {
        if (rollCallDialogPresenter != null) {
            rollCallDialogPresenter.timeOut();
        }
    }

    @Override
    public void onQuizStartArrived(final LPJsonModel jsonModel) {
        dismissQuizDlg();
        quizFragment = new QuizDialogFragment();
        Bundle args = new Bundle();
        int forceJoin = 0;
        if (JsonObjectUtil.isJsonNull(jsonModel.data, "force_join")) {
            forceJoin = 0;
        } else {
            forceJoin = JsonObjectUtil.getAsInt(jsonModel.data, "force_join");
        }
        args.putBoolean(QuizDialogFragment.KEY_FORCE_JOIN, forceJoin == 1);
        quizFragment.setArguments(args);
        quizFragment.setCancelable(false);
        quizPresenter = new QuizDialogPresenter(quizFragment);
        quizFragment.onStartArrived(jsonModel);
        bindVP(quizFragment, quizPresenter);
        showDialogFragment(quizFragment);
    }

    @Override
    public void onQuizEndArrived(LPJsonModel jsonModel) {
        if (quizFragment == null) {
            return;
        }
        quizFragment.onEndArrived(jsonModel);
    }

    @Override
    public void onQuizSolutionArrived(final LPJsonModel jsonModel) {
        dismissQuizDlg();
        quizFragment = new QuizDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(QuizDialogFragment.KEY_FORCE_JOIN, false);
        quizFragment.setArguments(args);
        quizPresenter = new QuizDialogPresenter(quizFragment);
        quizFragment.onSolutionArrived(jsonModel);
        bindVP(quizFragment, quizPresenter);
        showDialogFragment(quizFragment);
    }

    @Override
    public void onQuizRes(final LPJsonModel jsonModel) {
        dismissQuizDlg();
        quizFragment = new QuizDialogFragment();
        Bundle args = new Bundle();
        int forceJoin = 0;
        if (JsonObjectUtil.isJsonNull(jsonModel.data, "force_join")) {
            forceJoin = 0;
        } else {
            forceJoin = JsonObjectUtil.getAsInt(jsonModel.data, "force_join");
        }
        args.putBoolean(QuizDialogFragment.KEY_FORCE_JOIN, forceJoin == 1);
        quizFragment.setArguments(args);
        quizFragment.setCancelable(false);
        quizPresenter = new QuizDialogPresenter(quizFragment);
        quizFragment.onQuizResArrived(jsonModel);
        bindVP(quizFragment, quizPresenter);
        showDialogFragment(quizFragment);
    }

    @Override
    public void dismissQuizDlg() {
        if (quizFragment != null && quizFragment.isAdded() && quizFragment.isVisible()) {
            quizFragment.dismissAllowingStateLoss();
        }
    }

    private static final int REQUEST_CODE_PERMISSION_CAMERA = 1;
    private static final int REQUEST_CODE_PERMISSION_MIC = 2;

    @Override
    public boolean checkCameraPermission() {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(LiveRoomActivity.this, Manifest.permission.CAMERA)) {
            return true;
        } else {
            ActivityCompat.requestPermissions(LiveRoomActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION_CAMERA);
        }
        return false;
    }

    private boolean checkMicPermission() {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(LiveRoomActivity.this, Manifest.permission.RECORD_AUDIO)) {
            return true;
        } else {
            ActivityCompat.requestPermissions(LiveRoomActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_PERMISSION_MIC);
        }
        return false;
    }

    @Override
    public void attachLocalAudio() {
        if (checkMicPermission())
            liveRoom.getRecorder().attachAudio();
    }

    private void showSystemSettingDialog(int type) {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.live_sweet_hint))
                .content(type == REQUEST_CODE_PERMISSION_CAMERA ? getString(R.string.live_no_camera_permission) : getString(R.string.live_no_mic_permission))
                .positiveText(getString(R.string.live_quiz_dialog_confirm))
                .positiveColor(ContextCompat.getColor(LiveRoomActivity.this, R.color.live_blue))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                    }
                })
                .canceledOnTouchOutside(true)
                .build()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speakerPresenter.attachVideo();
                } else {
                    showSystemSettingDialog(REQUEST_CODE_PERMISSION_CAMERA);
                }
                break;
            case REQUEST_CODE_PERMISSION_MIC:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    liveRoom.getRecorder().attachAudio();
                } else {
                    showSystemSettingDialog(REQUEST_CODE_PERMISSION_MIC);
                }
                break;
            default:
                break;
        }
    }

    private void showNetError(LPError error) {
        if (errorFragment != null && errorFragment.isAdded()) return;
        if (flError.getChildCount() >= 2) return;
        if (loadingFragment != null && loadingFragment.isAdded()) {
            removeFragment(loadingFragment);
        }
        errorFragment = ErrorFragment.newInstance("好像断网了", error.getMessage(), ErrorFragment.ERROR_HANDLE_RECONNECT);
        errorFragment.setRouterListener(this);
        flError.setVisibility(View.VISIBLE);
        addFragment(R.id.activity_live_room_error, errorFragment);
        if (Build.VERSION.SDK_INT < 24) {
            getSupportFragmentManager().executePendingTransactions();
        }
    }

    @Override
    public void doReEnterRoom() {
        if (errorFragment != null && errorFragment.isAdded()) {
            removeFragment(errorFragment);
        }
        removeFragment(lppptFragment);
        removeFragment(topBarFragment);
        removeFragment(cloudRecordFragment);
        removeFragment(leftMenuFragment);
        removeFragment(pptLeftFragment);
        removeFragment(rightMenuFragment);
        removeFragment(rightBottomMenuFragment);
        removeFragment(chatFragment);
        removeFragment(speakersFragment);

        if (loadingFragment != null && loadingFragment.isAdded())
            removeFragment(loadingFragment);

        flBackground.removeAllViews();
        getSupportFragmentManager().executePendingTransactions();

        liveRoom.quitRoom();

        flLoading.setVisibility(View.VISIBLE);
        loadingFragment = new LoadingFragment();
        Bundle args = new Bundle();
        args.putBoolean("show_tech_support", shouldShowTechSupport);
        loadingFragment.setArguments(args);
        LoadingPresenter loadingPresenter;
        if (roomId == -1) {
            loadingPresenter = new LoadingPresenter(loadingFragment, code, name);
        } else {
            loadingPresenter = new LoadingPresenter(loadingFragment, roomId, sign, enterUser);
        }
        bindVP(loadingFragment, loadingPresenter);
        addFragment(R.id.activity_live_room_loading, loadingFragment);
    }

    @Override
    public void navigateToMain() {

        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        if (current <= 1) {
            liveRoom.getPlayer().mute();
        }

        globalPresenter = new GlobalPresenter();
        globalPresenter.setRouter(this);
        globalPresenter.subscribe();

        lppptFragment = MyPPTFragment.newInstance(liveRoom);
        bindVP(lppptFragment, new PPTPresenter(lppptFragment));
        addFragment(R.id.activity_live_room_background_container, lppptFragment);
        subscriptionOfEmptyBoard = lppptFragment.getObservableOfEmptyBoard().subscribe(new LPErrorPrintSubscriber<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                speakerPresenter.notifyEmptyPPTStatus(aBoolean);
            }
        });

        topBarFragment = new TopBarFragment();
        bindVP(topBarFragment, new TopBarPresenter(topBarFragment));
        addFragment(R.id.activity_live_room_top, topBarFragment);

        cloudRecordFragment = new CloudRecordFragment();
        bindVP(cloudRecordFragment, new CloudRecordPresenter());
        addFragment(R.id.activity_live_room_cloud_record, cloudRecordFragment);

        speakersFragment = new SpeakersFragment();
        speakerPresenter = new SpeakerPresenter(speakersFragment, disableSpeakQueuePlaceholder);
        bindVP(speakersFragment, speakerPresenter);
        addFragment(R.id.activity_live_room_speakers_container, speakersFragment);

        leftMenuFragment = new LeftMenuFragment();
        bindVP(leftMenuFragment, new LeftMenuPresenter(leftMenuFragment));
        addFragment(R.id.activity_live_room_bottom_left, leftMenuFragment);

        pptLeftFragment = new PPTLeftFragment();
        bindVP(pptLeftFragment, new PPTLeftPresenter(pptLeftFragment));
        addFragment(R.id.activity_live_room_ppt_left, pptLeftFragment);

        rightMenuFragment = new RightMenuFragment();
        rightMenuPresenter = new RightMenuPresenter(rightMenuFragment);
        bindVP(rightMenuFragment, rightMenuPresenter);
        addFragment(R.id.activity_live_room_right, rightMenuFragment);

        rightBottomMenuFragment = new RightBottomMenuFragment();
        bindVP(rightBottomMenuFragment, new RightBottomMenuPresenter(rightBottomMenuFragment));
        addFragment(R.id.activity_live_room_bottom_right, rightBottomMenuFragment);

        chatFragment = new ChatFragment();
        chatPresenter = new ChatPresenter(chatFragment);
        bindVP(chatFragment, chatPresenter);
        addFragment(R.id.activity_live_room_chat, chatFragment);

        RxUtils.unSubscribe(subscriptionOfLoginConflict);
        subscriptionOfLoginConflict = getLiveRoom().getObservableOfLoginConflict().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<ILoginConflictModel>() {
                    @Override
                    public void call(ILoginConflictModel iLoginConflictModel) {
                        if (enterRoomConflictListener != null) {
                            enterRoomConflictListener.onConflict(LiveRoomActivity.this, iLoginConflictModel.getConflictEndType()
                                    , new LiveSDKWithUI.LPRoomExitCallback() {
                                        @Override
                                        public void exit() {
                                            LiveRoomActivity.super.finish();
                                        }

                                        @Override
                                        public void cancel() {
                                            LiveRoomActivity.super.finish();
                                        }
                                    });
                        } else {
                            LiveRoomActivity.super.finish();
                        }
                    }
                });
        if (getLiveRoom().getCurrentUser().getType() == LPConstants.LPUserType.Teacher) {
            liveRoom.requestClassStart();
        }

        if (shareListener != null) {
            shareListener.getShareData(this, liveRoom.getRoomId());
        }

        // might delay 500ms to process
        Observable.timer(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<Long>() {
                    @Override
                    public void call(Long aLong) {
                        removeFragment(loadingFragment);
                        flLoading.setVisibility(View.GONE);
                        flError.setVisibility(View.GONE);

                        if (liveRoom.getCurrentUser().getType() == LPConstants.LPUserType.Teacher) {
                            liveRoom.getRecorder().publish();
                            attachLocalAudio();
                            attachLocalVideo();
                            if (liveRoom.getAutoStartCloudRecordStatus() == 1) {
                                liveRoom.requestCloudRecord(true);
                            }
                        } else if (liveRoom.getCurrentUser().getType() == LPConstants.LPUserType.Student) {
                            enableStudentSpeakMode();
                        }
                        if (!isTeacherOrAssistant() && liveRoom.getTeacherUser() == null)
                            showMessage("老师不在教室");
                    }
                });

        //成功进入房间后统一不再显示
        shouldShowTechSupport = false;
    }

    @Override
    public void clearScreen() {
        chatFragment.clearScreen();
        isClearScreen = true;
        rightBottomMenuFragment.clearScreen();
        hideFragment(topBarFragment);
        hideFragment(rightMenuFragment);
        flLeft.setVisibility(View.INVISIBLE);
        flRight.setVisibility(View.INVISIBLE);
        flRightBottom.setVisibility(View.INVISIBLE);
        onRecordFullScreenConfigurationChanged(true);
    }

    @Override
    public void unClearScreen() {
        chatFragment.unClearScreen();
        isClearScreen = false;
        rightBottomMenuFragment.unClearScreen();
        showFragment(topBarFragment);
        showFragment(rightMenuFragment);
        flLeft.setVisibility(View.VISIBLE);
        flRight.setVisibility(View.VISIBLE);
        flRightBottom.setVisibility(View.VISIBLE);
        onRecordFullScreenConfigurationChanged(false);
        dlChat.openDrawer(Gravity.START);
    }

    @Override
    public void navigateToMessageInput() {
        MessageSentFragment fragment = MessageSentFragment.newInstance();
        MessageSendPresenter presenter = new MessageSendPresenter(fragment);
        bindVP(fragment, presenter);
        showDialogFragment(fragment);
    }

    @Override
    public void navigateToQuickSwitchPPT(int currentIndex, int maxIndex) {
        if (lppptFragment != null && lppptFragment.isEditable() && rightMenuPresenter != null)
            rightMenuPresenter.changeDrawing();
        QuickSwitchPPTFragment quickSwitchPPTFragment = QuickSwitchPPTFragment.newInstance();
        Bundle args = new Bundle();
        args.putInt("currentIndex", currentIndex);
        args.putInt("maxIndex", maxIndex);
        quickSwitchPPTFragment.setArguments(args);
        switchPPTFragmentPresenter = new SwitchPPTFragmentPresenter(quickSwitchPPTFragment);
        bindVP(quickSwitchPPTFragment, switchPPTFragmentPresenter);
        showDialogFragment(quickSwitchPPTFragment);
    }

    @Override
    public void updateQuickSwitchPPTMaxIndex(int index) {
        if (switchPPTFragmentPresenter != null) {
            switchPPTFragmentPresenter.notifyMaxIndexChange(index);
        }
    }

    @Override
    public void notifyPageCurrent(int position) {
        lppptFragment.updatePage(position, true);
    }

    @Override
    public void navigateToPPTDrawing() {
        checkNotNull(lppptFragment);
        lppptFragment.changePPTCanvasMode();

        int currentOrientation = getResources().getConfiguration().orientation;
        if (lppptFragment.isEditable()) {
            flTop.setVisibility(View.GONE);
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                checkNotNull(leftMenuFragment);
                flLeft.setVisibility(View.GONE);
                dlChat.setVisibility(View.GONE);
                flRightBottom.setVisibility(View.INVISIBLE);
            }
            flPPTLeft.setVisibility(View.VISIBLE);
        } else {
            flTop.setVisibility(View.VISIBLE);
            flLeft.setVisibility(View.VISIBLE);
            dlChat.setVisibility(View.VISIBLE);
            flRightBottom.setVisibility(View.VISIBLE);
            dlChat.openDrawer(Gravity.START);
            flPPTLeft.setVisibility(View.GONE);
        }
    }

    @Override
    public LPConstants.LPPPTShowWay getPPTShowType() {
        return lppptFragment.getPPTShowWay();
    }

    @Override
    public void setPPTShowType(LPConstants.LPPPTShowWay type) {
        lppptFragment.setPPTShowWay(type);
    }

    @Override
    public void navigateToUserList() {
        OnlineUserDialogFragment userListFragment = OnlineUserDialogFragment.newInstance();
        OnlineUserPresenter userPresenter = new OnlineUserPresenter(userListFragment);
        bindVP(userListFragment, userPresenter);
        showDialogFragment(userListFragment);
    }

    @Override
    public void navigateToPPTWareHouse() {
        PPTManageFragment pptManageFragment = PPTManageFragment.newInstance();
        if (pptManagePresenter == null) {
            pptManagePresenter = new PPTManagePresenter();
            pptManagePresenter.setRouter(this);
            pptManagePresenter.subscribe();
        }
        pptManageFragment.setPresenter(pptManagePresenter);
        showDialogFragment(pptManageFragment);
    }

    @Override
    public void disableSpeakerMode() {
        if (!isTeacherOrAssistant())
            rightBottomMenuFragment.disableSpeakerMode();
        detachLocalVideo();
        if (getLiveRoom().getRecorder().isPublishing())
            getLiveRoom().getRecorder().stopPublishing();
    }

    @Override
    public void enableSpeakerMode() {
        rightBottomMenuFragment.enableSpeakerMode();
    }

    @Override
    public void showMorePanel(int anchorX, int anchorY) {
        MoreMenuDialogFragment fragment = MoreMenuDialogFragment.newInstance(anchorX, anchorY);
        MoreMenuPresenter presenter = new MoreMenuPresenter(fragment);
        bindVP(fragment, presenter);
        showDialogFragment(fragment);
    }

    @Override
    public void navigateToShare() {
        if (shareListener.setShareList() == null) {
            return;
        }
        LPShareDialog shareDialog = LPShareDialog.newInstance(shareListener.setShareList());
        shareDialog.setListener(new LPShareDialog.LPShareClickListener() {
            @Override
            public void onShareClick(int type) {
                shareListener.onShareClicked(LiveRoomActivity.this, type);
            }
        });
        showDialogFragment(shareDialog);
    }

    @Override
    public void navigateToAnnouncement() {
        globalPresenter.unObserveAnnouncementChange();
        AnnouncementFragment fragment = AnnouncementFragment.newInstance();
        AnnouncementPresenter presenter = new AnnouncementPresenter(fragment, globalPresenter);
        bindVP(fragment, presenter);
        showDialogFragment(fragment);
    }

    /**
     * @param recordStatus 当前的状态 1:正在录制 0:未录制
     */
    @Override
    public void navigateToCloudRecord(boolean recordStatus) {
        if (recordStatus) {
            flCloudRecord.setVisibility(View.VISIBLE);
            showFragment(cloudRecordFragment);
        } else {
            flCloudRecord.setVisibility(View.GONE);
            hideFragment(cloudRecordFragment);
        }
    }

    @Override
    public void navigateToHelp() {

    }

    @Override
    public void navigateToSetting() {
        SettingDialogFragment settingFragment = SettingDialogFragment.newInstance();
        SettingPresenter settingPresenter = new SettingPresenter(settingFragment);
        bindVP(settingFragment, settingPresenter);
        showDialogFragment(settingFragment);
    }

    @Override
    public boolean isTeacherOrAssistant() {
        return getLiveRoom().getCurrentUser().getType() == LPConstants.LPUserType.Teacher ||
                getLiveRoom().getCurrentUser().getType() == LPConstants.LPUserType.Assistant;
    }

    @Override
    public void attachLocalVideo() {
        speakerPresenter.attachVideo();
    }

    @Override
    public void detachLocalVideo() {
        speakerPresenter.detachVideo();
    }

    public boolean isPPTMax() {
        return flBackground.getChildAt(0) == lppptFragment.getView();
    }

    @Override
    public void clearPPTAllShapes() {
        checkNotNull(lppptFragment);
        lppptFragment.eraseAllShape();
    }

    @Override
    public void changeScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public int getCurrentScreenOrientation() {
        return getResources().getConfiguration().orientation;
    }

    @Override
    public int getSysRotationSetting() {
        int status = 0;
        try {
            status = Settings.System.getInt(getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return status;
    }

    @Override
    public void letScreenRotateItself() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public void forbidScreenRotateItself() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
    }

    @Override
    public void showBigChatPic(String url) {
        ChatPictureViewFragment fragment = ChatPictureViewFragment.newInstance(url);
        ChatPictureViewPresenter presenter = new ChatPictureViewPresenter();
        bindVP(fragment, presenter);
        showDialogFragment(fragment);
    }

    @Override
    public void sendImageMessage(String path) {
        chatPresenter.sendImageMessage(path);
    }

    public void showSavePicDialog(byte[] bmpArray) {
        ChatSavePicDialogFragment fragment = new ChatSavePicDialogFragment();
        ChatSavePicDialogPresenter presenter = new ChatSavePicDialogPresenter(bmpArray);
        bindVP(fragment, presenter);
        showDialogFragment(fragment);
    }

    @Override
    public void realSaveBmpToFile(byte[] bmpArray) {
        saveImageToGallery(bmpArray);
    }

    @Override
    public void doHandleErrorNothing() {
        removeFragment(errorFragment);
    }

    private void setZOrderMediaOverlayTrue(View view) {
        if (view instanceof SurfaceView) {
            ((SurfaceView) view).setZOrderMediaOverlay(true);
        } else if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setZOrderMediaOverlayTrue(((ViewGroup) view).getChildAt(i));
            }
        }
    }

    private void setZOrderMediaOverlayFalse(View view) {
        if (view instanceof SurfaceView) {
            ((SurfaceView) view).setZOrderMediaOverlay(false);
        } else if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setZOrderMediaOverlayFalse(((ViewGroup) view).getChildAt(i));
            }
        }
    }

    private <V extends BaseView, P extends BasePresenter> void bindVP(V view, P presenter) {
        presenter.setRouter(this);
        view.setPresenter(presenter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        shouldShowTechSupport = true;
        if (pptManagePresenter != null) {
            pptManagePresenter.destroy();
            pptManagePresenter = null;
        }
        if (globalPresenter != null) {
            globalPresenter.destroy();
            globalPresenter = null;
        }

        RxUtils.unSubscribe(subscriptionOfLoginConflict);
        RxUtils.unSubscribe(subscriptionOfEmptyBoard);
        RxUtils.unSubscribe(subscriptionOfStreamInfo);
        RxUtils.unSubscribe(subscriptionOfOnlineUserDebug);

        orientationEventListener = null;

        getLiveRoom().quitRoom();
    }

    @Override
    public void onBackPressed() {
        if (exitListener != null) {
            super.onBackPressed();
        } else {
            new MaterialDialog.Builder(this)
                    .title(getString(R.string.live_exit_hint_title))
                    .content(getString(R.string.live_exit_hint_content))
                    .contentColor(ContextCompat.getColor(getContext(), R.color.live_text_color_light))
                    .positiveColor(ContextCompat.getColor(getContext(), R.color.live_blue))
                    .positiveText(getString(R.string.live_exit_hint_confirm))
                    .negativeColor(ContextCompat.getColor(getContext(), R.color.live_text_color))
                    .negativeText(getString(R.string.live_cancel))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            finish();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            materialDialog.dismiss();
                        }
                    })
                    .build()
                    .show();
        }
    }

    //初始化时检测屏幕方向，以此设置聊天页面是否可隐藏
    private void checkScreenOrientationInit() {
        Configuration configuration = this.getResources().getConfiguration();
        int orientation = configuration.orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            dlChat.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            dlChat.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        }
    }

    /**
     * 保存图片
     */
    private void saveImageToGallery(final byte[] bmpArray) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 首先保存图片
                File appDir = new File(Environment.getExternalStorageDirectory(), "bjhl_lp_image");
                if (!appDir.exists()) {
                    appDir.mkdir();
                }
                String fileName = System.currentTimeMillis() + ".jpg";
                File file = new File(appDir, fileName);
                final String picPath = file.getAbsolutePath();
                try {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bmpArray, 0, bmpArray.length);
                    FileOutputStream fos = new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // 其次把文件插入到系统图库
                try {
                    MediaStore.Images.Media.insertImage(getContentResolver(),
                            file.getAbsolutePath(), fileName, null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                // 最后通知图库更新
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LiveRoomActivity.this, "图片保存在" + picPath, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).start();
    }

    @Override
    public void finish() {
        if (exitListener != null) {
            exitListener.onRoomExit(this, new LiveSDKWithUI.LPRoomExitCallback() {
                @Override
                public void exit() {
                    tryToCloseCloudRecord();
                    clearStaticCallback();
                    LiveRoomActivity.super.finish();
                }

                @Override
                public void cancel() {
                }
            });
        } else {
            tryToCloseCloudRecord();
            clearStaticCallback();
            super.finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int current = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            if (current >= 1) {
                liveRoom.getPlayer().unMute();
            }
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_RAISE,
                    AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int current = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            if (current <= 1) {
                liveRoom.getPlayer().mute();
            }
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_LOWER,
                    AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void tryToCloseCloudRecord() {
        // 如果是被踢下线 core里面会调quitRoom
        if (getLiveRoom().isQuit()) return;

        if (getLiveRoom().getCurrentUser().getType() == LPConstants.LPUserType.Teacher) {
            if (getLiveRoom().getCloudRecordStatus()) {
                liveRoom.requestCloudRecord(false);
            }
            liveRoom.requestClassEnd();
        }
    }

    /* 房间外部回调 */
    private static LiveSDKWithUI.LPShareListener shareListener;
    private static LiveSDKWithUI.LPRoomExitListener exitListener;
    private static LiveSDKWithUI.RoomEnterConflictListener enterRoomConflictListener;
    private static LiveSDKWithUI.LPRoomResumeListener roomLifeCycleListener;

    private static boolean shouldShowTechSupport = true;

    private static boolean disableSpeakQueuePlaceholder = false;

    private void clearStaticCallback() {
        shareListener = null;
        exitListener = null;
        enterRoomConflictListener = null;
        roomLifeCycleListener = null;
    }

    public static LiveSDKWithUI.LPRoomExitListener getExitListener() {
        return exitListener;
    }

    public static void setRoomLifeCycleListener(LiveSDKWithUI.LPRoomResumeListener roomLifeCycleListener) {
        LiveRoomActivity.roomLifeCycleListener = roomLifeCycleListener;
    }

    public static void setShareListener(LiveSDKWithUI.LPShareListener listener) {
        LiveRoomActivity.shareListener = listener;
    }

    public static void setRoomExitListener(LiveSDKWithUI.LPRoomExitListener roomExitListener) {
        LiveRoomActivity.exitListener = roomExitListener;
    }

    public static void setEnterRoomConflictListener(LiveSDKWithUI.RoomEnterConflictListener enterRoomConflictListener) {
        LiveRoomActivity.enterRoomConflictListener = enterRoomConflictListener;
    }

    public static void setShouldShowTechSupport(boolean shouldShow) {
        shouldShowTechSupport = shouldShow;
    }

    public static void disableSpeakQueuePlaceholder() {
        disableSpeakQueuePlaceholder = true;
    }
}
