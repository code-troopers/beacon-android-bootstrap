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
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.codetroopers.materialAndroidBootstrap.R;
import com.codetroopers.materialAndroidBootstrap.core.modules.ForApplication;
import com.codetroopers.materialAndroidBootstrap.ui.BeaconArrayAdapter;
import com.codetroopers.materialAndroidBootstrap.ui.activity.HomeActivity;
import com.kontakt.sdk.android.ble.configuration.scan.ScanContext;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.discovery.BluetoothDeviceEvent;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.common.profile.DeviceProfile;
import com.kontakt.sdk.android.common.profile.RemoteBluetoothDevice;
import com.kontakt.sdk.android.manager.KontaktProximityManager;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.List;

/**
 * Main UI and logic for scanning and validation of results.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BeaconsFragment extends Fragment implements ProximityManager.ProximityListener {

    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    @ForApplication
    Context appContext;
    @Inject
    KontaktProximityManager proximityManager;
    @Inject
    ScanContext scanContext;

    private BeaconArrayAdapter beaconArrayAdapter;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((HomeActivity) getActivity()).getComponent().inject(this);
        // see http://stackoverflow.com/questions/18896880/passing-context-to-arrayadapter-inside-fragment-with-setretaininstancetrue-wil
        beaconArrayAdapter = new BeaconArrayAdapter(appContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beacons, container, false);

        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mListView.setAdapter(beaconArrayAdapter);
        viewHolder.mListView.setEmptyView(viewHolder.mPlaceholder);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getActivity().getString(R.string.app_name));
    }

    @Override
    public void onStart() {
        super.onStart();

        proximityManager.initializeScan(scanContext, new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                proximityManager.attachListener(BeaconsFragment.this);
            }

            @Override
            public void onConnectionFailure() {
                Timber.e("Proximity manager connexion failure");
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        proximityManager.detachListener(this);
        proximityManager.disconnect();
    }

    @Override
    public void onScanStart() {
        Timber.d("scan started");
    }

    @Override
    public void onScanStop() {
        Timber.d("scan stopped");
    }

    @Override
    public void onEvent(BluetoothDeviceEvent bluetoothDeviceEvent) {
        List<? extends RemoteBluetoothDevice> deviceList = bluetoothDeviceEvent.getDeviceList();
        if(DeviceProfile.EDDYSTONE.equals(bluetoothDeviceEvent.getDeviceProfile())) {
            switch (bluetoothDeviceEvent.getEventType()) {
                case SPACE_ENTERED:
                    Timber.d("namespace or region entered");
                    break;
                case DEVICE_DISCOVERED:
                    Timber.d("found new beacon");
                    for (RemoteBluetoothDevice device : deviceList) {
                        beaconArrayAdapter.add(device);
                        beaconArrayAdapter.notifyDataSetChanged();
                    }
                    break;
                case DEVICES_UPDATE:
                    Timber.d("updated beacons");
                    break;
                case DEVICE_LOST:
                    Timber.d("lost device");
                    break;
                case SPACE_ABANDONED:
                    Timber.d("namespace or region abandoned");
                    break;
            }
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'fragment_main.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.listView)
        ListView mListView;
        @Bind(R.id.placeholder)
        TextView mPlaceholder;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
