package com.chinaBank.bean.ExcelFileModel;

import java.io.Serializable;

import lombok.Data;

@Data
public class ExcelModelData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id; //主键
	private String excel_model_name; //模板名
	private String model_status;  //是否有效
	private String file_id; //用于文件关联模板
	private String create_user;
	private String create_date;
}
