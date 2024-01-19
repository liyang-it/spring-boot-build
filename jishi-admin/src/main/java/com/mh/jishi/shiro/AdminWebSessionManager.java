package com.mh.jishi.shiro;

import com.mh.jishi.service.TAdminService;
import com.mh.jishi.service.TPermissionService;
import com.mh.jishi.service.TRoleService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

public class AdminWebSessionManager extends DefaultWebSessionManager {
    @Autowired
    @Lazy
    private TAdminService adminService;
    @Autowired
    @Lazy
    private TRoleService roleService;
    @Autowired
    @Lazy
    private TPermissionService permissionService;
    /**
     * 请求头参数
     */
    public static final String LOGIN_TOKEN_KEY = "token";
    private static final String REFERENCED_SESSION_ID_SOURCE = "Stateless request";


    public AdminWebSessionManager() {
        super();
        this.setGlobalSessionTimeout(MILLIS_PER_HOUR * 6);
        this.setSessionIdUrlRewritingEnabled(false);

    }

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        String id = WebUtils.toHttp(request).getHeader(LOGIN_TOKEN_KEY);
        if (StringUtils.isNotBlank(id)) {
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, "url");
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            //禁止在url上拼接SessionId
            request.setAttribute(ShiroHttpServletRequest.SESSION_ID_URL_REWRITING_ENABLED, this.isSessionIdUrlRewritingEnabled());
            return id;
        } else {
            return super.getSessionId(request, response);
        }
    }
    protected boolean checkToken(String token){
        return true;
    }
}
