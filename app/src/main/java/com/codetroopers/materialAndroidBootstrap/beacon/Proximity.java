package com.codetroopers.materialAndroidBootstrap.beacon;

/**
 * Inspired from KontakIO SDK
 */
public enum Proximity {
    /**
     * Android device distance from Beacon is within [0 - 0,5]m.
     */
    IMMEDIATE,
    /**
     * Android device distance from Beacon is within [0,5 - 3]m.
     */
    NEAR,
    /**
     * Android device distance from Beacon is higher than 3m.
     */
    FAR,
    /**
     * The UNKNOWN.
     */
    UNKNOWN;

    private static final double DISTANCE_THRESHOLD_UNKNOWN = 0.0;
    private static final double DISTANCE_THRESHOLD_IMMEDIATE = 0.5;
    private static final double DISTANCE_THRESHOLD_NEAR = 3.0;

    /**
     * Categorizes accuracy to Proximity.
     *
     * @param accuracy the accuracy
     * @return the proximity
     */
    public static Proximity fromDistance(final double accuracy) {
        if (accuracy < DISTANCE_THRESHOLD_UNKNOWN) {
            return UNKNOWN;
        }

        if (accuracy < DISTANCE_THRESHOLD_IMMEDIATE) {
            return IMMEDIATE;
        }

        if (accuracy < DISTANCE_THRESHOLD_NEAR) {
            return NEAR;
        }

        return FAR;
    }
}
