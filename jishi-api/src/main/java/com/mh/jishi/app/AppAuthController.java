package com.mh.jishi.app;

import com.mh.jishi.annotation.ApiDesc;
import com.mh.jishi.annotation.LoginUser;
import com.mh.jishi.entity.TUser;
import com.mh.jishi.service.CaptchaCodeManager;
import com.mh.jishi.service.TUserService;
import com.mh.jishi.service.UserTokenManager;
import com.mh.jishi.util.*;
import com.mh.jishi.vo.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Lizr
 * @Description 登录注册控制层
 * @CreateTime 2021-12-27 下午 5:33
 **/
@RestController
@RequestMapping(value = "/api/auth")
@SuppressWarnings("all")
public class AppAuthController {
    private final Logger logger = LoggerFactory.getLogger(AppAuthController.class);

    @Autowired
    private TUserService userService;

    @Resource
    private RedisUtil redisUtil;

    @Autowired
    private CaptchaCodeManager captchaCodeManager;
    /**
     * 账号登录
     *
     * @param body    请求内容，{ username: xxx, password: xxx }
     * @param request 请求对象
     * @return 登录结果
     */
    @ApiDesc(methodDesc = "用户登录")
    @PostMapping("login")
    public Object login(@RequestBody String body, HttpServletRequest request) {
        String mobile = JacksonUtil.parseString(body, "mobile");
        String password = JacksonUtil.parseString(body, "password");
        if (mobile == null || password == null) {
            return ResponseUtil.badArgument();
        }

        int count = userService.queryCountByMobile(mobile);
        TUser user = null;
        if (count > 1) {
            return ResponseUtil.serious();
        } else if (count == 0) {
            return ResponseUtil.fail(WxResponseCode.AUTH_INVALID_ACCOUNT, "手机号不存在");
        } else {
            user = userService.queryByMobile(mobile);
        }

        if (!password.equals(user.getPassword())) {
            return ResponseUtil.fail(WxResponseCode.AUTH_INVALID_ACCOUNT, "账号或者密码错误");
        }
        // 验证是否禁用
        if(user.getStatus().equals(1)){
            return ResponseUtil.fail(WxResponseCode.AUTH_INVALID_ACCOUNT, "您账号已被禁用");
        }

        // 更新登录情况
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(IpUtil.getIpAddr(request));
        if (userService.updateByIdAndUpdateDate(user) == false) {
            return ResponseUtil.updatedDataFailed();
        }

        // userInfo
        UserInfo userInfo = new UserInfo();
        userInfo.setNickName(user.getUsername());
        userInfo.setAvatarUrl(user.getAvatar());

        // token
        String token = UserTokenManager.generateToken(user.getId());

        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("token", token);
        result.put("userInfo", userInfo);
        return ResponseUtil.ok("登陆成功",result);
    }





    /**
     * 账号注册
     *
     * @param body    请求内容
     *                {
     *                password: xxx,
     *                mobile: xxx
     *                code: xxx
     *                }
     *                其中code是手机验证码，目前还不支持手机短信验证码
     * @param request 请求对象
     * @return 登录结果
     * 成功则
     * {
     * errno: 0,
     * errmsg: '成功',
     * data:
     * {
     * token: xxx,
     * tokenExpire: xxx,
     * userInfo: xxx
     * }
     * }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @ApiDesc(methodDesc = "用户注册")
    @PostMapping("register")
    @Transactional(rollbackFor = Exception.class)
    public Object register(@RequestBody String body, HttpServletRequest request) {
        String password = JacksonUtil.parseString(body, "password");
        String mobile = JacksonUtil.parseString(body, "mobile");
        String code = JacksonUtil.parseString(body, "code");

        if ( StringUtils.isEmpty(password) || StringUtils.isEmpty(mobile)  || StringUtils.isEmpty(code)) {
            return ResponseUtil.badArgument();
        }

        if (!RegexUtil.isMobileSimple(mobile)) {
            return ResponseUtil.fail(WxResponseCode.AUTH_INVALID_MOBILE, "手机号格式不正确");
        }

        //判断验证码是否正确
        String cacheCode = captchaCodeManager.getCachedCaptcha(mobile);
        if (cacheCode == null || cacheCode.isEmpty() || !cacheCode.equals(code)) {
            return ResponseUtil.fail(WxResponseCode.AUTH_CAPTCHA_UNMATCH, "验证码错误");
        }

        int count = userService.queryCountByMobile(mobile);
        if (count > 0) {
            return ResponseUtil.fail(WxResponseCode.AUTH_MOBILE_REGISTERED, "手机号已注册");
        }
        TUser user = new TUser();
        user.setUsername(mobile); // 用户名默认用手机号
        user.setPassword(password); // 密码
        user.setMobile(mobile);
        String avatart = null;
        user.setAvatar(avatart == null ? "https://yanxuan.nosdn.127.net/80841d741d7fa3073e0ae27bf487339f.jpg?imageView&quality=90&thumbnail=64x64" : avatart);
        int fl = mobile.length() - 4;
        String nickname = "用户" + mobile.substring(mobile.length() - 4, mobile.length());// 昵称 使用 用户 + 手机号尾数四位
        user.setNickname(nickname);
        user.setBirthday(LocalDate.now());
        user.setGender(1); // 性别
        user.setStatus(0); // 状态
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(IpUtil.getIpAddr(request));
        userService.save(user);
        // userInfo
        UserInfo userInfo = new UserInfo();
        userInfo.setNickName(nickname);
        userInfo.setAvatarUrl(user.getAvatar());
        // token
        String token = UserTokenManager.generateToken(user.getId());

        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("token", token);
        result.put("userInfo", userInfo);
        return ResponseUtil.ok("注册成功", result);
    }


    /**
     * 账号密码重置
     *
     * @param body    请求内容
     *                {
     *                password: xxx,
     *                mobile: xxx
     *                code: xxx
     *                }
     *                其中code是手机验证码，目前还不支持手机短信验证码
     * @param request 请求对象
     * @return 登录结果
     * 成功则 { errno: 0, errmsg: '成功' }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @ApiDesc(methodDesc = "用户重置登录密码")
    @PostMapping("reset")
    public Object reset(@RequestBody String body, HttpServletRequest request) {
        String password = JacksonUtil.parseString(body, "password");
        String mobile = JacksonUtil.parseString(body, "mobile");
        String code = JacksonUtil.parseString(body, "code");

        if (mobile == null || code == null || password == null) {
            return ResponseUtil.badArgument();
        }
        if (!RegexUtil.isMobileSimple(mobile)) {
            return ResponseUtil.fail(WxResponseCode.AUTH_INVALID_MOBILE, "手机号格式不正确");
        }
        //判断验证码是否正确
        String cacheCode = captchaCodeManager.getCachedCaptcha(mobile);
        if (cacheCode == null || cacheCode.isEmpty() || !cacheCode.equals(code))
            return ResponseUtil.fail(WxResponseCode.AUTH_CAPTCHA_UNMATCH, "验证码错误");

        int count = userService.queryCountByMobile(mobile);
        TUser user = null;
        if (count > 1) {
            return ResponseUtil.serious();
        } else if (count == 0) {
            return ResponseUtil.fail(WxResponseCode.AUTH_MOBILE_UNREGISTERED, "手机号未注册");
        } else {
            user = userService.queryByMobile(mobile);
        }
        user.setPassword(password);

        if (userService.updateByIdAndUpdateDate(user) == false) {
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.okUpd();
    }


    /**
     * 账号手机号码重置
     *
     * @param body    请求内容
     *                {
     *                oldMobile: xxx,
     *                newMobile: xxx
     *                code: xxx
     *                }
     *                其中code是手机验证码，目前还不支持手机短信验证码
     * @param request 请求对象
     * @return 登录结果
     * 成功则 { errno: 0, errmsg: '成功' }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @ApiDesc(methodDesc = "用户重置手机号")
    @PostMapping("resetPhone")
    public Object resetPhone(@LoginUser Integer userId, @RequestBody String body, HttpServletRequest request) {
        if(userId == null){
            return ResponseUtil.unlogin();
        }
        // 旧手机号
        String oldMobile = JacksonUtil.parseString(body, "oldMobile");
        // 新手机号
        String newMobile = JacksonUtil.parseString(body, "newMobile");
        // 验证码
        String code = JacksonUtil.parseString(body, "code");

        if (oldMobile == null || code == null || newMobile == null) {
            return ResponseUtil.badArgument();
        }
        if (!RegexUtil.isMobileSimple(oldMobile)) {
            return ResponseUtil.fail(WxResponseCode.AUTH_INVALID_MOBILE, "手机号格式不正确");
        }
        if (!RegexUtil.isMobileSimple(newMobile)) {
            return ResponseUtil.fail(WxResponseCode.AUTH_INVALID_MOBILE, "手机号格式不正确");
        }
        //判断验证码是否正确 使用旧手机号接受验证码
        String cacheCode = captchaCodeManager.getCachedCaptcha(oldMobile);
        if (cacheCode == null || cacheCode.isEmpty() || !cacheCode.equals(code))
            return ResponseUtil.fail(WxResponseCode.AUTH_CAPTCHA_UNMATCH, "验证码错误");
        // 判断新手机号是否已经注册
        int count = userService.queryCountByMobile(newMobile);
        TUser user = null;
        if (count > 1) {
            return ResponseUtil.fail(WxResponseCode.AUTH_MOBILE_REGISTERED, "新手机号已注册");
        }
        // 使用用户id查询
        user = userService.getById(userId);
        // 更改手机号
        user.setMobile(newMobile);
        if (userService.updateByIdAndUpdateDate(user) == false) {
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.okUpd();
    }
    /**
     * 保存用户个人信息
     * @param avatar 头像
     * @param nickName 昵称
     * @param gender 性别
     * @param birthday 生日
     */
    @ApiDesc(methodDesc = "用户修改个人信息")
    @PostMapping("/saveUserInfo")
    public Object saveUserInfo(@LoginUser Integer userId, @RequestBody String body){
        if(userId == null){
            return ResponseUtil.unlogin();
        }
        String avatar = JacksonUtil.parseString(body, "avatar");
        String nickName = JacksonUtil.parseString(body, "nickName");
        Integer gender = Integer.parseInt(JacksonUtil.parseString(body, "gender"));
        String birthdayStr = JacksonUtil.parseString(body, "birthday");

        LocalDate birthdayDate = LocalDate.parse(birthdayStr);
        TUser user = userService.getById(userId);
        if(org.apache.commons.lang3.StringUtils.isNoneBlank(avatar)){
            user.setAvatar(avatar);
        }
        if(org.apache.commons.lang3.StringUtils.isNoneBlank(nickName)){
            user.setNickname(nickName);
        }
        if(gender != null){
            user.setGender(gender);
        }
        if(birthdayDate != null){
            user.setBirthday(birthdayDate);
        }
        userService.updateByIdAndUpdateDate(user);
        return ResponseUtil.ok("保存成功", null);

    }

    /**
     * 微信登录
     *
     * @param wxLoginInfo 请求内容，{ code: xxx, userInfo: xxx }
     * @param request     请求对象
     * @return 登录结果
     */
//    @PostMapping("login_by_weixin")
//    public Object loginByWeixin(@RequestBody WxLoginInfo wxLoginInfo, HttpServletRequest request) {
//        String code = wxLoginInfo.getCode();
//        UserInfo userInfo = wxLoginInfo.getUserInfo();
//        if (code == null || userInfo == null) {
//            return ResponseUtil.badArgument();
//        }
//
//        String sessionKey = null;
//        String openId = null;
//        try {
//            WxMaJscode2SessionResult result = this.wxService.getUserService().getSessionInfo(code);
//            sessionKey = result.getSessionKey();
//            openId = result.getOpenid();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if (sessionKey == null || openId == null) {
//            return ResponseUtil.fail();
//        }
//
//        LitemallUser user = userService.queryByOid(openId);
//        if (user == null) {
//            user = new LitemallUser();
//            user.setUsername(openId);
//            user.setPassword(openId);
//            user.setWeixinOpenid(openId);
//            user.setAvatar(userInfo.getAvatarUrl());
//            user.setNickname(userInfo.getNickName());
//            user.setGender(userInfo.getGender());
//            user.setUserLevel((byte) 0);
//            user.setStatus((byte) 0);
//            user.setLastLoginTime(LocalDateTime.now());
//            user.setLastLoginIp(IpUtil.getIpAddr(request));
//            user.setSessionKey(sessionKey);
//
//            userService.add(user);
//
//            // 新用户发送注册优惠券
//            couponAssignService.assignForRegister(user.getId());
//        } else {
//            user.setLastLoginTime(LocalDateTime.now());
//            user.setLastLoginIp(IpUtil.getIpAddr(request));
//            user.setSessionKey(sessionKey);
//            if (userService.updateById(user) == 0) {
//                return ResponseUtil.updatedDataFailed();
//            }
//        }
//
//        // token
//        String token = UserTokenManager.generateToken(user.getId());
//
//        Map<Object, Object> result = new HashMap<Object, Object>();
//        result.put("token", token);
//        result.put("userInfo", userInfo);
//        return ResponseUtil.ok(result);
//    }

}
