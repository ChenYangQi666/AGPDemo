package com.example.agp.app.proxy;

import android.util.Log;

import com.example.agp.app.IApplication;

public class AppLikeProxy2 implements IApplication {
    @Override
    public void create() {
        Log.i("AppLifecycle", "AppLikeProxy2----->onCreate");
    }
}
