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

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static com.codetroopers.materialAndroidBootstrap.core.beacons.Constants.TLM_FRAME_TYPE;
import static com.codetroopers.materialAndroidBootstrap.core.beacons.TestUtils.DEVICE_ADDRESS;
import static com.codetroopers.materialAndroidBootstrap.core.beacons.TestUtils.INITIAL_RSSI;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Basic tests for the tlmValidator class.
 */
public class TlmValidatorTest {

    private static final byte TLM_VERSION = 0x00;
    private static final byte[] TLM_VOLTAGE = {
            0x13, (byte) 0x88  // 5000 millivolts
    };
    private static final byte[] TLM_TEMP = {
            0x15, 0x00  // 21 °C
    };
    private static final byte[] ADV_CNT = {
            0x00, 0x00, 0x00, 0x01
    };
    private static final byte[] SEC_CNT = {
            0x00, 0x00, 0x00, 0x01
    };

    private TlmValidator tlmValidator;
    private Beacon beacon;

    @Before
    public void setUp() {
        tlmValidator = new TlmValidator();
        beacon = new Beacon(DEVICE_ADDRESS, INITIAL_RSSI);
    }

    @Test
    public void testtlmValidator_success() throws IOException {
        byte[] serviceData = tlmServiceData();
        tlmValidator.validate(DEVICE_ADDRESS, serviceData, beacon);

        // Only a TLM frame.
        assertTrue(beacon.hasTlmFrame);
        assertFalse(beacon.hasUidFrame);
        assertFalse(beacon.hasUrlFrame);

        // With no errors.
        assertTrue(Arrays.equals(serviceData, beacon.tlmServiceData));
        assertTrue(beacon.tlmStatus.getErrors().isEmpty());
    }

    @Test
    public void testtlmValidator_failsBadVersion() throws IOException {
        byte[] serviceData = tlmServiceData();
        serviceData[1] = 0x10;
        tlmValidator.validate(DEVICE_ADDRESS, serviceData, beacon);

        assertTrue(beacon.hasTlmFrame);
        assertFalse(beacon.tlmStatus.getErrors().isEmpty());
    }

    @Test
    public void testtlmValidator_passesVoltagePowered() throws Exception {
        // Devices that are powered should set the voltage to 0.
        byte[] serviceData = tlmServiceData();
        serviceData[2] = 0x00;
        serviceData[3] = 0x00;
        tlmValidator.validate(DEVICE_ADDRESS, serviceData, beacon);

        assertTrue(beacon.tlmStatus.getErrors().isEmpty());
    }

    @Test
    public void testtlmValidator_failsVoltageTooLow() throws IOException {
        // 500 mV is certainly "valid" but let's flag that as an problem since
        // if it's not an encoding issue, the beacon is about to die anyway.
        byte[] serviceData = tlmServiceData();
        serviceData[2] = 0x01;
        serviceData[3] = (byte) 0xf3;  // 499 mV
        tlmValidator.validate(DEVICE_ADDRESS, serviceData, beacon);

        assertNotNull(beacon.tlmStatus.errVoltage);
        assertFalse(beacon.tlmStatus.getErrors().isEmpty());
    }

    @Test
    public void testtlmValidator_failsVoltageTooHigh() throws IOException {
        byte[] serviceData = tlmServiceData();
        serviceData[2] = 0x27;
        serviceData[3] = 0x11;  // 10001 mV
        tlmValidator.validate(DEVICE_ADDRESS, serviceData, beacon);

        assertNotNull(beacon.tlmStatus.errVoltage);
        assertFalse(beacon.tlmStatus.getErrors().isEmpty());
    }

    @Test
    public void testtlmValidator_failsTempTooLow() throws IOException {
        byte[] serviceData = tlmServiceData();
        // -1 °C. No doubt that's a valid operating temperature for most beacons
        // but are you really validating your beacons when it's this cold?
        serviceData[4] = (byte) 0xff;
        serviceData[5] = 0x00;
        tlmValidator.validate(DEVICE_ADDRESS, serviceData, beacon);

        assertNotNull(beacon.tlmStatus.errTemp);
        assertFalse(beacon.tlmStatus.getErrors().isEmpty());
    }

    @Test
    public void testtlmValidator_failsTempTooHigh() throws IOException {
        byte[] serviceData = tlmServiceData();
        serviceData[4] = 0x3d;
        serviceData[5] = 0x00;  // 61 °C.
        tlmValidator.validate(DEVICE_ADDRESS, serviceData, beacon);

        assertNotNull(beacon.tlmStatus.errTemp);
        assertFalse(beacon.tlmStatus.getErrors().isEmpty());
    }

    @Test
    public void testtlmValidator_passesTempNotSupported() throws IOException {
        byte[] serviceData = tlmServiceData();
        serviceData[4] = (byte) 0x80;
        serviceData[5] = 0x00;  // -128 °C.
        tlmValidator.validate(DEVICE_ADDRESS, serviceData, beacon);

        assertNull(beacon.tlmStatus.errTemp);
        assertTrue(beacon.tlmStatus.getErrors().isEmpty());
    }

    @Test
    public void testtlmValidator_failsPduCountTooLow() throws IOException {
        // If it's just been started then 0 is fine, but otherwise it's a bug.
        byte[] serviceData = tlmServiceData();
        serviceData[6] = serviceData[7] = serviceData[8] = serviceData[9] = 0x00;
        tlmValidator.validate(DEVICE_ADDRESS, serviceData, beacon);

        assertNotNull(beacon.tlmStatus.errPduCnt);
        assertFalse(beacon.tlmStatus.getErrors().isEmpty());
    }

    @Test
    public void testtlmValidator_failsPduCountTooHigh() throws IOException {
        // Highest expected value is 946080000, or 0x38640900.
        // See Constants.MAX_EXPECTED_PDU_COUNT.
        byte[] serviceData = tlmServiceData();
        serviceData[6] = 0x38;
        serviceData[7] = 0x64;
        serviceData[8] = 0x09;
        serviceData[9] = 0x01;
        tlmValidator.validate(DEVICE_ADDRESS, serviceData, beacon);

        assertNotNull(beacon.tlmStatus.errPduCnt);
        assertFalse(beacon.tlmStatus.getErrors().isEmpty());
    }

    @Test
    public void testtlmValidator_failsPduCountNotIncreasing() throws IOException {
        byte[] t1 = tlmServiceData();
        tlmValidator.validate(DEVICE_ADDRESS, t1, beacon);

        // Rewind the timestamp so the next frame is stored and compared.
        beacon.timestamp -= (TlmValidator.STORE_NEXT_FRAME_DELTA_MS + 1);

        byte[] t2 = tlmServiceData();
        tlmValidator.validate(DEVICE_ADDRESS, t2, beacon);

        // Advance the boot counter so it's valid.
        t2[13] += 1;

        assertNotNull(beacon.tlmStatus.errPduCnt);
        assertFalse(beacon.tlmStatus.getErrors().isEmpty());
    }

    @Test
    public void testtlmValidator_failsSecCountTooLow() throws IOException {
        // If it's just been started then 0 is fine, but otherwise it's a bug.
        byte[] serviceData = tlmServiceData();
        serviceData[10] = serviceData[11] = serviceData[12] = serviceData[13] = 0x00;
        tlmValidator.validate(DEVICE_ADDRESS, serviceData, beacon);

        assertNotNull(beacon.tlmStatus.errSecCnt);
        assertFalse(beacon.tlmStatus.getErrors().isEmpty());
    }

    @Test
    public void testtlmValidator_failsSecCountTooHigh() throws IOException {
        // Highest expected value is 946080000, or 0x38640900 (yes, same as PduCnt).
        // See tlmValidator.MAX_EXPECTED_PDU_COUNT.
        byte[] serviceData = tlmServiceData();
        serviceData[10] = 0x38;
        serviceData[11] = 0x64;
        serviceData[12] = 0x09;
        serviceData[13] = 0x01;
        tlmValidator.validate(DEVICE_ADDRESS, serviceData, beacon);

        assertNotNull(beacon.tlmStatus.errSecCnt);
        assertFalse(beacon.tlmStatus.getErrors().isEmpty());
    }

    @Test
    public void testtlmValidator_failsSecCountNotIncreasing() throws IOException {
        byte[] t1 = tlmServiceData();
        tlmValidator.validate(DEVICE_ADDRESS, t1, beacon);

        // Rewind the timestamp so the next frame is stored and compared.
        beacon.timestamp -= (TlmValidator.STORE_NEXT_FRAME_DELTA_MS + 1);

        byte[] t2 = tlmServiceData();
        tlmValidator.validate(DEVICE_ADDRESS, t2, beacon);

        // Advance the PDU counter so it's valid.
        t2[9] += 1;

        assertNotNull(beacon.tlmStatus.errSecCnt);
        assertFalse(beacon.tlmStatus.getErrors().isEmpty());
    }

    @Test
    public void testtlmValidator_succeedsWhenPduAndSecCountIncreasing() throws IOException {
        byte[] t1 = tlmServiceData();
        tlmValidator.validate(DEVICE_ADDRESS, t1, beacon);

        // Rewind the timestamp so the next frame is stored and compared.
        beacon.timestamp -= (TlmValidator.STORE_NEXT_FRAME_DELTA_MS + 1);

        byte[] t2 = tlmServiceData();
        t2[9] += 1;   // Advance PDU count.
        t2[13] += 1;  // Advance SEC count.
        tlmValidator.validate(DEVICE_ADDRESS, t2, beacon);

        assertNull(beacon.tlmStatus.errPduCnt);
        assertNull(beacon.tlmStatus.errSecCnt);
        assertTrue(beacon.tlmStatus.getErrors().isEmpty());
    }

    private byte[] tlmServiceData() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write(TLM_FRAME_TYPE);
        os.write(TLM_VERSION);
        os.write(TLM_VOLTAGE);
        os.write(TLM_TEMP);
        os.write(ADV_CNT);
        os.write(SEC_CNT);
        return os.toByteArray();
    }

}
