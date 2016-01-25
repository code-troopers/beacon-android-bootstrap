package com.codetroopers.materialAndroidBootstrap.core.modules;

import android.content.Context;

import com.codetroopers.materialAndroidBootstrap.core.beacons.BeaconsSession;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private final Context applicationContext;

    public ApplicationModule(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Allow the application context to be injected but require that it be annotated with
     * {@link ForApplication @Annotation} to explicitly differentiate it from an activity context.
     */
    @Provides
    @ForApplication
    Context provideApplicationContext() {
        return applicationContext;
    }

    @Provides
    @ForApplication
    BeaconsSession provideSession() {
        return new BeaconsSession();
    }
}