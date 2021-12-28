package com.mh.jishi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName(value = "t_user")
public class TUser {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    // 用户名
    private String username;

    // 密码
    private String password;

    // 性别 1 男 2 女
    private Integer gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;


    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户手机号
     */
    private String mobile;

    /**
     * 用户头像
     */
    private String avatar;


    private String weixinOpenid;


    /**
     * 用户状态
     * <h3> 0 正常</h3>
     * <h3> 1 禁用</h3>
     * <h3> 2 注销</h3>
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime addTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除状态 0 正常 1 删除
     */
    private Boolean deleted;
}
