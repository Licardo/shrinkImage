package com.ichoice.shrinkplugin;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtil {
    private final static String SIZE_TAG = "SizeCheck";

    public static boolean isImage(File file) {
        return (file.getName().endsWith(Const.JPG) ||
                file.getName().endsWith(Const.PNG) ||
                file.getName().endsWith(Const.JPEG)
        ) && !file.getName().endsWith(Const.DOT_9PNG);
    }

    public static boolean isJPG(File file) {
        return file.getName().endsWith(Const.JPG) || file.getName().endsWith(Const.JPEG);
    }

    public static boolean isAlphaPNG(File filePath) {
        if (filePath.exists()) {
            BufferedImage img = null;
            try {
                img = ImageIO.read(filePath);
                return img.getColorModel().hasAlpha();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public static boolean isBigSizeImage(File imgFile, float largeSize) {
        if (isImage(imgFile)) {
            if (imgFile.length() >= largeSize) {
                LogUtil.log(SIZE_TAG, imgFile.getPath(), String.valueOf(true));
                return true;
            }
        }
        return false;
    }

    public static boolean isBigPixelImage(File imgFile, int largeWidth, int largeHeight) {
        if (isImage(imgFile)) {
            BufferedImage sourceImg = null;
            try {
                sourceImg = ImageIO.read(new FileInputStream(imgFile));
                if (sourceImg.getHeight() > largeHeight || sourceImg.getWidth() > largeWidth) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
