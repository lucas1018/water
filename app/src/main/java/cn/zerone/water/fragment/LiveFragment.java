package cn.zerone.water.fragment;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

;import java.util.List;

import cn.zerone.water.R;

/**
 * created by qhk
 * on 2019/5/20
 */
public class LiveFragment extends Fragment {

    private Button startLive;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live,null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        try {
//            getAppInfo();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        startLive = view.findViewById(R.id.start_live);

        startLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

//    private void getAppInfo() throws Exception {
//        PackageManager packageManager = getContext().getPackageManager();
//        //获取所有安装的app
//        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
//        for (PackageInfo info : installedPackages) {
//            String packageName = info.packageName;//app包名
//            ApplicationInfo ai = packageManager.getApplicationInfo(packageName, 0);
//            String appName = (String) packageManager.getApplicationLabel(ai);//获取应用名称
//        }
//
//    }
}
