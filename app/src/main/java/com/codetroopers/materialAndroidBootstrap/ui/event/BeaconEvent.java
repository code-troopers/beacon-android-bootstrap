package com.codetroopers.materialAndroidBootstrap.ui.event;

import com.codetroopers.materialAndroidBootstrap.core.beacons.Beacon;

public abstract class BeaconEvent {
    public final Beacon beacon;

    public BeaconEvent(Beacon beacon) {
        this.beacon = beacon;
    }
}
