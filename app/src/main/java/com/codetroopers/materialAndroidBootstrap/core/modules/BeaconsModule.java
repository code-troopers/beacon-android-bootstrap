package com.codetroopers.materialAndroidBootstrap.core.modules;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import javax.inject.Singleton;

@Module
public class BeaconsModule {
    @Provides
    @Singleton
    BeaconManager provideBeaconManager(@ForApplication final Context context) {
        return BeaconManager.getInstanceForApplication(context);
    }

    @Provides
    @Singleton
    BackgroundPowerSaver provideBackgroundPowerSaver(@ForApplication final Context context) {
        return new BackgroundPowerSaver(context);
    }
}