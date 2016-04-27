package com.codetroopers.materialAndroidBootstrap.core.components;

import com.codetroopers.materialAndroidBootstrap.core.AndroidBootstrapApplication;
import com.codetroopers.materialAndroidBootstrap.core.modules.AndroidModule;
import com.codetroopers.materialAndroidBootstrap.core.modules.ApplicationModule;
import com.codetroopers.materialAndroidBootstrap.core.modules.BeaconsModule;
import com.codetroopers.materialAndroidBootstrap.core.modules.HomeActivityModule;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(
        modules = {
                ApplicationModule.class,
                AndroidModule.class,
                BeaconsModule.class
        }
)
public interface ApplicationComponent {
    HomeActivityComponent homeActivityComponent(HomeActivityModule homeActivityModule);

    void inject(AndroidBootstrapApplication androidBootstrapApplication);
}