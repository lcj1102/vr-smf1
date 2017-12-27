package com.suneee.smf.smf.common;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUnit {

	public static final String ENG_DATE_FROMAT = "EEE, d MMM yyyy HH:mm:ss z";
	public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	public static final String YYYY = "yyyy";
	public static final String MM = "MM";
	public static final String DD = "dd";

	/**
	 * @Title: getWorkDay 
	 * @author:致远
	 * @Description: 获取两个日期的工作日
	 * @param startDate
	 * @param endDate
	 * @return
	 * @return: int
	 */
	public static int getWorkDay(Date startDate, Date endDate) {
		int day = daysBetween(startDate, endDate);
		Calendar startCalendar = Calendar.getInstance();
		int workDay = 0;
		for (int i=0;i<=day;i++) {
			startCalendar.setTime(icDateByDay(startDate,i));
			System.out.println(startCalendar.getTime());
			if (startCalendar.get(Calendar.DAY_OF_WEEK) != 1 && startCalendar.get(Calendar.DAY_OF_WEEK) != 7) {
				workDay++;
			}
		}
		return workDay;
	}
	
	/**
	 * @param date
	 * @return
	 * @作者 xgx
	 * @创建日期 2015-11-16
	 * @创建时间 下午12:22:40
	 * @描述 —— 格式化日期对象
	 */
	public static Date date2date(Date date, String formatStr) {
		SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
		String str = sdf.format(date);
		try {
			date = sdf.parse(str);
		} catch (Exception e) {
			return null;
		}
		return date;
	}

	/**
	 * @param date
	 * @return
	 * @作者 xgx
	 * @创建日期 2015-11-16
	 * @创建时间 下午12:24:19
	 * @描述 —— 时间对象转换成字符串
	 */
	public static String date2string(Date date, String formatStr) {
		String strDate = "";
		SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
		strDate = sdf.format(date);
		return strDate;
	}

	/**
	 * @param date
	 * @return
	 * @作者 xgx
	 * @创建日期 2015-11-16
	 * @创建时间 下午12:24:19
	 * @描述 —— sql时间对象转换成字符串
	 */
	public static String timestamp2string(Timestamp timestamp, String formatStr) {
		String strDate = "";
		SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
		strDate = sdf.format(timestamp);
		return strDate;
	}

	/**
	 * @param dateString
	 * @param formatStr
	 * @return
	 * @作者 xgx
	 * @创建日期 2015-11-16
	 * @创建时间 下午3:09:24
	 * @描述 —— 字符串转换成时间对象
	 */
	public static Date string2date(String dateString, String formatStr) {
		Date formateDate = null;
		DateFormat format = new SimpleDateFormat(formatStr);
		try {
			formateDate = format.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
		return formateDate;
	}

	/**
	 * @param date
	 * @return
	 * @作者 xgx
	 * @创建日期 2015-11-16
	 * @创建时间 上午09:18:36
	 * @描述 —— Date类型转换为Timestamp类型
	 */
	public static Timestamp date2timestamp(Date date) {
		if (date == null)
			return null;
		return new Timestamp(date.getTime());
	}

	/**
	 * @return
	 * @作者 xgx
	 * @创建日期 2015-11-16
	 * @创建时间 下午05:02:57
	 * @描述 —— 获得当前年份
	 */
	public static String getNowYear() {
		SimpleDateFormat sdf = new SimpleDateFormat(YYYY);
		return sdf.format(new Date());
	}

	/**
	 * @return
	 * @作者 xgx
	 * @创建日期 2015-11-16
	 * @创建时间 下午05:03:15
	 * @描述 —— 获得当前月份
	 */
	public static String getNowMonth() {
		SimpleDateFormat sdf = new SimpleDateFormat(MM);
		return sdf.format(new Date());
	}

	/**
	 * @return
	 * @作者 xgx
	 * @创建日期 2015-11-16
	 * @创建时间 08:41:47
	 * @描述 —— 获得当前日期中的日
	 */
	public static String getNowDay() {
		SimpleDateFormat sdf = new SimpleDateFormat(DD);
		return sdf.format(new Date());
	}

	/**
	 * @param time
	 * @return
	 * @作者 xgx
	 * @创建日期 2015-11-16
	 * @创建时间 上午10:19:31
	 * @描述 —— 指定时间距离当前时间的中文信息
	 */
	public static String getLnow(long time) {
		Calendar cal = Calendar.getInstance();
		long timel = cal.getTimeInMillis() - time;
		if (timel / 1000 < 60) {
			return "1分钟以内";
		} else if (timel / 1000 / 60 < 60) {
			return timel / 1000 / 60 + "分钟前";
		} else if (timel / 1000 / 60 / 60 < 24) {
			return timel / 1000 / 60 / 60 + "小时前";
		} else {
			return timel / 1000 / 60 / 60 / 24 + "天前";
		}
	}
	
	/**
	 * 增减时间的天数
	 */
	public static Date icDateByDay(Date date , int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, days);
		return cal.getTime();
	}
	
	/**
	 * 增减时间的月数
	 */
	public static Date icDateByMonth(Date date , int months) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, months);
		return cal.getTime();
	}
	
	/**
	 * 增减时间的年数
	 */
	public static Date icDateByYear(Date date , int years) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, years);
		return cal.getTime();
	}
	
	 /**  
     * 计算两个日期之间相差的天数  
     * @param smdate 较小的时间 
     * @param bdate  较大的时间 
     * @return 相差天数 
     * @throws ParseException  
     */    
    public static int daysBetween(Date smdate,Date bdate)   
    {    
    	 Calendar calst = Calendar.getInstance();   
         Calendar caled = Calendar.getInstance();   
         calst.setTime(smdate);   
         caled.setTime(bdate);   
         //设置时间为0时   
         calst.set(Calendar.HOUR_OF_DAY, 0);   
         calst.set(Calendar.MINUTE, 0);   
         calst.set(Calendar.SECOND, 0);   
         caled.set(Calendar.HOUR_OF_DAY, 0);   
         caled.set(Calendar.MINUTE, 0);   
         caled.set(Calendar.SECOND, 0);   
         //得到两个日期相差的天数   
         int days = ((int) (caled.getTime().getTime() / 1000) - (int) (calst   
                 .getTime().getTime() / 1000)) / 3600 / 24;   
          
         return days;             
    }   
    
    /**
    * 获取两个日期相差的月数
    * @param d1  较大的日期
    * @param d2  较小的日期
    * @return 如果d1>d2返回 月数差 否则返回0
    */
    public static int getMonthDiff(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        if(c1.getTimeInMillis() < c2.getTimeInMillis()) return 0;
        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH);
        int month2 = c2.get(Calendar.MONTH);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        // 获取年的差值 假设 d1 = 2015-8-16 d2 = 2011-9-30
        int yearInterval = year1 - year2;
        // 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数
        if(month1 < month2 || month1 == month2 && day1 < day2) yearInterval --;
        // 获取月数差值
        int monthInterval = (month1 + 12) - month2 ;
        if(day1 < day2) monthInterval --;
        monthInterval %= 12;
        return yearInterval * 12 + monthInterval;
    }
    
    /**
     * 获取两个日期相差的年数
     * @param d1  较大的日期
     * @param d2  较小的日期
     * @return 如果d1>d2返回 月数差 否则返回0
     */
     public static int getYearDiff(Date d1, Date d2) {
         Calendar c1 = Calendar.getInstance();
         Calendar c2 = Calendar.getInstance();
         c1.setTime(d1);
         c2.setTime(d2);
         if(c1.getTimeInMillis() < c2.getTimeInMillis()) return 0;
         int year1 = c1.get(Calendar.YEAR);
         int year2 = c2.get(Calendar.YEAR);
      
         // 获取年的差值 假设 d1 = 2015-8-16 d2 = 2011-9-30
         int yearInterval = year1 - year2;
         return yearInterval;
     }
}