package com.codetroopers.materialAndroidBootstrap.core.beacons;

import android.support.annotation.NonNull;

import com.codetroopers.materialAndroidBootstrap.core.CTBus;
import com.codetroopers.materialAndroidBootstrap.ui.event.BeaconStateChangedEvent;
import com.codetroopers.materialAndroidBootstrap.ui.event.DeviceDetectedEvent;
import com.codetroopers.materialAndroidBootstrap.ui.event.NewBeaconEvent;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BeaconsSession {
    private final CTBus bus;
    private final BluetoothService bluetoothService;

    private final Map<String /* device address */, Beacon> deviceToBeaconMap = new HashMap<>();

    public BeaconsSession(CTBus bus, BluetoothService bluetoothService) {
        this.bus = bus;
        this.bluetoothService = bluetoothService;
    }

    @Subscribe
    public void onDeviceDetected(DeviceDetectedEvent event) {
        final Beacon beacon;
        if (unknownDeviceAddress(event.deviceAddress)) {
            beacon = addBeacon(event.deviceAddress, event.rssi);
            bus.post(new NewBeaconEvent(beacon));
        } else {
            beacon = updateBeacon(event.deviceAddress, System.currentTimeMillis(), event.rssi);
        }

        final boolean updateView = bluetoothService.validateServiceData(beacon, event.serviceData, event.deviceAddress);
        if (updateView) {
            bus.post(new BeaconStateChangedEvent(beacon));
        }
    }

    public int countBeacons() {
        return deviceToBeaconMap.size();
    }

    public Iterator<Map.Entry<String, Beacon>> getIterator() {
        return deviceToBeaconMap.entrySet().iterator();
    }

    private boolean unknownDeviceAddress(String deviceAddress) {
        return !deviceToBeaconMap.containsKey(deviceAddress);
    }

    @NonNull
    private Beacon addBeacon(String deviceAddress, int rssi) {
        Beacon beacon = new Beacon(deviceAddress, rssi);
        deviceToBeaconMap.put(deviceAddress, beacon);
        return beacon;
    }

    private Beacon updateBeacon(String deviceAddress, long currentTimeMillis, int rssi) {
        final Beacon beacon = deviceToBeaconMap.get(deviceAddress);
        beacon.lastSeenTimestamp = currentTimeMillis;
        beacon.rssi = rssi;
        return beacon;
    }
}
