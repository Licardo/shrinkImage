package com.ichoice.shrinkplugin;

import org.jetbrains.annotations.NotNull;

public class ShrinkConfig {
    public static final String OPTIMIZE_WEBP_CONVERT = "ConvertWebp"; //webp化
    public static final String OPTIMIZE_COMPRESS_PICTURE = "Compress"; //压缩图片

    public float largeSize = 1024 * 1024;
    public boolean isCheckSize = true; //是否检查大体积图片
    public String optimizeType = OPTIMIZE_WEBP_CONVERT; //优化方式，webp化、压缩图片
    public boolean enable = true; // 总开关
    public boolean isCheckPixels = true; //是否检查大像素图片
    public int largeWidth = 1000;
    public int largeHeight = 1000;
    public String[] whiteList = new String[]{}; //优化图片白名单
    public String toolsDir = ""; // 压缩工具路径，这里的路径是指对于根目录的相对路径，比如：/shrinkPlugin/imageTools/
    public boolean isSupportAlphaWebp = false; //是否支持webp化透明通道的图片,如果开启，请确保minSDK >= 18,或做了其他兼容措施
    public boolean multiThread = true;
    public boolean isCheckRepeat = true; // 是否检查重复图片
    public boolean isShrink = true; // 是否压缩或转webp

    @NotNull
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("<<<<<<<<<<<<<<ShrinkConfig>>>>>>>>>>>>" + "\n");
        result.append("largeSize :").append(largeSize).append("\n")
                .append("isCheckSize: ").append(isCheckSize).append("\n")
                .append("optimizeType: ").append(optimizeType).append("\n")
                .append("enable: ").append(enable).append("\n")
                .append("isCheckPixels: ").append(isCheckPixels).append("\n")
                .append("largeWidth: ").append(largeWidth).append(", largeHeight: ").append(largeHeight).append("\n")
                .append("toolsDir: ").append(toolsDir).append("\n")
                .append("isSupportAlphaWebp: ").append(isSupportAlphaWebp).append("\n")
                .append("multiThread: ").append(multiThread).append("\n")
                .append("isShrink ").append(isShrink).append("\n")
                .append("whiteList : ").append("\n");
        for(String file : whiteList) {
            result.append("     -> : ").append(file).append("\n");
        }
        result.append("<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>");
        return result.toString();
    }
}
