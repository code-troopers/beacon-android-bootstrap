package com.codetroopers.materialAndroidBootstrap.core;

import com.squareup.otto.Bus;

import hugo.weaving.DebugLog;

@DebugLog
public class CTBus extends Bus {
    @Override
    public void post(Object event) {
        super.post(event);
    }

    @Override
    public void register(Object object) {
        super.register(object);
    }

    @Override
    public void unregister(Object object) {
        super.unregister(object);
    }
}
