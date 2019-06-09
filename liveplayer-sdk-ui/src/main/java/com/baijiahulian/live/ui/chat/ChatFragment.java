package com.baijiahulian.live.ui.chat;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baijiahulian.live.ui.R;
import com.baijiahulian.live.ui.base.BaseFragment;
import com.baijiahulian.live.ui.utils.AliCloudImageUtil;
import com.baijiahulian.live.ui.utils.ChatImageUtil;
import com.baijiahulian.live.ui.utils.DisplayUtils;
import com.baijiahulian.live.ui.utils.LinearLayoutWrapManager;
import com.baijiahulian.livecore.context.LPConstants;
import com.baijiahulian.livecore.models.imodels.IMessageModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by Shubo on 2017/2/23.
 */

public class ChatFragment extends BaseFragment implements ChatContract.View {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private ChatContract.Presenter presenter;
    LinearLayoutManager mLayoutManager;
    private ColorDrawable failedColorDrawable;
    private int emojiSize;
    private int backgroundRes;
    @ColorInt
    private int textColor;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_chat;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        failedColorDrawable = new ColorDrawable(ContextCompat.getColor(getContext(), R.color.live_half_transparent));
        emojiSize = (int) (DisplayUtils.getScreenDensity(getContext()) * 32);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            backgroundRes = R.drawable.live_item_chat_bg_land;
            textColor = ContextCompat.getColor(getContext(), R.color.live_white);
        } else {
            backgroundRes = R.drawable.live_item_chat_bg;
            textColor = ContextCompat.getColor(getContext(), R.color.primary_text);
        }

        adapter = new MessageAdapter();
        mLayoutManager = new LinearLayoutWrapManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView = (RecyclerView) $.id(R.id.fragment_chat_recycler).view();
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void notifyDataChanged() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });
    }

    @Override
    public void notifyItemChange(int position) {
        adapter.notifyItemChanged(position);
    }

    @Override
    public void notifyItemInserted(int position) {
        adapter.notifyItemInserted(position);
        recyclerView.smoothScrollToPosition(adapter.getItemCount());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            backgroundRes = R.drawable.live_item_chat_bg_land;
            textColor = ContextCompat.getColor(getContext(), R.color.live_white);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            backgroundRes = R.drawable.live_item_chat_bg;
            textColor = ContextCompat.getColor(getContext(), R.color.primary_text);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void clearScreen() {
        recyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void unClearScreen() {
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setPresenter(ChatContract.Presenter presenter) {
        this.presenter = presenter;
        super.setBasePresenter(presenter);
    }

    private class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int MESSAGE_TYPE_TEXT = 0;
        private static final int MESSAGE_TYPE_EMOJI = 1;
        private static final int MESSAGE_TYPE_IMAGE = 2;

        @Override
        public int getItemViewType(int position) {
            switch (presenter.getMessage(position).getMessageType()) {
                case Text:
                    return MESSAGE_TYPE_TEXT;
                case Emoji:
                case EmojiWithName:
                    return MESSAGE_TYPE_EMOJI;
                case Image:
                    return MESSAGE_TYPE_IMAGE;
                default:
                    return MESSAGE_TYPE_TEXT;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == MESSAGE_TYPE_TEXT) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_text, parent, false);
                return new TextViewHolder(view);
            } else if (viewType == MESSAGE_TYPE_EMOJI) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_emoji, parent, false);
                return new EmojiViewHolder(view);
            } else if (viewType == MESSAGE_TYPE_IMAGE) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_image, parent, false);
                return new ImageViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            IMessageModel message = presenter.getMessage(position);
            int color;
            if (message.getFrom().getType() == LPConstants.LPUserType.Teacher) {
                color = ContextCompat.getColor(getContext(), R.color.live_blue);
            } else {
                color = ContextCompat.getColor(getContext(), R.color.live_text_color_light);
            }

            String name = message.getFrom().getName() + "：";
            SpannableString spanText = new SpannableString(name);
            spanText.setSpan(new ForegroundColorSpan(color), 0, name.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            if (holder instanceof TextViewHolder) {
                TextViewHolder textViewHolder = (TextViewHolder) holder;
                textViewHolder.textView.setText(spanText);
                textViewHolder.textView.setTextColor(textColor);
                textViewHolder.textView.append(message.getContent());
                if (message.getFrom().getType() == LPConstants.LPUserType.Teacher ||
                        message.getFrom().getType() == LPConstants.LPUserType.Assistant) {
                    Linkify.addLinks(textViewHolder.textView, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES);
                } else {
                    textViewHolder.textView.setAutoLinkMask(0);
                }
            } else if (holder instanceof EmojiViewHolder) {
                EmojiViewHolder emojiViewHolder = (EmojiViewHolder) holder;
                emojiViewHolder.tvName.setText(spanText);
                Picasso.with(getContext()).load(message.getUrl())
                        .placeholder(R.drawable.live_ic_emoji_holder)
                        .error(R.drawable.live_ic_emoji_holder)
                        .resize(emojiSize, emojiSize)
                        .into(emojiViewHolder.ivEmoji);
            } else if (holder instanceof ImageViewHolder) {
                ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
                imageViewHolder.ivImg.setOnClickListener(null);
                imageViewHolder.tvName.setText(spanText);
                if (message instanceof UploadingImageModel) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(message.getUrl(), options);
                    int[] size = {options.outWidth, options.outHeight};
                    ChatImageUtil.calculateImageSize(size, DisplayUtils.dip2px(getContext(), 100), DisplayUtils.dip2px(getContext(), 50));
                    Picasso.with(getContext()).load(new File(message.getUrl()))
                            .resize(size[0], size[1])
                            .placeholder(failedColorDrawable)
                            .error(failedColorDrawable)
                            .into(imageViewHolder.ivImg);
                    if (((UploadingImageModel) message).getStatus() == UploadingImageModel.STATUS_UPLOADING) {
                        imageViewHolder.tvMask.setVisibility(View.VISIBLE);
                        imageViewHolder.tvExclamation.setVisibility(View.GONE);
                    } else if (((UploadingImageModel) message).getStatus() == UploadingImageModel.STATUS_UPLOAD_FAILED) {
                        imageViewHolder.tvMask.setVisibility(View.GONE);
                        imageViewHolder.tvExclamation.setVisibility(View.VISIBLE);
                        imageViewHolder.ivImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                presenter.reUploadImage(holder.getAdapterPosition());
                            }
                        });
                    }
                } else {
                    imageViewHolder.tvMask.setVisibility(View.GONE);
                    imageViewHolder.tvExclamation.setVisibility(View.GONE);
                    ImageTarget target = new ImageTarget(getContext(), imageViewHolder.ivImg);
                    Picasso.with(getContext()).load(AliCloudImageUtil.getRectScaledUrl(getContext(), message.getUrl(), 100))
                            .placeholder(failedColorDrawable)
                            .error(failedColorDrawable)
                            .into(target);
                    // set tag to avoid target being garbage collected!
                    imageViewHolder.ivImg.setTag(target);
                    imageViewHolder.ivImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            presenter.showBigPic(holder.getAdapterPosition());
                        }
                    });
                }
            }
            holder.itemView.setBackgroundResource(backgroundRes);
        }

        @Override
        public int getItemCount() {
            return presenter.getCount();
        }
    }

    private static class TextViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        TextViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_chat_text);
        }
    }

    private static class ImageViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvExclamation, tvMask;
        ImageView ivImg;

        ImageViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.item_chat_image_name);
            ivImg = (ImageView) itemView.findViewById(R.id.item_chat_image);
            tvExclamation = (TextView) itemView.findViewById(R.id.item_chat_image_exclamation);
            tvMask = (TextView) itemView.findViewById(R.id.item_chat_image_mask);
        }
    }

    private static class EmojiViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        ImageView ivEmoji;

        EmojiViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.item_chat_emoji_name);
            ivEmoji = (ImageView) itemView.findViewById(R.id.item_chat_emoji);
        }
    }

    private static class ImageTarget implements Target {

        private ImageView imageView;
        private WeakReference<Context> mContext;

        ImageTarget(Context context, ImageView imageView) {
            this.imageView = imageView;
            this.mContext = new WeakReference<>(context);
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Context context = mContext.get();
            if (context == null) return;
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            int[] size = {bitmap.getWidth(), bitmap.getHeight()};
            ChatImageUtil.calculateImageSize(size, DisplayUtils.dip2px(context, 100), DisplayUtils.dip2px(context, 50));
            lp.width = size[0];
            lp.height = size[1];
            imageView.setLayoutParams(lp);
            imageView.setImageBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            imageView.setImageDrawable(errorDrawable);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            imageView.setImageDrawable(placeHolderDrawable);
        }
    }
}
