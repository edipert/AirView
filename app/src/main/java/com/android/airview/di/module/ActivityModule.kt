package com.android.airview.di.module

import com.android.airview.MainActivity
import com.android.airview.di.util.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityModule {

    //Bind AndroidInjector with the activity
    @ContributesAndroidInjector(modules = [FragmentModule::class])
    @ActivityScope
    internal abstract fun contributeMainActivity(): MainActivity
}