package com.mh.jishi.acontroller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mh.jishi.acontroller.dto.AddOrUpdAdminDTO;
import com.mh.jishi.annotation.RequiresPermissionsDesc;
import com.mh.jishi.config.ServiceException;
import com.mh.jishi.constants.RedisKeyPrefix;
import com.mh.jishi.entity.TAdmin;
import com.mh.jishi.entity.TAdminMenu;
import com.mh.jishi.service.TAdminService;
import com.mh.jishi.service.system.impl.AdminMenuServiceImpl;
import com.mh.jishi.util.RedisUtil;
import com.mh.jishi.util.ResponseUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.LinkedList;


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

    @Autowired
    private AdminMenuServiceImpl menuService;

    @Autowired
    private RedisUtil redisUtil;


    @RequiresPermissions("admin:admin:list")
    @RequiresPermissionsDesc(menu = {"系统管理", "后台管理员管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(String username, @RequestParam(defaultValue = "-1") Integer deleted, @RequestParam(defaultValue = "-1") Integer isSuper, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit) {
        IPage<TAdmin> adminList = adminService.querySelective(username, deleted, isSuper, page, limit);

        return ResponseUtil.ok(adminList);
    }


    @RequiresPermissions("admin:admin:create")
    @RequiresPermissionsDesc(menu = {"系统管理", "管理员管理"}, button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody @Valid AddOrUpdAdminDTO dto) {
        int adminList = adminService.checkUserName(dto.getUsername());
        if (adminList != 0) {
            throw new ServiceException("账户已存在");
        }
        // 新逻辑
        // 保存 管理员
        String avatar = "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif";
        TAdmin admin = new TAdmin();
        // 账户类型
        admin.setAvatar(avatar);
        admin.setUsername(dto.getUsername());
        admin.setPassword(dto.getPassword());
        admin.setRoleIds(new Integer[]{1});
        admin.setAddTime(LocalDateTime.now());
        admin.setDeleted(false);
        admin.setIsSuperAdmin(dto.getIsSuperAdmin());
        adminService.save(admin);
        // 添加管理员菜单
        LinkedList<TAdminMenu> linkedList = new LinkedList<>();
        for (int i = 0; i < dto.getMenu().length; i++) {
            TAdminMenu menu = new TAdminMenu();
            menu.setAdminId(admin.getId());
            menu.setMenuId(dto.getMenu()[i]);
            linkedList.add(menu);
        }
        // 保存管理员菜单
        menuService.saveBatch(linkedList);
        return ResponseUtil.ok();
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
        public Object update(@RequestBody AddOrUpdAdminDTO dto) {
            if (dto.getId() == null) {
                throw new ServiceException("唯一值缺失");
            }
            TAdmin admin = adminService.getById(dto.getId());
            if (admin == null) {
                throw new ServiceException("账户不存在");
            }
            if (!admin.getUsername().equals(dto.getUsername())) {
                int adminList = adminService.checkUserName(dto.getUsername());
                if (adminList != 0) {
                    throw new ServiceException("账户已存在");
                }
            }
            // 账户类型
            admin.setIsSuperAdmin(dto.getIsSuperAdmin());
            admin.setUsername(dto.getUsername());
            adminService.updateByIdQ(admin);
            // 删除管理员菜单
            menuService.remove(new QueryWrapper<TAdminMenu>().eq("admin_id", dto.getId()));
            // 添加管理员菜单
            LinkedList<TAdminMenu> linkedList = new LinkedList<>();
            for (int i = 0; i < dto.getMenu().length; i++) {
                TAdminMenu menu = new TAdminMenu();
                menu.setAdminId(admin.getId());
                menu.setMenuId(dto.getMenu()[i]);
                linkedList.add(menu);
            }
            // 保存管理员菜单
            menuService.saveBatch(linkedList);
            // 删除缓存
            redisUtil.hdel(RedisKeyPrefix.SyStemAdminMenu, admin.getId().toString());
            redisUtil.hdel(RedisKeyPrefix.SystemAdminMenuIds, admin.getId().toString());
            return ResponseUtil.ok();
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
