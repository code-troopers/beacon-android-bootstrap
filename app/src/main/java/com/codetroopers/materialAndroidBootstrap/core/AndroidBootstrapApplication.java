package com.codetroopers.materialAndroidBootstrap.core;

import android.app.Application;
import android.util.Log;
import com.codetroopers.materialAndroidBootstrap.BuildConfig;
import com.codetroopers.materialAndroidBootstrap.core.components.ApplicationComponent;
import com.codetroopers.materialAndroidBootstrap.core.components.ComponentsFactory;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.log.LogLevel;
import com.squareup.leakcanary.LeakCanary;
import icepick.Icepick;
import timber.log.Timber;

public class AndroidBootstrapApplication extends Application implements HasComponent<ApplicationComponent> {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        //Uncomment to add crashlytics
        //Fabric.with(this, new Crashlytics());

        initLoggers();
        Icepick.setDebug(BuildConfig.DEBUG);
        LeakCanary.install(this);

        initKontaktSdk();
    }

    @Override
    public ApplicationComponent getComponent() {
        if (applicationComponent == null) {
            // Dagger component init
            applicationComponent = ComponentsFactory.get().buildApplicationComponent(this);
        }
        return applicationComponent;
    }

    /**
     * Timber init
     */
    private void initLoggers() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            // only log INFO+ with no tag tracing the calling class
            Timber.plant(new Timber.Tree() {
                @Override
                protected void log(int priority, String tag, String message, Throwable t) {
                    if (priority != Log.VERBOSE && priority != Log.DEBUG) {
                        Log.println(priority, tag, message);
                    }
                }
            });
        }
    }

    private void initKontaktSdk() {
        KontaktSDK kontaktSDK = KontaktSDK.initialize(this);
        if (BuildConfig.DEBUG) {
            kontaktSDK
                    .setDebugLoggingEnabled(BuildConfig.DEBUG)
                    .setLogLevelEnabled(LogLevel.DEBUG, true);
        }
    }
}