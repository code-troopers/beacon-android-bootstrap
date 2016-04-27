package com.codetroopers.materialAndroidBootstrap.core.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

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

    @Provides
    protected SharedPreferences provideDefaultSharedPreferences(@ForApplication final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}