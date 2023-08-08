package com.govee.lifecycle.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author:YangQi.Chen
 * @date:2023/8/8 16:22
 * @description:
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface AppLifecycle {
}