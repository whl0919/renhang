package com.chinaBank.bean.ExcelFileModel;

import java.io.Serializable;

import lombok.Data;

@Data
public class ExcelScriptInfoData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String table_name; //表名称
	private String table_desc; //表描述
	private String script_information; //脚本
	private String script_type; //创建是否成功
	private String create_user; //创建者
	private String id; //主键
}
