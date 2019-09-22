package com.android.airview

import android.app.Activity
import android.app.Application
import com.android.airview.di.component.AppComponent
import com.android.airview.di.component.DaggerAppComponent
import com.android.airview.di.module.AppModule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

open class MainApplication : Application(), HasActivityInjector, AppComponent.ComponentProvider {

    override fun activityInjector(): AndroidInjector<Activity> {
        return activityInjector
    }

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        inject()
    }

    open fun inject() {
        appComponent = DaggerAppComponent.builder()
            // Bind application instance to component
            .application(this)
            .appModule(AppModule(BuildConfig.BASE_URL))
            .build()

        appComponent.inject(this)
    }
}