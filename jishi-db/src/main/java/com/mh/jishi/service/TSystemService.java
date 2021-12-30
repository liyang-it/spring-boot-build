package com.mh.jishi.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mh.jishi.entity.TSystem;
import com.mh.jishi.mapper.TSystemMapper;
import org.springframework.stereotype.Service;

/**
 * @Author Lizr
 * @Description 系统配置服务层
 * @CreateTime 2021-12-27 下午 4:38
 **/
@Service
public class TSystemService extends ServiceImpl<TSystemMapper, TSystem> {
    public String getValue(String key){
        String value = this.baseMapper.getKeyValue(key);
        return value;
    }
}
