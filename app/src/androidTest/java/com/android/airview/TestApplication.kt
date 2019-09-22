package com.android.airview

import com.android.airview.di.component.DaggerAppComponent
import com.android.airview.di.module.AppModule

class TestApplication : MainApplication() {
    override fun inject() {
        appComponent = DaggerAppComponent.builder()
            .application(this)
            .appModule(AppModule(BuildConfig.BASE_URL))
            .build()

        appComponent.inject(this)
    }
}