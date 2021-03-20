package com.ichoice.shrinkplugin;

import java.io.File;

public class CompressUtil {
    private final static String TAG = "Compress";
    public static void compressImg(File imgFile) {
        if (imgFile == null || !ImageUtil.isImage(imgFile)) {
            return;
        }
        long oldSize = imgFile.length();
        long newSize = 0L;
        if (ImageUtil.isJPG(imgFile)) {
            String tempFilePath = imgFile.getPath().substring(0, imgFile.getPath().lastIndexOf(".")) + "_temp" +
                    imgFile.getPath().substring(imgFile.getPath().lastIndexOf("."));
            // 参考文档：https://github.com/google/guetzli
            Tools.cmd("guetzli", imgFile.getPath() + " " + tempFilePath);
            File tempFile = new File(tempFilePath);
            newSize = tempFile.length();
            LogUtil.log("newSize = "+newSize);
            if (newSize < oldSize) {
                String imgFileName = imgFile.getPath();
                if (imgFile.exists()) {
                    imgFile.delete();
                }
                tempFile.renameTo(new File(imgFileName));
            } else {
                if (tempFile.exists()) {
                    tempFile.delete();
                }
            }

        } else {
            // 参考文档：https://pngquant.org/
            Tools.cmd("pngquant", "--skip-if-larger --speed 1 --nofs --strip --force --output " + imgFile.getPath() + " -- " + imgFile.getPath());
            newSize = new File(imgFile.getPath()).length();
        }

        LogUtil.log(TAG, imgFile.getPath(), String.valueOf(oldSize), String.valueOf(newSize));
    }

}
