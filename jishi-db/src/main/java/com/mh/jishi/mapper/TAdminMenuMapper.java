package com.mh.jishi.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mh.jishi.entity.TAdminMenu;

/**
 * <p>
 * 管理员菜单权限表 Mapper 接口
 * </p>
 *
 * @author Evan
 * @since 2022-03-29
 */
public interface TAdminMenuMapper extends BaseMapper<TAdminMenu> {
    /**
     * 验证管理员是否是超级管理员，使用 是否有全部菜单权限  0  验证
     * @param adminId 管理员id
     * @return 查询条数
     */
    int checkIsSuperAdmin(@Param("adminId") Integer adminId);

    @Select("SELECT menu_id FROM `t_admin_menu` where admin_id = #{adminId}")
    List<Integer> getAdminMenuIds(@Param("adminId") Integer adminId);

}
