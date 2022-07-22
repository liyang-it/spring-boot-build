package com.mh.jishi.controller;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mh.jishi.annotation.RequiresPermissionsDesc;
import com.mh.jishi.entity.TAdmin;
import com.mh.jishi.service.TAdminService;
import com.mh.jishi.util.ResponseUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * 系统管理 -  管理员管理控制层
 */
@RestController
@RequestMapping("/admin/admin")
@Validated
public class AdminAdminController {
    private final Log logger = LogFactory.getLog(AdminAdminController.class);

    @Autowired
    private TAdminService adminService;


    @RequiresPermissions("admin:admin:list")
    @RequiresPermissionsDesc(menu = {"系统管理", "后台管理员管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(String username,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort,
                       @RequestParam(defaultValue = "desc") String order) {
        IPage<TAdmin> adminList = adminService.querySelective(username, page, limit, sort, order, 0);

        return ResponseUtil.ok(adminList);
    }

    private Object validate(TAdmin admin, int type ) {
        String name = admin.getUsername();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument();
        }
//        if (!RegexUtil.isUsername(name)) {
//            return ResponseUtil.fail(401, "");
//        }
        if(type == 1){
            String password = admin.getPassword();
            if ( StringUtils.isEmpty(password) || password.length() < 6) {
                return ResponseUtil.fail(401, "密码长度不能小于6位字符");
            }
        }

        return null;
    }

    @RequiresPermissions("admin:admin:create")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody TAdmin admin) {
        Object error = validate(admin, 1);
        if (error != null) {
            return error;
        }

        String username = admin.getUsername();
        List<TAdmin> adminList = adminService.findAdmin(username);
        if (adminList.size() > 0) {
            return ResponseUtil.fail(501, "管理員已存在");
        }
        admin.setRoleIds(new Integer[]{1});
        adminService.add(admin);

        return ResponseUtil.ok(admin);
    }

    @RequiresPermissions("admin:admin:read")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        TAdmin admin = adminService.findById(id);
        return ResponseUtil.ok(admin);
    }

    @RequiresPermissions("admin:admin:update")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody TAdmin admin) {
        Object error = validate(admin, 2);
        if (error != null) {
            return error;
        }

        Integer anotherAdminId = admin.getId();
        if (anotherAdminId == null) {
            return ResponseUtil.badArgument();
        }

        // 不允许管理员通过编辑接口修改密码
        admin.setPassword(null);

        if (adminService.updateByIdQ(admin) == false) {
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.ok(admin);
    }

    @RequiresPermissions("admin:admin:delete")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody TAdmin admin) {
        Integer anotherAdminId = admin.getId();
        if (anotherAdminId == null) {
            return ResponseUtil.badArgument();
        }

        // 管理员不能删除自身账号
        Subject currentUser = SecurityUtils.getSubject();
        TAdmin currentAdmin = (TAdmin) currentUser.getPrincipal();
        if (currentAdmin.getId().equals(anotherAdminId)) {
            return ResponseUtil.fail(501, "不能刪除自己賬號");
        }

        adminService.deleteById(anotherAdminId);
        return ResponseUtil.ok();
    }
}
