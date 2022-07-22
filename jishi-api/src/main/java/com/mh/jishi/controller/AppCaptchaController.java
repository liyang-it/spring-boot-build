package com.mh.jishi.controller;

import com.mh.jishi.annotation.ApiDesc;
import com.mh.jishi.service.CaptchaCodeManager;
import com.mh.jishi.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Lizr
 * @Description 验证码控制层
 * @CreateTime 2021-12-28 下午 1:40
 **/
@RequestMapping("/api/auth")
@RestController
public class AppCaptchaController {

    @Autowired
    private CaptchaCodeManager captchaCodeManager;

    /**
     * 请求注册验证码
     * 这里需要一定机制防止短信验证码被滥用
     * @param body 手机号码 { mobile }
     * @return
     */
    @ApiDesc(methodDesc = "获取验证码")
    @PostMapping("captcha")
    public Object registerCaptcha(@RequestBody String body) {
        String phoneNumber = JacksonUtil.parseString(body, "mobile");
        if (StringUtils.isEmpty(phoneNumber)) {
            return ResponseUtil.badArgument();
        }
        // 手机号校验
        if (!RegexUtil.isMobileSimple(phoneNumber)) {
            return ResponseUtil.fail(401, "手机号格式不正确");
        }
        // 按照长度生成字符串
        String code = "1234"; // CharUtil.getRandomNum(6);
        boolean successful = captchaCodeManager.addToCache(phoneNumber, code);
        if (!successful) {
            return ResponseUtil.fail(WxResponseCode.AUTH_CAPTCHA_FREQUENCY, "验证码未超时1分钟，不能发送");
        }
        // TODO 这里要改为对接短信服务
        return ResponseUtil.ok(code);
    }
}
