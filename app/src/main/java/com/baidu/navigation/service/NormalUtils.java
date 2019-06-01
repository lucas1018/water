/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.navigation.service;

import android.app.Activity;
import android.content.Intent;

import cn.zerone.water.activity.BDNaviSettingActivity;

public class NormalUtils {

    public static void gotoSettings(Activity activity) {
        Intent it = new Intent(activity, BDNaviSettingActivity.class);
        activity.startActivity(it);
    }

    public static String getTTSAppID() {
        return "11213224";
    }
}
