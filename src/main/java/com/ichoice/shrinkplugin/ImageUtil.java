package com.ichoice.shrinkplugin;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

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

    /**
     *
     * @param filePath
     * @return 0: 不支持alpha通道；1：支持alpha通道；2：异常
     */
    public static int isAlphaPNG(File filePath) {
        BufferedImage img;
        try {
            img = ImageIO.read(filePath);
            return img.getColorModel().hasAlpha() ? 1 : 0;
        } catch (Exception e) {
            printFile("alphaPNGException.txt", filePath.getAbsolutePath());
            return 2;
        }
    }

    public static boolean isBigSizeImage(File imgFile, float largeSize) {
        if (isImage(imgFile) || imgFile.getName().endsWith(Const.WEBP)) {
            if (imgFile.length() >= largeSize) {
                LogUtil.log(SIZE_TAG, imgFile.getPath(), String.valueOf(imgFile.length()));
                return true;
            }
        }
        return false;
    }

    public static boolean isBigPixelImage(File imgFile, int largeWidth, int largeHeight) {
        // ImageIO无法读取webp图片的宽高
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

    /**
     * 输出到文件
     * @param fileName
     * @param line
     */
    private static void printFile(String fileName, String line) {
        File f = new File(FileUtil.getOutputDir()+"/"+fileName);
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
            writer.append(line).append("\n");
            writer.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
