package com.govee.lifecycle;

/**
 * @ClassName ApplicationLifecycleConfig
 * @Author linpeng
 * @Date 2022/6/14 3:38 下午
 * @Description
 */
public class ApplicationLifecycleConfig {
    /**
     * 生成代理类的包名
     */
    public static final String PROXY_CLASS_PACKAGE_NAME = "com.govee.lifecycle.apt.proxy";

    /**
     * 生成代理类统一的后缀
     */
    public static final String PROXY_CLASS_SUFFIX = "$$Proxy";

    /**
     * 生成代理类统一的前缀
     */
    public static final String PROXY_CLASS_PREFIX = "AppLife$$";


    public static final String APPLICATION_LIFECYCLE_CALLBACK_QUALIFIED_NAME = "com.ihoment.base2app.IApplication";

    public static final String APPLICATION_LIFECYCLE_CALLBACK_SIMPLE_NAME = "IApplication";

    public static final String CONTEXT = "android.content.Context";
}
