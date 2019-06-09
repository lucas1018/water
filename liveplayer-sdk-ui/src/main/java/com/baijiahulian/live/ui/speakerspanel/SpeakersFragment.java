package com.baijiahulian.live.ui.speakerspanel;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.baijiahulian.live.ui.R;
import com.baijiahulian.live.ui.base.BaseFragment;
import com.baijiahulian.live.ui.utils.DisplayUtils;
import com.baijiahulian.live.ui.utils.QueryPlus;
import com.baijiahulian.live.ui.utils.RxUtils;
import com.baijiahulian.livecore.models.imodels.IMediaModel;
import com.baijiahulian.livecore.models.imodels.IUserModel;
import com.baijiahulian.livecore.ppt.whiteboard.LPWhiteBoardView;
import com.baijiahulian.livecore.utils.LPErrorPrintSubscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.baijiahulian.live.ui.speakerspanel.SpeakersContract.PPT_TAG;
import static com.baijiahulian.live.ui.speakerspanel.SpeakersContract.RECORD_TAG;
import static com.baijiahulian.live.ui.speakerspanel.SpeakersContract.VIEW_TYPE_APPLY;
import static com.baijiahulian.live.ui.speakerspanel.SpeakersContract.VIEW_TYPE_PPT;
import static com.baijiahulian.live.ui.speakerspanel.SpeakersContract.VIEW_TYPE_PRESENTER;
import static com.baijiahulian.live.ui.speakerspanel.SpeakersContract.VIEW_TYPE_RECORD;
import static com.baijiahulian.live.ui.speakerspanel.SpeakersContract.VIEW_TYPE_SPEAKER;
import static com.baijiahulian.live.ui.speakerspanel.SpeakersContract.VIEW_TYPE_VIDEO_PLAY;

/**
 * Created by Shubo on 2017/6/5.
 */

public class SpeakersFragment extends BaseFragment implements SpeakersContract.View {

    private SpeakersContract.Presenter presenter;
    private LinearLayout container;
    private RecorderView recorderView;
    private ViewGroup.LayoutParams lpItem;
    private TextView speakerRequest;
    private HorizontalScrollView scrollView;
    private static final int SHRINK_THRESHOLD = 3;
    private boolean disableSpeakQueuePlaceholder;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_speakers;
    }

    @Override
    public void setPresenter(SpeakersContract.Presenter presenter) {
        super.setBasePresenter(presenter);
        this.presenter = presenter;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        container = (LinearLayout) $.id(R.id.fragment_speakers_container).view();
        speakerRequest = (TextView) $.id(R.id.fragment_speakers_new_request_toast).view();
        scrollView = (HorizontalScrollView) $.id(R.id.fragment_speakers_scroll_view).view();
        lpItem = new ViewGroup.LayoutParams(DisplayUtils.dip2px(getActivity(), 100),
                DisplayUtils.dip2px(getActivity(), 76));
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (scrollView.getScrollX() == scrollView.getChildAt(0).getMeasuredWidth() - scrollView.getMeasuredWidth()) {
                    if (speakerRequest.getVisibility() == View.VISIBLE) {
                        speakerRequest.setVisibility(View.GONE);
                    }
                }
            }
        });

    }

    private boolean attachVideoOnResume = false;
    private boolean attachAudioOnResume = false;

    @Override
    public void onResume() {
        super.onResume();
        if (attachAudioOnResume) {
            attachAudioOnResume = false;
            if (!presenter.getRecorder().isPublishing())
                presenter.getRecorder().publish();
            if (!presenter.getRecorder().isAudioAttached())
                presenter.getRecorder().attachAudio();
        }
        Observable.timer(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (attachVideoOnResume) {
                            attachVideoOnResume = false;
                            if (!presenter.getRecorder().isPublishing())
                                presenter.getRecorder().publish();
                            if (!presenter.getRecorder().isVideoAttached())
                                presenter.getRecorder().attachVideo();
                        }
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (presenter.getRecorder().isVideoAttached()) {
            presenter.getRecorder().detachVideo();
            attachVideoOnResume = true;
        }
        if (presenter.getRecorder().isAudioAttached()) {
            presenter.getRecorder().detachAudio();
            attachAudioOnResume = true;
        }
    }

    @Override
    public void notifyItemChanged(int position) {
        if (container == null) return;
        if (presenter.getItemViewType(position) == VIEW_TYPE_SPEAKER) {
            container.removeViewAt(position);
            container.addView(generateSpeakView(presenter.getSpeakModel(position)), position);

//            if (presenter.getSpeakModel(position).isVideoOn()) {
//                presenter.playVideo(presenter.getSpeakModel(position).getUser().getUserId());
//            }
        } else if (presenter.getItemViewType(position) == VIEW_TYPE_PRESENTER) {
            IMediaModel model = presenter.getSpeakModel(position);
            container.removeViewAt(position);
            if (model == null) return;
            if (model.isVideoOn() && presenter.isAutoPlay()) {
                VideoView videoView;
                if (presenter.getWaterMark() != null) {
                    videoView = new VideoView(getActivity(), model.getUser().getName() + getString(R.string.live_presenter_hint),
                            presenter.getWaterMark().url, presenter.getWaterMark().pos);
                } else {
                    videoView = new VideoView(getActivity(), model.getUser().getName());
                }
                container.addView(videoView, position, lpItem);

                final GestureDetector gestureDetector = new GestureDetector(getActivity(),
                        new ClickGestureDetector(model.getUser().getUserId()));
                videoView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        gestureDetector.onTouchEvent(event);
                        return true;
                    }
                });
                presenter.getPlayer().playAVClose(presenter.getItem(position));
                presenter.getPlayer().playVideo(presenter.getItem(position), videoView.getSurfaceView());
            } else {
                presenter.getPlayer().playAVClose(presenter.getItem(position));
                presenter.getPlayer().playAudio(presenter.getItem(position));
                container.addView(generateSpeakView(presenter.getSpeakModel(position)), position);
            }
        } else if (presenter.getItemViewType(position) == VIEW_TYPE_VIDEO_PLAY) {
            container.removeViewAt(position);
            IMediaModel model = presenter.getSpeakModel(position);
            VideoView videoView = new VideoView(getActivity(), model.getUser().getName());
            container.addView(videoView, position, lpItem);
            final GestureDetector gestureDetector = new GestureDetector(getActivity(), new ClickGestureDetector(model.getUser().getUserId()));
            videoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });
            presenter.getPlayer().playAVClose(presenter.getItem(position));
            presenter.getPlayer().playVideo(presenter.getItem(position), videoView.getSurfaceView());
        }
    }

    @Override
    public void notifyItemInserted(final int position) {
        if (container == null) return;
        switch (presenter.getItemViewType(position)) {
            case VIEW_TYPE_PPT:
//                container.addView(presenter.getPPTView(), position);
                break;
            case VIEW_TYPE_PRESENTER:
                IMediaModel presenterSpeakModel = presenter.getSpeakModel(position);
                if (presenterSpeakModel.isVideoOn()) {
                    VideoView videoView;
                    if (presenter.getWaterMark() == null) {
                        videoView = new VideoView(getActivity(), presenterSpeakModel.getUser().getName());
                    } else {
                        videoView = new VideoView(getActivity(), presenterSpeakModel.getUser().getName() + getString(R.string.live_presenter_hint),
                                presenter.getWaterMark().url, presenter.getWaterMark().pos);
                    }
                    container.addView(videoView, position, lpItem);

                    final GestureDetector gestureDetector = new GestureDetector(getActivity(), new ClickGestureDetector(presenterSpeakModel.getUser().getUserId()));
                    videoView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            gestureDetector.onTouchEvent(event);
                            return true;
                        }
                    });
                    presenter.getPlayer().playAVClose(presenter.getItem(position));
                    presenter.getPlayer().playVideo(presenter.getItem(position), videoView.getSurfaceView());
                } else {
                    container.addView(generateSpeakView(presenter.getSpeakModel(position)), position);
                }
                break;
            case VIEW_TYPE_RECORD:
                if (recorderView == null) {
                    recorderView = new RecorderView(getActivity());
                    presenter.getRecorder().setPreview(recorderView);
                } else {
                    container.removeView(recorderView);
                }
                container.addView(recorderView, position, lpItem);

                final GestureDetector gestureDetector1 = new GestureDetector(getActivity(), new ClickGestureDetector(RECORD_TAG));
                recorderView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        gestureDetector1.onTouchEvent(event);
                        return true;
                    }
                });

                if (!presenter.getRecorder().isPublishing())
                    presenter.getRecorder().publish();
                if (!presenter.getRecorder().isVideoAttached())
                    presenter.getRecorder().attachVideo();
                break;
            case VIEW_TYPE_VIDEO_PLAY:
                IMediaModel model = presenter.getSpeakModel(position);
                VideoView videoView = new VideoView(getActivity(), model.getUser().getName());
                container.addView(videoView, position, lpItem);
                final GestureDetector gestureDetector = new GestureDetector(getActivity(), new ClickGestureDetector(model.getUser().getUserId()));
                videoView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        gestureDetector.onTouchEvent(event);
                        return true;
                    }
                });

                presenter.getPlayer().playAVClose(presenter.getItem(position));
                presenter.getPlayer().playVideo(presenter.getItem(position), videoView.getSurfaceView());
                break;
            case VIEW_TYPE_SPEAKER:
                IMediaModel speakerModel = presenter.getSpeakModel(position);
                View view = generateSpeakView(speakerModel);
                container.addView(view, position);

//                if (speakerModel.isVideoOn()) {
//                    presenter.playVideo(speakerModel.getUser().getUserId());
//                }

                break;
            case VIEW_TYPE_APPLY:
                View applyView = generateApplyView(presenter.getApplyModel(position));
                container.addView(applyView, position);
                final ViewGroup.LayoutParams params = applyView.getLayoutParams();
                WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                if ((position + 1) * params.width > wm.getDefaultDisplay().getWidth())
                    speakerRequest.setVisibility(View.VISIBLE);
                speakerRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        speakerRequest.setAnimation(createAnimation());
                        speakerRequest.startAnimation(createAnimation());
                        speakerRequest.setVisibility(View.GONE);
                        scrollView.smoothScrollTo((position + 1) * params.width, 0);
                    }
                });
                break;
            default:
                break;
        }
        presenter.changeBackgroundContainerSize(container.getChildCount() >= SHRINK_THRESHOLD);
        if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                && container.getChildCount() > 0) {
            setBackGroundVisible(true);
        }
    }

    private Animation createAnimation() {
        TranslateAnimation animation = new TranslateAnimation(0, 2 * speakerRequest.getWidth(), 0, 0);
        animation.setDuration(500);
        return animation;
    }

    @Override
    public void notifyItemDeleted(int position) {
        if (container == null) return;
        if (container.getChildCount() <= position) return;
        container.removeViewAt(position);
        if (presenter.getItemViewType(position) == VIEW_TYPE_RECORD) {
            if (presenter.getRecorder().isVideoAttached() && !presenter.isStopPublish()) {
                presenter.getRecorder().detachVideo();
            }
            presenter.setIsStopPublish(false);
        }
        if (speakerRequest.getVisibility() == View.VISIBLE) {
            speakerRequest.setVisibility(View.GONE);
        }
        presenter.changeBackgroundContainerSize(container.getChildCount() >= SHRINK_THRESHOLD);
        if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                && container.getChildCount() == 0) {
            setBackGroundVisible(false);
        }
    }

    @Override
    public View removeViewAt(int position) {
        View view = container.getChildAt(position);
        if (presenter.getPPTFragment().getView() == view) {
            presenter.getPPTFragment().onStop();
        }
        container.removeView(view);
        return view;
    }

    public void pptResume() {
        presenter.getPPTFragment().setOnSingleTapListener(new LPWhiteBoardView.OnSingleTapListener() {
            @Override
            public void onSingleTap(LPWhiteBoardView whiteBoardView) {
                showOptionDialog(PPT_TAG);
            }
        });
        presenter.getPPTFragment().setOnDoubleTapListener(new LPWhiteBoardView.OnDoubleTapListener() {
            @Override
            public void onDoubleTap(LPWhiteBoardView whiteBoardView) {
                presenter.setFullScreenTag(PPT_TAG);
            }
        });
    }

    @Override
    public void notifyViewAdded(View view, int position) {
        if (view.getParent() != null) return;
        container.addView(view, position, lpItem);
        if (presenter.getPPTFragment().getView() == view) {
            presenter.getPPTFragment().onStart();
            presenter.getPPTFragment().setOnSingleTapListener(new LPWhiteBoardView.OnSingleTapListener() {
                @Override
                public void onSingleTap(LPWhiteBoardView whiteBoardView) {
                    showOptionDialog(PPT_TAG);
                }
            });
            presenter.getPPTFragment().setOnDoubleTapListener(new LPWhiteBoardView.OnDoubleTapListener() {
                @Override
                public void onDoubleTap(LPWhiteBoardView whiteBoardView) {
                    presenter.setFullScreenTag(PPT_TAG);
                }
            });
        } else if (view instanceof RecorderView) {
            presenter.getRecorder().invalidVideo();
        }
    }

    @Override
    public void showMaxVideoExceed() {
        showToast(getString(R.string.live_speakers_max_video_exceed));
    }

    @Override
    public void notifyPresenterVideoSizeChange(int position, int height, int width) {
        if (container == null) return;

        if (container.getChildAt(position) instanceof VideoView) {
            ((VideoView) container.getChildAt(position)).resizeWaterMark(height, width);
        }
    }

    @Override
    public void notifyFullScreenPresenterStatusChange(int position, boolean b) {
//        View view = container.getChildAt(position);
//        if (view instanceof VideoView) {
//            ((VideoView) view).setIsPresenter(b);
//        }
    }

    @Override
    public void disableSpeakQueuePlaceholder() {
        disableSpeakQueuePlaceholder = true;
        $.id(R.id.fragment_speakers_scroll_view).backgroundDrawable(null);
    }

    private View generateApplyView(final IUserModel model) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_speak_apply, container, false);
        QueryPlus q = QueryPlus.with(view);
        q.id(R.id.item_speak_apply_avatar).image(getActivity(), model.getAvatar());
        q.id(R.id.item_speak_apply_name).text(model.getName() + getContext().getString(R.string.live_media_speak_applying));
        q.id(R.id.item_speak_apply_agree).clicked().subscribe(new LPErrorPrintSubscriber<Void>() {
            @Override
            public void call(Void aVoid) {
                presenter.agreeSpeakApply(model.getUserId());
            }
        });
        q.id(R.id.item_speak_apply_disagree).clicked().subscribe(new LPErrorPrintSubscriber<Void>() {
            @Override
            public void call(Void aVoid) {
                presenter.disagreeSpeakApply(model.getUserId());
            }
        });
        return view;
    }

    private View generateSpeakView(final IMediaModel model) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_speak_speaker, container, false);
        QueryPlus q = QueryPlus.with(view);
        if (presenter.getItemViewType(model.getUser().getUserId()) == VIEW_TYPE_PRESENTER)
            q.id(R.id.item_speak_speaker_name).text(model.getUser().getName() + getString(R.string.live_presenter_hint));
        else
            q.id(R.id.item_speak_speaker_name).text(model.getUser().getName());
        String avatar = model.getUser().getAvatar().startsWith("//") ? "https:" + model.getUser().getAvatar() : model.getUser().getAvatar();
        q.id(R.id.item_speak_speaker_avatar).image(getActivity(), avatar);
        q.id(R.id.item_speak_speaker_video_label).visibility(model.isVideoOn() ? View.VISIBLE : View.GONE);
        q.contentView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionDialog(model.getUser().getUserId());
            }
        });
        return view;
    }

    private class ClickGestureDetector extends GestureDetector.SimpleOnGestureListener {

        private String tag;

        ClickGestureDetector(String tag) {
            this.tag = tag;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (!clickableCheck()) {
                showToast(getString(R.string.live_frequent_error));
                return super.onSingleTapConfirmed(e);
            }
            if (!presenter.isFullScreen(tag)) {
                showOptionDialog(tag);
            } else {
                // clear screen
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    presenter.clearScreen();
                }
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (!clickableCheck()) {
                showToast(getString(R.string.live_frequent_error));
                return super.onDoubleTap(e);
            }
            if (!presenter.isFullScreen(tag)) {
                presenter.setFullScreenTag(tag);
            }
            return super.onDoubleTap(e);
        }
    }

    private void showOptionDialog(final String tag) {

        List<String> options = new ArrayList<>();

        switch (presenter.getItemViewType(tag)) {
            case VIEW_TYPE_PPT:
                options.add(getString(R.string.live_full_screen));
                break;
            case VIEW_TYPE_PRESENTER:
                IMediaModel presenterModel = presenter.getSpeakModel(tag);
                if (presenterModel == null) return; // 主讲人音视频未开启
                if (presenterModel.isVideoOn() && presenter.isAutoPlay()) {
                    options.add(getString(R.string.live_full_screen));
                    options.add(getString(R.string.live_close_video));
                } else if (presenterModel.isVideoOn() && !presenter.isAutoPlay()) {
                    options.add(getString(R.string.live_open_video));
                }
                break;
            case VIEW_TYPE_RECORD:
                options.add(getString(R.string.live_full_screen));
                options.add(getString(R.string.live_recorder_switch_camera));
                if (presenter.getRecorder().isBeautyFilterOn()) {
                    options.add(getString(R.string.live_recorder_pretty_filter_off));
                } else {
                    options.add(getString(R.string.live_recorder_pretty_filter_on));
                }
                options.add(getString(R.string.live_close_video));
                break;
            case VIEW_TYPE_VIDEO_PLAY:
                options.add(getString(R.string.live_full_screen));
                options.add(getString(R.string.live_close_video));
                if (presenter.isTeacherOrAssistant() && presenter.isMultiClass())
                    options.add(getString(R.string.live_close_speaking));
                break;
            case VIEW_TYPE_SPEAKER:
                IMediaModel model = presenter.getSpeakModel(tag);
                if (model != null) {
                    if (model.isVideoOn())
                        options.add(getString(R.string.live_open_video));
                    if (presenter.isTeacherOrAssistant() && presenter.isMultiClass())
                        options.add(getString(R.string.live_close_speaking));
                }
                break;
            default:
                break;
        }
        if (options.size() <= 0) return;
        new MaterialDialog.Builder(getActivity())
                .items(options)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        if (getString(R.string.live_close_video).equals(charSequence.toString())) {
                            presenter.closeVideo(tag);
                        } else if (getString(R.string.live_close_speaking).equals(charSequence.toString())) {
                            presenter.closeSpeaking(tag);
                        } else if (getString(R.string.live_open_video).equals(charSequence.toString())) {
                            presenter.playVideo(tag);
                        } else if (getString(R.string.live_full_screen).equals(charSequence.toString())) {
                            presenter.setFullScreenTag(tag);
                        } else if (getString(R.string.live_recorder_switch_camera).equals(charSequence.toString())) {
                            presenter.switchCamera();
                        } else if (getString(R.string.live_recorder_pretty_filter_off).equals(charSequence.toString())
                                || getString(R.string.live_recorder_pretty_filter_on).equals(charSequence.toString())) {
                            presenter.switchPrettyFilter();
                        }
                        materialDialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setBackGroundVisible(true);
        } else {
            if (container.getChildCount() > 3) {
                setBackGroundVisible(true);
            } else {
                setBackGroundVisible(false);
            }
        }
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

    public void setBackGroundVisible(boolean visible) {
        if(disableSpeakQueuePlaceholder){
            $.id(R.id.fragment_speakers_scroll_view).backgroundDrawable(null);
            return;
        }
        if (visible) {
            if (container.getChildCount() == 0)
                return;
            $.id(R.id.fragment_speakers_scroll_view).backgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.live_bg_speaker));
        } else {
            $.id(R.id.fragment_speakers_scroll_view).backgroundDrawable(null);
        }
    }
}
