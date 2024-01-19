package com.mh.jishi.service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mh.jishi.entity.TRole;
import com.mh.jishi.mapper.TRoleMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public void add(TRole role) {
        role.setAddTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        this.baseMapper.insert(role);
    }
    public IPage<TRole> querySelective(String name, Integer page, Integer limit, String sort, String order) {
        IPage<TRole> iPage = new Page<>(page, limit);
        QueryWrapper<TRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.eq("deleted", 0);
        if(order.equals("desc")){
            queryWrapper.orderByDesc(sort);
        }else{
            queryWrapper.orderByAsc(sort);
        }
        iPage = this.baseMapper.selectPage(iPage, queryWrapper);
        return iPage;
    }
    public boolean checkExist(String name) {
        QueryWrapper<TRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        queryWrapper.eq("deleted", 0);
        return this.baseMapper.selectCount(queryWrapper) != 0;
    }
}
