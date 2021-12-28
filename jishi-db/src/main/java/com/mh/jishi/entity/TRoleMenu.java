package com.mh.jishi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mh.jishi.mybatis.JsonStringArrayTypeHandler;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Author Lizr
 * @Description 管理员后台页面菜单表
 * @CreateTime 2021-12-28 下午 2:39
 **/
@Data
@TableName(autoResultMap = true, value = "t_role_menu")
public class TRoleMenu {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "role_id")
    @NotNull(message = "請選擇角色")
    private Integer roleId;

    @NotEmpty(message = "菜單不能為空")
    @TableField(value = "menu", typeHandler = JsonStringArrayTypeHandler.class)
    private String[] menu;
}
