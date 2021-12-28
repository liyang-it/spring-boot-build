package com.mh.jishi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author Lizr
 * @Description 角色
 * @CreateTime 2021-12-28 下午 2:37
 **/
@Data
@TableName("t_role")
public class TRole {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 权限名称
     */
    @TableField(value = "name")
    private String name;


    /**
     * 权限说明
     */
    @TableField(value = "doc")
    private String doc;

    /**
     * 是否启用权限
     */
    @TableField(value = "enabled")
    private Integer enabled;

    /**
     * 创建时间
     */
    @TableField(value = "add_time")
    private LocalDateTime addTime;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    @TableField(value = "deleted")
    private Integer deleted;
}
