package com.baijiahulian.live.ui.ppt.quickswitchppt;

import com.baijiahulian.live.ui.base.BasePresenter;
import com.baijiahulian.live.ui.base.BaseView;
import com.baijiahulian.livecore.viewmodels.impl.LPDocListViewModel;

import java.util.List;

/**
 * Created by bjhl on 17/7/4.
 */
class SwitchPPTContract {
    interface View extends BaseView<Presenter> {
        void setIndex();

        void setMaxIndex(int maxIndex);

        void setType(boolean isStudent);

        void docListChanged(List<LPDocListViewModel.DocModel> docModelList);
    }

    interface Presenter extends BasePresenter {
        void setSwitchPosition(int position);
    }
}
