package com.ichoice.shrinkplugin;

import java.io.File;

public class WebpUtil {
    private static final String TAG = "Webp";

    private static void formatWebp(File imgFile) {
        if (ImageUtil.isImage(imgFile)) {
            File webpFile = new File(imgFile.getPath().substring(0, imgFile.getPath().lastIndexOf("."))+".webp");
            // 参考文档：https://developers.google.com/speed/webp/docs/cwebp
            Tools.cmd("cwebp", imgFile.getPath() + " -o " + webpFile.getPath() + " -m 6 -quiet");
            if (webpFile.length() < imgFile.length()) {
                LogUtil.log(TAG, imgFile.getPath(), String.valueOf(imgFile.length()), String.valueOf(webpFile.length()));
                if (imgFile.exists()) {
                    imgFile.delete();
                }
            } else {
                //如果webp的大的话就抛弃
                if (webpFile.exists()) {
                    webpFile.delete();
                }
                LogUtil.log(TAG+"["+imgFile.getName()+"] do not convert webp because the size become larger!");
            }
        }
    }

    public static void securityFormatWebp(File imgFile, ShrinkConfig config) {
        if (ImageUtil.isImage(imgFile)) {
            if(config.isSupportAlphaWebp) {
                formatWebp(imgFile);
            } else {
                if(imgFile.getName().endsWith(Const.JPG) || imgFile.getName().endsWith(Const.JPEG)) {
                    //jpg
                    formatWebp(imgFile);
                } else if(imgFile.getName().endsWith(Const.PNG) ){
                    //png
                    if(!ImageUtil.isAlphaPNG(imgFile)) {
                        //不包含透明通道
                        formatWebp(imgFile);
                    } else {
                        //包含透明通道的png，进行压缩
                        CompressUtil.compressImg(imgFile);
                    }
                }
            }
        }
    }
}
