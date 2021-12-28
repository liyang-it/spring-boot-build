package com.mh.jishi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author Lizr
 * @Description 权限
 * @CreateTime 2021-12-28 下午 2:40
 **/
@Data
@TableName(value = "t_permission")
public class TPermission {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    private Integer roleId;

    private String doc;

    private String permission;


    private LocalDateTime addTime;


    private LocalDateTime updateTime;

    private Boolean deleted;
}
