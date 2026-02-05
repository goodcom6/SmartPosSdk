package com.goodcom.pos.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.goodcom.smartpossdk.GcSmartPosUtils;

public class DeviceTools {
    public static final String TAG = "DeviceTools";

    public static void openKioskMode(Activity activity) {
        disableBackButton(activity, true);
        openScreen(activity);
        GcSmartPosUtils.getInstance().power_shut();
        disableSystemBarPullDown(activity, true);
    }

    public static void closeKioskMode(Activity activity) {
        disableBackButton(activity, false);
        closeScreen(activity);
        GcSmartPosUtils.getInstance().power_open();
        disableSystemBarPullDown(activity, false);
    }

    private static void disableBackButton(Activity activity, boolean flag) {
        Log.e(TAG, "disableBackButton flag:" + flag);
        int type = 0;
        if (flag) {
            type = 1;
        }
        Intent it = new Intent("android.intent.action.DISABLE_KEYCODE");
        ;
        if (Build.VERSION.SDK_INT >= 26) {
            it.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        }
        it.putExtra("keycode", KeyEvent.KEYCODE_BACK);
        it.putExtra("state", type);
        activity.sendBroadcast(it);
    }

    private static void disableSystemBarPullDown(Activity activity, boolean flag) {
        Log.e(TAG, "disableSystemBarPullDown flag:" + flag);
        Intent it = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            it.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        }
        it.setAction("com.android.action.STATUSBAR_SWITCH_STATE");
        it.putExtra("enable", !flag);
        activity.sendBroadcast(it);
    }

    private static void openScreen(Activity activity) {
        Log.e(TAG, "start keep screen on");
        try {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void closeScreen(Activity activity) {
        Log.e(TAG, "stop keep screen on");
        try {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
