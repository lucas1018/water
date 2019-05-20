package cn.zerone.water.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.zerone.water.R;
import io.reactivex.annotations.Nullable;

/**
 * Created by litinghui on 2019/5/10.
 */

public class UncheckedFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_unchecked, container, false);
        return view;
    }
}
