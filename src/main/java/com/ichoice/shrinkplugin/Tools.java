package com.ichoice.shrinkplugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Tools {
    public static void cmd(String cmd, String params) {
        String cmdStr = null;
        if (isCmdExist(cmd)) {
            cmdStr = cmd + " " + params;
        } else {
            if (isMac()) {
                cmdStr = FileUtil.getToolsDirPath() + "mac/" + cmd + " " + params;
            } else if (isLinux()) {
                cmdStr = FileUtil.getToolsDirPath() + "linux/" + cmd + " " + params;
            } else if (isWindows()) {
                cmdStr = FileUtil.getToolsDirPath() + "windows/" + cmd + " " + params;

            }
        }
        if (cmdStr == null || cmdStr.trim().length() < 1) {
            LogUtil.log("tool Not support this system");
            return;
        }
        outputMessage(cmdStr);
    }

    public static boolean isLinux() {
        String system = System.getProperty("os.name");
        return system.startsWith("Linux");
    }

    public static boolean isMac() {
        String system = System.getProperty("os.name");
        return system.startsWith("Mac OS");
    }

    public static boolean isWindows() {
        String system = System.getProperty("os.name");
        return system.toLowerCase().contains("win");
    }

    // 755 代表用户对该文件拥有读，写，执行的权限，同组其他人员拥有执行和读的权限，没有写的权限，其他
    public static void chmod() {
        outputMessage("chmod 755 -R " + FileUtil.getRootDirPath());
    }

    private static void outputMessage(String cmd) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
//            OutputProcessor error = new OutputProcessor(process.getErrorStream());
//            OutputProcessor input = new OutputProcessor(process.getInputStream());
//            error.start();
//            input.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
            LogUtil.log(cmd+"@@@："+e.getMessage());
        }
    }

    private static boolean isCmdExist(String cmd) {
        String result;
        if (isMac() || isLinux()) {
            result = executeCmd("which $cmd");
        } else {
            result = executeCmd("where $cmd");
        }
        return result != null && !result.isEmpty();
    }

    private static String executeCmd(String cmd) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
//            OutputProcessor error = new OutputProcessor(process.getErrorStream());
//            OutputProcessor input = new OutputProcessor(process.getInputStream());
//            error.start();
//            input.start();
            process.waitFor();
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return bufferReader.readLine();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    static class OutputProcessor extends Thread {
        private static final Logger LOGGER = LoggerFactory.getLogger(OutputProcessor.class);
        private final InputStream inputStream;
        public OutputProcessor(InputStream inputStream) {
            this.inputStream = inputStream;
        }
        @Override
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    LOGGER.info("{}", line);
                }
            } catch (Exception e) {
                LOGGER.error("OutputProcessor>>>", e.getMessage(), e);
            }
        }
    }
}
