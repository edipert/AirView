package com.android.airview.di.module

import com.android.airview.ui.main.MainFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {


    @ContributesAndroidInjector
    abstract fun provideMainFragmentFactory(): MainFragment
}