package com.baijiahulian.live.ui.pptleftmenu;

import android.os.Bundle;
import android.view.View;

import com.baijiahulian.live.ui.R;
import com.baijiahulian.live.ui.base.BaseFragment;
import com.baijiahulian.live.ui.utils.QueryPlus;

/**
 * Created by wangkangfei on 17/5/4.
 */

public class PPTLeftFragment extends BaseFragment implements PPTLeftContract.View {
    private PPTLeftContract.Presenter presenter;

    @Override
    public void setPresenter(PPTLeftContract.Presenter presenter) {
        super.setBasePresenter(presenter);
        this.presenter = presenter;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_ppt_left_menu;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        $.id(R.id.fragment_ppt_left_menu_container).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.clearPPTAllShapes();
            }
        });
    }
}
