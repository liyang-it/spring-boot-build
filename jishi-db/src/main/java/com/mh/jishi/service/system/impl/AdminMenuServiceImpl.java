package com.mh.jishi.service.system.impl;

import com.mh.jishi.service.system.IAdminMenuService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mh.jishi.constants.RedisKeyPrefix;
import com.mh.jishi.entity.TAdminMenu;
import com.mh.jishi.entity.TSystemMenu;
import com.mh.jishi.mapper.TAdminMenuMapper;
import com.mh.jishi.util.RedisUtil;

import cn.hutool.core.thread.ThreadUtil;

/**
 * <p>
 * 管理员菜单权限表 服务实现类
 * </p>
 *
 * @author Evan
 * @since 2022-03-29
 */
@Service
public class AdminMenuServiceImpl extends ServiceImpl<TAdminMenuMapper, TAdminMenu> implements IAdminMenuService {
    private final RedisUtil redisUtil;
    private final SystemMenuServiceImpl menuService;
    public AdminMenuServiceImpl(RedisUtil redisUtil, SystemMenuServiceImpl menuService){
        this.redisUtil = redisUtil;
        this.menuService = menuService;
    }
    @Override
    public Integer getMenuCount() {
        int count = 0;
        if(redisUtil.hasKey(RedisKeyPrefix.SystemAdminMenuCount)){
            count = Integer.parseInt(redisUtil.get(RedisKeyPrefix.SystemAdminMenuCount).toString());
        }else{
            count = loadMenuCount();
        }

        return count;
    }
    private int loadMenuCount(){
        QueryWrapper<TSystemMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);
        int count = menuService.count(queryWrapper);
        ThreadUtil.execAsync(new Runnable() {
            @Override
            public void run() {
                redisUtil.set(RedisKeyPrefix.SystemAdminMenuCount, Integer.toString(count));
            }
        });
        return count;
    }
}
