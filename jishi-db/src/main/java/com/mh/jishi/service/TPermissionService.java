package com.mh.jishi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mh.jishi.entity.TPermission;
import com.mh.jishi.mapper.TPermissionMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author Lizr
 * @Description 权限服务层
 * @CreateTime 2021-12-28 下午 2:42
 **/
@Service
public class TPermissionService extends ServiceImpl<TPermissionMapper, TPermission> {
    public Set<String> queryByRoleIds(Integer[] roleIds) {
        Set<String> permissions = new HashSet<String>();
        if(roleIds.length == 0){
            return permissions;
        }

        QueryWrapper<TPermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("role_id", Arrays.asList(roleIds));
        queryWrapper.eq("deleted", 0);
        List<TPermission> permissionList = this.baseMapper.selectList(queryWrapper);

        for(TPermission permission : permissionList){
            permissions.add(permission.getPermission());
        }

        return permissions;
    }
}
