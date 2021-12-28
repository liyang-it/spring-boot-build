package com.mh.jishi.util;

public class OrderHandleOption {
    private boolean cancel = false;      // 取消操作
    private boolean delete = false;      // 删除操作
    private boolean pay = false;         // 支付操作
    private boolean comment = false;    // 评论操作
    private boolean confirm = false;    // 确认收货操作
    private boolean refund = false;     // 取消订单并退款操作
    private boolean rebuy = false;        // 再次购买
    private boolean aftersale = false;        // 售后操作
    private boolean cancelRefund = false;        // 取消退款
    private boolean RemineShip = false;        // 是否可以提醒发货
    private boolean inShip = false;        // 是否可以填写退款物流信息
    private boolean isDepositRefund = false;        // 定金是否可退款

    public boolean getIsDepositRefund() {
        return isDepositRefund;
    }

    public void setDepositRefund(boolean depositRefund) {
        isDepositRefund = depositRefund;
    }

    public boolean isInShip() {
        return inShip;
    }

    public void setInShip(boolean inShip) {
        this.inShip = inShip;
    }

    public boolean isRemineShip() {
        return RemineShip;
    }

    public void setRemineShip(boolean remineShip) {
        RemineShip = remineShip;
    }

    public boolean isCancelRefund() {
        return cancelRefund;
    }

    public void setCancelRefund(boolean cancelRefund) {
        this.cancelRefund = cancelRefund;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean isPay() {
        return pay;
    }

    public void setPay(boolean pay) {
        this.pay = pay;
    }

    public boolean isComment() {
        return comment;
    }

    public void setComment(boolean comment) {
        this.comment = comment;
    }

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    public boolean isRefund() {
        return refund;
    }

    public void setRefund(boolean refund) {
        this.refund = refund;
    }

    public boolean isRebuy() {
        return rebuy;
    }

    public void setRebuy(boolean rebuy) {
        this.rebuy = rebuy;
    }

    public boolean isAftersale() {
        return aftersale;
    }

    public void setAftersale(boolean aftersale) {
        this.aftersale = aftersale;
    }
}
