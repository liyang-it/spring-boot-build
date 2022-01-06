/*
 * Copyright (c) 2020.
 * 东莞市梦幻网络科技有限公司/东莞星轨科技有限公司 All rights reserved.
 * 作者：verky
 * 邮箱：nhangno12@163.com
 * 日期：2020-04-14 19:02
 * ---
 * 类名：AliPayCreateOrderUtil
 * 路径：/Users/verky/custom_project/edu/common/src/main/java/com/live/edu/common/third/util/AliPayCreateOrderUtil.java
 * ---
 * 版权所有，侵权必究！
 */

package com.mh.jishi.pay.alipay.core;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;

/**
 * 阿里云生成订单号工具类
 *
 * @author Lizr
 * @create 2020-04-14 18:59
 */

public class AliPayCreateOrderUtil {

    public static String createOrderNo(String gateWayUrl, String appId, String privateKey, String charSet, String pulbicKey, String signgType, String callBack, AlipayTradeAppPayModel model) throws AlipayApiException {
        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gateWayUrl, appId, privateKey, "json", charSet, pulbicKey, signgType);

        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
//        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
//        model.setBody("我是测试数据");
//        model.setSubject("App支付测试Java");
//        model.setOutTradeNo("ali0987655555test");
//        model.setTimeoutExpress("30m");
//        model.setTotalAmount("0.01");
//        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl(callBack);
        //这里和普通的接口调用不同，使用的是sdkExecute
        AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
        return response.getBody();//就是orderString 可以直接给客户端请求，无需再做处理。
    }

}
