package com.mh.jishi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 对象存储表
 */
@Data
@TableName(value = "t_storage")
public class TStorage {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 文件唯一值
     */
    private String key;

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