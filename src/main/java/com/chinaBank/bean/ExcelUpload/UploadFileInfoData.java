package com.chinaBank.bean.ExcelUpload;

import java.io.Serializable;

import lombok.Data;

@Data
public class UploadFileInfoData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id; //逐渐
	private String file_name;  //文件名
	private String user;  //用户
	private String status; //上传状态
	private String adress; //上传后的地址
	private String org; //机构
	private String create_user; //上传人
	private String create_date;  //上传日期
	private String batch; //批次号
	private String check_type; //审核类型
	private String remark; //备注
	private String orgs; //下属机构
	
}
