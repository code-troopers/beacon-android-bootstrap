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

package com.codetroopers.materialAndroidBootstrap.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.codetroopers.materialAndroidBootstrap.R;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;
import com.kontakt.sdk.android.common.profile.RemoteBluetoothDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Simple ArrayAdapter to manage the UI for displaying validation results.
 */
public class BeaconArrayAdapter extends ArrayAdapter<RemoteBluetoothDevice> implements Filterable {

    private List<RemoteBluetoothDevice> filteredBeacons;

    public BeaconArrayAdapter(Context context) {
        this(context, new ArrayList<>());
    }

    private BeaconArrayAdapter(Context context, List<RemoteBluetoothDevice> allBeacons) {
        super(context, R.layout.beacon_list_item, allBeacons);
        this.filteredBeacons = allBeacons;
    }

    @Override
    public int getCount() {
        return filteredBeacons.size();
    }

    @Override
    public RemoteBluetoothDevice getItem(int position) {
        return filteredBeacons.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.beacon_list_item, parent, false);
        }

        // Note: this is a listView and the convertView object here is likely to be
        // a recycled view of some other row that isn't in view. You need to set every
        // field regardless of emptiness to avoid displaying erroneous data.

        final RemoteBluetoothDevice beacon = getItem(position);

        ViewHolder viewHolder = new ViewHolder(convertView);

        viewHolder.mRssi.setText(String.valueOf(beacon.getRssi()));

        final String distance = String.format(Locale.getDefault(), "%.2f", beacon.getDistance());
        viewHolder.mDistance.setText(distance);
        /**
         * IMMEDIATE = Android device distance from Beacon is within [0 - 0,5]m.
         * NEAR = Android device distance from Beacon is within [0,5 - 3]m.
         * FAR = Android device distance from Beacon is higher than 3m.
         * UNKNOWN = The UNKNOWN.
         */
        viewHolder.mProximity.setText(beacon.getProximity().toString());

        viewHolder.mUrl.setText(((IEddystoneDevice) beacon).getUrl());

        return convertView;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'beacon_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.rssi)
        TextView mRssi;
        @Bind(R.id.distance)
        TextView mDistance;
        @Bind(R.id.url)
        TextView mUrl;
        @Bind(R.id.proximity)
        TextView mProximity;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
