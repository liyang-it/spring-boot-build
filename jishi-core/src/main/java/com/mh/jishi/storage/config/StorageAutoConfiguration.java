package com.mh.jishi.storage.config;

import com.mh.jishi.storage.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 初始化 对线存储bean
 * 这里的配置都在yml文件里面，可以改为数据库加载配置
 */

/**
 * 使用 initStorage 取代
 * @Configuration
 * @EnableConfigurationProperties(StorageProperties.class)
 */
@Deprecated
public class StorageAutoConfiguration {

    private final StorageProperties properties;

    public StorageAutoConfiguration(StorageProperties properties) {
        this.properties = properties;
    }

    @Bean
    public StorageService storageService() {
        StorageService storageService = new StorageService();
        String active = this.properties.getActive();
        storageService.setActive(active);
        if (active.equals("local")) {
            storageService.setStorage(localStorage());
        } else if (active.equals("aliyun")) {
            storageService.setStorage(aliyunStorage());
        } else if (active.equals("tencent")) {
            storageService.setStorage(tencentStorage());
        } else if (active.equals("qiniu")) {
            storageService.setStorage(qiniuStorage());
        } else {
            throw new RuntimeException("当前存储模式 " + active + " 不支持");
        }

        return storageService;
    }

    @Bean
    public LocalStorage localStorage() {
        LocalStorage localStorage = new LocalStorage();
        StorageProperties.Local local = this.properties.getLocal();
        localStorage.setAddress(local.getAddress());
        localStorage.setStoragePath(local.getStoragePath());
        return localStorage;
    }

    @Bean
    public AliyunStorage aliyunStorage() {
        AliyunStorage aliyunStorage = new AliyunStorage();
        StorageProperties.Aliyun aliyun = this.properties.getAliyun();
        aliyunStorage.setAccessKeyId(aliyun.getAccessKeyId());
        aliyunStorage.setAccessKeySecret(aliyun.getAccessKeySecret());
        aliyunStorage.setBucketName(aliyun.getBucketName());
        aliyunStorage.setEndpoint(aliyun.getEndpoint());
        return aliyunStorage;
    }

    @Bean
    public TencentStorage tencentStorage() {
        TencentStorage tencentStorage = new TencentStorage();
        StorageProperties.Tencent tencent = this.properties.getTencent();
        tencentStorage.setSecretId(tencent.getSecretId());
        tencentStorage.setSecretKey(tencent.getSecretKey());
        tencentStorage.setBucketName(tencent.getBucketName());
        tencentStorage.setRegion(tencent.getRegion());
        tencentStorage.setDoman(tencent.getDoman());
        tencentStorage.setAppId(tencent.getAppId());
        return tencentStorage;
    }

    @Bean
    public QiniuStorage qiniuStorage() {
        QiniuStorage qiniuStorage = new QiniuStorage();
        StorageProperties.Qiniu qiniu = this.properties.getQiniu();
        qiniuStorage.setAccessKey(qiniu.getAccessKey());
        qiniuStorage.setSecretKey(qiniu.getSecretKey());
        qiniuStorage.setBucketName(qiniu.getBucketName());
        qiniuStorage.setEndpoint(qiniu.getEndpoint());
        return qiniuStorage;
    }
}
