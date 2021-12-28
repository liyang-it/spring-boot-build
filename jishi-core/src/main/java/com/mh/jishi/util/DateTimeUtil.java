package com.mh.jishi.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 * <h1>日期工具类</h1>
 */
public class DateTimeUtil {
    /**
     * 格式 yyyy年MM月dd日 HH:mm:ss
     *
     * @param dateTime
     * @return
     */
    public static String getDateTimeDisplayString(LocalDateTime dateTime) {
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
        String strDate2 = dtf2.format(dateTime);

        return strDate2;
    }

    /**
     * <h3>获取当前月最小日期 格式 yyyy-MM-dd HH:mm:ss</h3>
     * @return
     */
    public static String getMonthMinDate(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String first = format.format(c.getTime());
        return first;
    }
    /**
     * <h3>获取当前月最小日期 格式 yyyy-MM-dd HH:mm:ss</h3>
     * @return
     */
    public static String getMonthMaxDate(){
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.HOUR_OF_DAY, ca.getActualMaximum(Calendar.HOUR_OF_DAY));
        ca.set(Calendar.SECOND, ca.getActualMaximum(Calendar.SECOND));
        ca.set(Calendar.MINUTE, ca.getActualMaximum(Calendar.MINUTE));
        ca.set(Calendar.MILLISECOND, ca.getActualMaximum(Calendar.MILLISECOND));
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String last = format.format(ca.getTime());
        return last;
    }
}
