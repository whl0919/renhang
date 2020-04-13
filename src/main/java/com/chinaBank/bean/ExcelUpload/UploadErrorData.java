package com.chinaBank.bean.ExcelUpload;

import java.io.Serializable;

import lombok.Data;

@Data
public class UploadErrorData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id; //主键
	private String user; //上传用户
	private String file_name; //文件名
	private String rule_id;//规则id
	private String err_Info; //错误信息
	private String batch;//批次号
	private String check_type;//硬性/软性
	private String reason; //软性校验没通过的原因
	private String check_class; 
	private String ROWNUM;//违反当前规则的列
	private String sheet_name;//违反当前规则的列
	private String create_date;
	private String status; //状态 Y最新数据/N旧数据
	private String org; //机构
	private String zq; //账期
}
