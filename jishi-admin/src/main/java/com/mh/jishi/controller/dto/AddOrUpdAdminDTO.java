package com.mh.jishi.acontroller.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 添加或者修改管理员参数
 * </p>
 *
 * @author Evan
 * @CreateTime 2022/3/31   12:28
 */
@Data
public class AddOrUpdAdminDTO {
    private Integer id;
    @NotBlank(message = "账号不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    // menu_id数组
    private Integer[] menu;

    /**
     * 1 超级管理员， 0 普通管理员
     */
    @NotNull(message = "请选择是否为超级管理员")
    private Boolean isSuperAdmin;

    private Boolean status;


}
