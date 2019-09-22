package com.android.airview.di.component

import com.android.airview.BaseTest
import com.android.airview.di.module.ActivityModule
import com.android.airview.di.module.AppModule
import com.android.airview.di.module.TestRxJavaModule
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, AppModule::class, ActivityModule::class, TestRxJavaModule::class])
interface TestAppComponent {
    fun inject(baseTest: BaseTest)
}