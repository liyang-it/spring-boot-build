package com.mh.jishi.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mh.jishi.annotation.RequiresPermissionsDesc;
import com.mh.jishi.entity.TAdmin;
import com.mh.jishi.entity.TRole;
import com.mh.jishi.entity.TRoleMenu;
import com.mh.jishi.service.TAdminService;
import com.mh.jishi.service.TPermissionService;
import com.mh.jishi.service.TRoleMenuService;
import com.mh.jishi.service.TRoleService;
import com.mh.jishi.util.Permission;
import com.mh.jishi.util.PermissionUtil;
import com.mh.jishi.util.ResponseUtil;
import com.mh.jishi.vo.PermVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 系统管理 - 角色管理
 */
@RestController
@RequestMapping("/admin/role")
@Validated
@SuppressWarnings("all")
public class AdminRoleController {
    private final Log logger = LogFactory.getLog(AdminRoleController.class);

    @Autowired
    private TRoleService roleService;
    @Autowired
    private TPermissionService permissionService;
    @Autowired
    private TAdminService adminService;

    @Autowired
    private TRoleMenuService TRoleMenuService;

    @RequiresPermissions("admin:role:list")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "角色查询")
    @GetMapping("/list")
    public Object list(String name,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort,
                       @RequestParam(defaultValue = "desc") String order) {
        IPage<TRole> roleList = roleService.querySelective(name, page, limit, sort, order);
        return ResponseUtil.ok(roleList);
    }

    @RequiresPermissions("admin:role:read")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "角色详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        TRole role = roleService.getById(id);
        return ResponseUtil.ok(role);
    }


    private Object validate(TRole role) {
        String name = role.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.badArgument();
        }

        return null;
    }

    @RequiresPermissions("admin:role:create")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "角色添加")
    @PostMapping("/create")
    public Object create(@RequestBody TRole role) {
        Object error = validate(role);
        if (error != null) {
            return error;
        }

        if (roleService.checkExist(role.getName())) {
            return ResponseUtil.fail(501, "角色已经存在");
        }

        roleService.add(role);

        return ResponseUtil.ok(role);
    }

    @RequiresPermissions("admin:role:update")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "角色编辑")
    @PostMapping("/update")
    public Object update(@RequestBody TRole role) {
        Object error = validate(role);
        if (error != null) {
            return error;
        }
        role.setUpdateTime(LocalDateTime.now());
        roleService.updateById(role);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:role:delete")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "角色删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody TRole role) {
        Integer id = role.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }

        // 如果当前角色所对应管理员仍存在，则拒绝删除角色。
        List<TAdmin> adminList = adminService.all();
        for (TAdmin admin : adminList) {
            Integer[] roleIds = admin.getRoleIds();
            for (Integer roleId : roleIds) {
                if (id.equals(roleId)) {
                    return ResponseUtil.fail(501, "当前角色已有管理员使用，请先删除对应管理员");
                }
            }
        }
        TRole tRole = roleService.getById(id);
        if(tRole != null){
            tRole.setUpdateTime(LocalDateTime.now());
            roleService.updateById(tRole);
        }

        return ResponseUtil.ok();
    }


    @Autowired
    private ApplicationContext context;
    private List<PermVo> systemPermissions = null;
    private Set<String> systemPermissionsString = null;

    private List<PermVo> getSystemPermissions() {
        final String basicPackage = "org.linlinjava.litemall.admin";
        if (systemPermissions == null) {
            List<Permission> permissions = PermissionUtil.listPermission(context, basicPackage);
            systemPermissions = PermissionUtil.listPermVo(permissions);
            systemPermissionsString = PermissionUtil.listPermissionString(permissions);
        }
        return systemPermissions;
    }

    private Set<String> getAssignedPermissions(Integer roleId) {
        // 这里需要注意的是，如果存在超级权限*，那么这里需要转化成当前所有系统权限。
        // 之所以这么做，是因为前端不能识别超级权限，所以这里需要转换一下。
        Set<String> assignedPermissions = null;
        if (permissionService.checkSuperPermission(roleId)) {
            getSystemPermissions();
            assignedPermissions = systemPermissionsString;
        } else {
            assignedPermissions = permissionService.queryByRoleId(roleId);
        }

        return assignedPermissions;
    }


    /**
     * 查询角色菜單
     * @return
     */
    @GetMapping("queryRoleMenu")
    public Object queryAdminMenu(@RequestParam("roleId") Integer roleId) throws IOException {
        HashMap<String, Object> map = new HashMap<>();
        QueryWrapper<TRoleMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        Map<String, Object> menu = TRoleMenuService.getMap(queryWrapper);
        if(menu == null){
            map.put("menu", new String[0]);
        }else {
            ObjectMapper mapper = new ObjectMapper();
            String mStr = menu.get("menu").toString();
            String[] m = new String[0];
            if (org.apache.commons.lang3.StringUtils.isNotBlank(mStr)) {
                m = mapper.readValue(mStr, String[].class);
            }
            map.put("menu", m);
        }
        return ResponseUtil.ok(map);
    }

    /**
     * 保存角色页面菜单
     * @return
     */
    @PostMapping("saveRoleMenu")
    @RequiresPermissions("admin:role:permission:get")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色菜单管理"}, button = "保存角色菜单")
    public Object queryAdminMenu(@RequestBody @Valid TRoleMenu menu){
        QueryWrapper<TRoleMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", menu.getRoleId());
        TRoleMenu query = TRoleMenuService.getOne(queryWrapper);
        if(query == null){
            TRoleMenuService.save(menu);
        }else{
            query.setMenu(menu.getMenu());
            TRoleMenuService.updateById(query);
        }
        return ResponseUtil.ok();
    }

    /**
     * 管理员的权限情况
     *
     * @return 系统所有权限列表和管理员已分配权限
     */
    @RequiresPermissions("admin:role:permission:get")
    @RequiresPermissionsDesc(menu = {"系统管理", "角色管理"}, button = "权限详情")
    @GetMapping("/permissions")
    public Object getPermissions(Integer roleId) {
        List<PermVo> systemPermissions = getSystemPermissions();
        Set<String> assignedPermissions = getAssignedPermissions(roleId);

        Map<String, Object> data = new HashMap<>();
        data.put("systemPermissions", systemPermissions);
        data.put("assignedPermissions", assignedPermissions);
        return ResponseUtil.ok(data);
    }


}
