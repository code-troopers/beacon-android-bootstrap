package com.codetroopers.materialAndroidBootstrap.ui.event;

import com.codetroopers.materialAndroidBootstrap.core.beacons.Beacon;

public class BeaconStateChangedEvent extends BeaconEvent {
    public BeaconStateChangedEvent(Beacon beacon) {
        super(beacon);
    }
}
