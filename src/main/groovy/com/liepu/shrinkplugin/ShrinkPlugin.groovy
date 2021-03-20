package com.liepu.shrinkplugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.api.BaseVariantImpl
import com.android.build.gradle.tasks.MergeResources
import com.ichoice.shrinkplugin.CompressUtil
import com.ichoice.shrinkplugin.FileUtil
import com.ichoice.shrinkplugin.ImageUtil
import com.ichoice.shrinkplugin.LogUtil
import com.ichoice.shrinkplugin.MD5Util
import com.ichoice.shrinkplugin.ShrinkConfig
import com.ichoice.shrinkplugin.Tools
import com.ichoice.shrinkplugin.WebpUtil
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

public class ShrinkPlugin implements Plugin<Project> {
    def newSize = 0 // 压缩或转webp后的图片总尺寸
    private List<File> resFiles = new ArrayList<>() // 所有资源文件
    private List<File> resImages = new ArrayList<>() // 所有图片文件
    private List<File> resLargeImages = new ArrayList<>() // 所有大图片文件
    def isAssemble = false
    def isDebug = false

    @Override
    void apply(Project project) {

        boolean hasAppPlugin = project.plugins.hasPlugin("com.android.application")
        DomainObjectSet<ApplicationVariant> variants
        if (hasAppPlugin) {
            variants = (project.property("android") as AppExtension).applicationVariants
            AppExtension extension = project.property("android")
            variants = extension.applicationVariants
        } else {
            LibraryExtension extension = project.property("android")
            variants = extension.libraryVariants
        }
        project.extensions.create('wmShrink', ShrinkConfig)

        project.gradle.taskGraph.whenReady {
            List<Task> taskList = it.allTasks
            for (Task t: taskList) {
                String taskName = t.name
                if (taskName.contains('assemble') || taskName.contains('shrink')) {
                    if (taskName.toLowerCase().contains('debug')) {
                        isDebug = true
                    }
                    isAssemble = true
                    break
                }
            }
        }

        // 设置根目录
        FileUtil.rootDir = project.rootDir
        project.afterEvaluate {
            variants.all {
                BaseVariantImpl variant = it
                println(">>>${variant.name}")
                MergeResources resources = variant.mergeResourcesProvider.get()
                Task shrinkTask = project.task("shrink${variant.name}")
                shrinkTask.doLast {
                    ShrinkConfig shrinkConfig = project.extensions.wmShrink
                    if (!shrinkConfig.enable) {
                        LogUtil.log(">>>如需要检查优化图片资源，请打开开关:enable = true")
                        return
                    }
                    if (!isAssemble) {
                        LogUtil.log(">>>Don't contain assemble task, shrink passed")
                        return
                    }
                    FileUtil.setToolDir(shrinkConfig.toolsDir)
                    Set<File> files = variant.allRawAndroidResources.files
                    List<File> fileList = files.toList()
                    listAllFiles(fileList)
                    // 解析所有资源文件，获取图片文件,过滤条件有两个：1. 非png、jpg、jpeg、.9.png文件 2. 白名单中的文件
                    collectAllImage(shrinkConfig)
                    // 1. 检查大图片并输出
                    checkLargeImage(shrinkConfig)
                    // 2. 检查重复图片文件并输出
                    checkRepeatImage(shrinkConfig)
                    // 3. 压缩 or 转webp
                    handleImage(shrinkConfig)

                }

                // Linux平台运行需要修改文件权限
                def chomdTask = project.task("chomd${variant.name}")
                chomdTask.doLast {
                    if (Tools.isLinux()) {
                        Tools.chmod()
                    }
                }
                project.tasks.findByName(chomdTask.name).dependsOn(resources.taskDependencies.getDependencies(resources))
                project.tasks.findByName(shrinkTask.name).dependsOn(project.tasks.findByName(chomdTask.name))
                resources.dependsOn(project.tasks.findByName(shrinkTask.name))
            }
        }
    }

    /**
     * 查找所有图片资源
     */
    private void collectAllImage(ShrinkConfig shrinkConfig) {
        def allSize = 0
        for (File f: resFiles) {
            if (!ImageUtil.isImage(f) || shrinkConfig.whiteList.contains(f.getName())) {
                continue
            }
            resImages.add(f)
            allSize += f.length()
        }
        println(">>>原图片文件总大小：${allSize/1024}")
    }

    /**
     * 检查大图
     */
    private void checkLargeImage(ShrinkConfig shrinkConfig) {
        if (shrinkConfig.isCheckSize || shrinkConfig.isCheckPixels) {
            resImages.each {
                f ->
                    // 检查是否是大图片：检查标准有两个 1. 图片体积 2. 图片宽高
                    boolean isLargeImage = (shrinkConfig.isCheckSize && (ImageUtil.isBigSizeImage(f, shrinkConfig.largeSize))) ||
                            (shrinkConfig.isCheckPixels && (ImageUtil.isBigPixelImage(f, shrinkConfig.largeWidth, shrinkConfig.largeHeight)))
                    if (isLargeImage) {
                        resLargeImages.add(f)
                    }
            }
            // 输出大图文件

        }
    }

    /**
     * 检查重复图片
     */
    private void checkRepeatImage (ShrinkConfig shrinkConfig) {
        if (shrinkConfig.isCheckRepeat) {
            HashMap<String, List<File>> fileMD5Map = new HashMap<>()
            resImages.each {
                // 检查是否有重复图片
                def md5Value = MD5Util.getFileMD5String(it)
                if (fileMD5Map.containsKey(md5Value)) {
                    // 有重复的文件
                    fileMD5Map.get(md5Value).add(it)
                } else {
                    fileMD5Map.put(md5Value, [it])
                }
            }
            // 输出重复文件
            println(">>>输出重复文件：")
            fileMD5Map.each {
                key, value ->
                    if (value.size() > 1) {
                        println("-------------------------$key----------------------------")
                        value.each {
                            file ->
                                println(">>>${file.name}:${file.length()/1024}")
                        }
                    }
            }
        }
    }

    /**
     * 压缩图片或者转webp
     */
    private void handleImage(ShrinkConfig shrinkConfig) {
        if (shrinkConfig.isShrink){
            resImages.each {
                // 压缩图片 或者 转webp
                if (shrinkConfig.optimizeType == ShrinkConfig.OPTIMIZE_WEBP_CONVERT) {
                    WebpUtil.securityFormatWebp(it, shrinkConfig)
                } else if (shrinkConfig.optimizeType == ShrinkConfig.OPTIMIZE_COMPRESS_PICTURE) {
                    CompressUtil.compressImg(it)
                }
                // 统计图片转换或压缩后的体积
                countNewSize(it.path)
            }
            println(">>>压缩转换后总大小：${newSize/1024}")
        }
    }

    /**
     * 查找所有资源文件
     * @param files
     */
    private void listAllFiles(List<File> files) {
        for (File file: files) {
            if (file.isDirectory()) {
                listAllFiles(file.listFiles().toList())
            } else {
                resFiles.add(file)
            }
        }
    }

    /**
     * 计算压缩后图片资源大小
     * @param path
     */
    private void countNewSize(String path) {
        if (new File(path).exists()) {
            newSize += new File(path).length()
        } else {
            //转成了webp
            def indexOfDot = path.lastIndexOf(".")
            def webpPath = path.substring(0, indexOfDot) + ".webp"
            if (new File(webpPath).exists()) {
                newSize += new File(webpPath).length()
            } else {
                LogUtil.log("countNewSize: optimizeImage have some Exception!!!")
            }
        }
    }
}