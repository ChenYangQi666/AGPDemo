package com.example.plugin

import groovy.io.FileType
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

abstract class AppLifecycleTask extends DefaultTask {

    @InputFiles
    abstract ListProperty<RegularFile> getAllJars();

    @InputFiles
    abstract ListProperty<Directory> getAllDirectories();

    @OutputFiles
    abstract RegularFileProperty getOutput();

    @TaskAction
    void taskAction() {
        println("---------TheRouter transform start-------------")
        OutputStream jarOutput = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(getOutput().get().getAsFile())))
        allJars.get().each { file ->
            JarFile jarFile = new JarFile(file.asFile)
            for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
                JarEntry jarEntry = e.nextElement()
                try {
                    jarOutput.putNextEntry(new JarEntry(jarEntry.name))
                    jarFile.getInputStream(jarEntry).withCloseable { inputStream ->
                        if (jarEntry.name.contains("AppLike")) {
                            println("scan class in jar ：" + jarEntry.name)
                        }
                        jarOutput << inputStream
                    }
                    jarOutput.closeEntry()
                } catch (Exception e1) {
                    //println("open jar error，" + e1.getMessage())
                }
            }
            jarFile.close()
        }

        getAllDirectories().get().each { directory ->
            directory.asFile.traverse(type: FileType.FILES) { file ->
                String relativePath = directory.asFile.toURI()
                        .relativize(file.toURI())
                        .getPath()
                        .replace(File.separatorChar, '/' as char)
                jarOutput.putNextEntry(new JarEntry(relativePath))
                new FileInputStream(file).withCloseable { inputStream ->
                    if (relativePath.contains("AppLike")) {
                        println("scan class in dir:" + relativePath)
                    }
                    jarOutput << inputStream
                }
                jarOutput.closeEntry()
            }
        }
        jarOutput.close()


        println("---------TheRouter transform finish-------------")
    }
}