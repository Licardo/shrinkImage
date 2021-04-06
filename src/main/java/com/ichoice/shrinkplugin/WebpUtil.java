package com.ichoice.shrinkplugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class WebpUtil {
    private static final String TAG = "Webp";

    private static void formatWebp(File imgFile) {
        if (ImageUtil.isImage(imgFile)) {
            File webpFile = new File(imgFile.getPath().substring(0, imgFile.getPath().lastIndexOf("."))+".webp");
            // 参考文档：https://developers.google.com/speed/webp/docs/cwebp
            Tools.cmd("cwebp", imgFile.getPath() + " -o " + webpFile.getPath() + " -m 6 -quiet");
            if (webpFile.length() <= 0) {
                return;
            }
            printFile("webp.txt", webpFile.getAbsolutePath() + "\n" + imgFile.getAbsolutePath());
            if (webpFile.length() < imgFile.length()) {
//                LogUtil.log(TAG, imgFile.getPath(), String.valueOf(imgFile.length()), String.valueOf(webpFile.length()));
                if (imgFile.exists()) {
                    printFile("delete.txt", imgFile.getAbsolutePath());
                    imgFile.delete();
                }
            } else {
                //如果webp的大的话就抛弃
                if (webpFile.exists()) {
                    printFile("delete.txt", webpFile.getAbsolutePath());
                    webpFile.delete();
                }
//                LogUtil.log(TAG+"["+imgFile.getName()+"] do not convert webp because the size become larger!");
            }
        }
    }

    public static void securityFormatWebp(File imgFile, ShrinkConfig config) {
        if (ImageUtil.isImage(imgFile)) {
//            if(config.isSupportAlphaWebp) {
//                formatWebp(imgFile);
//            } else {
                if(imgFile.getName().endsWith(Const.JPG) || imgFile.getName().endsWith(Const.JPEG)) {
                    //jpg
                    formatWebp(imgFile);
                } else if(imgFile.getName().endsWith(Const.PNG) ){
                    //png
                    int flag = ImageUtil.isAlphaPNG(imgFile);
                    if(flag == 0) {
                        //不包含透明通道
                        formatWebp(imgFile);
                    } else if (flag == 1){
                        //包含透明通道的png，进行压缩
                        CompressUtil.compressImg(imgFile);
                        LogUtil.log(imgFile.getName()+":支持alpha通道");
                    } else {
                        // 解析异常图片不做处理
                        LogUtil.log(imgFile.getName()+":读取alpha通道异常");
                    }
                }
//            }
        }
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
            FileOutputStream fileOutputStream = new FileOutputStream(f, true);
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
            writer.append(line).append("\n");
            writer.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
