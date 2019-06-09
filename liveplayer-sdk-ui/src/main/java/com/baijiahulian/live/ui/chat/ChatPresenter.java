package com.baijiahulian.live.ui.chat;

import com.baijiahulian.common.networkv2.BJProgressCallback;
import com.baijiahulian.common.networkv2.BJResponse;
import com.baijiahulian.common.networkv2.HttpException;
import com.baijiahulian.live.ui.activity.LiveRoomRouterListener;
import com.baijiahulian.live.ui.utils.RxUtils;
import com.baijiahulian.livecore.context.LPConstants;
import com.baijiahulian.livecore.models.LPShortResult;
import com.baijiahulian.livecore.models.LPUploadDocModel;
import com.baijiahulian.livecore.models.imodels.IMessageModel;
import com.baijiahulian.livecore.utils.LPBackPressureBufferedSubscriber;
import com.baijiahulian.livecore.utils.LPChatMessageParser;
import com.baijiahulian.livecore.utils.LPErrorPrintSubscriber;
import com.baijiahulian.livecore.utils.LPJsonUtils;
import com.baijiahulian.livecore.utils.LPLogger;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.baijiahulian.live.ui.utils.Precondition.checkNotNull;

/**
 * Created by Shubo on 2017/2/23.
 */

public class ChatPresenter implements ChatContract.Presenter {

    private LiveRoomRouterListener routerListener;
    private ChatContract.View view;
    private Subscription subscriptionOfDataChange, subscriptionOfMessageReceived;
    private LinkedBlockingQueue<UploadingImageModel> imageMessageUploadingQueue;

    public ChatPresenter(ChatContract.View view) {
        this.view = view;
        imageMessageUploadingQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void setRouter(LiveRoomRouterListener liveRoomRouterListener) {
        this.routerListener = liveRoomRouterListener;
    }

    @Override
    public void subscribe() {
        checkNotNull(routerListener);
        view.notifyDataChanged();
        subscriptionOfDataChange = routerListener.getLiveRoom().getChatVM().getObservableOfNotifyDataChange()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        view.notifyDataChanged();
                    }
                });
        subscriptionOfMessageReceived = routerListener.getLiveRoom().getChatVM().getObservableOfReceiveMessage()
                .onBackpressureBuffer()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPBackPressureBufferedSubscriber<IMessageModel>() {
                    @Override
                    public void call(IMessageModel iMessageModel) {
                        if (iMessageModel.getMessageType() == LPConstants.MessageType.Image
                                && iMessageModel.getFrom().getUserId().equals(routerListener.getLiveRoom().getCurrentUser().getUserId())) {
                            view.notifyItemChange(getCount() - imageMessageUploadingQueue.size() - 1);
                        }
                        view.notifyItemInserted(getCount() - 1);
                    }
                });
    }

    @Override
    public void unSubscribe() {
        RxUtils.unSubscribe(subscriptionOfDataChange);
        RxUtils.unSubscribe(subscriptionOfMessageReceived);
    }

    @Override
    public int getCount() {
        checkNotNull(routerListener);
        return routerListener.getLiveRoom().getChatVM().getMessageCount() + imageMessageUploadingQueue.size();
    }

    @Override
    public IMessageModel getMessage(int position) {
        checkNotNull(routerListener);
        int messageCount = routerListener.getLiveRoom().getChatVM().getMessageCount();
        if (position < messageCount) {
            return routerListener.getLiveRoom().getChatVM().getMessage(position);
        } else {
            return (IMessageModel) imageMessageUploadingQueue.toArray()[position - messageCount];
        }
    }

    @Override
    public void showBigPic(int position) {
        checkNotNull(routerListener);
        routerListener.showBigChatPic(getMessage(position).getUrl());
    }

    @Override
    public void reUploadImage(int position) {
        continueUploadQueue();
    }

    @Override
    public void destroy() {
        view = null;
        routerListener = null;
        imageMessageUploadingQueue.clear();
        imageMessageUploadingQueue = null;
    }

    // add Uploading Image to queue
    public void sendImageMessage(String path) {
        UploadingImageModel model = new UploadingImageModel(path, routerListener.getLiveRoom().getCurrentUser());
        imageMessageUploadingQueue.offer(model);
        continueUploadQueue();
    }


    private void continueUploadQueue() {
        final UploadingImageModel model = imageMessageUploadingQueue.peek();
        if (model == null) return;
        view.notifyItemInserted(routerListener.getLiveRoom().getChatVM().getMessageCount() + imageMessageUploadingQueue.size() - 1);
        routerListener.getLiveRoom().getDocListVM().uploadImageWithProgress(model.getUrl(), this, new BJProgressCallback() {
            @Override
            public void onProgress(long l, long l1) {
                LPLogger.d(l + "/" + l1);
            }

            @Override
            public void onFailure(HttpException e) {
                model.setStatus(UploadingImageModel.STATUS_UPLOAD_FAILED);
//                view.notifyItemChange(getCount() - imageMessageUploadingQueue.size());
                view.notifyDataChanged();
            }

            @Override
            public void onResponse(BJResponse bjResponse) {
                LPShortResult shortResult = null;
                try {
                    shortResult = LPJsonUtils.parseString(bjResponse.getResponse().body().string(), LPShortResult.class);
                    LPUploadDocModel uploadModel = LPJsonUtils.parseJsonObject((JsonObject) shortResult.data, LPUploadDocModel.class);
                    String imageContent = LPChatMessageParser.toImageMessage(uploadModel.url);
                    routerListener.getLiveRoom().getChatVM().sendImageMessage(imageContent, uploadModel.width, uploadModel.height);
                    imageMessageUploadingQueue.poll();
//                    view.notifyItemChange(getCount() - imageMessageUploadingQueue.size());
//                    view.notifyDataChanged();
                    continueUploadQueue();
                } catch (IOException e) {
                    model.setStatus(UploadingImageModel.STATUS_UPLOAD_FAILED);
//                    view.notifyItemChange(getCount() - imageMessageUploadingQueue.size());
                    view.notifyDataChanged();
                    e.printStackTrace();
                }
            }
        });
    }
}
