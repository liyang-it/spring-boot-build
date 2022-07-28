package com.mh.jishi.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;

/**
 * <p>
 * 管理员菜单权限表
 * </p>
 *
 * @author Evan
 * @since 2022-03-29
 */
@TableName(value = "t_admin_menu", autoResultMap = true)
@ApiModel(value = "AdminMenu对象", description = "管理员菜单权限表")
public class TAdminMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    private Integer adminId;
    private Integer menuId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }
}
