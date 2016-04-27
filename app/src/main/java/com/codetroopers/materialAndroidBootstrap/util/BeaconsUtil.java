package com.codetroopers.materialAndroidBootstrap.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

public class BeaconsUtil {
    public static final String BEACON_LAYOUT_EDDYSTONE_UID = "s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19";
    public static final String BEACON_LAYOUT_EDDYSTONE_URL = "s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v";

    /**
     * Inspired from KontakIO SDK
     */
    public enum Proximity {
        /**
         * Android device distance from Beacon is within [0 - 0,5]m.
         */
        IMMEDIATE,
        /**
         * Android device distance from Beacon is within [0,5 - 3]m.
         */
        NEAR,
        /**
         * Android device distance from Beacon is higher than 3m.
         */
        FAR,
        /**
         * The UNKNOWN.
         */
        UNKNOWN;

        private static final double DISTANCE_THRESHOLD_UNKNOWN = 0.0;
        private static final double DISTANCE_THRESHOLD_IMMEDIATE = 0.5;
        private static final double DISTANCE_THRESHOLD_NEAR = 3.0;

        /**
         * Categorizes accuracy to Proximity.
         *
         * @param accuracy the accuracy
         * @return the proximity
         */
        public static Proximity fromDistance(final double accuracy) {
            if (accuracy < DISTANCE_THRESHOLD_UNKNOWN) {
                return UNKNOWN;
            }

            if (accuracy < DISTANCE_THRESHOLD_IMMEDIATE) {
                return IMMEDIATE;
            }

            if (accuracy < DISTANCE_THRESHOLD_NEAR) {
                return NEAR;
            }

            return FAR;
        }
    }

    // TODO: 27/04/16 extract wrapper object and remove these methods

    public static class EddystoneUID {
        public final Identifier namespaceId;
        public final Identifier instanceId;

        public EddystoneUID(Identifier namespaceId, Identifier instanceId) {
            this.namespaceId = namespaceId;
            this.instanceId = instanceId;
        }
    }

    @Nullable
    public static BeaconsUtil.EddystoneUID getEddystoneUIDfromBeacon(Beacon beacon) {
        BeaconsUtil.EddystoneUID eddystoneUID = null;
        if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00) {
            // This is a Eddystone-UID frame
            eddystoneUID = new BeaconsUtil.EddystoneUID(beacon.getId1(), beacon.getId2());
        }
        return eddystoneUID;
    }

    @NonNull
    public static Proximity getProximityFromBeacon(Beacon beacon) {
        return Proximity.fromDistance(beacon.getDistance());
    }

    @Nullable
    public static String getUrlFromBeacon(Beacon beacon) {
        String url = null;
        if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x10) {
            // This is a Eddystone-URL frame
            url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
        }
        return url;
    }
}
