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
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codetroopers.materialAndroidBootstrap.R;
import com.codetroopers.materialAndroidBootstrap.core.beacons.Beacon;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Simple ArrayAdapter to manage the UI for displaying validation results.
 */
public class BeaconArrayAdapter extends ArrayAdapter<Beacon> implements Filterable {

    private static final int DARK_GREEN = Color.argb(255, 0, 150, 0);
    private static final int DARK_RED = Color.argb(255, 150, 0, 0);

    private final List<Beacon> allBeacons;
    private List<Beacon> filteredBeacons;

    public BeaconArrayAdapter(Context context) {
        this(context, new ArrayList<Beacon>());
    }

    public BeaconArrayAdapter(Context context, List<Beacon> allBeacons) {
        super(context, R.layout.beacon_list_item, allBeacons);
        this.allBeacons = allBeacons;
        this.filteredBeacons = allBeacons;
    }

    @Override
    public int getCount() {
        return filteredBeacons.size();
    }

    @Override
    public Beacon getItem(int position) {
        return filteredBeacons.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.beacon_list_item, parent, false);
        }

        // Note: this is a listView and the convertView object here is likely to be
        // a recycled view of some other row that isn't in view. You need to set every
        // field regardless of emptiness to avoid displaying erroneous data.

        final Beacon beacon = getItem(position);

        ViewHolder viewHolder = new ViewHolder(convertView);

        viewHolder.mDeviceAddress.setText(beacon.deviceAddress);
        viewHolder.mRssi.setText(String.valueOf(beacon.rssi));

        final String distance = beacon.hasUidFrame ?
                String.format(Locale.getDefault(), "%.2f m", beacon.distanceFromRssi()) :
                "unknown";
        viewHolder.mDistance.setText(distance);


        if (!beacon.hasUidFrame) {
            grey(viewHolder.mUidLabel);
            viewHolder.mUidGroup.setVisibility(View.GONE);
        } else {
            if (beacon.uidStatus.getErrors().isEmpty()) {
                green(viewHolder.mUidLabel);
                viewHolder.mUidErrorGroup.setVisibility(View.GONE);
            } else {
                red(viewHolder.mUidLabel);
                viewHolder.mUidErrorGroup.setVisibility(View.VISIBLE);
                viewHolder.mUidErrors.setText(beacon.uidStatus.getErrors());
            }
            viewHolder.mUidNamespace.setText(beacon.uidStatus.uidValue.substring(0, 20));
            viewHolder.mUidInstance.setText(beacon.uidStatus.uidValue.substring(20, 32));
            viewHolder.mUidTxPower.setText(String.valueOf(beacon.uidStatus.txPower));
            viewHolder.mUidGroup.setVisibility(View.VISIBLE);
        }

        if (!beacon.hasTlmFrame) {
            grey(viewHolder.mTlmLabel);
            viewHolder.mTlmGroup.setVisibility(View.GONE);
        } else {
            if (beacon.tlmStatus.toString().isEmpty()) {
                green(viewHolder.mTlmLabel);
                viewHolder.mTlmErrorGroup.setVisibility(View.GONE);
            } else {
                red(viewHolder.mTlmLabel);
                viewHolder.mTlmErrorGroup.setVisibility(View.VISIBLE);
                viewHolder.mTlmErrors.setText(beacon.tlmStatus.getErrors());

            }
            viewHolder.mTlmVersion.setText(beacon.tlmStatus.version);
            viewHolder.mTlmVoltage.setText(beacon.tlmStatus.voltage);
            viewHolder.mTlmTemp.setText(beacon.tlmStatus.temp);
            viewHolder.mTlmAdvCount.setText(beacon.tlmStatus.advCnt);
            viewHolder.mTlmSecCnt.setText(beacon.tlmStatus.secCnt);
            viewHolder.mTlmGroup.setVisibility(View.VISIBLE);
        }

        if (!beacon.hasUrlFrame) {
            grey(viewHolder.mUrlLabel);
            viewHolder.mUrlStatus.setText("");
        } else {
            if (beacon.urlStatus.getErrors().isEmpty()) {
                green(viewHolder.mUrlLabel);
            } else {
                red(viewHolder.mUrlLabel);
            }
            viewHolder.mUrlStatus.setText(beacon.urlStatus.toString());
        }

        if (!beacon.frameStatus.getErrors().isEmpty()) {
            viewHolder.mFrameStatus.setText(beacon.frameStatus.toString());
            viewHolder.mFrameStatusGroup.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mFrameStatusGroup.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Beacon> filteredBeacons;
                if (constraint != null && constraint.length() != 0) {
                    filteredBeacons = new ArrayList<>();
                    for (Beacon beacon : allBeacons) {
                        if (beacon.contains(constraint.toString())) {
                            filteredBeacons.add(beacon);
                        }
                    }
                } else {
                    filteredBeacons = allBeacons;
                }
                results.count = filteredBeacons.size();
                results.values = filteredBeacons;
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredBeacons = (List<Beacon>) results.values;
                if (results.count == 0) {
                    notifyDataSetInvalidated();
                } else {
                    notifyDataSetChanged();
                }
            }
        };
    }

    private void green(TextView v) {
        v.setTextColor(DARK_GREEN);
    }

    private void red(TextView v) {
        v.setTextColor(DARK_RED);
    }

    private void grey(TextView v) {
        v.setTextColor(Color.GRAY);
    }


    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'beacon_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.deviceAddress)
        TextView mDeviceAddress;
        @Bind(R.id.rssi)
        TextView mRssi;
        @Bind(R.id.distance)
        TextView mDistance;
        @Bind(R.id.uidLabel)
        TextView mUidLabel;
        @Bind(R.id.uidNamespace)
        TextView mUidNamespace;
        @Bind(R.id.uidInstance)
        TextView mUidInstance;
        @Bind(R.id.uidTxPower)
        TextView mUidTxPower;
        @Bind(R.id.uidErrors)
        TextView mUidErrors;
        @Bind(R.id.uidErrorGroup)
        LinearLayout mUidErrorGroup;
        @Bind(R.id.uidGroup)
        LinearLayout mUidGroup;
        @Bind(R.id.tlmLabel)
        TextView mTlmLabel;
        @Bind(R.id.tlmVersion)
        TextView mTlmVersion;
        @Bind(R.id.tlmVoltage)
        TextView mTlmVoltage;
        @Bind(R.id.tlmTemp)
        TextView mTlmTemp;
        @Bind(R.id.tlmAdvCount)
        TextView mTlmAdvCount;
        @Bind(R.id.tlmSecCnt)
        TextView mTlmSecCnt;
        @Bind(R.id.tlmErrors)
        TextView mTlmErrors;
        @Bind(R.id.tlmErrorGroup)
        LinearLayout mTlmErrorGroup;
        @Bind(R.id.tlmGroup)
        LinearLayout mTlmGroup;
        @Bind(R.id.urlLabel)
        TextView mUrlLabel;
        @Bind(R.id.urlStatus)
        TextView mUrlStatus;
        @Bind(R.id.frameStatus)
        TextView mFrameStatus;
        @Bind(R.id.frameStatusGroup)
        LinearLayout mFrameStatusGroup;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
