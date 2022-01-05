package com.mh.jishi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mh.jishi.constants.RedisKeyPrefix;
import com.mh.jishi.entity.TSystem;
import com.mh.jishi.mapper.TSystemMapper;
import com.mh.jishi.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Lizr
 * @Description 系统配置服务层
 * @CreateTime 2021-12-27 下午 4:38
 **/
@Service
public class TSystemService extends ServiceImpl<TSystemMapper, TSystem> {
    @Autowired
    private RedisUtil redisUtil;

    public String getValue(String key){
        String value = null;
        String redisKey = RedisKeyPrefix.SystemConfig + key;
        if(!redisUtil.hasKey(redisKey)){
            value = this.baseMapper.getKeyValue(key);
            redisUtil.set(redisKey, value);
        }else{
            try {
                value = redisUtil.get(redisKey).toString();
            }catch (Exception e){
                value = null;
            }

        }
        return value;
    }

    /**
     * 根据前缀查询配置
     * @param pre
     * @return
     */
    public List<Map<String, String>> queryKeyAndValueByKeyLike(String pre){
        return this.baseMapper.queryKeyAndValueByLikeKey(pre);
    }

    /**
     * 查询所有配置 返回map格式
     * @return
     */
    public Map<String, String> queryAll(){
        Map<String, String> map = new HashMap<>();
        List<TSystem> list = list(new QueryWrapper<TSystem>().eq("deleted", 0));
        for(int i = 0; i < list.size(); i ++){
            map.put(list.get(i).getKeyName(), list.get(i).getKeyValue());
        }
        return map;
    }

    /**
     * 根据配置前缀查询 配置 返回map
     * @param prefix
     * @return
     */
    public Map<String, String> queryByPriFixAll(String prefix){
        Map<String, String> map = new HashMap<>();
        List<TSystem> list = list(new QueryWrapper<TSystem>().eq("deleted", 0).likeRight("key_name", prefix));
        for(int i = 0; i < list.size(); i ++){
            map.put(list.get(i).getKeyName(), list.get(i).getKeyValue());
        }
        return map;
    }

    public List<TSystem> queryByPriFixAllList(String prefix) {
        List<TSystem> list = list(new QueryWrapper<TSystem>().eq("deleted", 0).likeRight("key_name", prefix));
        return list;
    }

    /**
     * 修改配置
     * @param system
     */
    public boolean updateByNouNull(TSystem system){
        boolean isUpd = false;
        TSystem query =  getById(system.getId());
        if(query != null){
            system.setUpdateTime(LocalDateTime.now());
            isUpd = updateById(system);
        }
        return isUpd;
    }

    /**
     *  添加配置
     * @param key
     * @param value
     * @param doc
     */
    public void addConfig(String key, String value, String doc){
        TSystem system = new TSystem();
        system.setAddTime(LocalDateTime.now());
        system.setDeleted(false);
        system.setReadme(doc);
        system.setKeyName(key);
        system.setKeyValue(value);
        save(system);
    }
}
