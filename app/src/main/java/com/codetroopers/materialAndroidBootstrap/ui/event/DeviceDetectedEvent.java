package com.codetroopers.materialAndroidBootstrap.ui.event;

public class DeviceDetectedEvent {
    public final String deviceAddress;
    public final int rssi;
    public final byte[] serviceData;

    public DeviceDetectedEvent(String deviceAddress, int rssi, byte[] serviceData) {
        this.deviceAddress = deviceAddress;
        this.rssi = rssi;
        this.serviceData = serviceData;
    }
}
