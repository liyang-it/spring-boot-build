package com.mh.jishi.system;

import com.mh.jishi.service.TSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 系统启动服务，用于设置系统配置信息、检查系统状态及打印系统信息
 */
@Component
class SystemInistService {

    @Autowired
    private TSystemService service;

    /**
     * 系统默认配置
     */
    private  Map<String, String> DEFAULT_CONFIGS = new HashMap<>();
    private Map<String, String> DEFAULT_DESC = new HashMap<>();


    @PostConstruct
    private void inist() {

        DEFAULT_CONFIGS.put(SystemConfig.DefaultAvatart, "https://yanxuan.nosdn.127.net/80841d741d7fa3073e0ae27bf487339f.jpg?imageView&quality=90");
        DEFAULT_DESC.put(SystemConfig.DefaultAvatart, "用户注册默认头像");
        initConfigs();
    }

    private void initConfigs() {
        // 1. 读取数据库全部配置信息
        Map<String, String> configs = service.queryByPriFixAll(SystemConfig.prefix);

        // 2. 分析DEFAULT_CONFIGS, 如果数据库不存在配置则新增
        for (Map.Entry<String, String> entry : DEFAULT_CONFIGS.entrySet()) {
            if (configs.containsKey(entry.getKey())) {
                continue;
            }
            configs.put(entry.getKey(), entry.getValue());
            service.addConfig(entry.getKey(), entry.getValue(), DEFAULT_DESC.get(entry.getKey()));
        }

    }
}
