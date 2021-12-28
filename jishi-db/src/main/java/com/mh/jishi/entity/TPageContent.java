package com.mh.jishi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author Lizr
 * @Description 页面内容表
 * @CreateTime 2021-11-15 上午 10:21
 **/
@Data
@TableName(value = "t_page_content")
public class TPageContent {
    @NotNull(message = "id不能為空")
    @TableId(type = IdType.AUTO, value = "id")
    private Integer id;

    @TableField(value = "label")
    private String label;

    @TableField(value = "keyword")
    private String keyword;

    @NotNull(message = "內容不能為空")
    @TableField(value = "content")
    private String content;

    /**
     * 1 正常  2 禁用
     */
    @NotNull(message = "請選擇狀態")
    @TableField(value = "status")
    private Integer status;
}
