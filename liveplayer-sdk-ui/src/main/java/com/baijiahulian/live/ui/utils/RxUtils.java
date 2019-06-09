package com.baijiahulian.live.ui.utils;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.MainThreadSubscription;

import static com.baijiahulian.live.ui.utils.Precondition.checkNotNull;
import static com.baijiahulian.livecore.utils.LPRxUtils.checkUiThread;

/**
 * Created by Shubo on 2017/2/13.
 */

public class RxUtils {

    public static void unSubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @CheckResult
    @NonNull
    public static Observable<Void> clicks(@NonNull View view) {
        checkNotNull(view, "view == null");
        return Observable.create(new ViewClickOnSubscribe(view));
    }

    private static class ViewClickOnSubscribe implements Observable.OnSubscribe<Void> {
        View view;

        ViewClickOnSubscribe(View view) {
            this.view = view;
        }

        @Override
        public void call(final Subscriber<? super Void> subscriber) {
            checkUiThread();

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(null);
                    }
                }
            };
            view.setOnClickListener(listener);

            subscriber.add(new MainThreadSubscription() {
                @Override
                protected void onUnsubscribe() {
                    view.setOnClickListener(null);
                }
            });
        }
    }
}
