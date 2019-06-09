package com.baijiahulian.live.ui.announcement;

import android.text.TextUtils;

import com.baijiahulian.live.ui.activity.GlobalPresenter;
import com.baijiahulian.live.ui.activity.LiveRoomRouterListener;
import com.baijiahulian.live.ui.utils.RxUtils;
import com.baijiahulian.livecore.models.imodels.IAnnouncementModel;
import com.baijiahulian.livecore.utils.LPErrorPrintSubscriber;

import java.util.regex.Pattern;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Shubo on 2017/4/19.
 */

public class AnnouncementPresenter implements AnnouncementContract.Presenter {

    private AnnouncementContract.View view;

    private LiveRoomRouterListener routerListener;

    private String content;

    private String link;

    private Subscription subscriptionOfAnnouncementChange;

    private Pattern pattern;

    private GlobalPresenter globalPresenter;

    public AnnouncementPresenter(AnnouncementContract.View view, GlobalPresenter globalPresenter) {
        String mode = "(http|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
        pattern = Pattern.compile(mode);
        this.view = view;
        this.globalPresenter = globalPresenter;
    }

    @Override
    public void setRouter(LiveRoomRouterListener liveRoomRouterListener) {
        routerListener = liveRoomRouterListener;
    }

    @Override
    public void subscribe() {
        subscriptionOfAnnouncementChange = routerListener.getLiveRoom().getObservableOfAnnouncementChange()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<IAnnouncementModel>() {
                    @Override
                    public void call(IAnnouncementModel iAnnouncementModel) {
                        content = iAnnouncementModel.getContent();
                        link = iAnnouncementModel.getLink();
                        view.showAnnouncementText(content);
                        view.showAnnouncementUrl(link);
                        checkInput(content, link);
                    }
                });

        if (routerListener.isTeacherOrAssistant()) {
            view.showTeacherView();
        } else {
            view.showStudentView();
        }

        routerListener.getLiveRoom().requestAnnouncement();
    }

    @Override
    public void unSubscribe() {
        RxUtils.unSubscribe(subscriptionOfAnnouncementChange);
    }

    @Override
    public void destroy() {
        globalPresenter.observeAnnouncementChange();
        globalPresenter = null;
        routerListener = null;
        view = null;
    }

    @Override
    public void saveAnnouncement(String text, String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            if (!TextUtils.isEmpty(url))
                url = "http://" + url;
        }
        routerListener.getLiveRoom().changeRoomAnnouncement(text, url);
    }

    @Override
    public void checkInput(String text, String url) {
        if (text.equals(content) && link.equals(url)) {
            view.showCheckStatus(AnnouncementContract.STATUS_CHECKED_SAVED);
            return;
        }
        if (TextUtils.isEmpty(url)) {
            view.showCheckStatus(AnnouncementContract.STATUS_CHECKED_CAN_SAVE);
        } else {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            if (pattern.matcher(url).find()) {
                view.showCheckStatus(AnnouncementContract.STATUS_CHECKED_CAN_SAVE);
            } else {
                view.showCheckStatus(AnnouncementContract.STATUS_CHECKED_CANNOT_SAVE);
            }
        }
    }
}
