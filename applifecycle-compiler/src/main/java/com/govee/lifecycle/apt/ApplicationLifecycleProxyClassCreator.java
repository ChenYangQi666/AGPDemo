package com.govee.lifecycle.apt;

import com.govee.lifecycle.ApplicationLifecycleConfig;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * @ClassName ApplicationLifecycleProxyClassCreator
 * @Author linpeng
 * @Date 2022/6/14 3:38 下午
 * @Description
 */
public class ApplicationLifecycleProxyClassCreator {
    private static final String METHOD_CREAT = "create";
    private static final String METHOD_CLOSE = "close";
    private static final String METHOD_APP2FOREGROUND = "app2Foreground";
    private static final String METHOD_APP2BACKGROUND = "app2Background";
    private static final String METHOD_ONDEVICESLIST = "onDevicesList";
    private static final String METHOD_ONLOGOUT = "onLogout";

    private static final String FIELD_APPLICATION_LIFECYCLE_CALLBACK = "mApplicationLifecycleCallback";

    public static boolean generateProxyClassCode(TypeElement typeElement, Filer filer) {
        TypeSpec appLifecycleProxyClass = getApplicationLifecycleProxyClass(typeElement);
        JavaFile javaFile = JavaFile.builder(ApplicationLifecycleConfig.PROXY_CLASS_PACKAGE_NAME, appLifecycleProxyClass).build();
        try {
            javaFile.writeTo(filer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static TypeSpec getApplicationLifecycleProxyClass(TypeElement typeElement) {
        return TypeSpec.classBuilder(getProxyClassName(typeElement.getSimpleName().toString()))
                .addSuperinterface(TypeName.get(typeElement.getInterfaces().get(0)))
                .addModifiers(Modifier.PUBLIC)
                .addField(TypeName.get(typeElement.getInterfaces().get(0)), FIELD_APPLICATION_LIFECYCLE_CALLBACK, Modifier.PRIVATE, Modifier.FINAL)
                .addMethod(getConstructorMethod(typeElement))
                .addMethod(getCreateMethod())
                .addMethod(getCloseMethod())
                .addMethod(getApp2ForegroundMethod())
                .addMethod(getApp2BackMethod())
                .addMethod(getOnDevicesListMethod())
                .addMethod(getOnLogoutMethod())
                .build();
    }

    private static String getProxyClassName(String simpleClassName) {
        return ApplicationLifecycleConfig.PROXY_CLASS_PREFIX + simpleClassName +
                ApplicationLifecycleConfig.PROXY_CLASS_SUFFIX;
    }

    private static MethodSpec getOnLogoutMethod() {
        return MethodSpec.methodBuilder(METHOD_ONLOGOUT)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(Override.class)
                .addStatement("this.$N.$N()", FIELD_APPLICATION_LIFECYCLE_CALLBACK, METHOD_ONLOGOUT)
                .build();
    }

    private static MethodSpec getOnDevicesListMethod() {
        return MethodSpec.methodBuilder(METHOD_ONDEVICESLIST)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(Override.class)
                .addStatement("this.$N.$N()", FIELD_APPLICATION_LIFECYCLE_CALLBACK, METHOD_ONDEVICESLIST)
                .build();
    }

    private static MethodSpec getApp2BackMethod() {
        return MethodSpec.methodBuilder(METHOD_APP2BACKGROUND)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(Override.class)
                .addStatement("this.$N.$N()", FIELD_APPLICATION_LIFECYCLE_CALLBACK, METHOD_APP2BACKGROUND)
                .build();
    }

    private static MethodSpec getApp2ForegroundMethod() {
        return MethodSpec.methodBuilder(METHOD_APP2FOREGROUND)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(Override.class)
                .addStatement("this.$N.$N()", FIELD_APPLICATION_LIFECYCLE_CALLBACK, METHOD_APP2FOREGROUND)
                .build();
    }

    private static MethodSpec getCloseMethod() {
        return MethodSpec.methodBuilder(METHOD_CLOSE)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(Override.class)
                .addStatement("this.$N.$N()", FIELD_APPLICATION_LIFECYCLE_CALLBACK, METHOD_CLOSE)
                .build();
    }

    private static MethodSpec getConstructorMethod(TypeElement typeElement) {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("this.$N = new $T()", FIELD_APPLICATION_LIFECYCLE_CALLBACK, ClassName.get(typeElement))
                .build();
    }

    private static MethodSpec getCreateMethod() {
        return MethodSpec.methodBuilder(METHOD_CREAT)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(Override.class)
                .addStatement("this.$N.$N()", FIELD_APPLICATION_LIFECYCLE_CALLBACK, METHOD_CREAT)
                .build();
    }

}
