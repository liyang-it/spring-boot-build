package com.mh.jishi.util;

/**
 * @Author Lizr
 * @Description 退款状态说明
 * @CreateTime 2021-10-27 下午 1:47
 *
 * 退款状态
 * 101 已付款未发货退款申请 102 已付款已发货 申请退款 103 已收货申请退款
 * 201 退款申请通过(已发货的话，需要用户寄回商品) 202 确认退款
 * 301 管理员或者门店拒绝退款
 * 401 用户取消退款
 */
public class OrderRefundUtil {
    /**
     * 已付款未发货退款
     */
    public final static int SHIPPED_REFUND = 101;

    /**
     * 已发货退款/仅退款
     */
    public final static int DELIVERY_REFUND = 102;

    /**
     * 已发货退款/退货退款
     */
    public final static int TAKE_REFUND = 103;

    /**
     * 未上门取件退款
     */
    public final static int NOTQJ_REFUND = 104;

    /**
     * 退款申请通过(未发货退款/ 仅退款)
     */
    public final static int AUDIT_N_REFUND = 200;

    /**
     * 退款申请通过(已发货的话，需要用户寄回商品/退货退款)
     */
    public final static int AUDIT_REFUND = 201;


    /**
     * 确认退款
     */
    public final static int CONFIRM_REFUND = 203;

    /**
     * 拒绝退款
     */
    public final static int GUN_REFUND = 301;
    /**
     * 用戶取消退款
     */
    public final static int CANCEL_REFUND = 401;

    /**
     * 根据退款信息返回退款状态文字描述
     * @param refundRecords 退款信息
     * @return
     */
    /*
    public static String getRefundText(LitemallRefundRecords refundRecords){
        String str = null;
        switch (refundRecords.getRStatus()){
            case 101:
                str = "未發貨退款";
                break;

            case 102:
                str = "已發貨退款/僅退款";
                break;

            case 103:
                str = "已發貨退款/退貨退款";
                break;

            case 104:
                str = "用戶已付款/未上門取件";
                break;
            case 200:
                str = "退款申請通過/未發貨退款/僅退款";
                break;
            case 201:
                str = "退款申請通過/待用戶寄回貨物/等待管理員確認商品";
                break;

            case 203:
                str = "已確認退款";
                break;

            case 301:
                str = "拒絕退款";
                break;

            case 401:
                str = "用戶已取消退款";
                break;
        }
        return str;
    }


     */


}
