package com.codetroopers.materialAndroidBootstrap.beacon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

public class BeaconWrapper {
    private final Beacon beacon;
    private final EddystoneUID eddystoneUID;
    private final Proximity proximity;
    private final String url;

    public BeaconWrapper(@NonNull Beacon beacon) {
        this.beacon = beacon;

        eddystoneUID = loadEddystoneUID();
        proximity = loadProximity();
        url = loadUrl();
    }

    @NonNull
    public Beacon getBeacon() {
        return beacon;
    }

    @Nullable
    public EddystoneUID getEddystoneUID() {
        return eddystoneUID;
    }

    @NonNull
    public Proximity getProximity() {
        return proximity;
    }

    @Nullable
    public String getUrl() {
        return url;
    }

    @Nullable
    private EddystoneUID loadEddystoneUID() {
        if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00) {
            // This is a Eddystone-UID frame
            return new EddystoneUID(beacon.getId1(), beacon.getId2());
        } else {
            return null;
        }
    }

    @NonNull
    private Proximity loadProximity() {
        return Proximity.fromDistance(beacon.getDistance());
    }

    @Nullable
    private String loadUrl() {
        if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x10) {
            // This is a Eddystone-URL frame
            return UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
        } else {
            return null;
        }
    }
}
