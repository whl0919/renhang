package com.chinaBank.bean.ExcelTask;

import lombok.Data;

@Data
public class ExcelNoticeData {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String user; //用户
	private String title; //标题
	private String text; //正文
	private String path;//附件地址
	private String create_user;//创建人
	private String create_date;//创建日期
	private String update_user;//更新人
	private String update_date;//更新时间
	private String org;//机构
	private String notice_id;//通知id
	private String status;//状态
	private String read_type;//是否读取
}
