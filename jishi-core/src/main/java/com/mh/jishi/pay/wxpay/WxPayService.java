package com.mh.jishi.pay.wxpay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mh.jishi.constants.WxPayConstants;
import com.mh.jishi.pay.wxpay.core.*;
import com.mh.jishi.service.TSystemService;
import com.mh.jishi.util.IpUtil;
import com.mh.jishi.util.QRUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @Author Lizr
 * @Doc 微信支付
 * @CreateTime 2022-01-05 下午 3:15
 **/
@Component
@SuppressWarnings("all")
public class WxPayService {

    private final static Logger logger = LoggerFactory.getLogger(WxPayService.class);

    @Autowired
    private TSystemService systemService;

    /**
     * Map<String, String> orderParam 参数说明：
     *
     *  orderParam.put("body", "支付內容");
     *  orderParam.put("out_trade_no", "後台生成的订单号");
     *  orderParam.put("total_fee", “支付金额，单位为 分”);
     *
     */


    /**
     * h5微信支付 统一下单 预支付
     * @param orderParam
     * @return
     * @throws Exception
     */
    public String h5UnifiedPay(Map<String, String> orderParam) throws Exception{
        orderParam.put("appid", systemService.getValue(WxPayConstants.WeH5AppId));
        orderParam.put("mch_id", systemService.getValue(WxPayConstants.MchId));
        orderParam.put("nonce_str", WXPayUtil.generateNonceStr());
        orderParam.put("notify_url", systemService.getValue(WxPayConstants.NotifyUrl));
        orderParam.put("trade_type", "MWEB");
        String key = systemService.getValue(WxPayConstants.Key);
        orderParam.put("sign", WXPayUtil.generateSignature(orderParam,key));
        WXPay wxPay = new WXPay(new WXPayConfig(orderParam));
        Map<String, String> resultMap = wxPay.unifiedOrder(orderParam);
        Map<String, String> returnMap = new HashMap<>(16);
        // 获取返回码
        String returnCode = resultMap.get("return_code");
        // 若返回码为SUCCESS，则会返回一个result_code,再对该result_code进行判断
        String resultSuccess = "SUCCESS";
        if (resultSuccess.equals(returnCode)) {
            //主要返回以下5个参数(必须按照顺序，否则APP报错：-1)
            String resultCode = resultMap.get("result_code");
            returnMap.put("appId", resultMap.get("appid"));
            returnMap.put("nonceStr", resultMap.get("nonce_str"));
            // resultCode为SUCCESS，才会返回prepay_id和trade_type
            if (resultSuccess.equals(resultCode)) {
                returnMap.put("package","prepay_id=" .concat(resultMap.get("prepay_id")));
                // IOS必须10位数时间戳
                returnMap.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
                returnMap.put("signType", "MD5");
                // 二次签名
                String sign = WXPayUtil.generateSignature(returnMap, key);
                returnMap.put("sign",sign);
                returnMap.put("partnerid", resultMap.get("mch_id"));
                returnMap.put("prepayid", resultMap.get("prepay_id"));
                // 支付 跳转链接
                returnMap.put("mweb_url", resultMap.get("mweb_url"));
                return JSON.toJSONString(returnMap);
            } else {
                //此时返回没有预付订单的数据
                return JSON.toJSONString(returnMap);
            }
        } else {
            return JSON.toJSONString(returnMap);
        }
    }

    /**
     * 微信公众号 统一下单 预支付
     * @param orderParam, type 1 小程序支付
     * @return
     * @throws Exception
     */
    public String weh5UnifiedPay(Map<String, String> orderParam) throws Exception{
        orderParam.put("appid", systemService.getValue(WxPayConstants.WeH5AppId));
        orderParam.put("mch_id", systemService.getValue(WxPayConstants.MchId));
        orderParam.put("nonce_str", WXPayUtil.generateNonceStr());
        orderParam.put("notify_url", systemService.getValue(WxPayConstants.NotifyUrl));
        orderParam.put("trade_type", "JSAPI");
        String key = systemService.getValue(WxPayConstants.Key);
        orderParam.put("sign", WXPayUtil.generateSignature(orderParam,key));
        WXPay wxPay = new WXPay(new WXPayConfig(orderParam));
        Map<String, String> resultMap = wxPay.unifiedOrder(orderParam);
        Map<String, String> returnMap = new HashMap<>(16);
        // 获取返回码
        String returnCode = resultMap.get("return_code");
        // 若返回码为SUCCESS，则会返回一个result_code,再对该result_code进行判断
        String resultSuccess = "SUCCESS";
        if (resultSuccess.equals(returnCode)) {
            //主要返回以下5个参数(必须按照顺序，否则APP报错：-1)
            String resultCode = resultMap.get("result_code");
            returnMap.put("appId", resultMap.get("appid"));
            returnMap.put("nonceStr", resultMap.get("nonce_str"));
            // resultCode为SUCCESS，才会返回prepay_id和trade_type
            if (resultSuccess.equals(resultCode)) {
                returnMap.put("package","prepay_id=" .concat(resultMap.get("prepay_id")));
                // IOS必须10位数时间戳
                returnMap.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
                returnMap.put("signType", "MD5");
                // 二次签名
                String sign = WXPayUtil.generateSignature(returnMap, key);
                returnMap.put("sign",sign);
                returnMap.put("partnerid", resultMap.get("mch_id"));
                returnMap.put("prepayid", resultMap.get("prepay_id"));
                return JSON.toJSONString(returnMap);
            } else {
                //此时返回没有预付订单的数据
                return JSON.toJSONString(returnMap);
            }
        } else {
            return JSON.toJSONString(returnMap);
        }
    }
    /**
     * 小程序 统一下单 预支付
     * @param orderParam, type 1 小程序支付
     * @return
     * @throws Exception
     */
    public String weappUnifiedPay(Map<String, String> orderParam) throws Exception{
        orderParam.put("appid", systemService.getValue(WxPayConstants.WeAppAppId));
        orderParam.put("mch_id", systemService.getValue(WxPayConstants.MchId));
        orderParam.put("nonce_str", WXPayUtil.generateNonceStr());
        orderParam.put("notify_url", systemService.getValue(WxPayConstants.NotifyUrl));
        orderParam.put("trade_type", "JSAPI");
        String key = systemService.getValue(WxPayConstants.Key);
        orderParam.put("sign", WXPayUtil.generateSignature(orderParam,key));
        WXPay wxPay = new WXPay(new WXPayConfig(orderParam));
        Map<String, String> resultMap = wxPay.unifiedOrder(orderParam);
        Map<String, String> returnMap = new HashMap<>(16);
        // 获取返回码
        String returnCode = resultMap.get("return_code");
        // 若返回码为SUCCESS，则会返回一个result_code,再对该result_code进行判断
        String resultSuccess = "SUCCESS";
        if (resultSuccess.equals(returnCode)) {
            //主要返回以下5个参数(必须按照顺序，否则APP报错：-1)
            String resultCode = resultMap.get("result_code");
            returnMap.put("appId", resultMap.get("appid"));
            returnMap.put("nonceStr", resultMap.get("nonce_str"));
            // resultCode为SUCCESS，才会返回prepay_id和trade_type
            if (resultSuccess.equals(resultCode)) {
                returnMap.put("package","prepay_id=" .concat(resultMap.get("prepay_id")));
                // IOS必须10位数时间戳
                returnMap.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
                returnMap.put("signType", "MD5");
                // 二次签名
                String sign = WXPayUtil.generateSignature(returnMap, key);
                returnMap.put("sign",sign);
                returnMap.put("partnerid", resultMap.get("mch_id"));
                returnMap.put("prepayid", resultMap.get("prepay_id"));
                return JSON.toJSONString(returnMap);
            } else {
                //此时返回没有预付订单的数据
                return JSON.toJSONString(returnMap);
            }
        } else {
            return JSON.toJSONString(returnMap);
        }
    }

    /**
     * App 微信预下单
     * @param orderParam
     * @return
     * @throws Exception
     */
    public String unifiedPay(Map<String, String> orderParam) throws Exception {
        orderParam.put("appid", systemService.getValue(WxPayConstants.AppId));
        orderParam.put("mch_id", systemService.getValue(WxPayConstants.MchId));
        orderParam.put("nonce_str", WXPayUtil.generateNonceStr());
        orderParam.put("notify_url", systemService.getValue(WxPayConstants.NotifyUrl));
        orderParam.put("trade_type", "APP");
        String key = systemService.getValue(WxPayConstants.Key);
        orderParam.put("sign", WXPayUtil.generateSignature(orderParam,key));
        WXPay wxPay = new WXPay(new WXPayConfig(orderParam));
        Map<String, String> resultMap = wxPay.unifiedOrder(orderParam);
        Map<String, String> returnMap = new HashMap<>(16);
        // 获取返回码
        String returnCode = resultMap.get("return_code");
        // 若返回码为SUCCESS，则会返回一个result_code,再对该result_code进行判断
        String resultSuccess = "SUCCESS";
        if (resultSuccess.equals(returnCode)) {
            //主要返回以下5个参数(必须按照顺序，否则APP报错：-1)
            String resultCode = resultMap.get("result_code");
            returnMap.put("appid", resultMap.get("appid"));
            returnMap.put("noncestr", resultMap.get("nonce_str"));
            // resultCode为SUCCESS，才会返回prepay_id和trade_type
            if (resultSuccess.equals(resultCode)) {
                returnMap.put("package","Sign=WXPay");
                returnMap.put("partnerid", resultMap.get("mch_id"));
                returnMap.put("prepayid", resultMap.get("prepay_id"));
                // IOS必须10位数时间戳
                returnMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
                // 二次签名
                String sign = WXPayUtil.generateSignature(returnMap, key);
                returnMap.put("sign",sign);
                return JSON.toJSONString(returnMap);
            } else {
                //此时返回没有预付订单的数据
                return JSON.toJSONString(returnMap);
            }
        } else {
            return JSON.toJSONString(returnMap);
        }
    }


    /**
     * web 二维码扫码支付
     * @param object  预支付参数对象
     * @return
     */
    public String webWxPay(HttpServletRequest request, JSONObject object) {
        String urlCode = null;
        SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
        packageParams.put("appid", object.getString("appId"));//公众账号ID
        packageParams.put("mch_id", object.getString("mchId"));//商户号.
        String currTime = PayToolUtil.getCurrTime();
        String strTime = currTime.substring(8, currTime.length());
        String strRandom = String.valueOf(PayToolUtil.buildRandom(4));
        String nonce_str = strTime + strRandom;
        packageParams.put("nonce_str", nonce_str);//随机字符串
        packageParams.put("body", object.getString("body"));  //商品描述
        packageParams.put("out_trade_no", object.getString("orderSn"));//商户订单号
        BigDecimal totalFee = object.getBigDecimal("payAmount").multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP);
        packageParams.put("total_fee", totalFee.toString()); //标价金额 订单总金额，单位为分
        packageParams.put("spbill_create_ip", IpUtil.getIpAddr(request));//终端IP APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP
        packageParams.put("notify_url", object.getString("notifyUrl"));//通知地址 异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数
        packageParams.put("trade_type", "NATIVE");//交易类型 NATIVE 扫码支付
        // 签名
        String sign = PayToolUtil.createSign("UTF-8", packageParams, object.getString("apiKey"));
        packageParams.put("sign", sign);
        // 将请求参数转换为xml格式的string
        String requestXML = PayToolUtil.getRequestXml(packageParams);
        logger.info("requestXML:{}", requestXML);
        // 调用微信支付统一下单接口
        String resXml = WXPayRequest.postData(object.getString("unifiedorderUrl"), requestXML);
        logger.info("resXml: {}", resXml);
        // 解析微信支付结果
        Map map = null;
        try {
            map = XMLUtils.doXMLParse(resXml);
            logger.info("map: {}", map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 返回微信支付的二维码连接
        urlCode = (String) map.get("code_url");
        logger.info("urlCode:{}", urlCode);
        if (StringUtils.isNotBlank(urlCode)) {
            String qrBase64 = QRUtil.writeToBase64(urlCode);
            return qrBase64;
        }
        return "";
    }
}
