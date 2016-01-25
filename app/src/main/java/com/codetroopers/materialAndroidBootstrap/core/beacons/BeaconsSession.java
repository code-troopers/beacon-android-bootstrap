package com.codetroopers.materialAndroidBootstrap.core.beacons;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BeaconsSession {
    private final Map<String /* device address */, Beacon> deviceToBeaconMap = new HashMap<>();

    public boolean unknownDeviceAddress(String deviceAddress) {
        return !deviceToBeaconMap.containsKey(deviceAddress);
    }

    @NonNull
    public Beacon addBeacon(String deviceAddress, int rssi) {
        Beacon beacon = new Beacon(deviceAddress, rssi);
        deviceToBeaconMap.put(deviceAddress, beacon);
        return beacon;
    }

    public Beacon updateBeacon(String deviceAddress, long currentTimeMillis, int rssi) {
        final Beacon beacon = deviceToBeaconMap.get(deviceAddress);
        beacon.lastSeenTimestamp = currentTimeMillis;
        beacon.rssi = rssi;
        return beacon;
    }

    public int countBeacons() {
        return deviceToBeaconMap.size();
    }

    public Iterator<Map.Entry<String, Beacon>> getIterator() {
        return deviceToBeaconMap.entrySet().iterator();
    }
}
