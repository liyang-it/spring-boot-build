package com.mh.jishi.pay.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.mh.jishi.constants.AliPayConstants;
import com.mh.jishi.pay.alipay.core.AliPayCreateOrderUtil;
import com.mh.jishi.pay.alipay.core.AlipayConfig;
import com.mh.jishi.service.TSystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author Lizr
 * @Doc 支付宝支付
 * @CreateTime 2022-01-05 下午 3:15
 **/
@Component
public class AliPayService {

    private final static Logger logger = LoggerFactory.getLogger(AliPayService.class);

    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    private TSystemService systemService;

    /**
     * 创建支付宝订单
     *
     * @param orderNo 订单号
     * @param amount  金额
     * @param body    支付内容
     * @return
     * @throws AlipayApiException
     */
    public String createOrder(String orderNo, double amount, String body) throws AlipayApiException {
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        if (StringUtils.isBlank(alipayConfig.getGatewayUrl())) {
            alipayConfig = new AlipayConfig();
            alipayConfig.setGatewayUrl(systemService.getValue(AliPayConstants.GatewayUrl));
            alipayConfig.setAppid(systemService.getValue(AliPayConstants.AppId));
            alipayConfig.setAppPrivateKey(systemService.getValue(AliPayConstants.AppPrivateKey));
            alipayConfig.setNotifyUrl(systemService.getValue(AliPayConstants.notifyUrl));
        }

        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setSubject(body);
        model.setOutTradeNo(orderNo);
        model.setTotalAmount(String.valueOf(amount));
//        model.setProductCode("QUICK_MSECURITY_PAY");
//        model.setPassbackParams("公用回传参数，如果请求时传递了该参数，则返回给商户时会回传该参数");

        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        /*AlipayTradeAppPayRequest ali_request = new AlipayTradeAppPayRequest();
        ali_request.setBizModel(model);
        ali_request.setNotifyUrl(alipayConfig.getNotifyUrl());// 回调地址
        AlipayTradeAppPayResponse ali_response = alipayClient.sdkExecute(ali_request);*/
        //就是orderString 可以直接给客户端请求，无需再做处理。
        //return ali_response.getBody();
        String payStr = AliPayCreateOrderUtil.createOrderNo(alipayConfig.getGatewayUrl(), alipayConfig.getAppid(),
                alipayConfig.getAppPrivateKey(), alipayConfig.getCharset(),
                alipayConfig.getAlipayPublicKey(), alipayConfig.getSignType(), alipayConfig.getNotifyUrl(), model);
        logger.info("支付宝支付结果: 【{}】", payStr);
        return payStr;
    }

    /**
     * 支付宝签名验证
     * @param request
     * @return
     */
    public boolean rsaCheckV1(HttpServletRequest request){
        try {
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String[] values = requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                params.put(name, valueStr);
            }

            boolean verifyResult = AlipaySignature.rsaCheckV1(params, systemService.getValue(AliPayConstants.AliPayPublicKey), alipayConfig.getCharset(), alipayConfig.getSignType());
            return verifyResult;
        } catch (AlipayApiException e) {
            logger.debug("verify sigin error, exception is:{}", e);
            return false;
        }
    }
    /*
    @Override
    public boolean notify(String tradeStatus, String orderNo, String tradeNo) {
        if ("TRADE_FINISHED".equals(tradeStatus)
                || "TRADE_SUCCESS".equals(tradeStatus)) {
            // 支付成功，根据业务逻辑修改相应数据的状态
            // boolean state = orderPaymentService.updatePaymentState(orderNo, tradeNo);
//            if (state) {
//                return true;
//            }
        }
        return false;
    }



    @Override
    public ResultMap refund(String orderNo, double amount, String refundReason) {
        if(StringUtils.isBlank(orderNo)){
            return ResultMap.error("订单编号不能为空");
        }
        if(amount <= 0){
            return ResultMap.error("退款金额必须大于0");
        }

        AlipayTradeRefundModel model=new AlipayTradeRefundModel();
        // 商户订单号
        model.setOutTradeNo(orderNo);
        // 退款金额
        model.setRefundAmount(String.valueOf(amount));
        // 退款原因
        model.setRefundReason(refundReason);
        // 退款订单号(同一个订单可以分多次部分退款，当分多次时必传)
        // model.setOutRequestNo(UUID.randomUUID().toString());
        AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();
        alipayRequest.setBizModel(model);
        AlipayTradeRefundResponse alipayResponse = null;
        try {
            alipayResponse = alipayClient.execute(alipayRequest);
        } catch (AlipayApiException e) {
            logger.error("订单退款失败，异常原因:{}", e);
        }
        if(alipayResponse != null){
            String code = alipayResponse.getCode();
            String subCode = alipayResponse.getSubCode();
            String subMsg = alipayResponse.getSubMsg();
            if("10000".equals(code)
                    && StringUtils.isBlank(subCode)
                    && StringUtils.isBlank(subMsg)){
                // 表示退款申请接受成功，结果通过退款查询接口查询
                // 修改用户订单状态为退款
                return ResultMap.ok("订单退款成功");
            }
            return ResultMap.error(subCode + ":" + subMsg);
        }
        return ResultMap.error("订单退款失败");
    }







    /**
     * WEB支付宝支付
     *
     * @param object 预支付参数对象
     * @return
     */
    /*
    @Override
    public String webAliPay(JSONObject object) {
        // 获取要向支付宝支付的参数,由页面传过来
        // 商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = object.getString("orderSn");
        // 付款金额，单位为元，精确到小数点后两位，必填
        String total_amount = object.getString("payAmount");
        // 订单名称，必填
        String subject = object.getString("subject");
        // 商品描述，可空
        String body = "";
        // 获得初始化的aliPayClient
        AlipayClient aliPayClient = new DefaultAlipayClient(object.getString("gatewayUrl"),
                object.getString("appId"),
                object.getString("mchPrivateKey"),
                "json",
                object.getString("charset"),
                object.getString("aliPayPublicKey"),
                object.getString("signType"));
        // 设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(object.getString("returnUrl"));  //设置同步回调通知
        alipayRequest.setNotifyUrl(object.getString("notifyUrl"));  //设置异步回调通知
        // 设置支付参数
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        try {
            // 请求
            AlipayTradePagePayResponse response = aliPayClient.pageExecute(alipayRequest, "get");
//            AlipayResponse result = aliPayClient.pageExecute(alipayRequest);
            logger.info("result{}", JSON.toJSONString(response));
            if (response.isSuccess())
                return response.getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return "";
    }
    */

}
