package com.example.agp.applifecycle.api;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class IApplicationManager {

    private static List<IApplication> iApplicationList = new ArrayList<>();

    public static void init() {
        // 通过AMS在这里插入代码
    }

    public static void registerAppLifecycle(IApplication appLifecycleCallbacks) {
        iApplicationList.add(appLifecycleCallbacks);
    }

    public static void registerAppLifecycle(String appLifecycleClassName) {
        if (TextUtils.isEmpty(appLifecycleClassName)) {
            return;
        }
        try {
            Object object = Class.forName(appLifecycleClassName).getConstructor().newInstance();
            if (object instanceof IApplication) {
                registerAppLifecycle((IApplication) object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void create() {
        if (iApplicationList.isEmpty()) {
            return;
        }
        for (IApplication callbacks : iApplicationList) {
            callbacks.create();
        }
    }

    public static List<IApplication> getIApplicationList() {
        return iApplicationList;
    }
}
