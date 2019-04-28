package cn.zerone.water.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import cn.zerone.water.R;
import cn.zerone.water.fragment.WebFragment;

/**
 * Created by zero on 2018/12/3.
 */

public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        findViewById(R.id.fragment_container);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        WebFragment webFragment = new WebFragment();
        webFragment.setArguments(new Bundle());
        webFragment.getArguments().putString("url",getIntent().getStringExtra("url"));
        transaction.replace(R.id.fragment_container, webFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
