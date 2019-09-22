package com.android.airview

import android.os.Bundle
import com.android.airview.ui.base.BaseActivity
import com.android.airview.ui.main.MainFragment

class MainActivity : BaseActivity() {

    // Single activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
}
