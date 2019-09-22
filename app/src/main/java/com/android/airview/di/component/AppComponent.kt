package com.android.airview.di.component

import android.app.Application
import com.android.airview.MainApplication
import com.android.airview.di.module.ActivityModule
import com.android.airview.di.module.AppModule
import com.android.airview.di.module.RxJavaModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, ActivityModule::class, RxJavaModule::class, AndroidSupportInjectionModule::class])
interface AppComponent : AndroidInjector<MainApplication> {
    override fun inject(instance: MainApplication)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun appModule(appModule: AppModule): Builder
        fun rxJavaModule(rxJavaModule: RxJavaModule): Builder
        fun build(): AppComponent
    }

    interface ComponentProvider {
        val appComponent: AppComponent
    }
}