package com.mh.jishi.service.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.mh.jishi.controller.system.dto.AddOrUpdMenuDTO;
import com.mh.jishi.config.ServiceException;
import com.mh.jishi.constants.RedisKeyPrefix;
import com.mh.jishi.entity.TAdmin;
import com.mh.jishi.entity.TSystemMenu;
import com.mh.jishi.exception.ConcurrentUpdateException;
import com.mh.jishi.service.system.impl.AdminMenuServiceImpl;
import com.mh.jishi.service.system.impl.SystemMenuServiceImpl;
import com.mh.jishi.util.RedisUtil;
import com.mh.jishi.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h2>系统管理 - 菜单管理服务层</h2>
 * <p>
 *
 * </p>
 *
 * @author Evan
 * @CreateTime 2022/4/7 9:08
 */
@Service
public class AdminSystemMenuService {

    private SystemMenuServiceImpl service;


    @Resource
    private RedisUtil redisUtil;

    @Autowired
    private AdminMenuServiceImpl menuService;
    private final int SAVE_ALL_MENU_CACHE_SECOND = 4 * (60 * 60);


    @Autowired
    public AdminSystemMenuService(SystemMenuServiceImpl service) {
        this.service = service;
    }

    /**
     * <h3>返回某个管理员菜单数据列表</h3>
     *
     * @param admin 管理员对象
     * @return List<TSystemMenu>
     */
    public List<TSystemMenu> queryMenuByAdmin(TAdmin admin) {
        String adminIdStr = admin.getId().toString();
        if (redisUtil.hHasKey(RedisKeyPrefix.SyStemAdminMenu, adminIdStr)) {
            return (List<TSystemMenu>) redisUtil.hget(RedisKeyPrefix.SyStemAdminMenu, adminIdStr);
        }
        List<TSystemMenu> list = null;
        if (admin.getIsSuperAdmin()) {
            list = queryAllByList();
        } else {
            // 元数据
            List<TSystemMenu> metaList = service.getBaseMapper().queryMenuByAdminId(admin.getId());
            // 过滤出一级菜单
            list = metaList.stream().filter(item -> item.getParent().equals(0)).collect(Collectors.toList());
            // 遍历一级菜单，筛选出子级菜单
            for (TSystemMenu menu : list) {
                Integer parent = menu.getId();
                recursionByStream(parent, metaList, menu);
            }
        }
        redisUtil.hset(RedisKeyPrefix.SyStemAdminMenu, adminIdStr, list, SAVE_ALL_MENU_CACHE_SECOND);
        return list;
    }

    /**
     * <h2>递归查询子级菜单列表，如果没有子级退出, 从本地菜单列表递归</h2>
     *
     * @param parent 父菜单id
     * @param list   元数据列表
     * @param menu   当前菜单
     * @return 菜单对象
     */
    private TSystemMenu recursionByStream(Integer parent, List<TSystemMenu> list, TSystemMenu menu) {
        List<TSystemMenu> children = list.stream().filter(item -> item.getParent().equals(parent)).collect(Collectors.toList());
        for (TSystemMenu systemMenu : children) {
            Integer parent2 = systemMenu.getId();
            recursionByStream(parent2, list, systemMenu);
        }
        menu.setChildren(children);

        return menu;
    }

    /**
     * 查询所有菜单
     *
     * @return ResponseUtil
     */
    public ResponseUtil queryAll() {
        // 验证缓存是否存在 - 条件查询不适用
        if (redisUtil.hasKey(RedisKeyPrefix.SyStemAllMenu)) {
            return ResponseUtil.ok(redisUtil.get(RedisKeyPrefix.SyStemAllMenu));
        }
        // 查询所有一级 菜单
        QueryWrapper<TSystemMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);
        queryWrapper.eq("parent", 0);

        List<TSystemMenu> parent = service.list(queryWrapper);
        // 遍历父级菜单，查询出子级菜单
        for (TSystemMenu systemMenu : parent) {
            recursionByMysql(systemMenu);
        }

        redisUtil.set(RedisKeyPrefix.SyStemAllMenu, parent, SAVE_ALL_MENU_CACHE_SECOND);
        return ResponseUtil.ok(parent);
    }

    // 返回list
    public List<TSystemMenu> queryAllByList() {
        // 验证缓存是否存在 - 条件查询不适用
        if (redisUtil.hasKey(RedisKeyPrefix.SyStemAllMenu)) {
            return (List<TSystemMenu>) redisUtil.get(RedisKeyPrefix.SyStemAllMenu);
        }
        // 查询所有一级 菜单
        QueryWrapper<TSystemMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);
        queryWrapper.eq("parent", 0);

        List<TSystemMenu> parent = service.list(queryWrapper);
        // 遍历父级菜单，查询出子级菜单
        for (TSystemMenu systemMenu : parent) {
            recursionByMysql(systemMenu);
        }
        int SAVE_ALL_MENU_CACHE_SECOND = 4 * (60 * 60);
        redisUtil.set(RedisKeyPrefix.SyStemAllMenu, parent, SAVE_ALL_MENU_CACHE_SECOND);
        return parent;
    }


    /**
     * <h2>递归查询子级菜单，如果没有子级退出, 从MySql获取数据查询</h2>
     *
     * @param menu 菜单对象
     * @return 菜单对象
     */
    private TSystemMenu recursionByMysql(TSystemMenu menu) {
        QueryWrapper<TSystemMenu> parentQueryWrapper = new QueryWrapper<>();
        parentQueryWrapper.eq("deleted", 0);
        parentQueryWrapper.eq("parent", menu.getId());

        List<TSystemMenu> children = service.list(parentQueryWrapper);


        if (children.size() > 0) {
            // 再次查询子级
            for (TSystemMenu child : children) {
                recursionByMysql(child);
            }
        }
        menu.setChildren(children);
        return menu;
    }

    public ResponseUtil addMenu(AddOrUpdMenuDTO dto) {
        TSystemMenu menu = new TSystemMenu();
        menu.setParent(dto.getParent());
        menu.setName(dto.getName());
        menu.setPath(dto.getPath());
        menu.setComponent(dto.getComponent());
        menu.setAlwaysShow(dto.getAlwaysShow());
        menu.setMeta(dto.getMeta());
        menu.setAddTime(LocalDateTime.now());
        menu.setDeleted(false);
        menu.setVersion(1);
        menu.setHidden(dto.getHidden());
        service.save(menu);
        redisUtil.del(RedisKeyPrefix.SyStemAllMenu);
        return ResponseUtil.ok();
    }

    public ResponseUtil updMenu(AddOrUpdMenuDTO dto) {
        if (dto.getId() == null) {
            throw new ServiceException("唯一值缺失");
        }

        /*
        TSystemMenu menu = service.getById(dto.getId());
        if (menu == null) {
            throw new ServiceException("无效菜单");
        }
        menu.setParent(dto.getParent());
        menu.setName(dto.getName());
        menu.setPath(dto.getPath());
        menu.setComponent(dto.getComponent());
        menu.setAlwaysShow(dto.getAlwaysShow());
        menu.setMeta(dto.getMeta());
        menu.setUpdateTime(LocalDateTime.now());
        menu.setVersion(dto.getVersion());
        if(service.getBaseMapper().lockUpdateData(menu) == 0){
            throw new ConcurrentUpdateException();
        }
         */
        UpdateWrapper<TSystemMenu> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("parent", dto.getParent());
        updateWrapper.set("name", dto.getName());
        updateWrapper.set("path", dto.getPath());
        updateWrapper.set("component", dto.getComponent());
        updateWrapper.set("always_show", dto.getAlwaysShow());
        updateWrapper.set("meta", dto.getMeta().toJSONString());
        updateWrapper.set("update_time", LocalDateTime.now());
        updateWrapper.set("hidden", dto.getHidden());
        updateWrapper.setSql("version = version + 1");
        updateWrapper.eq("id", dto.getId()).eq("version", dto.getVersion());
        if(!service.update(updateWrapper)){
            throw new ConcurrentUpdateException();
        }
        redisUtil.del(RedisKeyPrefix.SyStemAllMenu);
        return ResponseUtil.ok();
    }

    public ResponseUtil delMenu(Integer id) {
        if (id == null) {
            throw new ServiceException("唯一值缺失");
        }
        TSystemMenu menu = service.getById(id);
        if (menu == null) {
            throw new ServiceException("无效菜单");
        }
        menu.setUpdateTime(LocalDateTime.now());
        menu.setDeleted(true);
        service.updateById(menu);
        redisUtil.del(RedisKeyPrefix.SyStemAllMenu);
        return ResponseUtil.ok();
    }
}
