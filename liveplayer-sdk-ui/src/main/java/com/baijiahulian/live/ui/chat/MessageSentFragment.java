package com.baijiahulian.live.ui.chat;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.baijiahulian.common.cropperv2.BJCommonImageCropHelper;
import com.baijiahulian.common.cropperv2.ThemeConfig;
import com.baijiahulian.common.cropperv2.model.PhotoInfo;
import com.baijiahulian.live.ui.R;
import com.baijiahulian.live.ui.base.BaseDialogFragment;
import com.baijiahulian.live.ui.chat.emoji.EmojiFragment;
import com.baijiahulian.live.ui.chat.emoji.EmojiSelectCallBack;
import com.baijiahulian.live.ui.utils.QueryPlus;
import com.baijiahulian.livecore.models.imodels.IExpressionModel;

import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Shubo on 2017/3/4.
 */

public class MessageSentFragment extends BaseDialogFragment implements MessageSendContract.View {

    private MessageSendContract.Presenter presenter;
    private QueryPlus $;
    private MessageTextWatcher textWatcher;
    private EmojiFragment emojiFragment;

    public static MessageSentFragment newInstance() {
        Bundle args = new Bundle();

        MessageSentFragment fragment = new MessageSentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_message_send;
    }

    @Override
    protected void setWindowParams(WindowManager.LayoutParams windowParams) {
        windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        windowParams.gravity = Gravity.BOTTOM | GravityCompat.END;
        windowParams.windowAnimations = R.style.LiveBaseSendMsgDialogAnim;
    }

    @Override
    protected void init(Bundle savedInstanceState, Bundle arguments) {
        super.hideTitleBar();
        $ = QueryPlus.with(contentView);
        textWatcher = new MessageTextWatcher();
        ((EditText) $.id(R.id.dialog_message_send_et).view()).addTextChangedListener(textWatcher);
        $.id(R.id.dialog_message_send_et).view().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                imm.showSoftInput($.id(R.id.dialog_message_send_et).view(), InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);
        $.id(R.id.dialog_message_send_btn).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.sendMessage(((EditText) $.id(R.id.dialog_message_send_et).view())
                        .getEditableText().toString());
                dismissAllowingStateLoss();
            }
        });
        $.id(R.id.dialog_message_send_pic).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemeConfig.Builder builder = new ThemeConfig.Builder();
                builder.setMainElementsColor(ContextCompat.getColor(getContext(), R.color.live_blue));
                BJCommonImageCropHelper.openImageSingleAblum(getActivity(), BJCommonImageCropHelper.PhotoCropType.Free, builder.build(), new BJCommonImageCropHelper.OnHandlerResultCallback() {
                    @Override
                    public void onHandlerSuccess(List<PhotoInfo> list) {
                        if (list.size() == 1) {
                            presenter.sendPicture(list.get(0).getPhotoPath());
                        }
                    }

                    @Override
                    public void onHandlerFailure(String s) {
                        showToast(s);
                    }
                });
            }
        });
        if (presenter.isTeacherOrAssistant()) {
            $.id(R.id.dialog_message_send_pic).visible();
        } else {
            $.id(R.id.dialog_message_send_pic).gone();
        }

        $.id(R.id.dialog_message_emoji).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.chooseEmoji();
            }
        });
        $.id(R.id.dialog_message_send_btn).enable(false);
    }

    @Override
    public void onStart() {
        super.onStart();
//        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }


    @Override
    public void setPresenter(MessageSendContract.Presenter presenter) {
        super.setBasePresenter(presenter);
        this.presenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((EditText) $.id(R.id.dialog_message_send_et).view()).removeTextChangedListener(textWatcher);
        $ = null;
        presenter = null;
    }

    @Override
    public void showMessageSuccess() {
        $.id(R.id.dialog_message_send_et).text("");
        dismissAllowingStateLoss();
    }

    @Override
    public void showEmojiPanel() {
        if (emojiFragment == null) {
            // hide keyborad
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {//isOpen若返回true，则表示输入法打开
                imm.hideSoftInputFromWindow($.id(R.id.dialog_message_send_et).view().getWindowToken(), 0);
            }
            contentView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    $.id(R.id.dialog_message_send_emoji).visible();
                    emojiFragment = EmojiFragment.newInstance();
                    emojiFragment.setCallBack(new EmojiSelectCallBack() {
                        @Override
                        public void onEmojiSelected(IExpressionModel emoji) {
                            presenter.sendEmoji("[" + emoji.getKey() + "]");
                        }
                    });
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.add(R.id.dialog_message_send_emoji, emojiFragment);
                    transaction.commitAllowingStateLoss();
                }
            }, 100);
        } else {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput($.id(R.id.dialog_message_send_et).view(), InputMethodManager.SHOW_IMPLICIT);

            $.id(R.id.dialog_message_send_emoji).gone();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.remove(emojiFragment);
            if (Build.VERSION.SDK_INT >= 24) {
                transaction.commitNowAllowingStateLoss();
            } else {
                transaction.commitAllowingStateLoss();
            }
            emojiFragment = null;
        }
    }

    @Override
    public void onPictureSend() {
        dismissAllowingStateLoss();
    }

    private class MessageTextWatcher implements android.text.TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(s)) {
                $.id(R.id.dialog_message_send_btn).enable(false);
            } else {
                $.id(R.id.dialog_message_send_btn).enable(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
