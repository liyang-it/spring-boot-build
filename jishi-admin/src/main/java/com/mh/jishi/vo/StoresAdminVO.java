package com.mh.jishi.vo;

import lombok.Data;

/**
 * @Author Lizr
 * @Description 门店管理员与门店关联vo
 * @CreateTime 2021-10-25 下午 2:42
 **/
@Data
public class StoresAdminVO {

    private String username;
    private String password;
    private String avatar;
    private Integer storesId;
    private String storesName;

}
