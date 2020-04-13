package com.chinaBank.bean.SysSeting;

import lombok.Data;

@Data
public class HolidayData {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id; //主键
	private String holiday_date; //节假日
	private String work_type; //调休日/节假日
	private String create_user; //创建人
	private String create_date; //创建日期
	private String update_user; //更新人
	private String update_date; //更新日期
	
}
