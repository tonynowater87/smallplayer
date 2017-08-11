package com.tonynowater.smallplayer.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by tonynowater on 2017/6/4.
 */

public class DateUtil {

    private static final String DAFAULT_DATE_TEMPLATE = "yyyy/MM/dd";
    private static final String DAFAULT_DATE_FULL_TEMPLATE = "yyyyMMddhhmmss";

    private DateUtil() {
    }

    /**
     * @return 今天的日期 yyyy/MM/dd
     */
    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.TAIWAN);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DAFAULT_DATE_TEMPLATE);
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * @return 目前時間 yyyyMMddhhmmss
     */
    public static String getCurrentDateFullFormate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.TAIWAN);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DAFAULT_DATE_FULL_TEMPLATE);
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * @return 詳細日期內建格式
     */
    public static String getFullLocaleDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.TAIWAN);
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.TAIWAN);
        return dateFormat.format(calendar.getTime());
    }
}
