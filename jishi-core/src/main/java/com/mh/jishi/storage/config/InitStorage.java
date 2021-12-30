package com.mh.jishi.storage.config;

import com.mh.jishi.constants.QiNiuConstants;
import com.mh.jishi.service.TSystemService;
import com.mh.jishi.storage.QiniuStorage;
import com.mh.jishi.util.CharUtil;
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

    /**
     * 初始化七牛云
     * @return
     */
    @Bean
    public QiniuStorage qiniuStorage(){
        QiniuStorage QiNiu = new QiniuStorage();
        QiNiu.setBucketName(systemService.getValue(QiNiuConstants.Backetname));
        QiNiu.setAccessKey(systemService.getValue(QiNiuConstants.AccessKey));
        QiNiu.setSecretKey(systemService.getValue(QiNiuConstants.Secretkey));
        QiNiu.setEndpoint(systemService.getValue(QiNiuConstants.Domain));
        logger.info("初始化七牛云配置: 【{}】", QiNiu.toString());
        // 测试七牛云是否初始化正常
        try {
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
