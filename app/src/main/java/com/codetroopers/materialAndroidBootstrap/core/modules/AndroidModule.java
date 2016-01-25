package com.codetroopers.materialAndroidBootstrap.core.modules;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A module for Android-specific dependencies which require a {@link Context} or
 * {@link android.app.Application} to create.
 */
@Module
public class AndroidModule {
    @Provides
    @Singleton
    protected LocationManager provideLocationManager(@ForApplication final Context context) {
        return (LocationManager) context.getSystemService(LOCATION_SERVICE);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Provides
    @Singleton
    protected BluetoothManager provideBluetoothManager(@ForApplication final Context context) {
        return (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    @Provides
    protected SharedPreferences provideDefaultSharedPreferences(@ForApplication final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}