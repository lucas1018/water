package com.baijiahulian.live.ui.chat.preview;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.baijiahulian.live.ui.R;
import com.baijiahulian.live.ui.base.BaseDialogFragment;
import com.baijiahulian.live.ui.utils.AliCloudImageUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

/**
 * Created by Shubo on 2017/3/23.
 */

public class ChatPictureViewFragment extends BaseDialogFragment implements ChatPictureViewContract.View {

    private ImageView imageView;
    private TextView tvLoading;
    //    private Button btnSave;
    private ChatPictureViewContract.Presenter presenter;

    public static ChatPictureViewFragment newInstance(String url) {

        Bundle args = new Bundle();
        args.putString("url", url);
        ChatPictureViewFragment fragment = new ChatPictureViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_big_picture;
    }

    @Override
    protected void init(Bundle savedInstanceState, Bundle arguments) {
        super.hideBackground().contentBackgroundColor(ContextCompat.getColor(getContext(), R.color.live_transparent));
        String url = arguments.getString("url");
        imageView = (ImageView) contentView.findViewById(R.id.lp_dialog_big_picture_img);
        tvLoading = (TextView) contentView.findViewById(R.id.lp_dialog_big_picture_loading_label);
//        btnSave = (Button) view.findViewById(R.id.lp_dialog_big_picture_save);
        Picasso.with(getContext())
                .load(AliCloudImageUtil.getScreenScaledUrl(getContext(), url))
                .memoryPolicy(NO_CACHE, NO_STORE)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        tvLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        tvLoading.setText(getString(R.string.live_image_loading_fail));
                    }
                });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(btnSave.getVisibility() == View.VISIBLE){
//                    btnSave.setVisibility(View.GONE);
//                }else{
                dismissAllowingStateLoss();
//                }
            }
        });
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                presenter.showSaveDialog(convertBmpToByteArray());
                return true;
            }
        });
//        btnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveImageToGallery(getContext(), ((BitmapDrawable)imageView.getDrawable()).getBitmap());
//                Toast.makeText(getContext(), "图片成功保存到本地", Toast.LENGTH_SHORT).show();
//                btnSave.setVisibility(View.GONE);
//            }
//        });
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });
    }

    @Override
    protected void setWindowParams(WindowManager.LayoutParams windowParams) {
        windowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        windowParams.dimAmount = 0.85f;
//        windowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        windowParams.windowAnimations = R.style.ViewBigPicAnim;
    }

    /**
     * 将bitmap转为字节数组,避免presenter使用Android api
     */
    private byte[] convertBmpToByteArray() {
        Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }


    @Override
    public void setPresenter(ChatPictureViewContract.Presenter presenter) {
        super.setBasePresenter(presenter);
        this.presenter = presenter;
    }
}
