package com.mh.jishi.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;

import com.mh.jishi.service.system.impl.AdminMenuServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mh.jishi.constants.RedisKeyPrefix;
import com.mh.jishi.entity.TAdmin;
import com.mh.jishi.entity.TRole;
import com.mh.jishi.mapper.TAdminMapper;
import com.mh.jishi.mapper.TRoleMapper;
import com.mh.jishi.util.RedisUtil;

/**
 * @Author Lizr
 * @Description 管理员服务类
 * @CreateTime 2021-12-28 下午 2:36
 **/
@Service
@SuppressWarnings("all")
public class TAdminService extends ServiceImpl<TAdminMapper, TAdmin> {

    private final AdminMenuServiceImpl menuService;
    private final RedisUtil redisUtil;

    @Resource
    private TRoleMapper roleMapper;

    public TAdminService( AdminMenuServiceImpl menuService, RedisUtil redisUtil) {
        this.menuService = menuService;
        this.redisUtil = redisUtil;
    }

    public List<TAdmin> findAdmin(String username) {
        return this.baseMapper.selectList(new QueryWrapper<TAdmin>().eq("username", username).eq("deleted", 0));
    }

    public int checkUserName(String username) {
        return this.baseMapper.selectCount(new QueryWrapper<TAdmin>().eq("username", username).eq("deleted", 0));
    }

    public TAdmin findAdmin(Integer id) {
        return getById(id);
    }

    /**
     * 获取管理员菜单Id列表
     *
     * @param id 管理员id
     * @return List<Integer>
     */
    public List<Integer> getAdminMenuIds(Integer id) {
        List<Integer> menuIds = null;
        String idStr = id.toString();
        if (redisUtil.hHasKey(RedisKeyPrefix.SystemAdminMenuIds, idStr)) {
            menuIds = (List<Integer>) redisUtil.hget(RedisKeyPrefix.SystemAdminMenuIds, idStr);
        } else {
            menuIds = menuService.getBaseMapper().getAdminMenuIds(id);
            redisUtil.hset(RedisKeyPrefix.SystemAdminMenuIds, idStr, menuIds);
        }
        return menuIds;
    }

    /**
     * 分页级联生产厂家查询
     *
     * @param username  账户用户名
     * @param deleted   是否删除 -1 全部， 0 正常 1 删除或者禁用
     * @param isSuper   -1 全部，1 超级管理员，0 普通管理员
     * @param page      当前页
     * @param limit     显示条数
     * @return IPage<TAdmin> 分页对象数据
     */
    public IPage<TAdmin> querySelective(String username, Integer deleted, Integer isSuper, Integer page, Integer limit) {


        IPage<TAdmin> iPage = new Page<>(page, limit);
        QueryWrapper<TAdmin> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(username), "username", username);
        queryWrapper.eq(!deleted.equals(-1), "deleted", username);
        queryWrapper.eq(!isSuper.equals(-1), "is_super_admin", username);
        iPage = page(iPage, queryWrapper);
        for (TAdmin item : iPage.getRecords()) {
            List<Integer> menuIds = getAdminMenuIds(item.getId());
            /**
             * 使用是否有全部菜单权限*来判断 是不是超级管理员, 修改逻辑，判断是否拥有全部权限 用 menu_id是否 有 0  - 修改逻辑
             * 2022-05-18 新逻辑，因为加入了外协厂逻辑，旧管理员逻辑废弃，使用管理员权限数量对比是否拥有全部权限
             * // boolean checkIsSuperAdmin = menuIds.size() == menuService.getMenuCount();
             * // item.setIsSuperAdmin(checkIsSuperAdmin);
             * 2022-05-19 新逻辑， 用数量判断是否是否为超级管理员，不严谨，使用额外字段控制
             * // boolean checkIsSuperAdmin = menuIds.contains(0);
             */
            item.setMenu(menuIds);
        }
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

    public void restoreById(Integer id) {
        TAdmin admin = this.findAdmin(id);
        admin.setUpdateTime(LocalDateTime.now());
        admin.setDeleted(false);
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
        list.forEach(item2 -> {
            sb.append(item2.getName());
            sb.append(",");
        });
        String rolsStr = sb.substring(0, sb.lastIndexOf(",") - 1);
        item.setRolsStr(rolsStr);
        return item;
    }


    public List<TAdmin> all() {
        return this.baseMapper.selectList(new QueryWrapper<TAdmin>().eq("deleted", 0));
    }
}
