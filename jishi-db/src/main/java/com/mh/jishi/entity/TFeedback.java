package com.mh.jishi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mh.jishi.mybatis.JsonStringArrayTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 反馈信息表
 */
@Data
@TableName(value = "t_feeback")
public class TFeedback {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 反馈人ID， 未登录为空
     */
    private Integer userId;

    /**
     * 反馈人名称， 未登录为空
     */
    private String username;

    /**
     * 反馈人手机号， 未登录为空
     */
    private String mobile;

    /**
     * 反馈类型
     */
    private String feedType;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 是否带图片
     */
    private Boolean hasPicture;

    /**
     * 图片数组
     */
    @TableField(typeHandler = JsonStringArrayTypeHandler.class)
    private String[] picUrls;


    private LocalDateTime addTime;


    private LocalDateTime updateTime;


    private Boolean deleted;


}