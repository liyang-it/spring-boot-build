package com.mh.jishi.acontroller;

import com.mh.jishi.service.AdminStorageService;
import com.mh.jishi.util.ResponseUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <h2>对象存储控制层</h2>
 * <p>
 *
 * </p>
 *
 * @author Evan <1922802352@qq.com>
 * @since 2022年06月14日 10:11
 */
@RestController
@RequestMapping(value = "/admin/storage")
public class AdminStorageController {
    private final AdminStorageService service;
    public AdminStorageController(AdminStorageService service){
        this.service = service;
    }


    /**
     * 刷新本地对象存储配置
     *
     * @return
     */
    @PostMapping("/reLoadLocalConfig")
    public ResponseUtil reLoadLocalConfig() {
        return service.reLoadLocalConfig();
    }

    /**
     * 上传文件,到本地
     *
     * @param files MultipartFile 文件列表
     * @return 上传成功文件信息
     */
    @PostMapping("/uploadLocalFiles")
    public ResponseUtil uploadLocalFiles(MultipartFile[] files) throws IOException {
        return service.uploadLocalFiles(files);
    }

    /**
     * 输出本地文件
     *
     * @param key 文件索引
     * @return 上传成功文件信息
     */
    @RequestMapping("/outLocalFile")
    public void outLocalFile(@RequestParam String key, HttpServletRequest request, HttpServletResponse response)  {
        service.outLocalFile(key, request, response);
    }
}
