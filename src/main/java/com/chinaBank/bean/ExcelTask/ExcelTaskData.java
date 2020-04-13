package com.chinaBank.bean.ExcelTask;

import lombok.Data;

@Data
public class ExcelTaskData {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String file_name; //文件名称
	private String dept_id; //部门
	private String dept_name; //部门
	private String adress;//文件下发地址
	private String report_cycle_type;//上报周期类型（定时、临时、系列）
	private String report_cycle;//上传类型（日、周、旬、月、季、年报）
	private String [] reportDates;//临时、系列上传时间
	private String report_date;//上传时间
	private String report_cycle_class; //上报类型（工作日、自然日）
	private String report_days; //上报天数
	private String status; //是否有效
	private String create_user; //创建人
	private String create_date;//创建日期
	private String update_user; //更新用户
	private String update_date;//更新日期
	private String report_cycle_last_day; //上报截止日期
	private String batch;//批次号
	private String oldStatus;//原来的状态
	private String model_id;//文件配置模板
	private String model_name;//文件配置模板
}
