package com.codetroopers.materialAndroidBootstrap.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import com.codetroopers.materialAndroidBootstrap.R;

@SuppressWarnings("UnusedDeclaration")
public class UIUtils {

    /**
     * Helps determine if the app is running in a Tablet context.
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static void showEnableBluetoothDialog(Activity activity) {
        showFinishingDialog(activity, R.string.bl_not_enabled_title, R.string.bl_not_enabled_message);
    }

    public static void showNoBLESupportDialog(Activity activity) {
        showFinishingDialog(activity, R.string.ble_not_supported_title, R.string.ble_not_supported_message);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void showGrantLocationAccessDialog(Activity activity, int requestCode) {
        showDialog(activity,
                R.string.grant_location_access_title,
                R.string.grant_location_access_message,
                dialog -> activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode));
    }

    public static void showNoLocationAccessDialog(Activity activity) {
        showDialog(activity,
                R.string.R_string_no_location_access_title,
                R.string.no_location_access_message,
                dialog -> {
                });
    }

    private static void showFinishingDialog(Activity activity, int title, int message) {
        showDialog(activity, title, message, dialog -> {
            activity.finish();
            System.exit(0);
        });
    }

    private static void showDialog(Activity activity, int title, int message, DialogInterface.OnDismissListener onDismissListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(title));
        builder.setMessage(activity.getString(message));
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setOnDismissListener(onDismissListener);
        builder.show();
    }
}
