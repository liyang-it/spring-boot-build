package com.mh.jishi.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mh.jishi.entity.TAdminMenu;

/**
 * <p>
 * 管理员菜单权限表 服务类
 * </p>
 *
 * @author Evan
 * @since 2022-03-29
 */
public interface IAdminMenuService extends IService<TAdminMenu> {
    /**
     * 获取所有菜单数量 不分层级，过滤禁用和删除
     * @return 数量
     */
    Integer getMenuCount();
}
