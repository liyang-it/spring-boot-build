package com.mh.jishi.storage.config;

import com.mh.jishi.constants.QiNiuConstants;
import com.mh.jishi.service.TSystemService;
import com.mh.jishi.storage.LocalStorage;
import com.mh.jishi.storage.QiniuStorage;
import com.mh.jishi.storage.StorageService;
import com.mh.jishi.system.SystemConfig;
import com.mh.jishi.util.CharUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @Author Lizr
 * @Description 使用数据库 初始化对象存储
 * @CreateTime 2021-12-29 下午 2:53
 * 数据库初始化和 yml配置文件 初始化 只能选一个
 **/
@Configuration
public class InitStorage {
    private final static Logger logger = LoggerFactory.getLogger(InitStorage.class);
    @Autowired
    private TSystemService systemService;
    @Bean
    public StorageService storageService() {
        StorageService storageService = new StorageService();
        String active = systemService.getValue(SystemConfig.StorageActive);
        if(StringUtils.isBlank(active)){
            active = "local";
            logger.info("数据库未配置对象存储模式，使用本地对象存储");
        }
        storageService.setActive(active);
        if (active.equals("local")) {
            storageService.setStorage(localStorage());
        } else if (active.equals("qiniu")) {
            storageService.setStorage(qiniuStorage());
        }
        logger.info("当前对象存储使用的是【{}】", active);
        return storageService;
    }
    /**
     * 初始化 本地对象存储
     * @return LocalStorage
     */
    public LocalStorage localStorage(){
        LocalStorage localStorage = new LocalStorage();
        // 测试
        try {

            localStorage.setStoragePath(systemService.getValue(SystemConfig.StorageLocalStoragePath));
            localStorage.setAddress(systemService.getValue(SystemConfig.StoragelocalAddress));
            String filePath = localStorage.getStoragePath() + "/test-qiniu.txt";
            String fileName = System.currentTimeMillis() + CharUtil.getRandomString(3);
            File file = new File(filePath);
            if(!file.exists()){
                file.createNewFile();
            }
            InputStream inputStream = new FileInputStream(file);
            localStorage.store(inputStream, 0, ".txt", fileName);
            logger.info("本地存储测试上传成功");
        }catch (Exception e){
            logger.info("本地存储上传失败，请检查本地存储配置是否正确");
            logger.error("具体原因：", e);
        }
        return localStorage;
    }


    public QiniuStorage qiniuStorage(){
        QiniuStorage QiNiu = new QiniuStorage();

        // 测试七牛云是否初始化正常
        try {
            QiNiu.setBucketName(systemService.getValue(QiNiuConstants.Backetname));
            QiNiu.setAccessKey(systemService.getValue(QiNiuConstants.AccessKey));
            QiNiu.setSecretKey(systemService.getValue(QiNiuConstants.Secretkey));
            QiNiu.setEndpoint(systemService.getValue(QiNiuConstants.Domain));
            logger.info("初始化七牛云配置: 【{}】", QiNiu.toString());
            String filePath = System.getProperty("user.dir") + "/test-qiniu.txt";
            String fileName = System.currentTimeMillis() + CharUtil.getRandomString(3);
            File file = new File(filePath);
            if(!file.exists()){
                file.createNewFile();
            }
            InputStream inputStream = new FileInputStream(file);
            QiNiu.store(inputStream, 0, ".txt", fileName);
            logger.info("七牛云测试上传成功");
        }catch (Exception e){
            logger.info("七牛云测试上传失败，请检查七牛云配置是否正确");
            logger.error("具体原因：", e);
        }
        return QiNiu;
    }
}
