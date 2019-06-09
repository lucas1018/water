package com.baijiahulian.live.ui.error;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.baijiahulian.live.ui.R;
import com.baijiahulian.live.ui.activity.LiveRoomRouterListener;
import com.baijiahulian.live.ui.base.BaseFragment;

/**
 * Created by Shubo on 2017/5/10.
 */

public class ErrorFragment extends BaseFragment {

    private LiveRoomRouterListener routerListener;
    public final static int ERROR_HANDLE_RECONNECT = 0;
    public final static int ERROR_HANDLE_REENTER = 1;
    public final static int ERROR_HANDLE_NOTHING = 2;

    public static ErrorFragment newInstance(String title, String content, int handleWay) {

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("content", content);
        args.putInt("handleWay", handleWay);

        ErrorFragment fragment = new ErrorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_error;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        $.id(R.id.fragment_error_back).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        $.id(R.id.fragment_error_title).text(getArguments().getString("title"));
        $.id(R.id.fragment_error_reason).text(getArguments().getString("content"));
        if (TextUtils.isEmpty(routerListener.getLiveRoom().getCustomerSupportDefaultExceptionMessage())) {
            $.id(R.id.fragment_error_suggestion).gone();
        } else {
            $.id(R.id.fragment_error_suggestion).visible();
            $.id(R.id.fragment_error_suggestion).text(routerListener.getLiveRoom().getCustomerSupportDefaultExceptionMessage());
        }
        $.id(R.id.fragment_error_retry).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (routerListener != null) {
                    int handleWay = getArguments().getInt("handleWay");
                    if (handleWay == ERROR_HANDLE_RECONNECT) {
                        routerListener.doReEnterRoom();
                    } else if (handleWay == ERROR_HANDLE_REENTER) {
                        routerListener.doReEnterRoom();
                    } else {
                        routerListener.doHandleErrorNothing();
                    }
                }
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void setRouterListener(LiveRoomRouterListener routerListener) {
        this.routerListener = routerListener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        routerListener = null;
    }
}
