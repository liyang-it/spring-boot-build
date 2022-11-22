package com.mh.jishi.job;

import com.mh.jishi.util.DbUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 数据库定时备份任务
 * 在backup文件夹中备份最近七日的数据库文件, 备份文件夹 与当前程序同一目录
 */
@Component
@Slf4j
public class DbJob {

    private final Environment environment;

    public DbJob(Environment environment) {
        this.environment = environment;
    }

    /**
     * 定时时间是每天凌晨5点。
     * <h3>cron 标识符</h3>
     * 从左到右用空格隔开分别是：秒 分 时 日 月 周 年(可省略)<br/>
     * <p>
     * 第一个位置：Seconds 秒：区间 0-59 秒，代表一分钟内的秒数。<br/>
     * <p>
     * 第二个位置：Minutes 分：区间 0-59 分，代表一小时内的分钟数。<br/>
     * <p>
     * 第三个位置：Hours 时：区间 0-23 时，代表一天中的小时数。<br/>
     * <p>
     * 第四个位置：Day of month 日：区间 1-31 (?根据每月有多少天来)，代表一月中的多少号。<br/>
     * <p>
     * 第五个位置：Month 月：区间 1-12 ，代表一年中的月份。<br/>
     * <p>
     * 第六个位置：Day of week 周：区间 1-7或者英文星期的缩写，代表星期几。<br/>
     */
//    @Scheduled(cron = "0 0 5 * * ?")
    public void backup() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        log.info("*******************时间：【{}】, 系统开启定时任务数据库备份*******************", now);

        // 数据库配置信息
        String user = environment.getProperty("spring.datasource.druid.username");

        String password = environment.getProperty("spring.datasource.druid.password");

        String url = environment.getProperty("spring.datasource.druid.url");

        // 第三个 ：号下标
        int subStrIndex = url.indexOf(":", url.indexOf(":", url.indexOf(":") + 1) + 1);

        // IP
        String host = url.substring(url.indexOf("//") + 2, subStrIndex);

        // 端口
        String subStr2 = url.substring(subStrIndex);
        String port = subStr2.substring(1, subStr2.indexOf("/"));

        // 数据库名
        String dataBaseName = subStr2.substring(subStr2.indexOf("/") + 1, subStr2.indexOf("?"));

        // 环境
        String os = System.getProperties().getProperty("os.name");

        log.info("备份环境信息：【{}】, 用户名：【{}】,密码：【{}】, 地址：【{}】, 端口：【{}】,数据库：【{}】", os, url, password, host, port, dataBaseName);


        LocalDate localDate = LocalDate.now();

        String fileName = localDate + ".sql";

        File file = new File("backup", fileName);

        file.getParentFile().mkdirs();

        file.createNewFile();

        // 备份今天数据库
        DbUtil.backup(host, port, user, password, dataBaseName, file);


        // 删除七天前数据库备份文件 LocalDate
        LocalDate before = localDate.minusDays(7);
        String fileBeforeName = before + ".sql";
        File fileBefore = new File("backup", fileBeforeName);
        if (fileBefore.exists()) {
            fileBefore.delete();
        }
        log.info("*******************时间：【{}】, 系统结束定时任务数据库备份*******************", now);
    }

    /**
     * 每过 3 小时 备份一次
     */
//    @Scheduled(fixedRate = (60000 * 60) * 3)
    public void backup2() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        log.info("*******************时间：【{}】, 系统开启定时任务数据库备份*******************", now);

        // 数据库配置信息
        String user = environment.getProperty("spring.datasource.druid.username");

        String password = environment.getProperty("spring.datasource.druid.password");

        String url = environment.getProperty("spring.datasource.druid.url");

        // 第三个 ：号下标
        int subStrIndex = url.indexOf(":", url.indexOf(":", url.indexOf(":") + 1) + 1);

        // IP
        String host = url.substring(url.indexOf("//") + 2, subStrIndex);

        // 端口
        String subStr2 = url.substring(subStrIndex);
        String port = subStr2.substring(1, subStr2.indexOf("/"));

        // 数据库名
        String dataBaseName = subStr2.substring(subStr2.indexOf("/") + 1, subStr2.indexOf("?"));

        // 环境
        String os = System.getProperties().getProperty("os.name");

        log.info("备份环境信息：【{}】, 用户名：【{}】,密码：【{}】, 地址：【{}】, 端口：【{}】,数据库：【{}】", os, user, password, host, port, dataBaseName);

        log.info("数据库连接信息：【{}】", url);

        LocalDate localDate = LocalDate.now();

        String fileName = localDate + "/" + now.getHour() + ".sql";


        File file = new File("backup", fileName);

        // 创建当天日期文件夹
        file.getParentFile().mkdirs();

        // 创建备份文件
        file.createNewFile();


        // 备份数据库
        DbUtil.backup(host, port, user, password, dataBaseName, file);


        // 删除七天前数据库备份文件目录 LocalDate
        LocalDate before = localDate.minusDays(1);
        String fileBeforeName = before.toString();
        File fileBefore = new File("backup", fileBeforeName);
        if (fileBefore.exists()) {
            fileBefore.delete();
        }
        log.info("*******************时间：【{}】, 系统结束定时任务数据库备份*******************", now);
    }

}
