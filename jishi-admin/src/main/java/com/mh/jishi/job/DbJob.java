package com.mh.jishi.job;

import com.mh.jishi.util.DbUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 数据库定时备份任务
 * 在backup文件夹中备份最近七日的数据库文件
 */
@Component
public class DbJob {
    private final Logger logger = LoggerFactory.getLogger(DbJob.class);


    private final Environment environment;

    public DbJob(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        DbJob job = new DbJob(null);
        String user = "root";
        String password = "123456";
        String db = "pms";
        job.toBackupMysql(user, password, db);
    }

    /*
     * 定时时间是每天凌晨5点。
     */
    @Scheduled(cron = "0 0 5 * * ?")
    public void backup() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        logger.info("系统开启定时任务数据库备份，时间：【{}】", now);
        String user = environment.getProperty("spring.datasource.druid.username");
        String password = environment.getProperty("spring.datasource.druid.password");
        String url = environment.getProperty("spring.datasource.druid.url");
        int index1 = url.indexOf("3306/");
        int index2 = url.indexOf("?");
        String db = url.substring(index1 + 5, index2);

        LocalDate localDate = LocalDate.now();
        String fileName = localDate + ".sql";
        File file = new File("backup", fileName);
        file.getParentFile().mkdirs();
        file.createNewFile();
        // 备份今天数据库
        DbUtil.backup(file, user, password, db);
        // 删除七天前数据库备份文件
        LocalDate before = localDate.minusDays(7);
        String fileBeforeName = before + ".sql";
        File fileBefore = new File("backup", fileBeforeName);
        if (fileBefore.exists()) {
            fileBefore.delete();
        }
        logger.info("系统结束定时任务数据库备份, 完成时间：【{}】", now);
    }

    /**
     * mysql备份
     *
     * @param user     数据库用户名
     * @param password 数据库密码
     * @param db       数据库名称
     */
    public void toBackupMysql(String user, String password, String db) {
        LocalDateTime now = LocalDateTime.now();
        logger.info("系统开始备份MysQL数据库，时间：【{}】", now);

        try {
            // TODO 如果异常 请打开cmd或者命令工具 输入 mysqldump 测试，是否正常
            LocalDate localDate = LocalDate.now();
            String fileName = localDate + ".sql";
            File file = new File("backup", fileName);
            file.getParentFile().mkdirs();
            file.createNewFile();
            // 备份今天数据库
            DbUtil.backup(file, user, password, db);
            // 删除七天前数据库备份文件
            LocalDate before = localDate.minusDays(7);
            String fileBeforeName = before + ".sql";
            File fileBefore = new File("backup", fileBeforeName);
            if (fileBefore.exists()) {
                fileBefore.delete();
            }
        } catch (IOException e) {
            logger.error("系统备份MySQl数据库异常, 时间：【{}】,原因: ", now, e);
        }
        logger.info("系统结束备份MySQL数据库,结束时间：【{}】", now);
    }


}
