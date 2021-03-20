package com.ichoice.shrinkplugin;

public class LogUtil {

    public static void log(String stage, String filePath, String oldInfo, String newInfo) {
        System.out.println("["+stage+"]["+filePath+"][oldInfo: "+oldInfo+"][newInfo: "+newInfo+"]");
    }

    public static void log(String stage, String info, String result) {
        System.out.println("["+stage+"][Info: "+info+"][Result: "+result+"]");
    }

    public static void log(String str) {
        System.out.println(str);
    }

    public static void log(Exception exception) {
        System.out.println(exception);
    }
}
