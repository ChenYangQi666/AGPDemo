package com.chen.apt.test1;

import android.util.Log;

import com.example.agp.applifecycle.api.IApplication;

public class Test1Application implements IApplication {

    @Override
    public void create() {
        Log.i("IApplication","Test1Application---->create()");
    }
}
