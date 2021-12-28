package com.mh.jishi.util;

import java.util.Random;

public class CharUtil {
    /**
     * 随机获取订单编号
     * @param type 1 普通商品 2 VIP商品 3 预售商品 4 退款  12 购物车下单 5 抽奖
     * @return
     */
    public static String getRandomOrderNoStr(Integer type){
        return type.toString().concat(getRandomNum(13));
    }

    /**
     * 生成抽奖订单ID
     * @return
     */
    public static Integer getRandomOrderId(){
        int id = Integer.valueOf("25" + getRandomNum(7));
        return id;
    }
    /**
     * 获取 自定义长度随机字母加数字字符串
     * @param num
     * @return
     */
    public static String getRandomString(Integer num) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 获取自定义长度 数字字符串
     * @param num
     * @return
     */
    public static String getRandomNum(Integer num) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

}
