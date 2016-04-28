package com.codetroopers.materialAndroidBootstrap.core;

import android.app.Application;
import android.util.Log;
import com.codetroopers.materialAndroidBootstrap.BuildConfig;
import com.codetroopers.materialAndroidBootstrap.core.components.ApplicationComponent;
import com.codetroopers.materialAndroidBootstrap.core.components.ComponentsFactory;
import com.squareup.leakcanary.LeakCanary;
import icepick.Icepick;
import org.altbeacon.beacon.logging.LogManager;
import org.altbeacon.beacon.logging.Loggers;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import timber.log.Timber;

import javax.inject.Inject;

public class AndroidBootstrapApplication extends Application implements HasComponent<ApplicationComponent> {
    private ApplicationComponent applicationComponent;

    /**
     * Simply constructing this class and holding a reference to it in the Application class
     * enables auto battery saving of about 60%
     */
    @Inject
    BackgroundPowerSaver backgroundPowerSaver;

    @Override
    public void onCreate() {
        super.onCreate();
        //Uncomment to add crashlytics
        //Fabric.with(this, new Crashlytics());

        getComponent().inject(this);

        initLoggers();
        Icepick.setDebug(BuildConfig.DEBUG);
        LeakCanary.install(this);
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
            LogManager.setLogger(Loggers.verboseLogger());
            LogManager.setVerboseLoggingEnabled(true);
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
            LogManager.setLogger(Loggers.warningLogger());
            LogManager.setVerboseLoggingEnabled(false);
        }
    }
}