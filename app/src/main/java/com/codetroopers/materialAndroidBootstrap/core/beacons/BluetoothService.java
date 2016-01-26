package com.codetroopers.materialAndroidBootstrap.core.beacons;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.ParcelUuid;

import javax.inject.Inject;

import timber.log.Timber;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BluetoothService {

    // The Eddystone Service UUID, 0xFEAA.
    public static final ParcelUuid EDDYSTONE_SERVICE_UUID = ParcelUuid.fromString("0000FEAA-0000-1000-8000-00805F9B34FB");

    // An aggressive scan for nearby devices that reports immediately.
    private static final ScanSettings SCAN_SETTINGS =
            new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setReportDelay(0)
                    .build();

    private final BluetoothManager bluetoothManager;
    private final TlmValidator tlmValidator;
    private final UidValidator uidValidator;
    private final UrlValidator urlValidator;

    @Inject
    public BluetoothService(BluetoothManager bluetoothManager, TlmValidator tlmValidator, UidValidator uidValidator, UrlValidator urlValidator) {
        this.bluetoothManager = bluetoothManager;
        this.tlmValidator = tlmValidator;
        this.uidValidator = uidValidator;
        this.urlValidator = urlValidator;
    }

    public ScanFilter getScanFilter() {
        return new ScanFilter.Builder().setServiceUuid(EDDYSTONE_SERVICE_UUID).build();
    }

    // Checks the frame type and hands off the service data to the validation module.
    public boolean validateServiceData(Beacon beacon, byte[] serviceData, String deviceAddress) {
        if (serviceData == null) {
            String err = "Null Eddystone service data";
            beacon.frameStatus.nullServiceData = err;
            Timber.e("%s: %s", deviceAddress, err);
            return false;
        }
        Timber.v("%s %s", deviceAddress, Utils.toHexString(serviceData));
        switch (serviceData[0]) {
            case Constants.UID_FRAME_TYPE:
                uidValidator.validate(deviceAddress, serviceData, beacon);
                break;
            case Constants.TLM_FRAME_TYPE:
                tlmValidator.validate(deviceAddress, serviceData, beacon);
                break;
            case Constants.URL_FRAME_TYPE:
                urlValidator.validate(deviceAddress, serviceData, beacon);
                break;
            default:
                String err = String.format("Invalid frame type byte %02X", serviceData[0]);
                beacon.frameStatus.invalidFrameType = err;
                Timber.e("%s: %s", deviceAddress, err);
                break;
        }
        return true;
    }

    public BluetoothAdapter getAdapter() {
        return bluetoothManager.getAdapter();
    }

    public ScanSettings getScanSettings() {
        return SCAN_SETTINGS;
    }
}
