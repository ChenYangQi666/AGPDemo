package com.example.agp.app;

import java.util.ArrayList;
import java.util.List;

public class AppLifecycleManager {
    private static List<IApplication> iApplicationList = new ArrayList<>();

    public static void init() {
    }

    public static void registerAppLifecycle(String appLifecycleClassName) {
        if (appLifecycleClassName == null || appLifecycleClassName.isEmpty()) {
            return;
        }
        try {
            Object object = Class.forName(appLifecycleClassName).getConstructor().newInstance();
            if (object instanceof IApplication) {
                iApplicationList.add((IApplication) object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<IApplication> getIApplicationList() {
        return iApplicationList;
    }
}
