package com.mh.jishi.shiro;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mh.jishi.entity.TAdmin;
import com.mh.jishi.mapper.TAdminMapper;
import com.mh.jishi.service.TAdminService;
import com.mh.jishi.service.TPermissionService;
import com.mh.jishi.service.TRoleService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * 下面三个Autowired注解需要配合Lazy注解使用，否则会导致这三个service相关的事务失效。
 * https://gitee.com/linlinjava/litemall/issues/I3I94X#note_4809495
 */
public class AdminAuthorizingRealm extends AuthorizingRealm {

    @Autowired
    @Lazy
    private TAdminService adminService;

    @Autowired
    @Lazy
    private TRoleService roleService;

    @Autowired
    @Lazy
    private TPermissionService permissionService;

    @Resource
    @Lazy
    private TAdminMapper adminMapper;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // 查询用户权限
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }

        TAdmin admin = (TAdmin) getAvailablePrincipal(principals);
        Integer[] roleIds = admin.getRoleIds();
        Set<String> roles = roleService.queryByIds(roleIds);
        Set<String> permissions = permissionService.queryByRoleIds(roleIds);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(roles);
        info.setStringPermissions(permissions);
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // TODO 这里是后台账户密码登录逻辑
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        String password = new String(upToken.getPassword());

        if (StringUtils.isEmpty(username)) {
            throw new AccountException("用戶名不能為空");
        }
        if (StringUtils.isEmpty(password)) {
            throw new AccountException("密碼不能為空");
        }

        List<TAdmin> adminList = adminMapper.selectList(new QueryWrapper<TAdmin>().eq("username", username));
        Assert.state(adminList.size() < 2, "同一个用户名存在两个账户");
        if (adminList.size() == 0) {
            throw new UnknownAccountException("找不到用戶（" + username + "）的账号信息");
        }
        TAdmin admin = adminList.get(0);

        if (!password.equals(admin.getPassword())) {
            throw new UnknownAccountException("密码错误");
        }
        return new SimpleAuthenticationInfo(admin, password, getName());
    }
}
