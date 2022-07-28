package com.mh.jishi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mh.jishi.mybatis.JsonIntegerArrayTypeHandler;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author Lizr
 * @Description 管理员
 * @CreateTime 2021-12-28 下午 2:32
 **/
@Data
@TableName(autoResultMap = true, value = "t_admin")
public class TAdmin {
    @TableField(exist = false)
    private List<Integer> menu;

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    private Boolean isSuperAdmin;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime addTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
