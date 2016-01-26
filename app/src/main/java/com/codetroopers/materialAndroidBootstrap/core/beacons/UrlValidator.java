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

import java.util.Arrays;
import java.util.Locale;

import timber.log.Timber;

import static com.codetroopers.materialAndroidBootstrap.core.beacons.Constants.MAX_EXPECTED_TX_POWER;
import static com.codetroopers.materialAndroidBootstrap.core.beacons.Constants.MIN_EXPECTED_TX_POWER;


/**
 * Basic validation of an Eddystone-URL frame. <p>
 *
 * @see <a href="https://github.com/google/eddystone/eddystone-url">URL frame specification</a>
 */
public class UrlValidator {

    public UrlValidator() {
    }

    public void validate(String deviceAddress, byte[] serviceData, Beacon beacon) {
        beacon.hasUrlFrame = true;

        // Tx power should have reasonable values.
        int txPower = (int) serviceData[1];
        if (txPower < MIN_EXPECTED_TX_POWER || txPower > MAX_EXPECTED_TX_POWER) {
            String err = String.format(Locale.getDefault(),
                    "Expected URL Tx power between %d and %d, got %d",
                    MIN_EXPECTED_TX_POWER, MAX_EXPECTED_TX_POWER, txPower);
            beacon.urlStatus.txPower = err;
            Timber.e("%s: %s", deviceAddress, err);
        }

        // The URL bytes should not be all zeroes.
        byte[] urlBytes = Arrays.copyOfRange(serviceData, 2, 20);
        if (Utils.isZeroed(urlBytes)) {
            String err = "URL bytes are all 0x00";
            beacon.urlStatus.urlNotSet = err;
            Timber.e("%s: %s", deviceAddress, err);
        }

        beacon.urlStatus.urlValue = UrlUtils.decodeUrl(serviceData);
    }
}
