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

package com.codetroopers.materialAndroidBootstrap.core.beacons;

public class Beacon {
    private static final String BULLET = "● ";
    public final String deviceAddress;
    public int rssi;
    // TODO: rename to make explicit the validation intent of this timestamp. We use it to
    // remember a recent frame to make sure that non-monotonic TLM values increase.
    long timestamp = System.currentTimeMillis();

    // Used to remove devices from the listview when they haven't been seen in a while.
    public long lastSeenTimestamp = System.currentTimeMillis();

    byte[] uidServiceData;
    byte[] tlmServiceData;
    byte[] urlServiceData;

    public class UidStatus {
        public String uidValue;
        public int txPower;

        String errTx;
        String errUid;
        String errRfu;

        public String getErrors() {
            StringBuilder sb = new StringBuilder();
            if (errTx != null) {
                sb.append(BULLET).append(errTx).append("\n");
            }
            if (errUid != null) {
                sb.append(BULLET).append(errUid).append("\n");
            }
            if (errRfu != null) {
                sb.append(BULLET).append(errRfu).append("\n");
            }
            return sb.toString().trim();
        }
    }

    public class TlmStatus {
        public String version;
        public String voltage;
        public String temp;
        public String advCnt;
        public String secCnt;

        String errIdentialFrame;
        String errVersion;
        String errVoltage;
        String errTemp;
        String errPduCnt;
        String errSecCnt;
        String errRfu;

        public String getErrors() {
            StringBuilder sb = new StringBuilder();
            if (errIdentialFrame != null) {
                sb.append(BULLET).append(errIdentialFrame).append("\n");
            }
            if (errVersion != null) {
                sb.append(BULLET).append(errVersion).append("\n");
            }
            if (errVoltage != null) {
                sb.append(BULLET).append(errVoltage).append("\n");
            }
            if (errTemp != null) {
                sb.append(BULLET).append(errTemp).append("\n");
            }
            if (errPduCnt != null) {
                sb.append(BULLET).append(errPduCnt).append("\n");
            }
            if (errSecCnt != null) {
                sb.append(BULLET).append(errSecCnt).append("\n");
            }
            if (errRfu != null) {
                sb.append(BULLET).append(errRfu).append("\n");
            }
            return sb.toString().trim();
        }

        @Override
        public String toString() {
            return getErrors();
        }
    }

    public class UrlStatus {
        String urlValue;
        String urlNotSet;
        String txPower;

        public String getErrors() {
            StringBuilder sb = new StringBuilder();
            if (txPower != null) {
                sb.append(BULLET).append(txPower).append("\n");
            }
            if (urlNotSet != null) {
                sb.append(BULLET).append(urlNotSet).append("\n");
            }
            return sb.toString().trim();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (urlValue != null) {
                sb.append(urlValue).append("\n");
            }
            return sb.append(getErrors()).toString().trim();
        }
    }

    public class FrameStatus {
        public String nullServiceData;
        String tooShortServiceData;
        public String invalidFrameType;

        public String getErrors() {
            StringBuilder sb = new StringBuilder();
            if (nullServiceData != null) {
                sb.append(BULLET).append(nullServiceData).append("\n");
            }
            if (tooShortServiceData != null) {
                sb.append(BULLET).append(tooShortServiceData).append("\n");
            }
            if (invalidFrameType != null) {
                sb.append(BULLET).append(invalidFrameType).append("\n");
            }
            return sb.toString().trim();
        }

        @Override
        public String toString() {
            return getErrors();
        }
    }

    public boolean hasUidFrame;
    public final UidStatus uidStatus = new UidStatus();

    public boolean hasTlmFrame;
    public final TlmStatus tlmStatus = new TlmStatus();

    public boolean hasUrlFrame;
    public final UrlStatus urlStatus = new UrlStatus();

    public final FrameStatus frameStatus = new FrameStatus();

    public Beacon(String deviceAddress, int rssi) {
        this.deviceAddress = deviceAddress;
        this.rssi = rssi;
    }

    /**
     * Performs a case-insensitive contains test of s on the device address (with or without the
     * colon separators) and/or the UID value, and/or the URL value.
     */
    public boolean contains(String s) {
        return s == null
                || s.isEmpty()
                || deviceAddress.replace(":", "").toLowerCase().contains(s.toLowerCase())
                || (uidStatus.uidValue != null
                && uidStatus.uidValue.toLowerCase().contains(s.toLowerCase()))
                || (urlStatus.urlValue != null
                && urlStatus.urlValue.toLowerCase().contains(s.toLowerCase()));
    }
}
