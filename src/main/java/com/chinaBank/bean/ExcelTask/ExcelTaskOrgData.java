package com.chinaBank.bean.ExcelTask;

import lombok.Data;

@Data
public class ExcelTaskOrgData {
	
	private static final long serialVersionUID = 1L;
	private String id;//主键
	private String org; //机构
	private String create_user; //创建人
	private String create_date;//创建时间
	private String batch; //批次号
	private String orgs;//临时、系列上传机构
	private String status;//是否有效
	private String type;//ALL全部
}
