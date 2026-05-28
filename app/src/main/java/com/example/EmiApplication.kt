package com.example

import android.app.Application
import com.example.data.AppContainer

class EmiApplication : Application() {
    
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
