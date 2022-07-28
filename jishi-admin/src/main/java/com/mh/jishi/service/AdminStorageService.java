package com.mh.jishi.service;

import com.mh.jishi.config.ServiceException;
import com.mh.jishi.constants.RedisKeyPrefix;
import com.mh.jishi.entity.TStorage;
import com.mh.jishi.storage.LocalStorage;
import com.mh.jishi.storage.StorageService;
import com.mh.jishi.system.SystemConfig;
import com.mh.jishi.util.RedisUtil;
import com.mh.jishi.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.LinkedList;

/**
 * <h2>对象存储服务层</h2>
 * <p>
 *
 * </p>
 *
 * @author Evan <1922802352@qq.com>
 * @since 2022年06月14日 10:12
 */
@Service
@Slf4j
public class AdminStorageService {
    private final StorageService service;
    private final TStorageService storageService;
    private final TSystemService systemService;
    private final RedisUtil redisUtil;

    public AdminStorageService(StorageService service, TStorageService storageService, TSystemService systemService, RedisUtil redisUtil) {
        this.service = service;
        this.storageService = storageService;
        this.systemService = systemService;
        this.redisUtil = redisUtil;
    }

    /**
     * 刷新本地对象存储配置
     *
     * @return
     */
    public ResponseUtil reLoadLocalConfig() {
        redisUtil.del(RedisKeyPrefix.SystemConfig + SystemConfig.StorageLocalStoragePath);
        redisUtil.del(RedisKeyPrefix.SystemConfig + SystemConfig.StorageLocalStoragePath);

        LocalStorage localStorage = new LocalStorage();
        localStorage.setStoragePath(systemService.getValue(SystemConfig.StorageLocalStoragePath));
        localStorage.setAddress(systemService.getValue(SystemConfig.StoragelocalAddress));
        service.setStorage(localStorage);
        return ResponseUtil.ok();
    }

    /**
     * 上传文件
     *
     * @param files MultipartFile 文件列表
     * @return 上传成功文件信息
     */
    public ResponseUtil uploadLocalFiles(MultipartFile[] files) throws IOException {
        Assert.notEmpty(files, "文件列表不能为空");
        LinkedList<TStorage> tStorages = new LinkedList<>();
        for (MultipartFile file : files) {
            InputStream stream = file.getInputStream();
            long size = file.getSize();
            if(size == 0){
                continue;
            }
            String contentType = file.getContentType();
            String fileName = file.getOriginalFilename();
            TStorage save = service.store(stream, size, contentType, fileName);
            tStorages.add(save);
        }
        return ResponseUtil.ok(tStorages);
    }

    /**
     * 输出本地存储文件
     *
     * @param key 文件唯一索引
     */
    public void outLocalFile(String key, HttpServletRequest request, HttpServletResponse response) {
        TStorage storage = storageService.getBaseMapper().findByKey(key);
        Assert.notNull(storage, "文件不存在");

        try {

            String path = null;

            LocalStorage localStorage = (LocalStorage) service.getStorage();
            path = localStorage.getStoragePath();


            File file = new File(path + storage.getFileKey());
            /**
             * 中文乱码解决
             */
            String type = request.getHeader("User-Agent").toLowerCase();
            String fileName = storage.getName();
            String charsetCode = "UTF-8";
            if (type.indexOf("firefox") > 0 || type.indexOf("chrome") > 0) {
                /**
                 * 谷歌或火狐
                 */
                fileName = new String(fileName.getBytes(charsetCode), "iso8859-1");
            } else {
                /**
                 * IE
                 */
                fileName = URLEncoder.encode(fileName, charsetCode);
            }
            // 设置响应的头部信息
            response.setHeader("content-disposition", "attachment;filename=" + fileName);
            // 设置响应内容的类型
            response.setContentType(storage.getType() + "; charset=" + charsetCode);
            response.setContentLength((int) file.length());
            // 设置响应内容的长度
            response.setHeader("file-length", storage.getSize().toString());
            //文件管道
            WritableByteChannel writableByteChannel = Channels.newChannel(response.getOutputStream());
            FileChannel fileChannel = new FileInputStream(file.getAbsolutePath()).getChannel();
            fileChannel.transferTo(0, fileChannel.size(), writableByteChannel);
        } catch (Exception e) {
            log.error("输出本地文件异常, 文件唯一索引名: 【{}】, 异常原因: ", key, e);
            throw new ServiceException("文件不存在或者已删除");
        }

    }

}
