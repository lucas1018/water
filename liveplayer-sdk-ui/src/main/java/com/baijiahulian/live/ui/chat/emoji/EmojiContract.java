package com.baijiahulian.live.ui.chat.emoji;

import com.baijiahulian.live.ui.base.BasePresenter;
import com.baijiahulian.live.ui.base.BaseView;
import com.baijiahulian.livecore.models.imodels.IExpressionModel;

/**
 * Created by Shubo on 2017/5/6.
 */

interface EmojiContract {

    interface View extends BaseView<Presenter> {
        // get item count per Row
        int getSpanCount();

        int getRowCount();

    }

    interface Presenter extends BasePresenter {

        IExpressionModel getItem(int page, int position);

        int getCount(int page);

        int getPageCount();

        void onSizeChanged();

        int getPageOfCurrentFirstItem();

        void onPageSelected(int page);
    }

}
