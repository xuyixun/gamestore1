package com.ijianjian.shell;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 游戏加壳工具
 * <p>
 *  执行java -cp AppShell.jar com.ijianjian.shell.GameShellToolkit /home/games/upload/11900.apk "Farm Day.apk"
 * /home/games/upload/11900.apk 为apk所在路径，"Farm Day.apk"为实际的apk名称
 *
 * Created by yishion on 2019-08-17.
 */
public class GameShellToolkit {

    private static String executeShellLogFile = "/games/logs/shell.log";
    private static String gameManifestPath = "/games/AndroidManifest.xml";

    public static void main(String[] args) throws Exception {
        String apkName = args[0];
        String apkPath = args[1];
        System.out.println("apkName:" + apkName + ",apkPath:" + apkPath);
        gameShell(apkName, apkPath);
    }

    public static String gameShell(String apkName, String apkPath) throws Exception{

        String shellCommand = "/games/gameShellToolkit.sh \"" + apkName + "\" \"" + apkPath + "\"";
        return executeShell(shellCommand, apkPath);
    }

    /**
     *  执行加壳shell脚本
     * @param shellCommand
     * @return 1表示成功，0表示失败
     * @throws IOException
     */
    public static String executeShell(String shellCommand, String apkPath) throws IOException {
        String success = null;
        StringBuffer stringBuffer = new StringBuffer();
        BufferedReader bufferedReader = null;
        //格式化日期时间，记录日志时使用
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS ");

        Process pid = null;
        try {
            stringBuffer.append(dateFormat.format(new Date())).append("准备执行Shell命令 ").append(shellCommand).append(" \r\n");
            System.out.println(stringBuffer.toString());
            String[] cmd = {"/bin/sh", "-c", shellCommand};
            //执行Shell命令
            pid = Runtime.getRuntime().exec(cmd);
            if (pid != null) {
                stringBuffer.append("进程号：").append(pid.toString()).append("\r\n");
                bufferedReader = new BufferedReader(new InputStreamReader(pid.getInputStream()), 1024);

                String line = null;
                //读取Shell的输出内容，并添加到stringBuffer中
                while (bufferedReader != null && (line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line).append("\r\n");
                    System.out.println(line);
                }
                pid.waitFor();
            } else {
                stringBuffer.append("没有pid\r\n");
            }
            stringBuffer.append(dateFormat.format(new Date())).append("Shell命令执行完毕\r\n");
            System.out.println("Shell命令执行完毕");
        } catch (Exception ioe) {
            ioe.printStackTrace();
            stringBuffer.append("执行Shell命令时发生异常：\r\n").append(ioe.getMessage()).append("\r\n");
        } finally {
            if (bufferedReader != null) {
                OutputStreamWriter outputStreamWriter = null;
                try {
                    bufferedReader.close();
                    //将Shell的执行情况输出到日志文件中
                    OutputStream outputStream = new FileOutputStream(executeShellLogFile);
                    outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                    outputStreamWriter.write(stringBuffer.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    outputStreamWriter.close();
                }
            }

            try {
                ApkInfo apkInfo = new ApkUtil().getApkInfo("/home/gm/gm/app/" + apkPath);
                success = apkInfo.getPackageName() + "___" + apkInfo.getVersionName();
            } catch(Exception e) {
                success = getPackageName(gameManifestPath) + "___1.0";
                e.printStackTrace();
            }

            System.out.println("packageName:" + success);
            if(pid != null) {
                pid.destroy();
            }
        }
        return success;
    }

    public static String getPackageName(String filePath) {
        Document document = getDocument(filePath);
        if(document != null) {
            Element root = document.getRootElement();
            String gmPackage = root.attributeValue("package");
            return gmPackage;
        }
        return null;
    }

    public static Document getDocument(String path) {
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }
}
