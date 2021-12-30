package com.mh.jishi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mh.jishi.entity.TAdmin;
import com.mh.jishi.entity.TRole;
import com.mh.jishi.mapper.TAdminMapper;
import com.mh.jishi.mapper.TRoleMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Lizr
 * @Description 管理员服务类
 * @CreateTime 2021-12-28 下午 2:36
 **/
@Service
public class TAdminService extends ServiceImpl<TAdminMapper, TAdmin> {

    @Resource
    private TRoleMapper roleMapper;
    public List<TAdmin> findAdmin(String username) {
        return this.baseMapper.selectList(new QueryWrapper<TAdmin>().eq("username",username).eq("deleted", 0));
    }

    public TAdmin findAdmin(Integer id) {
        return getById(id);
    }

    // 查询超级管理员
    public IPage<TAdmin> querySelective(String username, Integer page, Integer limit, String sort, String order, Integer type) {
        IPage<TAdmin> iPage = new Page<>(page, limit);
        QueryWrapper<TAdmin> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "username", "avatar", "role_ids");
        queryWrapper.like(org.apache.commons.lang3.StringUtils.isNotBlank(username), "username", username);
        queryWrapper.eq("deleted", 0);
        if(order.equals("desc")){
            queryWrapper.orderByDesc(sort);
        }else{
            queryWrapper.orderByAsc(sort);
        }
        iPage = this.baseMapper.selectPage(iPage, queryWrapper);
        iPage.getRecords().forEach(item ->{
            List<TRole> list = roleMapper.selectList(new QueryWrapper<TRole>().in("id", item.getRoleIds()));
            StringBuilder sb = new StringBuilder();
            list.forEach(item2 ->{
                sb.append(item2.getName());
                sb.append(",");
            });
            String rolsStr = sb.substring(0,sb.lastIndexOf(",") - 1);
            item.setRolsStr(rolsStr);
        });
        return iPage;
    }


    public boolean updateByIdQ(TAdmin admin) {
        admin.setUpdateTime(LocalDateTime.now());
        return updateById(admin);
    }

    public void deleteById(Integer id) {
        TAdmin admin = this.findAdmin(id);
        admin.setUpdateTime(LocalDateTime.now());
        admin.setDeleted(true);
        updateById(admin);
    }

    public void add(TAdmin admin) {
        admin.setAddTime(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());
        save(admin);
    }

    public TAdmin findById(Integer id) {
        TAdmin item = getById(id);
        List<TRole> list = roleMapper.selectList(new QueryWrapper<TRole>().in("id", item.getRoleIds()));
        StringBuilder sb = new StringBuilder();
        list.forEach(item2 ->{
            sb.append(item2.getName());
            sb.append(",");
        });
        String rolsStr = sb.substring(0,sb.lastIndexOf(",") - 1);
        item.setRolsStr(rolsStr);
        return item;
    }


    public List<TAdmin> all() {
        return this.baseMapper.selectList(new QueryWrapper<TAdmin>().eq("deleted", 0));
    }
}
