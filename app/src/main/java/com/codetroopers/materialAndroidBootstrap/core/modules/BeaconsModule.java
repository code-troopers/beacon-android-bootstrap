package com.codetroopers.materialAndroidBootstrap.core.modules;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import javax.inject.Singleton;
import java.util.List;

@Module
public class BeaconsModule {
    private static final String BEACON_LAYOUT_EDDYSTONE_UID = "s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19";
    private static final String BEACON_LAYOUT_EDDYSTONE_URL = "s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v";

    @Provides
    @Singleton
    BeaconManager provideBeaconManager(@ForApplication final Context context) {
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(context);
        // Detect the main Eddystone-UID frame:
        List<BeaconParser> beaconParsers = beaconManager.getBeaconParsers();
        beaconParsers.add(new BeaconParser().setBeaconLayout(BEACON_LAYOUT_EDDYSTONE_UID));
        // Detect the URL frame:
        beaconParsers.add(new BeaconParser().setBeaconLayout(BEACON_LAYOUT_EDDYSTONE_URL));
        return beaconManager;
    }

    @Provides
    @Singleton
    BackgroundPowerSaver provideBackgroundPowerSaver(@ForApplication final Context context) {
        return new BackgroundPowerSaver(context);
    }
}