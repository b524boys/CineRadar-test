package com.wztc.demo.util;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


public class DateUtils {

    /**
     * 日期 -> 字符串  yyyy-MM-dd
     * @return
     */
    public static String date2ShortString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        SpringTransaction.beginTransaction();
        return dateString;
    }

    /**
     * 日期 -> 字符串  yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String date2LongString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        return dateString;
    }


    /**
     * 获取日期长字符串  不带分隔符
     * @return
     */
    public static String getStringId() {
        Random rand = new Random();
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
        return dateString + (rand.nextInt(900) + 100);
    }

    /**
     * 字符串  -> 长时间
     * @param strDate
     * @return
     */
    public static Date str2DateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 字符串  -> 短时间
     * @param strDate
     * @return
     */
    public static Date str2DateShort(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 提取时间的年
     * @param date
     * @return
     */
    public static String getYear(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * 当前时间 +-时间
     * @param hours
     * @return
     */
    public static Date offsetHours(int hours) {
        // 获取当前时间
        Calendar cal = Calendar.getInstance();
        // 增加指定小时数
        cal.add(Calendar.HOUR_OF_DAY, hours);
        // 将 Calendar 对象转换为 Date 对象
        Date newDate = cal.getTime();
        return newDate;
    }



}