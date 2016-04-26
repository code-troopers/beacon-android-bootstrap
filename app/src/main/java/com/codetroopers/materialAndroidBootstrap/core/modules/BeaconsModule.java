package com.codetroopers.materialAndroidBootstrap.core.modules;

import android.content.Context;
import com.kontakt.sdk.android.ble.configuration.ActivityCheckConfiguration;
import com.kontakt.sdk.android.ble.configuration.ForceScanConfiguration;
import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.configuration.scan.EddystoneScanContext;
import com.kontakt.sdk.android.ble.configuration.scan.IBeaconScanContext;
import com.kontakt.sdk.android.ble.configuration.scan.ScanContext;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.rssi.RssiCalculators;
import com.kontakt.sdk.android.ble.spec.EddystoneFrameType;
import com.kontakt.sdk.android.manager.KontaktProximityManager;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.Arrays;

@Module
public class BeaconsModule {
    @Provides
    KontaktProximityManager provideKontaktProximityManager(@ForApplication final Context context) {
        return new KontaktProximityManager(context);
    }

    @Provides
    @Singleton
    ScanContext provideScanContext(final EddystoneScanContext eddystoneScanContext) {
        return new ScanContext.Builder()
                .setForceScanConfiguration(ForceScanConfiguration.DISABLED)
                .setScanMode(ProximityManager.SCAN_MODE_LOW_LATENCY)
                .setActivityCheckConfiguration(ActivityCheckConfiguration.MINIMAL)
                .setEddystoneScanContext(eddystoneScanContext)
                .setIBeaconScanContext(IBeaconScanContext.DEFAULT)
                .setScanPeriod(new ScanPeriod(3000, 0))
                .build();
    }

    @Provides
    EddystoneScanContext provideEddystoneScanContext() {
        return new EddystoneScanContext.Builder()
                .setTriggerFrameTypes(Arrays.asList(EddystoneFrameType.UID, EddystoneFrameType.URL, EddystoneFrameType.TLM))
                .setRssiCalculator(RssiCalculators.newLimitedMeanRssiCalculator(3))
                .build();
    }
}
