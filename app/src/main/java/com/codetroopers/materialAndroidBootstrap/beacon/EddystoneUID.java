package com.codetroopers.materialAndroidBootstrap.beacon;

import org.altbeacon.beacon.Identifier;

public class EddystoneUID {
    public final Identifier namespaceId;
    public final Identifier instanceId;

    public EddystoneUID(Identifier namespaceId, Identifier instanceId) {
        this.namespaceId = namespaceId;
        this.instanceId = instanceId;
    }
}
