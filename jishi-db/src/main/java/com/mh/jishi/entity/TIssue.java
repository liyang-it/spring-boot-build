package com.mh.jishi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 帮助信息表
 */
@Data
@TableName(value = "t_issue")
public class TIssue {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 问题
     */
    private String question;

    /**
     * 答案
     */
    private String answer;


    private LocalDateTime addTime;


    private LocalDateTime updateTime;

    private Boolean deleted;


}