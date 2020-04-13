/*
 * Copyright (C), 2015-2018
 * FileName: MyScheduler
 * Author:   zhao
 * Date:     2018/10/12 19:07
 * Description: 测试用的定时任务
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.chinaBank.utils.Scheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.chinaBank.bean.ExcelTask.ExcelTaskReportOrgData;
import com.chinaBank.bean.SysSeting.HolidayData;
import com.chinaBank.service.ExcelTask.ExcelTaskService;
import com.chinaBank.service.SysSeting.SysSetingService;
import com.github.pagehelper.PageHelper;

/**
 * 〈一句话功能简述〉<br>
 * 〈测试用的定时任务〉
 *
 * @author zhao
 * @date 2018/10/12 19:07
 * @since 1.0.1
 */
@Component
public class MyScheduler {

  private static final Logger logger = LoggerFactory.getLogger(MyScheduler.class);
  
  @Autowired
  private ExcelTaskService excelTaskService;
  
  @Autowired
  private SysSetingService sysSetingService;
//  private int count = 0;
//  private int count2 = 0;
//  private int count3 = 0;
//
//  @Scheduled(fixedRate = 3000)
//  public void fixedRate() {
//    try {
//      Thread.sleep(1000);
//    } catch (InterruptedException e) {
//      logger.debug("fixedRate", e);
//    }
//    count++;
//    logger.info("fixedRate " + count + " time " + System.currentTimeMillis());
//  }
//
//  @Scheduled(fixedDelay = 3000)
//  public void fixedDelay() {
//    try {
//      Thread.sleep(1000);
//    } catch (InterruptedException e) {
//      logger.debug("fixedDelay", e);
//    }
//    count2++;
//    logger.info("fixedDelay " + count2 + " time " + System.currentTimeMillis());
//  }
//
//  //  每隔3秒执行一次：*/3 * * * * ?
//  @Scheduled(cron = "*/3 * * * * ?")
//  public void cron() {
//    try {
//      Thread.sleep(1000);
//    } catch (InterruptedException e) {
//      logger.debug("cron", e);
//    }
//    count3++;
//    logger.info("cron " + count3 + " time " + System.currentTimeMillis());
//  }
  

//　　0 0 10,14,16 * * ? 每天上午10点，下午2点，4点 
//　　0 0/30 9-17 * * ? 朝九晚五工作时间内每半小时 
//　　0 0 12 ? * WED 表示每个星期三中午12点 
//　　"0 0 12 * * ?" 每天中午12点触发 
//　　"0 15 10 ? * *" 每天上午10:15触发 
//　　"0 15 10 * * ?" 每天上午10:15触发 
//　　"0 15 10 * * ? *" 每天上午10:15触发  
//　　"0 * 14 * * ?" 在每天下午2点到下午2:59期间的每1分钟触发 
//　　"0 0/5 14 * * ?" 在每天下午2点到下午2:55期间的每5分钟触发 
//　　"0 0/5 14,18 * * ?" 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发 
//　　"0 0-5 14 * * ?" 在每天下午2点到下午2:05期间的每1分钟触发 
//　　"0 10,44 14 ? 3 WED" 每年三月的星期三的下午2:10和2:44触发 
//　　"0 15 10 ? * MON-FRI" 周一至周五的上午10:15触发 
//　　"0 15 10 15 * ?" 每月15日上午10:15触发 
//　　"0 15 10 L * ?" 每月最后一日的上午10:15触发 
//　　"0 15 10 ? * 6L" 每月的最后一个星期五上午10:15触发
//　　"0 15 10 ? * 6#3" 每月的第三个星期五上午10:15触发

	//  每隔3秒执行一次：*/3 * * * * ?
	@Scheduled(cron = "*/59 * * * * ?")   //0 0 12 * * ?
//	@Scheduled(cron = "0 0 14 * * ?")
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void getTask() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		String year = calendar.get(Calendar.YEAR) +""; //年
		String month = (calendar.get(Calendar.MONTH)+1)+""; //月
		String day =calendar.get(Calendar.DATE)+""; //日
		String oldDay = calendar.get(Calendar.DATE)+""; //日
		int weekday=calendar.get(Calendar.DAY_OF_WEEK); //星期
		if(Integer.parseInt(month) < 10) {
			month = "0"+month;
		}
		if(Integer.parseInt(day) < 10) {
			day = "0"+day;
		}
		String date = year+"-"+month+"-"+day;//当前时间
		System.out.println("当前时间："+date);
		String md = month+day;//当前时间
	  try {
		  Date nowDate = df.parse(date);
		  List<String> orgs = excelTaskService.selectExcelTaskOrgs();
		  for(String org:orgs) {
				ExcelTaskReportOrgData excelTaskReportOrgData = new ExcelTaskReportOrgData();
			    excelTaskReportOrgData.setOrg(org);
				List<ExcelTaskReportOrgData> excelTaskDatas = excelTaskService.selectExcelTaskReportOrg(excelTaskReportOrgData);//查询机构下所有的任务			
				for(ExcelTaskReportOrgData e:excelTaskDatas) {//循环所有任务
					e.setCreate_date(date);//设置创建任务的时间
					ExcelTaskReportOrgData data = new ExcelTaskReportOrgData();
					if(!"2".equals(e.getReportType())) {//非系列才做判断
						Date endDate = df.parse(e.getEndDate());
//						if(endDate.after(nowDate)) { //看结束日期是否到期，如果到期，不会生成新的任务
							if("1".equals(e.getReportType())) { //定时
//									DA日   DW 周 DX询 DY月 DJ季 DN年
								if("DA".equals(e.getReportCycle())) {
									e.setPaymentDay(date);
									if("CD".equals(e.getReport_cycle_class())) {//自然日
										if(1 == weekday ||7 == weekday) {//周6和周天，不管是否休假，都直接发
											data = excelTaskService.selectExcelTaskReport(e.getFileName(), date, e.getReportCycle(), "",e.getOrg());
											if(data == null) {
//												Date d = new Date();
//												e.setCreate_date(df.format(d));
												excelTaskService.addExcelTaskReportOrg(e);
											}
										}else {//周一到周5，休假不发，不休假发
											HolidayData holidayYData = sysSetingService.selecttHolidayDateByDate(date,"Y");
											if(holidayYData == null) {
												data = excelTaskService.selectExcelTaskReport(e.getFileName(), date, e.getReportCycle(), "",e.getOrg());
												if(data == null) {
//													Date d = new Date();
//													e.setCreate_date(df.format(d));
													excelTaskService.addExcelTaskReportOrg(e);
												}
											}
										}
									}else {//WD工作日
										if(1 == weekday ||7 == weekday) {//周6和周天，上班不发，不上班发
											HolidayData holidayNData = sysSetingService.selecttHolidayDateByDate(date,"N");
											if(holidayNData!=null) {
												data = excelTaskService.selectExcelTaskReport(e.getFileName(), date, e.getReportCycle(), "",e.getOrg());
												if(data == null) {
//													Date d = new Date();
//													e.setCreate_date(df.format(d));
													excelTaskService.addExcelTaskReportOrg(e);
												}
											}
										}else {//周一到周5，休假不发，不休假发
											HolidayData holidayYData = sysSetingService.selecttHolidayDateByDate(date,"Y");
											if(holidayYData == null) {
												data = excelTaskService.selectExcelTaskReport(e.getFileName(), date, e.getReportCycle(), "",e.getOrg());
												if(data == null) {
//													Date d = new Date();
//													e.setCreate_date(df.format(d));
													excelTaskService.addExcelTaskReportOrg(e);
												}
											}
										}
									}
								}else if("DW".equals(e.getReportCycle())) {
									if(2 == weekday) {//周一
										String WD = getWD(date);
										e.setPaymentDay(WD);
										boolean flag = isNotWorkDay( year, month, oldDay); //把整周的休假情况判断玩，如果这周有一天非休假，周一可以发任务
										if(flag == true) {
											data = excelTaskService.selectExcelTaskReport(e.getFileName(), WD, e.getReportCycle(), "",e.getOrg());
											if(data == null) {
//												Date d = new Date();
//												e.setCreate_date(df.format(d));
												excelTaskService.addExcelTaskReportOrg(e);
											}
										}				
									}
								}else if("DX".equals(e.getReportCycle())) {//每月的1\11\21号生成任务
									if("01".equals(day) || "11".equals(day) || "21".equals(day)) {
										String DX = "";
										if("01".equals(day)) {
											DX = year+"-"+month+"-"+"上旬";
										}else if("11".equals(day)) {
											DX = year+"-"+month+"-"+"中旬";
										}else if("11".equals(day)) {
											DX = year+"-"+month+"-"+"下旬";
										}
										e.setPaymentDay(DX);
										data = excelTaskService.selectExcelTaskReport(e.getFileName(), DX, e.getReportCycle(), "",e.getOrg());
										if(data == null) {
//											Date d = new Date();
//											e.setCreate_date(df.format(d));
											excelTaskService.addExcelTaskReportOrg(e);
										}
									}
								}else if("DY".equals(e.getReportCycle())) {
									if("01".equals(day)) {//每月1号
										String DY = year+"-"+month;
										e.setPaymentDay(DY);
										data = excelTaskService.selectExcelTaskReport(e.getFileName(), DY, e.getReportCycle(), "",e.getOrg());
										if(data == null) {
//											Date d = new Date();
//											e.setCreate_date(df.format(d));
											excelTaskService.addExcelTaskReportOrg(e);
										}
									}
								}else if("DJ".equals(e.getReportCycle())) {//1月1、4月1、7月1号、10月1号
									if("0101".equals(md) || "0401".equals(md) || "0701".equals(md) || "1001".equals(md)) {
										String DJ = "";
										if("0101".equals(md)) {
											DJ = year+"-"+"一季度";
										}else if("0401".equals(md)) {
											DJ = year+"-"+"二季度";
										}else if("0701".equals(md)) {
											DJ = year+"-"+"三季度";
										}else if("1001".equals(md)) {
											DJ = year+"-"+"四季度";
										}
										e.setPaymentDay(DJ);
										data = excelTaskService.selectExcelTaskReport(e.getFileName(), DJ, e.getReportCycle(), "",e.getOrg());
										if(data == null) {
//											Date d = new Date();
//											e.setCreate_date(df.format(d));
											excelTaskService.addExcelTaskReportOrg(e);
										}
									}
								}else if("DN".equals(e.getReportCycle())) {
									if("0101".equals(md)) {
										e.setPaymentDay(year);
										data = excelTaskService.selectExcelTaskReport(e.getFileName(), year, e.getReportCycle(), "",e.getOrg());
										if(data == null) {
//											Date d = new Date();
//											e.setCreate_date(df.format(d));
											excelTaskService.addExcelTaskReportOrg(e);
										}
									}
								}
							}else if("3".equals(e.getReportType())){//自定义
								if(date.equals(e.getStartDate())) {
									e.setPaymentDay(date);
									data = excelTaskService.selectExcelTaskReport(e.getFileName(), date, "", e.getStartDate(),e.getOrg());
									if(data == null) {
//										Date d = new Date();
//										e.setCreate_date(df.format(d));
										excelTaskService.addExcelTaskReportOrg(e);
									}
								}
							}
//						}
					}
				 }
			}
	  } catch (Exception e) {
	    logger.debug("cron", e);
	  }
	  logger.info("cron " + " time " + System.currentTimeMillis());
	}
	
	public boolean isNotWorkDay(String year,String month,String oldDay) {
		int day = Integer.parseInt(oldDay);
		for(int i=0;i<7;i++) {//循环一周
			++day;
			String newDay = "";
			if(day < 10) {
				 newDay = year+"-"+month+"-"+"0"+day;
			}
			newDay = year+"-"+month+"-"+"0"+month;
			if(i==5 || i==6) {//星期6和星期天才去看是否有工作日调休，只有有一天是在工作日，就直接返回
				HolidayData holidayNData = sysSetingService.selecttHolidayDateByDate(newDay,"N");
				if(holidayNData!=null) {
					return true;
				}
			}else {//星期1到5，只看当前日期是不是休假,任何一天不休假，就直接返回
				HolidayData holidayYData = sysSetingService.selecttHolidayDateByDate(newDay,"Y");
				if(holidayYData == null) {//表示当前日期是假期
					return true;
				}
			}
		}

		return false;
	}
	
	public String getWD(String date) throws Exception {
//		 Map<String,Integer> result =  new HashMap<String,Integer>();
		    String WD = "";
	        Calendar cal = Calendar.getInstance();
	 
	               //设置一周的开始,默认是周日,这里设置成星期一
	        cal.setFirstDayOfWeek(Calendar.MONDAY);
	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	        SimpleDateFormat formatMon = new SimpleDateFormat("MM");
	        SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");
	        Date d = null;
	        d = format.parse(date);
	
	        cal.setTime(d);
	        int month = Integer.valueOf(formatMon.format(d));
	        int year = Integer.valueOf(formatYear.format(d));
	        
	        int week = cal.get(Calendar.WEEK_OF_YEAR);
//	        result.put("week", week);
	        if(week == 1 && month == 12){
	        	WD = year + 1+"-"+week+"周";
//	            result.put("year", year + 1);
	        }else{
	        	WD = year +"-"+week+"周";
//	            result.put("year", year);
	        }
	        
	        return WD;
	}
}