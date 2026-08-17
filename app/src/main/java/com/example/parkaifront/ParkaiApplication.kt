package com.example.parkaifront

import android.app.Application
import android.preference.PreferenceManager
import org.osmdroid.config.Configuration

class ParkaiApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Configuration.getInstance().load(
            this,
            PreferenceManager.getDefaultSharedPreferences(this)
        )
        // User-Agent único y descriptivo, como pide la política de OSM
        Configuration.getInstance().userAgentValue = packageName
    }
}