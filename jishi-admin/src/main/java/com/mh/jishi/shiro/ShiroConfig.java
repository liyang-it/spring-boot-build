package com.mh.jishi.config;

import com.mh.jishi.shiro.AdminAuthorizingRealm;
import com.mh.jishi.shiro.AdminWebSessionManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * shiro权限
 */
@Configuration
public class ShiroConfig {

    @Bean
    public Realm realm() {
        return new AdminAuthorizingRealm();
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        filterChainDefinitionMap.put("/admin/auth/kaptcha", "anon");
        filterChainDefinitionMap.put("/admin/auth/login", "anon");
        filterChainDefinitionMap.put("/admin/auth/401", "anon");
        filterChainDefinitionMap.put("/admin/auth/index", "anon");
        filterChainDefinitionMap.put("/admin/auth/403", "anon");
        filterChainDefinitionMap.put("/admin/auth/unlogin", "anon");
        filterChainDefinitionMap.put("/admin/index/*", "anon");

        filterChainDefinitionMap.put("/admin/**", "authc");
        // 如果请求没有登录信息，重定向 这个接口
        shiroFilterFactoryBean.setLoginUrl("/admin/auth/unlogin");
        shiroFilterFactoryBean.setSuccessUrl("/admin/auth/index");
        shiroFilterFactoryBean.setUnauthorizedUrl("/admin/auth/403");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public SessionManager sessionManager() {
		AdminWebSessionManager adminWebSessionManager = new AdminWebSessionManager();
		adminWebSessionManager.setDeleteInvalidSessions(true);
        // 当会话超过指定空闲时间(毫秒)后标记为过期，尽量不要设置-1.如果用户量大的话，内存会占用很大, 设置空闲时间30分钟
        int timeOutHours = 1000 * (60 * 30);
        adminWebSessionManager.setGlobalSessionTimeout(timeOutHours);
        // 如果会话验证时为过期或者无效无效则删除这个会话，搭配setGlobalSessionTimeout使用
        adminWebSessionManager.setDeleteInvalidSessions(true);
        // 开启Shiro定时检测无效过期会话并且删除，一定要配置，因为会话过期还是会停留在内存上，时间长了肯定会内存溢出
        adminWebSessionManager.setSessionValidationSchedulerEnabled(true);
        // Shiro定时检测的间隔时间(毫秒)
        adminWebSessionManager.setSessionValidationInterval(timeOutHours);
		return adminWebSessionManager;
    }

    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm());
        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor =
                new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager((SecurityManager) securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * 去掉代理，不然AOP会出现两次代理<br>
     * 参考博客：https://blog.csdn.net/weixin_43950014/article/details/124860288
     @Bean
     @DependsOn("lifecycleBeanPostProcessor") public static DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
     DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
     creator.setProxyTargetClass(true);
     return creator;
     }
     */
}
