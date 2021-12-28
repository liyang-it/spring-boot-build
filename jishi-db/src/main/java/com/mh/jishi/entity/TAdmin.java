package com.mh.jishi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mh.jishi.mybatis.JsonIntegerArrayTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author Lizr
 * @Description 管理员
 * @CreateTime 2021-12-28 下午 2:32
 **/
@Data
@TableName(autoResultMap = true, value = "t_admin")
public class TAdmin {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 登陆时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 创建时间
     */
    private LocalDateTime addTime;


    private LocalDateTime updateTime;


    private Boolean deleted;

    /**
     * 权限 id列表
     */
    @TableField(typeHandler = JsonIntegerArrayTypeHandler.class, value = "role_ids")
    private Integer[] roleIds;
    /**
     * 权限 名称列表
     */
    @TableField(exist = false)
    private String rolsStr;
}
