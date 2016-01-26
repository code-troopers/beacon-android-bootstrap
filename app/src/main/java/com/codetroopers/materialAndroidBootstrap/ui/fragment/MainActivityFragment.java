// Copyright 2015 Google Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.codetroopers.materialAndroidBootstrap.ui.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.materialAndroidBootstrap.R;
import com.codetroopers.materialAndroidBootstrap.core.beacons.Beacon;
import com.codetroopers.materialAndroidBootstrap.core.beacons.BeaconsSession;
import com.codetroopers.materialAndroidBootstrap.core.beacons.BluetoothService;
import com.codetroopers.materialAndroidBootstrap.core.modules.ForApplication;
import com.codetroopers.materialAndroidBootstrap.ui.BeaconArrayAdapter;
import com.codetroopers.materialAndroidBootstrap.ui.activity.HomeActivity;
import com.codetroopers.materialAndroidBootstrap.ui.activity.SettingsActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Main UI and logic for scanning and validation of results.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MainActivityFragment extends Fragment {

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    private static final Handler handler = new Handler(Looper.getMainLooper());

    @Inject
    BluetoothService bluetoothService;
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    @ForApplication
    BeaconsSession beaconsSession;

    private BluetoothLeScanner scanner;
    private BeaconArrayAdapter beaconArrayAdapter;

    private List<ScanFilter> scanFilters;
    private ScanCallback scanCallback;

    private int onLostTimeoutMillis;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((HomeActivity) getActivity()).getComponent().inject(this);
        init();
        beaconArrayAdapter = new BeaconArrayAdapter(getActivity(), new ArrayList<Beacon>());
        scanFilters = new ArrayList<>();
        scanFilters.add(bluetoothService.getScanFilter());
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                ScanRecord scanRecord = result.getScanRecord();
                if (scanRecord == null) {
                    return;
                }

                final String deviceAddress = result.getDevice().getAddress();
                final int rssi = result.getRssi();

                final Beacon beacon;
                if (beaconsSession.unknownDeviceAddress(deviceAddress)) {
                    beacon = beaconsSession.addBeacon(deviceAddress, rssi);
                    beaconArrayAdapter.add(beacon);
                } else {
                    beacon = beaconsSession.updateBeacon(deviceAddress, System.currentTimeMillis(), rssi);
                }

                final boolean updateView = bluetoothService.validateServiceData(beacon, scanRecord, deviceAddress);
                if (updateView) {
                    beaconArrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                switch (errorCode) {
                    case SCAN_FAILED_ALREADY_STARTED:
                        logErrorAndShowToast("SCAN_FAILED_ALREADY_STARTED");
                        break;
                    case SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                        logErrorAndShowToast("SCAN_FAILED_APPLICATION_REGISTRATION_FAILED");
                        break;
                    case SCAN_FAILED_FEATURE_UNSUPPORTED:
                        logErrorAndShowToast("SCAN_FAILED_FEATURE_UNSUPPORTED");
                        break;
                    case SCAN_FAILED_INTERNAL_ERROR:
                        logErrorAndShowToast("SCAN_FAILED_INTERNAL_ERROR");
                        break;
                    default:
                        logErrorAndShowToast("Scan failed, unknown error code");
                        break;
                }
            }
        };

        onLostTimeoutMillis = sharedPreferences.getInt(SettingsActivity.ON_LOST_TIMEOUT_SECS_KEY, 5) * 1000;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // NOP
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // NOP
            }

            @Override
            public void afterTextChanged(Editable s) {
                beaconArrayAdapter.getFilter().filter(viewHolder.mFilter.getText().toString());
            }
        });
        viewHolder.mListView.setAdapter(beaconArrayAdapter);
        viewHolder.mListView.setEmptyView(viewHolder.mPlaceholder);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (scanner != null) {
            scanner.stopScan(scanCallback);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        handler.removeCallbacksAndMessages(null);

        int timeoutMillis = sharedPreferences.getInt(SettingsActivity.ON_LOST_TIMEOUT_SECS_KEY, 5) * 1000;

        if (timeoutMillis > 0) {  // 0 is special and means don't remove anything.
            onLostTimeoutMillis = timeoutMillis;
            setOnLostRunnable();
        }

        if (sharedPreferences.getBoolean(SettingsActivity.SHOW_DEBUG_INFO_KEY, false)) {
            Runnable updateTitleWithNumberSightedBeacons = new Runnable() {
                final String appName = getActivity().getString(R.string.app_name);

                @Override
                public void run() {
                    getActivity().setTitle(String.format(Locale.getDefault(), "%s (%d)", appName, beaconsSession.countBeacons()));
                    handler.postDelayed(this, 1000);
                }
            };
            handler.postDelayed(updateTitleWithNumberSightedBeacons, 1000);
        } else {
            getActivity().setTitle(getActivity().getString(R.string.app_name));
        }

        if (scanner != null) {
            scanner.startScan(scanFilters, bluetoothService.getScanSettings(), scanCallback);
        }
    }

    private void setOnLostRunnable() {
        Runnable removeLostDevices = new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                Iterator<Entry<String, Beacon>> itr = beaconsSession.getIterator();
                while (itr.hasNext()) {
                    Beacon beacon = itr.next().getValue();
                    if ((time - beacon.lastSeenTimestamp) > onLostTimeoutMillis) {
                        itr.remove();
                        beaconArrayAdapter.remove(beacon);
                    }
                }
                handler.postDelayed(this, onLostTimeoutMillis);
            }
        };
        handler.postDelayed(removeLostDevices, onLostTimeoutMillis);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                init();
            } else {
                getActivity().finish();
            }
        }
    }

    // Attempts to create the scanner.
    private void init() {
        BluetoothAdapter btAdapter = bluetoothService.getAdapter();
        if (btAdapter == null) {
            showFinishingAlertDialog();
        } else if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            this.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            scanner = btAdapter.getBluetoothLeScanner();
        }
    }

    // Pops an AlertDialog that quits the app on OK.
    private void showFinishingAlertDialog() {
        new AlertDialog.Builder(getActivity()).setTitle("Bluetooth Error").setMessage("Bluetooth not detected on device")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().finish();
                    }
                }).show();
    }

    private void logErrorAndShowToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        Timber.e(message);
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'fragment_main.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.filter)
        EditText mFilter;
        @Bind(R.id.listView)
        ListView mListView;
        @Bind(R.id.placeholder)
        TextView mPlaceholder;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
