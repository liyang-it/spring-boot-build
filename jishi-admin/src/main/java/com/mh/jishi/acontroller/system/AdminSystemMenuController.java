package com.mh.jishi.acontroller.system;

import com.mh.jishi.acontroller.system.dto.AddOrUpdMenuDTO;
import com.mh.jishi.annotation.RequiresPermissionsDesc;
import com.mh.jishi.entity.TAdmin;
import com.mh.jishi.service.TAdminService;
import com.mh.jishi.service.system.AdminSystemMenuService;
import com.mh.jishi.util.ResponseUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 系统管理 - 菜单管理信息表 前端控制器
 * </p>
 *
 * @author Evan
 * @since 2022-04-07
 */
@RestController
@RequestMapping("/admin/system/menu")
public class AdminSystemMenuController {

    private final AdminSystemMenuService service;
    private final TAdminService adminService;

    @Autowired
    public AdminSystemMenuController(AdminSystemMenuService service, TAdminService adminService){
        this.service = service;
        this.adminService = adminService;
    }

    @RequiresPermissions("admin:system-menu")
    @RequiresPermissionsDesc(menu = {"系统管理", "菜单管理"}, button = "查询全部菜单")
    @GetMapping("/queryAll")
    public ResponseUtil queryAll(){
        return service.queryAll();
    }

    @RequiresPermissions("admin:system-menu")
    @RequiresPermissionsDesc(menu = {"系统管理", "菜单管理"}, button = "根据管理员id查询")
    @GetMapping("/queryAllById")
    public ResponseUtil queryAllById(@RequestParam Integer adminId){
        TAdmin admin = adminService.getById(adminId);
        Assert.notNull(admin, "管理员不存在");
        return ResponseUtil.ok(service.queryMenuByAdmin(admin));
    }

    @RequiresPermissions("admin:system-menu")
    @RequiresPermissionsDesc(menu = {"系统管理", "菜单管理"}, button = "添加")
    @PostMapping("/addMenu")
    public ResponseUtil addMenu(@RequestBody @Valid AddOrUpdMenuDTO dto){
        return service.addMenu(dto);
    }

    @RequiresPermissions("admin:system-menu")
    @RequiresPermissionsDesc(menu = {"系统管理", "菜单管理"}, button = "修改")
    @PostMapping("/updMenu")
    public ResponseUtil updMenu(@RequestBody @Valid AddOrUpdMenuDTO dto){
        return service.updMenu(dto);
    }

    @RequiresPermissions("admin:system-menu")
    @RequiresPermissionsDesc(menu = {"系统管理", "菜单管理"}, button = "删除")
    @GetMapping("/delMenu")
    public ResponseUtil delMenu(@RequestParam Integer id){
        return service.delMenu(id);
    }



}
