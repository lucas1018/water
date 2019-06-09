package com.baijiahulian.live.ui.users;

import com.baijiahulian.live.ui.base.BasePresenter;
import com.baijiahulian.live.ui.base.BaseView;
import com.baijiahulian.livecore.models.imodels.IUserModel;

/**
 * Created by Shubo on 2017/4/5.
 */

public interface OnlineUserContract {

    interface View extends BaseView<Presenter> {
        void notifyDataChanged();

        void notifyNoMoreData();

        void notifyUserCountChange(int count);
    }

    interface Presenter extends BasePresenter {
        int getCount();

        IUserModel getUser(int position);

        void loadMore();

        boolean isLoading();
    }
}
