package com.mh.jishi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置表
 */
@Data
@TableName(value = "t_system")
public class TSystem {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 配置key
     */
    private String keyName;

    /**
     * 配置value
     */
    private String keyValue;

    /**
     * 创建时间
     */
    private LocalDateTime addTime;

    /**
     * 配置描述
     */
    private String doc;

    /**
     * 配置修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除状态 false 正常 true 删除
     */
    private Boolean deleted;

}