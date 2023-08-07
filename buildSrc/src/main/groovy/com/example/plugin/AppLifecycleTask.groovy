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
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter

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
        def iApplicationList = []
        File theManagerFile = null

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
                            iApplicationList.add(jarEntry.name)
                        }
                        if (jarEntry.name.contains("AppLifecycleManager")) {
                            println("-------------*****>>>" + jarEntry.name)
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
                        iApplicationList.add(relativePath)
                    }
                    if (relativePath.contains("AppLifecycleManager")) {
                        println("-2------------*****>>>" + relativePath)
                        theManagerFile = file
                    }
                    jarOutput << inputStream
                }
                jarOutput.closeEntry()
            }
        }

        if (iApplicationList.empty) {
            println("iApplicationList is empty!")
        } else {
            if (theManagerFile != null) {
                println("iApplicationList size=" + iApplicationList.size())
                println("Manager class name=" + theManagerFile.name)
                new FileInputStream(theManagerFile).withCloseable { inputStream ->
                    ClassReader reader = new ClassReader(new FileInputStream(theManagerFile.absolutePath))
                    // 构建一个ClassWriter对象，并设置让系统自动计算栈和本地变量大小
                    ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
                    ClassVisitor classVisitor = new AppLifecycleClassVisitor(classWriter, iApplicationList)
                    //开始扫描class文件
                    reader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
                    byte[] bytes = classWriter.toByteArray()
                    println("----------------->6")
                    jarOutput.write(bytes)
                    println("----------------->7")
                    jarOutput << inputStream
                }
            } else {
                println("theManagerFile is empty!")
            }
        }
        jarOutput.close()
        println("---------TheRouter transform finish-------------")
    }

    class AppLifecycleClassVisitor extends ClassVisitor {

        private ClassVisitor mClassVisitor
        List<String> proxyAppLifecycleClassList

        AppLifecycleClassVisitor(ClassVisitor classVisitor, List<String> list) {
            super(Opcodes.ASM5, classVisitor)
            mClassVisitor = classVisitor
            proxyAppLifecycleClassList = list
        }

        @Override
        MethodVisitor visitMethod(int access, String name,
                                  String desc, String signature,
                                  String[] exception) {
            println "LifeCycleTransform: visit method: " + name
            MethodVisitor methodVisitor = mClassVisitor.visitMethod(access, name, desc, signature, exception)
            //找到 AppLifeCycleManager里的init()方法
            if ("init" == name) {
                methodVisitor = new LoadAppLifecycleMethodAdapter(proxyAppLifecycleClassList, methodVisitor, access, name, desc)
            }
            return methodVisitor
        }
    }

    class LoadAppLifecycleMethodAdapter extends AdviceAdapter {
        List<String> proxyAppLifecycleClassList

        LoadAppLifecycleMethodAdapter(List<String> list, MethodVisitor mv, int access, String name, String desc) {
            super(Opcodes.ASM5, mv, access, name, desc)
            proxyAppLifecycleClassList = list
        }

        @Override
        protected void onMethodEnter() {
            super.onMethodEnter()
            println "LifeCycleTransform: -------onMethodEnter------"
            proxyAppLifecycleClassList.forEach({ proxyClassName ->
                println "LifeCycleTransform: 开始注入代码：${proxyClassName}"
                def fullName = proxyClassName.replace("/", ".").substring(0, proxyClassName.length() - 6)
                println "LifeCycleTransform: full classname = ${fullName}"
                mv.visitLdcInsn(fullName)
                mv.visitMethodInsn(INVOKESTATIC, "com/example/agp/app/AppLifecycleManager", "registerAppLifecycle", "(Ljava/lang/String;)V", false)
            })
        }

        @Override
        protected void onMethodExit(int opcode) {
            super.onMethodExit(opcode)
            println "LifeCycleTransform: -------onMethodExit------"
        }
    }
}