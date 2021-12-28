package com.mh.jishi.util;

import io.netty.util.HashedWheelTimer;

import java.util.ArrayList;
import java.util.List;

/*
 * 订单流程：下单成功－》支付订单－》发货－》收货
 * 订单状态：
 * 101 订单生成，未支付；102，下单未支付用户取消；103，下单未支付超期系统自动取消 104： 超时未支付尾款,系统自动关闭订单
 * 201 (快递配送方式)支付完成，商家未发货；202 用户申请退款, 205 申请退款审核通过, 206 退款待寄回货物确认, 203，管理员执行退款操作，确认退款成功；
 * (门店自取方式)204 支付成功,待上门自提；
 * 207 已支付定金, 208 已支付尾款 ， 208狀態和支付完成狀態一樣
 * 301 商家发货，用户未确认；
 * 401 (快递配送方式)用户确认收货，订单结束； 402 用户没有确认收货，但是快递反馈已收货后，超过一定时间，系统自动确认收货，订单结束。
 * (门店自取方式) 403 门店已确定用户上门取件成功
 *
 * 当101用户未付款时，此时用户可以进行的操作是取消或者付款
 * 当201支付完成而商家未发货时，此时用户可以退款
 * 当301商家已发货时，此时用户可以有确认收货
 * 当401用户确认收货以后，此时用户可以进行的操作是退货、删除、去评价或者再次购买
 * 当402系统自动确认收货以后，此时用户可以删除、去评价、或者再次购买
 *
 * 门店自取方式 只有在门店确认用户取货之后才算订单完成
 */
public class OrderUtil {
    /**
     * 待付款
     */
    public static final Short STATUS_CREATE = 101;

    /**
     * 用户取消订单
     */
    public static final Short STATUS_CANCEL = 102;

    /**
     * 用户超时未支付 系统取消
     */
    public static final Short STATUS_AUTO_CANCEL = 103;

    /**
     * 用户超时未支付尾款 系统取消
     */
    public static final Short STATUS_AUTO_CANCEL_LAST = 104;

    /**
     * 订单已支付定金
     */
    public static final Short STATUS_PAY_FIRST = 207;

    /**
     * 订单已支付尾款
     */
    public static final Short STATUS_PAY_LAST = 208;

    /**
     * 订单付款完成 未发货
     */
    public static final Short STATUS_PAY = 201;

    /**
     * 已申请退款,待审核
     */
    public static final Short STATUS_REFUND = 202;

    /**
     * 退款申请通过,待确认退款
     */
    public static final Short STATUS_AUDIT_REFUND = 205;

    /**
     * 退款寄回货物确认
     */
    public static final Short STATUS_THHW_REFUND = 206;

    /**
     * 已申请定金,待审核
     */
    public static final Short STATUS_DEPOSIT_REFUND = 209;


    /**
     * 订单已退款
     */
    public static final Short STATUS_REFUND_CONFIRM = 203;

    /**
     * 订单待自提
     */
    public static final Short STATUS_PICK = 204;
    /**
     * 订单已发货
     */
    public static final Short STATUS_SHIP = 301;

    /**
     * 用户确认收货
     */
    public static final Short STATUS_CONFIRM = 401;

    /**
     * 系统自动确认收货
     */
    public static final Short STATUS_AUTO_CONFIRM = 402;

    /**
     * 门店确认自提完成
     */
    public static final Short STATUS_CONFIRM_PICK = 403;

    /**
     *  订单交易完成
     */
    public static final Short STATUS_END = 501;

//    public static OrderHandleOption build(LitemallOrder order) {
//        int status = order.getOrderStatus().intValue();
//        OrderHandleOption handleOption = new OrderHandleOption();
//
//        if (status == 101) {
//            // 如果订单没有被取消，且没有支付，则可支付，可取消
//            handleOption.setCancel(true);
//            handleOption.setPay(true);
//        } else if (status == 102 || status == 103 || status == 104) {
//            // 如果订单已经取消或是已完成，则可删除
//            handleOption.setDelete(true);
//        } else if (status == 201 || status == 204 || status == 208) {
//            // 如果订单已付款，没有发货，则可退款 或者订单已付款 还未门店取件, 或者已支付尾款,才可以退款
//            handleOption.setRefund(true);
//            if(order.getReceivingWay() == 0){
//                handleOption.setRemineShip(true);
//            }
//        } else if(status == 207){
//            // 支付完定金, 此订单还不算是已付款, 只有支付完尾款才算是 已付款, 定金无法退款
//        } else if (status == 202 || status == 205 || status == 206) {
//            // 如果订单处于退款流程 可以取消退款
//            handleOption.setCancelRefund(true);
//            if(status == 206){
//                handleOption.setInShip(true);
//            }
////        } else if (status == 205) {
////            // 待确认退款
////        } else if (status == 206) {
////            // 退款待寄回货物确认
//        } else if (status == 203) {
//            // 如果订单已经退款，则可删除
//            handleOption.setDelete(true);
//        } else if (status == 301) {
//            // 如果订单已经发货，没有收货，则可收货操作,
//            // 此时不能取消订单
//            handleOption.setRefund(true);
//            handleOption.setConfirm(true);
//        } else if (status == 401 || status == 402) {
//            handleOption.setRefund(true);
//            // 如果订单已经支付，且已经收货，则可删除、去评论、申请售后和再次购买
//            handleOption.setDelete(true);
//            handleOption.setComment(true);
//            handleOption.setRebuy(true);
//            handleOption.setAftersale(true);
//
//        } else if (status == 403) {
//            // 門店自提完成只能 删除、去评论、
//            handleOption.setDelete(true);
//            handleOption.setComment(true);
//        } else if (status == 501) {
//            // 订单交易完成
//        } else if(status == 209){
//            // 这个时候可以取消定金退款
//            handleOption.setCancelRefund(true);
//        } else {
//            throw new IllegalStateException("status不支持");
//        }
//        HashedWheelTimer timer = new HashedWheelTimer();
//        return handleOption;
//    }
    /*
    public static String orderStatusText(LitemallOrder order) {
        int status = order.getOrderStatus().intValue();

        if (status == 101) {
            return "未付款";
        }

        if (status == 102) {
            return "已取消";
        }

        if (status == 103) {
            return "已取消(系統)";
        }

        if (status == 104) {
            return "已取消,未支付尾款(系統)";
        }

        if (status == 201) {
            return "已付款";
        }

        if (status == 202) {
            return "已申請退款，待審核";
        }
        if (status == 205) {
            return "退款申請通過,待確認退款";
        }
        if (status == 206) {
            return "退款待寄回貨物";
        }
        if (status == 207) {
            return "已支付定金";
        }
        if (status == 208) {
            return "已支付尾款";
        }
        if (status == 209) {
            return "已申請定金退款,待審核";
        }
        if (status == 203) {
            return "已退款";
        }

        if (status == 204) {
            return "待自提";
        }

        if (status == 301) {
            return "已發貨";
        }

        if (status == 401) {
            return "已收貨";
        }

        if (status == 402) {
            return "已收貨(系統)";
        }
        if(status == 403){
            return "自提完成";
        }
        if(status == 501){
            return "交易完成";
        }
        throw new IllegalStateException("orderStatus不支持");
    }




    public static List<Short> orderStatus(Integer showType) {
        // 全部订单
        if (showType == 0) {
            return null;
        }

        List<Short> status = new ArrayList<Short>(2);

        if (showType.equals(1)) {
            // 待付款订单
            status.add((short) 101);
        } else if (showType.equals(2)) {
            // 待发货订单
            status.add((short) 201);
        } else if (showType.equals(3)) {
            // 待收货订单
            status.add((short) 301);
        } else if (showType.equals(4)) {
            // 待评价订单
            status.add((short) 401);
//            系统超时自动取消，此时应该不支持评价
//            status.add((short)402);
        } else {
            return null;
        }

        return status;
    }

    public static boolean isRemindShip(LitemallOrder litemallOrder){
        return OrderUtil.STATUS_PAY == litemallOrder.getOrderStatus().shortValue();
    }
    public static boolean isCreateStatus(LitemallOrder litemallOrder) {
        return OrderUtil.STATUS_CREATE == litemallOrder.getOrderStatus().shortValue();
    }

    public static boolean hasPayed(LitemallOrder order) {
        return OrderUtil.STATUS_CREATE != order.getOrderStatus().shortValue()
                && OrderUtil.STATUS_CANCEL != order.getOrderStatus().shortValue()
                && OrderUtil.STATUS_AUTO_CANCEL != order.getOrderStatus().shortValue();
    }

    public static boolean isPayStatus(LitemallOrder litemallOrder) {
        return OrderUtil.STATUS_PAY == litemallOrder.getOrderStatus().shortValue();
    }

    public static boolean isShipStatus(LitemallOrder litemallOrder) {
        return OrderUtil.STATUS_SHIP == litemallOrder.getOrderStatus().shortValue();
    }

    public static boolean isConfirmStatus(LitemallOrder litemallOrder) {
        return OrderUtil.STATUS_CONFIRM == litemallOrder.getOrderStatus().shortValue();
    }

    public static boolean isCancelStatus(LitemallOrder litemallOrder) {
        return OrderUtil.STATUS_CANCEL == litemallOrder.getOrderStatus().shortValue();
    }

    public static boolean isAutoCancelStatus(LitemallOrder litemallOrder) {
        return OrderUtil.STATUS_AUTO_CANCEL == litemallOrder.getOrderStatus().shortValue();
    }

    public static boolean isRefundStatus(LitemallOrder litemallOrder) {
        return OrderUtil.STATUS_REFUND == litemallOrder.getOrderStatus().shortValue();
    }

    public static boolean isRefundConfirmStatus(LitemallOrder litemallOrder) {
        return OrderUtil.STATUS_REFUND_CONFIRM == litemallOrder.getOrderStatus().shortValue();
    }

    public static boolean isAutoConfirmStatus(LitemallOrder litemallOrder) {
        return OrderUtil.STATUS_AUTO_CONFIRM == litemallOrder.getOrderStatus().shortValue();
    }

     */
}
