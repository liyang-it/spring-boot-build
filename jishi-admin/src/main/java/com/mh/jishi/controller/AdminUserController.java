package com.mh.jishi.controller;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mh.jishi.annotation.RequiresPermissionsDesc;
import com.mh.jishi.entity.TUser;
import com.mh.jishi.service.TUserService;
import com.mh.jishi.util.ResponseUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 用户管理 - 会员管理
 */
@RestController
@RequestMapping("/admin/user")
@Validated
public class AdminUserController {
    private final Log logger = LogFactory.getLog(AdminUserController.class);

    @Autowired
    private TUserService userService;

    @RequiresPermissions("admin:user:list")
    @RequiresPermissionsDesc(menu = {"用户管理", "会员管理"}, button = "查询")
    @GetMapping("/pageQuery")
    public Object list(
                       Integer id,
                       String username,
                       String mobile,
                       @RequestParam(defaultValue = "1") Integer pageIndex,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       @RequestParam(defaultValue = "add_time") String sort,
                       @RequestParam(defaultValue = "desc") String order) {
        IPage<TUser> userList = userService.querySelective(id, username, mobile,  pageIndex, pageSize, sort, order);
        return ResponseUtil.ok(userList);
    }
    @RequiresPermissions("admin:user:list")
    @RequiresPermissionsDesc(menu = {"用户管理", "会员管理"}, button = "详情")
    @GetMapping("/detail")
    public Object userDetail(@NotNull Integer id) {
    	TUser user=userService.getById(id);
        return ResponseUtil.ok(user);
    }
    @RequiresPermissions("admin:user:list")
    @RequiresPermissionsDesc(menu = {"用户管理", "会员管理"}, button = "编辑")
    @PostMapping("/update")
    public Object userUpdate(@RequestBody TUser user) {
        return ResponseUtil.ok(userService.updateByIdAndUpdateDate(user));
    }


    @RequiresPermissions("admin:user:upd-status")
    @RequiresPermissionsDesc(menu = {"用戶管理","會員管理"}, button = "禁用用戶或者啟用用戶")
    @PostMapping("/updStatus")
    public Object updStatus(@RequestBody Map<String, Object> map){
        Integer userId = (Integer) map.get("userId");
        // 0 啟用 1 禁用
        Integer status = (Integer) map.get("status");
        if(userId == null || status == null){
            return ResponseUtil.fail(401, "參數缺失");
        }
        TUser user = userService.getById(userId);
        if(user == null){
            return ResponseUtil.fail(401, "用戶不存在");
        }

        user.setStatus(status);
        userService.updateByIdAndUpdateDate(user);
        return ResponseUtil.ok();
    }
}
