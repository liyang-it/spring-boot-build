package com.mh.jishi.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;


public class DbUtil {
    /**
     * 导出sql文件
     *
     * @param host     ip地址
     * @param port     端口
     * @param userName 用户名
     * @param password 密码
     * @param dbName   数据库名
     * @param file     文件对象
     */
    public static void backup(String host, String port, String userName, String password, String dbName, File file) {

        String cmd = "mysqldump --single-transaction" + " -h" + host + " -P" + port + " -u" + userName + " -p" + password + " " + dbName + " > " + file.getPath();
        String os = System.getProperties().getProperty("os.name");
        if(os.contains("Windows")){
            // Windows 需要加上 cmd /c
            cmd = "cmd /c " + cmd;
        }
        System.out.printf("cmd命令为：%s%n", cmd);
        try {
            Process process = Runtime.getRuntime().exec(cmd);

            if (process.waitFor() == 0) {
                System.out.printf(" 数据库：%s 备份成功!%n", dbName);
            } else {
                System.out.printf(" 数据库：%s 备份失败!%n", dbName);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导入sql文件
     *
     * @param host         ip地址
     * @param port         端口
     * @param userName     用户名
     * @param password     密码
     * @param databaseName 数据库名
     * @param file         文件对象
     */
    public static void reduction(String host, String port, String userName, String password, String databaseName, File file) throws Exception {
        if (!file.exists()) {
            System.out.printf("文件：%s 不存在，请检查%n", file.getPath());
            return;
        }
        String filePath = file.getPath();
        String cmd = "mysql -h" + host + " -P" + port + " -u" + userName + " -p" + password + " " + databaseName + " < " + filePath;
        String os = System.getProperties().getProperty("os.name");
        if(os.contains("Windows")){
            // Windows 需要加上 cmd /c
            cmd = "cmd /c " + cmd;
        }
        System.out.printf("数据库还原命令：%s%n", cmd);


        //拼接cmd命令
        Process exec = Runtime.getRuntime().exec(cmd);
        if (exec.waitFor() == 0) {
            System.out.printf("数据库：%s 还原成功，还原的文件为：%s%n", databaseName, filePath);
        } else {
            System.out.println(databaseName + "数据库还原失败");
            System.out.printf("数据库：%s 还原失败", databaseName);
        }
    }



    /**
     * 导入sql文件
     *
     * @param file     文件对象
     * @param user     用户
     * @param password 密码
     * @param db       数据库
     */
    public static void load(File file, String user, String password, String db) {
        try {
            Runtime rt = Runtime.getRuntime();
            String command = "mysql -u" + user + " -p" + password + " --default-character-set=utf8 " + db;
            Process child = rt.exec(command);
            OutputStream outputStream = child.getOutputStream();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8));
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                outputStreamWriter.write(str + "\r\n");
            }
            outputStreamWriter.flush();
            outputStream.close();
            bufferedReader.close();
            outputStreamWriter.close();
            System.out.println("数据库导入完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


/*
    public static void main(String[] args) throws Exception {
        File file = new File("C:\\Users\\hansonh\\Desktop\\jar", "backup.sql");


        System.out.println("系统环境：" + System.getProperties().getProperty("os.name"));

        String subStr = "jdbc:mysql://127.0.0.1:3306/pms?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&verifyServerCertificate=false&useSSL=false&rewriteBatchedStatements=true&zeroDateTimeBehavior=convertToNull";


        // 第三个 ：号下标
        int subStrIndex = subStr.indexOf(":", subStr.indexOf(":", subStr.indexOf(":") + 1) + 1);
        // IP
        String host = subStr.substring(subStr.indexOf("//") + 2, subStrIndex);
        System.out.println("IP：" + host);

        // 端口
        String subStr2 = subStr.substring(subStrIndex);
        String port = subStr2.substring(1, subStr2.indexOf("/"));
        System.out.println("端口：" + port);

        // 数据库名
        String dataBaseName = subStr2.substring(subStr2.indexOf("/") + 1, subStr2.indexOf("?"));

        System.out.println("数据库名：" + dataBaseName);

        // 备份数据库
        DbUtil.backup( "127.0.0.1","3306", "dev1", "dev1", "pms", file);
        // 恢复数据库
//        DbUtil.reduction( "127.0.0.1","3306", "root", "123456", "pms", file);
    }

 */
}
