package com.codetroopers.materialAndroidBootstrap.core.components;

import com.codetroopers.materialAndroidBootstrap.core.modules.BeaconsModule;
import com.codetroopers.materialAndroidBootstrap.core.modules.HomeActivityModule;
import com.codetroopers.materialAndroidBootstrap.ui.activity.HomeActivity;
import com.codetroopers.materialAndroidBootstrap.ui.fragment.MainActivityFragment;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(
        modules = {
                HomeActivityModule.class,
                BeaconsModule.class
        }
)
public interface HomeActivityComponent {
    void injectActivity(HomeActivity homeActivity);

    void inject(MainActivityFragment mainActivityFragment);
}
