package com.example.agp.test2;

import android.util.Log;

import com.example.agp.applifecycle.api.IApplication;

public class Test2Application implements IApplication {
    @Override
    public void create() {
        Log.i("IApplication","Test2Application---->create()");
    }
}
