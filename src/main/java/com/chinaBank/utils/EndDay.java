package com.chinaBank.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component("endDay")
public class EndDay {

	public static void main(String[] args) throws Exception {
		String source = "2019-11-29 10:45:06";// 开始时间
		int workDay = 10;// 工作日
		String dayType = "CD";// WD 工作日/CD 自然日
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.parse(source);
//		for (int i = 0; i < workDay; i++) {
//			System.out.print("工作日：" + i + "天， ");
		String day = getWorkDay(df.parse(source), workDay, dayType);
//		}
	}

	/**
	 * * 根据开始日期 ，需要的工作日天数 ，计算工作截止日期，并返回截止日期
	 * 
	 * @param startDate 开始日期
	 * @param workDay   工作日天数(周一到周五)
	 * @return Date类型
	 * @createTime 2014-2-14 \
	 * @author Sunqinbo
	 */
//	public static Date getWorkDay(Date startDate, int workDay, int hour, int second, int minute) {
	public static String getWorkDay(Date startDate, int workDay, String dayType) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(startDate); // 将系统时间设置为开始时间
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		if ("WD".equals(dayType)) {// 工作日
			int wk = 0;// 自然日
			int count = 0;//
//			for (int i = 0; i < workDay; i++) { // 判断当天是否为周末，如果是周末加1
			do {
				c1.set(Calendar.DATE, c1.get(Calendar.DATE) + 1);// 每加一天
//				System.out.println(df.format(c1.getTime())+"----wk---"+wk);
				int dayForWeek = 0;
				if (c1.get(Calendar.DAY_OF_WEEK) == 1) {
					dayForWeek = 7;
				} else {
					dayForWeek = c1.get(Calendar.DAY_OF_WEEK) - 1;
				}
				if (dayForWeek == 6 || dayForWeek == 7) {
					++wk;
				} else {
					++count;
				}
			} while (count != workDay);
			c1.setTime(startDate); // 将系统时间设置为开始时间
			c1.set(Calendar.DATE, c1.get(Calendar.DATE) + wk + count);// 每加一天
//			}

		} else {// 自然日
			c1.setTime(startDate); // 将系统时间设置为开始时间
			c1.set(Calendar.DATE, c1.get(Calendar.DATE) + workDay);// 每加一天
		}
//		for (int i = 0; i < workDay; i++) { // 判断当天是否为周末，如果是周末加1
//			if (Calendar.SATURDAY == c1.get(Calendar.SATURDAY) || Calendar.SUNDAY == c1.get(Calendar.SUNDAY)) {
//				workDay = workDay + 1;
//				c1.set(Calendar.DATE, c1.get(Calendar.DATE) + 1);
//				continue;
//			}
////			c1.set(Calendar.DATE, c1.get(Calendar.DATE) + 1); // 当天数加1 判断是否为周末 如果是周末加1
////			if (Calendar.SATURDAY == c1.get(Calendar.SATURDAY) || Calendar.SUNDAY == c1.get(Calendar.SUNDAY)) {
////				workDay = workDay + 1;
////				c1.set(Calendar.DATE, c1.get(Calendar.DATE) + 1);
////				continue;
////			}
//		}
		// c1.set(Calendar.HOUR_OF_DAY, hour);
		// c1.set(Calendar.SECOND, second);
		// c1.set(Calendar.MINUTE, minute);
		// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// System.out.println(df.format(c1.getTime()) + " " +
		// getWeekOfDate(c1.getTime()));
		return df.format(c1.getTime());
	}

	public static int dayForWeek(String pTime) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(format.parse(pTime));
		int dayForWeek = 0;
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			dayForWeek = 7;
		} else {
			dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		}
		return dayForWeek;
	}

	/**
	 * * 根据日期，获取星期几
	 * 
	 * @param dt
	 * @return String类型
	 * @createTime 2014-2-14
	 * @author Sunqinbo
	 */
	public static String getWeekOfDate(Date dt) {
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}

}