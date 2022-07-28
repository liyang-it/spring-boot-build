package com.mh.jishi.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * 对象存储表
 */
@Data
@TableName(value = "t_storage")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TStorage implements Serializable {

    private final static long serialVersionUID = 1L;
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 文件唯一值
     */
    private String fileKey;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 文件类型值
     */
    private String type;

    /**
     * 文件大小
     */
    private Integer size;

    /**
     * 文件访问url
     */
    private String url;

    /**
     * 创建时间
     */
    private LocalDateTime addTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;


    private Boolean deleted;


}