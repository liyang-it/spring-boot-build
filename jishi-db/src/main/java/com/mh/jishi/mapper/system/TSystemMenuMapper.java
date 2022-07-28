package com.mh.jishi.mapper.system;

import java.util.ArrayList;

import com.mh.jishi.entity.TSystemMenu;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 系统管理 - 菜单管理信息表 Mapper 接口
 * </p>
 *
 * @author Evan
 * @since 2022-04-07
 */
public interface TSystemMenuMapper extends BaseMapper<TSystemMenu> {

    /**
     * 查询管理员菜单
     * @param adminId 管理员ID
     * @return 菜单列表
     */
    ArrayList<TSystemMenu> queryMenuByAdminId(@Param("adminId") Integer adminId);

    @Deprecated
    /**
     * 有异常，需要处理 meta 字段 mybatis自定义处理器
     */
    int lockUpdateData(TSystemMenu menu);

}
