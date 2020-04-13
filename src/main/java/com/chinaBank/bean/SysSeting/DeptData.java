package com.chinaBank.bean.SysSeting;

import java.io.Serializable;

import lombok.Data;

@Data
public class DeptData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id; //主键
	private String dept_name;  //部门名称
	private String superior_dept_name;   //上级部门
	private String dept_status;    //启用状态 启用Y/不启用N
	private String create_user;    //创建人
	private String create_date;    //创建时间
	
}
