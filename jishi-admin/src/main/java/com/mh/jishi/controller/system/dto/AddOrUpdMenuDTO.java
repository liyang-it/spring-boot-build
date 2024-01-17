package com.mh.jishi.controller.system.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <h2>添加或者修改菜单参数</h2>
 * <p>
 *
 * </p>
 *
 * @author Evan
 * @since 2022/4/7 14:28
 */
@Data
public class AddOrUpdMenuDTO {

    private Integer id;
    /**
     * 父级菜单id， 0表示没有上级
     */
    @NotNull(message = "请选择上级菜单")
    private Integer parent;

    /**
     * 菜单名称
     */
    @NotBlank(message = "请输入菜单名称")
    private String name;
    /**
     * 菜单路由路径
     */
    @NotBlank(message = "请输入菜单路由路径")
    private String path;
    /**
     * 菜单组件路径，一级菜单固定 Layout
     */
    @NotBlank(message = "请输入组件")
    private String component;

    /**
     *  是否为目录，0/false 不是 1/true 是
     */
    @NotNull(message = "请选择是否为目录")
    private Boolean alwaysShow;
    /**
     * 标题对象{title: ‘’, icon: ‘’} json字符串格式存储
     */
    @NotNull(message = "请输入标题对象")
    private JSONObject meta;

    @NotNull(message = "数据版本不能为空")
    private Integer version;

    @NotNull(message = "请选择是否隐藏")
    private Boolean hidden;


}
