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
        showFinishingDialog(activity,
                activity.getString(R.string.bl_not_enabled_title),
                activity.getString(R.string.bl_not_enabled_message));
    }

    public static void showNoBLESupportDialog(Activity activity) {
        showFinishingDialog(activity,
                activity.getString(R.string.ble_not_supported_title),
                activity.getString(R.string.ble_not_supported_message));
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void showGrantLocationAccessDialog(Activity activity, int requestCode) {
        showDialog(activity,
                activity.getString(R.string.grant_location_access_title),
                activity.getString(R.string.grant_location_access_message),
                dialog -> activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode));
    }

    private static void showFinishingDialog(Activity activity, String title, String message) {
        showDialog(activity, title, message, dialog -> {
            activity.finish();
            System.exit(0);
        });
    }

    private static void showDialog(Activity activity, String title, String message, DialogInterface.OnDismissListener onDismissListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setOnDismissListener(onDismissListener);
        builder.show();
    }
}
