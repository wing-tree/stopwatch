package com.wing.tree.bruni.stopwatch

import android.app.Application
import com.google.android.gms.ads.MobileAds

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
    }
}