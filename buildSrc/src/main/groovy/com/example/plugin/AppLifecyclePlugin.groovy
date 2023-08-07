package com.example.plugin

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.api.variant.Variant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Action
import org.gradle.api.tasks.TaskProvider

class AppLifecyclePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println "--->AppLifecyclePlugin"
        def isLibrary = project.plugins.hasPlugin("com.android.library")
        if (!isLibrary) {
            def android = project.extensions.getByType(AndroidComponentsExtension.class)
            android.onVariants(android.selector().all(), new Action<Variant>() {
                @Override
                void execute(Variant variant) {
                    TaskProvider<AppLifecycleTask> getAllClassesTask = project.tasks.register("${variant.name}TheRouterGetAllClasses", AppLifecycleTask.class)
                    variant.artifacts
                            .forScope(ScopedArtifacts.Scope.ALL)
                            .use(getAllClassesTask)
                            .toTransform(ScopedArtifact.CLASSES.INSTANCE, { it.getAllJars() }, { it.getAllDirectories() }, { it.getOutput() })
                }
            })
        } else {
            throw new RuntimeException("com.example.applifecyele must call in Application module")
        }
    }
}