package com.codetroopers.materialAndroidBootstrap.core.modules;

import com.codetroopers.materialAndroidBootstrap.core.beacons.BeaconsSession;
import com.codetroopers.materialAndroidBootstrap.core.beacons.TlmValidator;
import com.codetroopers.materialAndroidBootstrap.core.beacons.UidValidator;
import com.codetroopers.materialAndroidBootstrap.core.beacons.UrlValidator;

import dagger.Module;
import dagger.Provides;

@Module
public class BeaconsModule {
    @Provides
    @ForApplication
    BeaconsSession provideSession() {
        return new BeaconsSession();
    }

    @Provides
    TlmValidator provideTlmValidator() {
        return new TlmValidator();
    }

    @Provides
    UidValidator provideUidValidator() {
        return new UidValidator();
    }

    @Provides
    UrlValidator provideUrlValidator() {
        return new UrlValidator();
    }
}
