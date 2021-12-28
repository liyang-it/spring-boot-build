package com.mh.jishi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mh.jishi.entity.TRole;
import com.mh.jishi.mapper.TRoleMapper;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author Lizr
 * @Description 角色服务层
 * @CreateTime 2021-12-28 下午 2:43
 **/
@Service
public class TRoleService extends ServiceImpl<TRoleMapper, TRole> {
    public Set<String> queryByIds(Integer[] roleIds) {
        Set<String> roles = new HashSet<String>();
        if(roleIds.length == 0){
            return roles;
        }
        QueryWrapper<TRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enabled", true);
        queryWrapper.eq("deleted", false);
        queryWrapper.in("id", roleIds);
        List<TRole> roleList = this.baseMapper.selectList(queryWrapper);

        for(TRole role : roleList){
            roles.add(role.getName());
        }
        return roles;

    }
}
