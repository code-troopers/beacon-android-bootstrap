package com.codetroopers.materialAndroidBootstrap.ui.event;

import com.codetroopers.materialAndroidBootstrap.core.beacons.Beacon;

public class NewBeaconEvent extends BeaconEvent {
    public NewBeaconEvent(Beacon beacon) {
        super(beacon);
    }
}
