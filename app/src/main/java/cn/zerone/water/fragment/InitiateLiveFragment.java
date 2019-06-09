package cn.zerone.water.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.zerone.water.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InitiateLiveFragment extends Fragment {


    public InitiateLiveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_initiate_live, container, false);
    }

}
