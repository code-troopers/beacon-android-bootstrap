package com.codetroopers.materialAndroidBootstrap.core.components;

import com.codetroopers.materialAndroidBootstrap.core.modules.AndroidModule;
import com.codetroopers.materialAndroidBootstrap.core.modules.ApplicationModule;
import com.codetroopers.materialAndroidBootstrap.core.modules.BeaconsModule;
import com.codetroopers.materialAndroidBootstrap.core.modules.HomeActivityModule;

import javax.inject.Singleton;

import dagger.Component;

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
}