package com.mh.jishi.controller;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.code.kaptcha.Producer;
import com.mh.jishi.entity.TAdmin;
import com.mh.jishi.entity.TSystemMenu;
import com.mh.jishi.service.TAdminService;
import com.mh.jishi.service.system.AdminSystemMenuService;
import com.mh.jishi.util.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @Author Lizr
 * @Description 后台登录控制层
 * @CreateTime 2021-12-28 下午 2:55
 **/
@RestController
@RequestMapping(value = "/admin/auth")
@SuppressWarnings("all")
public class AdminAuthController {
    private final Logger log = LoggerFactory.getLogger(AdminAuthController.class);

    private final TAdminService adminService;

    private final Producer kaptchaProducer;

    private final RedisUtil redisUtil;

    private final AdminSystemMenuService systemMenuService;

    private final ApplicationContext context;

    private HashMap<String, String> systemPermissionsMap = null;

    public AdminAuthController(TAdminService adminService, Producer kaptchaProducer, RedisUtil redisUtil, AdminSystemMenuService systemMenuService, ApplicationContext context) {
        this.adminService = adminService;
        this.kaptchaProducer = kaptchaProducer;
        this.redisUtil = redisUtil;
        this.systemMenuService = systemMenuService;
        this.context = context;
    }

    /**
     * 生产base64验证码图片
     *
     * @param request
     * @return
     */
    @GetMapping("/kaptcha")
    public Object kaptcha(HttpServletRequest request) {
        String kaptcha = doKaptcha(request);
        if (kaptcha != null) {
            return ResponseUtil.ok(kaptcha);
        }
        return ResponseUtil.fail();
    }

    /**
     * 生产验证码方法
     *
     * @param request
     * @return
     */
    private String doKaptcha(HttpServletRequest request) {
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        // 使用redis 存储验证码，也可以使用一个 静态map缓存
        log.info("后台登录 验证码:  {}", text);
        redisUtil.set("kaptcha:".concat(IpUtil.getIpAddr(request)), text, (60 * 5));

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpeg", outputStream);
            String base64 = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            return "data:image/jpeg;base64," + base64.replaceAll("\r\n", "");
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 重置登录密码
     *
     * @param json
     * @return
     */
    @RequiresAuthentication
    @PostMapping("/resetPswd")
    public Object resetPswd(@RequestBody String json) {
        JSONObject jsonObject = JSONUtil.parseObj(json);
        String passwod = jsonObject.getStr("password");
        if (org.apache.commons.lang3.StringUtils.isBlank(passwod)) {
            return ResponseUtil.fail(401, "请输入密码");
        }
        Subject currentUser = SecurityUtils.getSubject();
        TAdmin admin = (TAdmin) currentUser.getPrincipal();
        admin.setPassword(passwod);
        adminService.updateByIdQ(admin);
        currentUser.logout();
        return ResponseUtil.ok();
    }

    /*
     * 后台管理员登录
     *  { username : value, password : value }
     */
    @PostMapping("/login")
    public Object login(@RequestBody String body, HttpServletRequest request) {
        String username = JacksonUtil.parseString(body, "username");
        String password = JacksonUtil.parseString(body, "password");
        String code = JacksonUtil.parseString(body, "code");

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return ResponseUtil.badArgument();
        }
        if (StringUtils.isEmpty(code)) {
            return ResponseUtil.fail("验证码不能为空");
        }
        Object kaptcha = redisUtil.get("kaptcha:".concat(IpUtil.getIpAddr(request)));

        if (kaptcha == null) {
            // 验证码已过期 设置随机值 给 下一步判断 返回验证码 不正确
            kaptcha = "431024";
        }
        if (Objects.requireNonNull(code).compareToIgnoreCase(kaptcha.toString()) != 0) {
            return ResponseUtil.fail(401, "验证码错误", doKaptcha(request));
        }

        Subject currentUser = SecurityUtils.getSubject();
        try {
            currentUser.login(new UsernamePasswordToken(username, password));
        } catch (UnknownAccountException uae) {
            log.error("后台登录, 账户或密码错误; 登录信息：【{}】", body);
            return ResponseUtil.fail(501, "账户或密码错误", doKaptcha(request));
        } catch (LockedAccountException lae) {
            log.error("后台登录, 账户已禁用; 登录信息：【{}】", body);
            return ResponseUtil.fail(501, "账户已禁用");

        } catch (AuthenticationException ae) {
            log.error("后台登录, 认证失败; 登录信息：【{}】", body);
            return ResponseUtil.fail(501, "认证失败");
        }

        currentUser = SecurityUtils.getSubject();
        TAdmin admin = (TAdmin) currentUser.getPrincipal();
        admin.setLastLoginIp(IpUtil.getIpAddr(request));
        admin.setLastLoginTime(LocalDateTime.now());
        adminService.updateByIdQ(admin);

        log.info("后台登录， 成功");

        // userInfo
        Map<String, Object> adminInfo = new HashMap<String, Object>();
        adminInfo.put("nickName", admin.getUsername());
        adminInfo.put("avatar", admin.getAvatar());

        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("token", currentUser.getSession().getId());
        result.put("adminInfo", adminInfo);
        return ResponseUtil.ok(result);
    }

    /*
     * 退出登录
     */
    @RequiresAuthentication
    @PostMapping("/logout")
    public Object logout() {
        Subject currentUser = SecurityUtils.getSubject();
        log.info("后台退出登录成功！");
        currentUser.logout();
        return ResponseUtil.ok();
    }
    @RequestMapping("/unlogin")
    public Object unlogin() {
        return ResponseUtil.unlogin();
    }

    /**
     * 重寫info接口
     * @return
     */
    @RequiresAuthentication
    @GetMapping("/info")
    public Object info() throws IOException, ExecutionException, InterruptedException {
        Subject currentUser = SecurityUtils.getSubject();
        final TAdmin admin = (TAdmin) currentUser.getPrincipal();

        Map<String, Object> data = new HashMap<>();
        Future<Object> task1 = ThreadUtil.execAsync(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                data.put("id", admin.getId());
                data.put("name", admin.getUsername());
                data.put("avatar", admin.getAvatar());
                boolean checkIsSuperAdmin = admin.getIsSuperAdmin();
                data.put("isSuperAdmin", checkIsSuperAdmin);
                return null;
            }
        });
        Future<Object> task2 = ThreadUtil.execAsync(new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                // 查詢 管理員菜單ID
                List<Integer> menuIds = adminService.getAdminMenuIds(admin.getId());
                data.put("menuIds", menuIds);
                return null;
            }
        });

        Future<Object> task3 = ThreadUtil.execAsync(new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                // 查询管理员菜单具体内容
                List<TSystemMenu> list = systemMenuService.queryMenuByAdmin(admin);
                data.put("menu", list);
                return null;
            }
        });

        /*
        控制接口权限
        Integer[] roleIds = admin.getRoleIds();
         Set<String> roles = roleService.queryByIds(roleIds);
         Set<String> permissions = permissionService.queryByRoleIds(roleIds);
         data.put("roles", roles);
         NOTE
         // 这里需要转换perms结构，因为对于前端而已API形式的权限更容易理解
         data.put("perms", toApi(permissions));
        //  查詢 管理員角色菜單
        QueryWrapper<TRoleMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleIds[0]);
        Map<String, Object> menu = LitemallRoleMenuService.getMap(queryWrapper);
        if (menu == null) {
            data.put("menu", new String[0]);
        } else {
            ObjectMapper mapper = new ObjectMapper();
            String mStr = menu.get("menu").toString();
            String[] m = new String[0];
            if (org.apache.commons.lang3.StringUtils.isNotBlank(mStr)) {
                m = mapper.readValue(mStr, String[].class);
            }
            data.put("menu", m);
        }
        */
        task1.get();
        task2.get();
        task3.get();
        return ResponseUtil.ok(data);
    }

    private Collection<String> toApi(Set<String> permissions) {
        if (systemPermissionsMap == null) {
            systemPermissionsMap = new HashMap<>();
            final String basicPackage = "org.linlinjava.litemall.admin";
            List<Permission> systemPermissions = PermissionUtil.listPermission(context, basicPackage);
            for (Permission permission : systemPermissions) {
                String perm = permission.getRequiresPermissions().value()[0];
                String api = permission.getApi();
                systemPermissionsMap.put(perm, api);
            }
        }

        Collection<String> apis = new HashSet<>();
        for (String perm : permissions) {
            String api = systemPermissionsMap.get(perm);
            apis.add(api);
            if (perm.equals("*")) {
                apis.clear();
                apis.add("*");
                return apis;
            }
        }
        return apis;
    }

}


