package com.baijiahulian.live.ui.quiz;

import com.baijiahulian.live.ui.base.BasePresenter;
import com.baijiahulian.live.ui.base.BaseView;
import com.baijiahulian.livecore.models.LPJsonModel;
import com.baijiahulian.livecore.models.imodels.IUserModel;

/**
 * Created by wangkangfei on 17/5/31.
 */

public interface QuizDialogContract {

    interface View extends BaseView<Presenter> {
        void onStartArrived(LPJsonModel jsonModel);

        void onEndArrived(LPJsonModel jsonModel);

        void onSolutionArrived(LPJsonModel jsonModel);

        void onQuizResArrived(LPJsonModel jsonModel);

        void onGetCurrentUser(IUserModel userModel);

        void dismissDlg();
    }

    interface Presenter extends BasePresenter {
        void submitAnswer(String submitContent);

        void sendCommonRequest(String request);

        void getCurrentUser();

        String getRoomToken();

        void dismissDlg();
    }
}
