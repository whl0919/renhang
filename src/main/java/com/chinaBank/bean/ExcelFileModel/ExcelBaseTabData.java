package com.chinaBank.bean.ExcelFileModel;

import java.io.Serializable;

import lombok.Data;

@Data
public class ExcelBaseTabData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id; //主键
	private String table_name; //表名
	private String table_desc; //表描述
	private String read_row; //开始读取行
	private String sheet_name; //页签名
	private String status;  //是否有效
	private String create_user;  //创建人
	private String update_user;  //更新人
	private String model_id; //模板id
	private String create_date; //创建日期
	private String update_date; //更新日期
	private String script_type;//表是否执行成功（Y成功/N不成功）
}
