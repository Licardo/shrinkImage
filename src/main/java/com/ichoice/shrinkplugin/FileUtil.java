package com.ichoice.shrinkplugin;

import java.io.File;

public class FileUtil {
    private static  String rootDir;
    private static String toolDir;
    private static final String outputDir = "/wm_image";

    public static void setRootDir(String rootDir) {
        FileUtil.rootDir = rootDir;
    }

    public static void setToolDir(String toolDir) {
        FileUtil.toolDir = toolDir;
    }

    public static String getRootDirPath() {
        return rootDir;
    }

    public static File getToolsDir() {
        return new File(rootDir + toolDir);
    }

    public static String getToolsDirPath() {
        return rootDir + toolDir;
    }

    public static String getOutputDir() {
        return rootDir + outputDir;
    }
}
