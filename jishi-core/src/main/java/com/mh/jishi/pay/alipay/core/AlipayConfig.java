/*
 * Copyright (c) 2020.
 * 东莞市梦幻网络科技有限公司/东莞星轨科技有限公司 All rights reserved.
 * 作者：verky
 * 邮箱：nhangno12@163.com
 * 日期：2020-04-17 11:33
 * ---
 * 类名：WXPayController
 * 路径：/Users/verky/custom_project/edu/api/src/main/java/com/live/edu/app/controller/WXPayController.java
 * ---
 * 版权所有，侵权必究！
 */
package com.mh.jishi.pay.alipay.core;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Description 支付宝自定义配置类
 * @Author   Lizr
 * @Date 2020/05/12 21:03
 */
@Data
@Component
public class AlipayConfig {

    /**
     * 支付宝gatewayUrl
     */
    private String gatewayUrl;
    /**
     * 商户应用id
     */
    private String appid;
    /**
     * RSA私钥，用于对商户请求报文加签
     */
    private String appPrivateKey;
    /**
     * 支付宝RSA公钥，用于验签支付宝应答
     */
    private String alipayPublicKey;
    /**
     * 签名类型
     */
    private String signType = "RSA2";
    /**
     * 格式
     */
    private String formate = "json";
    /**
     * 编码
     */
    private String charset = "utf-8";
    /**
     * 同步地址
     */
    private String returnUrl;
    /**
     * 异步地址
     */
    private String notifyUrl;
    /**
     * 最大查询次数
     */
    private static int maxQueryRetry = 5;
    /**
     * 查询间隔（毫秒）
     */
    private static long queryDuration = 5000;
    /**
     * 最大撤销次数
     */
    private static int maxCancelRetry = 3;
    /**
     * 撤销间隔（毫秒）
     */
    private static long cancelDuration = 3000;

    @Bean
    public AlipayClient alipayClient(){
        return new DefaultAlipayClient(this.getGatewayUrl(),
                this.getAppid(),
                this.getAppPrivateKey(),
                this.getFormate(),
                this.getCharset(),
                this.getAlipayPublicKey(),
                this.getSignType());
    }
}
