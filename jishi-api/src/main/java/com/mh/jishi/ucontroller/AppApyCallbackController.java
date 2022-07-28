package com.mh.jishi.ucontroller;

import com.alibaba.fastjson.JSONObject;
import com.mh.jishi.annotation.ApiDesc;
import com.mh.jishi.constants.WxPayConstants;
import com.mh.jishi.pay.alipay.AliPayService;
import com.mh.jishi.pay.applePay.IosVerifyUtil;
import com.mh.jishi.pay.wxpay.core.WXPayUtil;
import com.mh.jishi.pay.wxpay.core.XMLUtils;
import com.mh.jishi.service.TSystemService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.SortedMap;

/**
 * @Author Lizr
 * @Doc 支付回调控制层
 * @CreateTime 2022-01-06 上午 10:54
 **/
@RestController
@RequestMapping(value = "/api/callback")
public class AppApyCallbackController {

    private final static Logger log = LoggerFactory.getLogger(AppApyCallbackController.class);

    @Autowired
    private AliPayService aliPayService;

    @Autowired
    private TSystemService systemService;

    /**
     * 支付异步通知
     *
     * 接收到异步通知并验签通过后，一定要检查通知内容，
     * 包括通知中的app_id、out_trade_no、total_amount是否与请求中的一致，并根据trade_status进行后续业务处理。
     * https://docs.open.alipay.com/194/103296
     * request 是阿里发送过来的参数
     */
    @ApiDesc(methodDesc = "支付宝支付回调")
    @RequestMapping("/rechargeAliNotify")
    public String rechargeAliNotify(HttpServletRequest request) {
        // 验证签名
        boolean flag = aliPayService.rsaCheckV1(request);
        if (flag) {
            // 交易状态
            String tradeStatus = request.getParameter("trade_status");
            // 商户订单号
            String outTradeNo = request.getParameter("out_trade_no");
            // 支付宝订单号
            String tradeNo = request.getParameter("trade_no");
            // 订单总金额
            String totalAmount = request.getParameter("total_amount");
            String tradeFinished = "TRADE_FINISHED";
            if (tradeStatus.equals(tradeFinished) || "TRADE_SUCCESS".equals(tradeStatus)) {
                synchronized (this) {
                    // 和苹果支付回调校验逻辑一样
                    return "success";
                }
            }
        }
        return "fail";

    }

    /**
     * 订单微信支付异步回调通知
     * @return String
     */
    @ApiDesc(methodDesc = "微信支付回调")
    @RequestMapping("/rechargeWxNotify")
    public String rechargeWxNotify(@RequestBody String wxPayNotify) throws Exception {
        // 返回通知应答
        String resXml = "";
        // 通知结果Xml转换成Map
        SortedMap<Object, Object> notifyMap;
        notifyMap = XMLUtils.doXMLParse(wxPayNotify);
        if(notifyMap == null) {
            return resXml;
        }
        // 1.通知结果签名验证
        boolean flag = WXPayUtil.isTenpaySign(notifyMap, systemService.getValue(WxPayConstants.Key));
        String outTradeNo = notifyMap.get("out_trade_no").toString();
        if(flag){
            // 校验逻辑和 苹果支付校验一样
            synchronized (this) {
                return "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" +
                        "<return_msg><![CDATA[OK]]></return_msg>" + "</xml>";
            }
        }
        return "fail";
    }

    /**
     * 苹果支付回调通知
     * 苹果支付流程:
     *  1、前端调用苹果支付前，需要调用后台接口，生成订单号和金额等参数，然后返回前端
     *  2、前端拿到后台返回的订单号数据之后 调用苹果支付
     *  3、前端调用苹果支付后 拿到回调数据 在将回调数据和 后台返回的订单号数据等 再次传递给后台 进行二次验证
     *  4、接收到前端传递的 支付回调凭据和 订单号数据， 第一步请求苹果验证凭据是否支付完成,第二步验证 订单号是否在 自己后台是否存在 。
     *
     * @param iosJson{
     *          receipt-data: 苹果支付凭据,
     *          out_trade_no : 自定义字段 交易单号,
     *          total_amount: 自定义字段 支付金额,
     *          cz_type:  自定义字段 充值类型
     *          }
     * 苹果支付官方文档: https://developer.apple.com/library/archive/ApplePay_Guide/index.html#//apple_ref/doc/uid/TP40014764
     */
    @ApiDesc(methodDesc = "苹果支付验证")
    @RequestMapping(value = "/rechargeIosCheck")
    public String rechargeIosNotify(@RequestBody  String iosJson){
        log.info("开始处理苹果支付回调,回调数据: \n\r  [{}]", iosJson);
        // 验证失败json结果
        String rFailJson = "{\"status\":\"501\",\"info\":\"验证失败,如有疑问请联系管理\"}";

        // 验证成功json结果
        String rSuccessJson = "{\"status\":\"0\",\"info\":\"支付验证成功\"}";

        JSONObject jsonObject = JSONObject.parseObject(iosJson);
        // 凭据
        String iptData = jsonObject.getString("receipt-data");
        //请求苹果苹果支付验证
        String verifyResult = IosVerifyUtil.buyAppVerify(iptData, "");

        if (StringUtils.isBlank(verifyResult)) {
            log.error("苹果支付回调验证失败， 原因: 验证结果为空");
            return rFailJson;
        }else{
            // 得到苹果平台返回的json数据
            JSONObject appleReturn = JSONObject.parseObject(verifyResult);

            // 拿到交易status
            String states = appleReturn.getString("status");
            //  状态为 21007 表示 此收据来至测试环境  状态为 0 请求线上验证
            if("21007".equals(states)){
                verifyResult = IosVerifyUtil.buyAppVerify(iptData, "test");
                appleReturn = JSONObject.parseObject(verifyResult);
                states = appleReturn.getString("status");
            }
            if(states.equals("0")){
                log.info("苹果验证解析后数据: [{}]" , appleReturn);
                // 前端传递上来的订单号
                String outTradeNo = jsonObject.getString("out_trade_no");
                // 前端传递上来的订单号 金额
                BigDecimal totalAmount = new BigDecimal(jsonObject.getString("total_amount"));

                // 前端传递上来的 此次消费类型
                Integer czType = jsonObject.getInteger("cz_type");

                // 验证数据库订单号
                // 验证数据库订单是否存在等逻辑
                // 不存在 就返回验证失败,
                // 存在就 同步验证 数据库记录金额和前端传递上来的是否一致，一致后在进行消费成功逻辑
                int checkRecords = 0;
                if(checkRecords == 0){
                    log.error("苹果支付回调验证失败，原因: 没有查询到交易号: [{}] 相关数据", outTradeNo);
                    return rFailJson;
                }else{
                    synchronized (this){
                        // 判断金额
                        return rSuccessJson;
                    }
                }
            }else{
                return rFailJson;
            }
        }
    }
}
