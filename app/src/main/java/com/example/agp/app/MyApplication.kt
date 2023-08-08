package com.example.agp.app

import android.app.Application
import android.util.Log
import com.example.agp.applifecycle.api.IApplicationManager

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        IApplicationManager.init()
        IApplicationManager.create()

        Log.i(
            "IApplication",
            "收集到的IApplication实现类=" + IApplicationManager.getIApplicationList()
        )
    }
}